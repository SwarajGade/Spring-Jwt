package com.example.Spring_jwt_demo.rest;

import com.example.Spring_jwt_demo.entity.Customer;
import com.example.Spring_jwt_demo.repo.CustomerRepo;
import com.example.Spring_jwt_demo.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for managing Customer login and registration.
 */
@RestController
@RequestMapping("/api")
public class CustomerRestController {

    @Autowired
    private CustomerRepo crepo;

    @Autowired
    private PasswordEncoder pwdEncoder;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService jwtService;

    /**
     * Endpoint to display a welcome message.
     *
     * @return a welcome string message
     */
    //http://localhost:8081/api/welcome
    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to java";
    }

    /**
     * Endpoint for authenticating customer login.
     *
     * @param customer the customer login details (username and password)
     * @return a JWT token if authentication is successful, or an error message if unsuccessful
     */
    //http://localhost:8081/api/login
    @PostMapping("/login")
    public ResponseEntity<String> loginCheck(@RequestBody Customer customer) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(customer.getUname(), customer.getPwd());

        try {
            Authentication authentication = authManager.authenticate(token);

            if (authentication.isAuthenticated()) {
                String jwtToken = jwtService.generateToken(customer.getUname());  // Generate JWT token
                return new ResponseEntity<>(jwtToken, HttpStatus.OK);
            }
        } catch (Exception e) {
            // Log authentication failure for debugging purposes
            // Example: logger.error("Authentication failed: ", e);
        }

        return new ResponseEntity<>("Invalid Credentials", HttpStatus.BAD_REQUEST);
    }

    /**
     * Endpoint for customer registration.
     *
     * @param customer the customer details for registration
     * @return a success message upon registration
     */
    //http://localhost:8081/api/register
    @PostMapping("/register")
    public ResponseEntity<String> registerCustomer(@RequestBody Customer customer) {

        // Encode the customer password before saving
        String encodedPwd = pwdEncoder.encode(customer.getPwd());
        customer.setPwd(encodedPwd);

        // Save the customer in the repository
        crepo.save(customer);

        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }
}
