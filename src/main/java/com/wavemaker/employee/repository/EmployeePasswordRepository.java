package com.wavemaker.employee.repository;

import com.wavemaker.employee.pojo.EmployeePassword;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeePasswordRepository {
    EmployeePassword findByEmailId(String email);
}
