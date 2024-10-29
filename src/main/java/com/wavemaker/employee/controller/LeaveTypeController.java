package com.wavemaker.employee.controller;

import com.wavemaker.employee.pojo.EmployeePassword;
import com.wavemaker.employee.pojo.LeaveType;
import com.wavemaker.employee.pojo.UserEntity;
import com.wavemaker.employee.repository.EmployeePasswordRepository;
import com.wavemaker.employee.service.LeaveTypeService;
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

@RestController
@RequestMapping("/leavetypes")
public class LeaveTypeController {

    private static final Logger logger = LoggerFactory.getLogger(LeaveTypeController.class);

    @Autowired
    private LeaveTypeService leaveTypeService;

    @Autowired
    private EmployeePasswordRepository employeePasswordRepository;


    @GetMapping("/list")
    public List<LeaveType> getLeaveTypesByGender(
            @RequestParam(value = "gender", required = false) String gender,
            HttpServletRequest request, HttpServletResponse response) {

        UserEntity userEntity = null;
        List<LeaveType> leaveTypeList = null;
        logger.info("Fetching all leave types");
        userEntity = handleUserSessionAndReturnUserEntity(request, response, logger);
        logger.info("Fetching leave types for user ID: {}", userEntity != null ? userEntity.getUserId() : "Unknown");
        leaveTypeList = leaveTypeService.getAllLeaveTypes(gender);
        logger.info("Leave types fetched: {}", leaveTypeList);
        return leaveTypeList;
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
