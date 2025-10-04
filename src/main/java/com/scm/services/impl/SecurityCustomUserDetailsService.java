package com.scm.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.scm.helpers.ResourceNotFoundException;
import com.scm.repositories.UserRepo;

// used to fetch users from database for security/login purpose 
@Service
public class SecurityCustomUserDetailsService implements UserDetailsService{

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // we're considering email as username
    return userRepo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + username));
    } 
}