package com.example.Spring_jwt_demo.config;

import com.example.Spring_jwt_demo.filter.AppFilter;
import com.example.Spring_jwt_demo.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configures security settings for the application, including custom authentication provider,
 * JWT filter, and stateless session management.
 */
@Configuration
@EnableWebSecurity
public class AppSecurityConfig {

    @Autowired
    private AppFilter filter; // JWT filter for request validation

    @Autowired
    private MyUserDetailsService userDtlsSvc; // Custom UserDetailsService for authentication

    /**
     * Configures a BCrypt password encoder for hashing passwords.
     *
     * @return PasswordEncoder instance using BCrypt.
     */
    @Bean
    public PasswordEncoder pwdEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures an authentication provider with the custom UserDetailsService and PasswordEncoder.
     *
     * @return AuthenticationProvider configured with user details service and password encoder.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDtlsSvc);
        authenticationProvider.setPasswordEncoder(pwdEncoder());
        return authenticationProvider;
    }

    /**
     * Provides the AuthenticationManager bean for managing authentication requests.
     *
     * @param config AuthenticationConfiguration instance.
     * @return Configured AuthenticationManager.
     * @throws Exception if configuration fails.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configures the HTTP security settings for the application, including CSRF protection,
     * public and protected endpoints, and the JWT authentication filter.
     *
     * @param http HttpSecurity instance.
     * @return SecurityFilterChain for handling request security.
     * @throws Exception if configuration fails.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                //.csrf(csrf -> csrf.disable())     /or/
                .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF since JWT is stateless  or  //.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/login", "/api/register","/api/welcome").permitAll()  // Allow public access to login and register endpoints
                        .anyRequest().authenticated()  // Require authentication for all other endpoints
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Stateless session management for JWT
                )
                .authenticationProvider(authenticationProvider())  // Register custom authentication provider
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)  // Add JWT filter before UsernamePasswordAuthenticationFilter
                .build();
    }

}
