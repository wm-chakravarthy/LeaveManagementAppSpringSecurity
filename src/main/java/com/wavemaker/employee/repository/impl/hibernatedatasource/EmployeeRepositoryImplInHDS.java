package com.wavemaker.employee.repository.impl.hibernatedatasource;

import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.Employee;
import com.wavemaker.employee.pojo.dto.EmployeeVO;
import com.wavemaker.employee.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("EmployeeRepositoryImplInHDS")
public class EmployeeRepositoryImplInHDS implements EmployeeRepository {

    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Override
    public EmployeeVO getEmployeeById(int empId) throws ServerUnavailableException {
        Employee employee = hibernateTemplate.get(Employee.class, empId);
        if (employee != null) {
            return setEmployeeFieldsToEmployeeVO(employee);
        }
        return null;
    }

    @Override
    public Employee addEmployee(Employee employee) throws ServerUnavailableException {
        hibernateTemplate.save(employee);
        return employee;
    }

    @Override
    public Employee updateEmployee(Employee employee) throws ServerUnavailableException {
        hibernateTemplate.update(employee);
        return employee;
    }

    @Override
    public Employee deleteEmployee(int empId) throws ServerUnavailableException {
        Employee employee = hibernateTemplate.get(Employee.class, empId);
        if (employee != null) {
            hibernateTemplate.delete(employee);
        }
        return employee;
    }

    @Override
    public List<Employee> getEmployees() throws ServerUnavailableException {
        return hibernateTemplate.loadAll(Employee.class);
    }

    @Override
    public List<Employee> getAllManagers() throws ServerUnavailableException {
        return null;
    }

    private EmployeeVO setEmployeeFieldsToEmployeeVO(Employee employee) {
        EmployeeVO employeeVO = new EmployeeVO();
        employeeVO.setEmpId(employee.getEmpId());
        employeeVO.setManagerId(employee.getManagerId());
        employeeVO.setEmpName(employee.getEmpName());
        employeeVO.setEmpDateOfBirth(employee.getEmpDateOfBirth());
        employeeVO.setPhoneNumber(employee.getPhoneNumber());
        employeeVO.setEmail(employee.getEmail());
        employeeVO.setGender(employee.getGender());
        employeeVO.setRole(employee.getRole());
        employeeVO.setManagerName(employee.getEmployeeByManagerId().getEmpName());
        employeeVO.setManagerPhoneNumber(String.valueOf(employee.getEmployeeByManagerId().getPhoneNumber()));
        employeeVO.setManagerEmailId(employee.getEmployeeByManagerId().getEmail());
        return employeeVO;
    }
}
