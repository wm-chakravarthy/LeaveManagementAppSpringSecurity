package com.wavemaker.employee.util;

import com.google.gson.Gson;
import com.wavemaker.employee.exception.ErrorResponse;
import com.wavemaker.employee.pojo.EmployeePassword;
import com.wavemaker.employee.pojo.UserEntity;
import com.wavemaker.employee.repository.EmployeePasswordRepository;
import com.wavemaker.employee.repository.impl.hibernatedatasource.EmployeePasswordRepositoryImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class UserSessionHandler {

    @Autowired
    private EmployeePasswordRepository employeePasswordRepository;

    @Autowired
    public UserSessionHandler(EmployeePasswordRepository employeePasswordRepository) {
        UserSessionHandler.employeePasswordRepository = employeePasswordRepository;
    }

    private   UserEntity handleUserSessionAndReturnUserEntity(HttpServletRequest request, HttpServletResponse response, Logger logger) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserEntity userEntity = new UserEntity();
            EmployeePassword employeePassword = employeePasswordRepository.findByEmailId(userDetails.getUsername());
            if (employeePassword != null) {
               userEntity.setEmail(employeePassword.getEmail());
               userEntity.setEmpId(employeePassword.getEmpId());
               userEntity.setPassword(employeePassword.getPassword());
               userEntity.setUserId(employeePassword.getEmployeePasswordId());
            }
            return userEntity;
        }
        return null;
    }
}
