package com.example.Spring_jwt_demo.repo;

import com.example.Spring_jwt_demo.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Customer entity operations.
 * Extends CrudRepository to provide standard CRUD operations on Customer entity.
 */
@Repository
public interface CustomerRepo extends JpaRepository<Customer, Long> {

    /**
     * Custom query method to find a Customer by their username.
     *
     * @param uname the username of the customer
     * @return the Customer entity if found, otherwise null
     */
    Customer findByUname(String uname);
}
