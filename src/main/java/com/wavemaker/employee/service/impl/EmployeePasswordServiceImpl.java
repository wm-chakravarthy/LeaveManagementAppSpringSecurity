//package com.wavemaker.employee.service.impl;
//
//import com.wavemaker.employee.pojo.EmployeePassword;
//import com.wavemaker.employee.repository.EmployeePasswordRepository;
//import com.wavemaker.employee.service.EmployeePasswordService;
//import jakarta.transaction.Transactional;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Service;
//
//@Service
//@Transactional
//public class EmployeePasswordServiceImpl implements EmployeePasswordService {
//
//    @Autowired
//    @Qualifier("employeePasswordRepository")
//    private EmployeePasswordRepository employeePasswordRepository;
//
//    @Override
//    public EmployeePassword findByEmailId(String email) {
//        return employeePasswordRepository.findByEmailId(email);
//    }
//}
