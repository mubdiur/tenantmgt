package io.github.tenantmgt.security.auth.filter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String APPLICATION_JSON_VALUE = "application/json";
    private final AuthenticationManager authenticationManager;
    private final String jwtSecret;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, String jwtSecret) {
        this.authenticationManager = authenticationManager;
        this.jwtSecret = jwtSecret;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String username = "", password = "";
        try {
            // BufferedReader reader = request.getReader();
            // Gson gson = new Gson();
            // LoginRequest loginRequest = gson.fromJson(reader, LoginRequest.class);
            // username = loginRequest.getUsername();
            // password = loginRequest.getPassword();

            String requestString = request.getReader().lines().toArray().toString();
            log.info("Requesttttt: {}", requestString);
        } catch (IOException e) {
            log.error("Error reading json");
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        User user = (User) authResult.getPrincipal();

        if (jwtSecret == null) {
            throw new IOException("Secret key 'jwt.secret' not found");
        }
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret.getBytes());

        final String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000)) // 10 minutes
                .withIssuer(request.getRequestURI().toString())
                .withClaim(
                        "roles",
                        user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        final String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)) // 30 days
                .withIssuer(request.getRequestURI().toString())
                .sign(algorithm);

        log.info("Login was successful");

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setStatus(200);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        log.error("login error: {}", failed.getMessage());
        response.sendError(200, failed.getMessage());
    }

}
