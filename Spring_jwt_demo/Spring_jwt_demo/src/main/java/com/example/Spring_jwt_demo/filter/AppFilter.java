package com.example.Spring_jwt_demo.filter;

import java.io.IOException;

import com.example.Spring_jwt_demo.service.JwtService;
import com.example.Spring_jwt_demo.service.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter to intercept and validate JWT tokens on each request.
 * Ensures the user is authenticated by setting the security context.
 */
@Component
public class AppFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;  // Service for JWT token handling

    @Autowired
    private MyUserDetailsService userDetailsServiceImpl;  // Service to load user details

    /**
     * Filters each request to check for a valid JWT token and set up authentication if valid.
     *
     * @param request the HTTP request object
     * @param response the HTTP response object
     * @param filterChain the chain of filters to be executed
     * @throws IOException if an I/O error occurs during filtering
     * @throws ServletException if a servlet error occurs during filtering
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        // Extract the Authorization header from the request
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Check if the header contains a Bearer token and extract it
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtService.extractUsername(token);  // Extract username from JWT token
        }

        // Validate the token and set the authentication context if it's valid
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);  // Load user details by username
            if (jwtService.validateToken(token, userDetails)) {  // Validate the JWT token
                // Create an authentication token and set it in the security context
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Continue with the next filter in the chain
        filterChain.doFilter(request, response);
    }
}
