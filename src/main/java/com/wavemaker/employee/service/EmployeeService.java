package com.wavemaker.employee.service;

import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.Employee;
import com.wavemaker.employee.pojo.dto.EmployeeVO;

import java.util.List;

public interface EmployeeService {
    public EmployeeVO getEmployeeById(int empId) throws ServerUnavailableException;

    public Employee addEmployee(Employee employee) throws ServerUnavailableException;

    public Employee updateEmployee(Employee employee) throws ServerUnavailableException;

    public Employee deleteEmployee(int empId) throws ServerUnavailableException;

    public List<Employee> getEmployees() throws ServerUnavailableException;

    public List<Employee> getAllManagers() throws ServerUnavailableException;
}
