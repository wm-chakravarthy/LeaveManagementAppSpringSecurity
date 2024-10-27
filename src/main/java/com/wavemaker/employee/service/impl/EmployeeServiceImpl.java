package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.Employee;
import com.wavemaker.employee.pojo.dto.EmployeeVO;
import com.wavemaker.employee.repository.EmployeeRepository;
import com.wavemaker.employee.service.EmployeeService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    @Qualifier("EmployeeRepositoryImplInHDS")
    private EmployeeRepository employeeRepository;

    @Override
    public EmployeeVO getEmployeeById(int empId) throws ServerUnavailableException {
        return employeeRepository.getEmployeeById(empId);
    }

    @Override
    public Employee addEmployee(Employee employee) throws ServerUnavailableException {
        return employeeRepository.addEmployee(employee);
    }

    @Override
    public Employee updateEmployee(Employee employee) throws ServerUnavailableException {
        return employeeRepository.updateEmployee(employee);
    }

    @Override
    public Employee deleteEmployee(int empId) throws ServerUnavailableException {
        return employeeRepository.deleteEmployee(empId);
    }

    @Override
    public List<Employee> getEmployees() throws ServerUnavailableException {
        return employeeRepository.getEmployees();
    }

    @Override
    public List<Employee> getAllManagers() throws ServerUnavailableException {
        return employeeRepository.getAllManagers();
    }
}
