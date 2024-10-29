package com.wavemaker.employee.controller;

import com.wavemaker.employee.pojo.Employee;
import com.wavemaker.employee.pojo.EmployeeLeaveSummary;
import com.wavemaker.employee.pojo.EmployeePassword;
import com.wavemaker.employee.pojo.UserEntity;
import com.wavemaker.employee.repository.EmployeePasswordRepository;
import com.wavemaker.employee.service.EmployeeLeaveSummaryService;
import com.wavemaker.employee.util.UserSessionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employee/team/leave/summary")
public class TeamLeaveSummaryController {

    private static final Logger logger = LoggerFactory.getLogger(TeamLeaveSummaryController.class);

    @Autowired
    private EmployeeLeaveSummaryService employeeLeaveSummaryService;

    @Autowired
    private EmployeePasswordRepository employeePasswordRepository;

    @GetMapping
    public List<EmployeeLeaveSummary> getMyTeamLeaveSummariesById(
            @RequestParam(value = "empId", required = false) String empId,
            HttpServletRequest request, HttpServletResponse response) {

        List<EmployeeLeaveSummary> employeeLeaveSummaryList = null;
        UserEntity userEntity = null;

        userEntity = handleUserSessionAndReturnUserEntity(request, response, logger);
        logger.info("Fetching leave summary for employee ID: {}", empId);
        employeeLeaveSummaryList = employeeLeaveSummaryService.getEmployeeLeaveSummariesById(Integer.parseInt(empId));
        logger.info("Leave summary fetched: {}", employeeLeaveSummaryList);
        return employeeLeaveSummaryList;
    }

    @GetMapping("/list")
    public Map<Employee, List<EmployeeLeaveSummary>> getMyTeamEmployeeLeaveSummaries(HttpServletRequest request, HttpServletResponse response) {
        Map<Employee, List<EmployeeLeaveSummary>> leaveSummaryMap = null;
        UserEntity userEntity = null;

        userEntity = handleUserSessionAndReturnUserEntity(request, response, logger);
        logger.info("Fetching leave summary for employee : {}", userEntity);
        leaveSummaryMap = employeeLeaveSummaryService.getMyTeamEmployeeLeaveSummaries(userEntity.getEmpId());
        logger.info("Leave summary fetched: {}", leaveSummaryMap);
        return leaveSummaryMap;
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
