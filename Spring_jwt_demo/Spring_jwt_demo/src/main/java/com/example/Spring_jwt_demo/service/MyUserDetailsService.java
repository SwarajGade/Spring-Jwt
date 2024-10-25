package com.example.Spring_jwt_demo.service;

import java.util.Collections;

import com.example.Spring_jwt_demo.entity.Customer;
import com.example.Spring_jwt_demo.repo.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User; // Import the correct User class
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepo crepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find the customer by username
        Customer customer = crepo.findByUname(username);

        // Check if customer exists
        if (customer == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Create and return a UserDetails object
        return new User(customer.getUname(), customer.getPwd(), Collections.emptyList());
    }
}
