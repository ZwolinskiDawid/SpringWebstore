package com.packt.webstore.domain;

public class Customer {
    
    private String customerId;
    private String name;

    public Customer(String id, String name) {
        this.customerId = id;
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getCustomerId() {
        return this.customerId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(this.getClass() != obj.getClass())
            return false;
        
        Customer other = (Customer) obj;
        if(this.customerId == null && other.getCustomerId() != null)
            return false;
        if(!this.customerId.equals(other.getCustomerId()))
            return false;
        
        return true;
    }
    
    
}
