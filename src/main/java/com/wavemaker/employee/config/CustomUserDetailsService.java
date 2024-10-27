package com.wavemaker.employee.config;


import com.wavemaker.employee.pojo.EmployeePassword;
import com.wavemaker.employee.repository.EmployeePasswordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private EmployeePasswordRepository employeePasswordRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        EmployeePassword employeePassword = employeePasswordRepository.findByEmailId(email);
        if (employeePassword == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        return new User(employeePassword.getEmail(), employeePassword.getPassword(), Collections.emptyList());
    }
}
