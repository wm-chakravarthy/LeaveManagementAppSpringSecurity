package com.wavemaker.employee.controller;

import com.wavemaker.employee.pojo.Employee;
import com.wavemaker.employee.pojo.UserEntity;
import com.wavemaker.employee.pojo.dto.EmployeeVO;
import com.wavemaker.employee.service.EmployeeService;
import com.wavemaker.employee.util.UserSessionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public EmployeeVO getEmployeeById(
            @RequestParam(value = "empId", required = false) String empId,
            HttpServletRequest request, HttpServletResponse response) {
        EmployeeVO employee = null;
        UserEntity userEntity = null;
        if (empId != null) {
            userEntity = UserSessionHandler.handleUserSessionAndReturnUserEntity(request, response, logger);
            logger.info("Fetching employee details for empId: {}", empId);
            employee = employeeService.getEmployeeById(Integer.parseInt(empId));
            logger.info("Employee details fetched: {}", employee);
        }
        return employee; // Returning EmployeeVO
    }

    @GetMapping("/list")
    public List<Employee> getEmployees(
            @RequestParam(value = "action", required = false) String action,
            HttpServletRequest httpServletRequest, HttpServletResponse response) {
        List<Employee> employeeList = null;
        if (action != null && action.equals("getManagers")) {
            logger.info("Fetching all managers");
            employeeList = employeeService.getAllManagers();
            logger.info("Managers fetched: {}", employeeList);
        }
        logger.info("Fetching all employees");
        employeeList = employeeService.getEmployees();
        logger.info("Employees fetched: {}", employeeList);
        return employeeList; // Returning List<Employee>
    }
}


