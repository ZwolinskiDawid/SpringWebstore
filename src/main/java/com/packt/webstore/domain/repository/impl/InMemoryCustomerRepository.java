package com.packt.webstore.domain.repository.impl;

import com.packt.webstore.domain.Customer;
import com.packt.webstore.domain.repository.CustomerRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryCustomerRepository implements CustomerRepository {

    private List<Customer> listOfCustomers = new ArrayList<>();
    
    public InMemoryCustomerRepository() {
        
        Customer c1 = new Customer("C1234", "John");
        Customer c2 = new Customer("C2355", "Mat");
        Customer c3 = new Customer("C5346", "Jim");
        Customer c4 = new Customer("C6324", "Tomas");
        
        this.listOfCustomers.add(c1);
        this.listOfCustomers.add(c2);
        this.listOfCustomers.add(c3);
        this.listOfCustomers.add(c4);
        
    }
    
    @Override
    public List<Customer> getAllCustomers() {
        return this.listOfCustomers;
    }
    
}
