package io.github.tenantmgt.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import io.github.tenantmgt.security.auth.filter.CustomAuthenticationFilter;
import io.github.tenantmgt.security.auth.filter.CustomAuthorizationFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManagerBuilder authManagerBuilder;

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);

        // authenticationManagerBuilder.authenticationProvider(authProvider);

        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // http.authorizeRequests().anyRequest().permitAll();
        CustomAuthenticationFilter customAuthenticationFilter = 
                new CustomAuthenticationFilter(authManagerBuilder.getOrBuild(), jwtSecret);
        
        customAuthenticationFilter.setFilterProcessesUrl("/api/login/**");
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers("/api/login/**").permitAll();
        http.authorizeRequests().antMatchers("/api/register/**").permitAll();
        http.authorizeRequests().antMatchers("/api/tenant/**").hasAnyAuthority("ROLE_TENANT");
        http.authorizeRequests().antMatchers("/api/admin/**").hasAnyAuthority("ROLE_ADMIN");
        http.authorizeRequests().antMatchers("/api/owner/**").hasAnyAuthority("ROLE_OWNER");
        http.authorizeRequests().antMatchers("/api/manager/**").hasAnyAuthority("ROLE_MANAGER");
        http.authorizeRequests().anyRequest().authenticated();

        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(jwtSecret), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}