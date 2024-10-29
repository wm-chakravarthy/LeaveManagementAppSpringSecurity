package com.wavemaker.employee.repository;

import com.wavemaker.employee.pojo.EmployeePassword;

//@Repository("employeePasswordRepository")
public interface EmployeePasswordRepository {
    EmployeePassword findByEmailId(String email);
}
