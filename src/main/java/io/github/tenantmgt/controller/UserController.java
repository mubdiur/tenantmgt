package io.github.tenantmgt.controller;

import java.io.IOException;
import java.net.URI;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.tenantmgt.model.ErrorResponse;
import io.github.tenantmgt.model.LoginRequest;
import io.github.tenantmgt.model.Role;
import io.github.tenantmgt.model.TokenResponse;
import io.github.tenantmgt.model.User;
import io.github.tenantmgt.serviceinterface.UserServiceInterface;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@CrossOrigin
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    @Value("${jwt.secret}")
    private String jwtSecret;


    private final UserServiceInterface userService;
    private static final String APPLICATION_JSON_VALUE = "application/json";
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/admin/user")
    public ResponseEntity<User> getUser(@RequestParam String username) {
        return ResponseEntity.ok().body(userService.getUser(username));
    }

    
    @PostMapping("/admin/user/save")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        URI uri = URI.create(
                ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/admin/user/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/login").toUriString());
        User user = userService.getUser(loginRequest.getUsername());
        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            // Passwords matched
            // generate tokens
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());

            final String accessToken = JWT.create()
                    .withSubject(user.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000)) // 10 minutes
                    .withIssuer("localhost")
                    .withClaim(
                            "roles",
                            user.getRoles().stream().map(Role::getName)
                                    .collect(Collectors.toList()))
                    .sign(algorithm);

            final String refreshToken = JWT.create()
                    .withSubject(user.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)) // 30 days
                    .withIssuer("localhost")
                    .sign(algorithm);

            log.info("Login was successful");
            TokenResponse tokenResponse = new TokenResponse();
            tokenResponse.setAccessToken(accessToken);
            tokenResponse.setRefreshToken(refreshToken);
            return ResponseEntity.created(uri).body(tokenResponse);
        } else {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrorType("loginError");
            errorResponse.setErrorMsg("Username Password combination did not match");
            log.error("Login Error", errorResponse.getErrorMsg());
            return ResponseEntity.created(uri).body(errorResponse);
        }
    }

    
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/register").toUriString());
        user.setRoles(new ArrayList<>());
        User savedUser = userService.saveUser(user);
        userService.addRoleToUser(savedUser.getUsername(), "ROLE_USER");
        return ResponseEntity.created(uri).body(savedUser);
    }

    @PostMapping("/admin/role/save")
    public ResponseEntity<Role> saveRole(@RequestBody Role role) {
        URI uri = URI.create(
                ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/admin/role/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }

    @PostMapping("/admin/role/addtouser")
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserForm form) {
        userService.addRoleToUser(form.getUsername(), form.getRoleName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response)
            throws StreamWriteException, DatabindException, IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);
                String username = decodedJWT.getSubject();
                User user = userService.getUser(username);
                final String accessToken = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000)) // 10 minutes
                        .withIssuer(request.getRequestURI().toString())
                        .withClaim(
                                "roles",
                                user.getRoles().stream().map(Role::getName)
                                        .collect(Collectors.toList()))
                        .sign(algorithm);

                log.info("Token refresh was successful");

                Map<String, String> tokens = new HashMap<>();
                tokens.put("accessToken", accessToken);
                tokens.put("refreshToken", refreshToken);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);

            } catch (Exception e) {
                log.error("Error with token: {}", e.getMessage());
                response.setHeader("error", e.getMessage());
                response.setStatus(HttpStatus.FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("errorType", "refresh-token-error");
                error.put("errorMsg", e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }

        } else {
            throw new RuntimeException("Refresh token is missing");
        }

    }

}

@Data
class RoleToUserForm {
    private String username;
    private String roleName;
}