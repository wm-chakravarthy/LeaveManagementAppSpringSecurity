package com.wavemaker.employee.controller;

import com.wavemaker.employee.constants.LeaveRequestStatus;
import com.wavemaker.employee.pojo.EmployeePassword;
import com.wavemaker.employee.pojo.LeaveRequest;
import com.wavemaker.employee.pojo.UserEntity;
import com.wavemaker.employee.pojo.dto.EmployeeLeaveRequestVO;
import com.wavemaker.employee.repository.EmployeePasswordRepository;
import com.wavemaker.employee.service.MyLeaveService;
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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/employee/leave")
public class MyLeavesController {

    private static final Logger logger = LoggerFactory.getLogger(MyLeavesController.class);

    @Autowired
    private MyLeaveService myLeaveService;

    @Autowired
    private EmployeePasswordRepository employeePasswordRepository;
    @GetMapping
    public List<EmployeeLeaveRequestVO> getMyLeaveRequests(
            @RequestParam(value = "status", required = false) String status,
            HttpServletRequest request, HttpServletResponse response) {

        List<EmployeeLeaveRequestVO> employeeLeaveRequestVOList = null;
        UserEntity userEntity = null;
        List<String> statusList = new ArrayList<>();
        List<LeaveRequestStatus> leaveRequestStatuses = new ArrayList<>();
        if (status != null && !status.isEmpty()) {
            statusList = Arrays.asList(status.split(","));
        } else {
            statusList.add("APPROVED");
            statusList.add("REJECTED");
            statusList.add("PENDING");
            statusList.add("CANCELLED");
        }
        for (String statusParam : statusList) {
            leaveRequestStatuses.add(LeaveRequestStatus.valueOf(statusParam));
        }
        logger.info("Fetching Leave details for user ID: {}", userEntity != null ? userEntity.getUserId() : "Unknown");
        userEntity = handleUserSessionAndReturnUserEntity(request, response, logger);
        employeeLeaveRequestVOList = myLeaveService.getMyLeaveRequests(userEntity.getEmpId(), leaveRequestStatuses);
        logger.info("Leave details fetched: {}", employeeLeaveRequestVOList);
        return employeeLeaveRequestVOList;
    }

    @PatchMapping
    public boolean cancelMyLeaveRequest(
            @RequestParam(value = "leaveRequestId", required = false) String leaveRequestId,
            HttpServletRequest request, HttpServletResponse response) {

        LeaveRequest leaveRequest = null;
        UserEntity userEntity = null;
        boolean isSuccess = false;
        userEntity = handleUserSessionAndReturnUserEntity(request, response, logger);
        if (leaveRequestId != null) {
            logger.info("Canceling Leave request for user ID: {}", userEntity != null ? userEntity.getUserId() : "Unknown");
            isSuccess = myLeaveService.cancelMyLeaveRequest(Integer.parseInt(leaveRequestId), userEntity.getEmpId());
            if (isSuccess)
                logger.info("Leave request cancelled for user ID: {}", userEntity != null ? userEntity.getUserId() : "Unknown");
        }
        return isSuccess;
    }

    @PostMapping
    public LeaveRequest applyForLeave(@RequestBody LeaveRequest leaveRequest,
                                      HttpServletRequest request, HttpServletResponse response) {

        UserEntity userEntity = null;
        userEntity = handleUserSessionAndReturnUserEntity(request, response, logger);
        logger.info("Applying for Leave for user ID: {}", userEntity != null ? userEntity.getUserId() : "Unknown");
        leaveRequest.setEmpId(userEntity.getEmpId());
        leaveRequest = myLeaveService.applyForLeave(leaveRequest);
        if (leaveRequest != null)
            logger.info("Leave applied for user ID: {}", userEntity != null ? userEntity.getUserId() : "Unknown");
        return leaveRequest;
    }

    @PutMapping
    public LeaveRequest updateMyLeaveRequest(@RequestBody LeaveRequest leaveRequest,
                                             HttpServletRequest request, HttpServletResponse response) {

        UserEntity userEntity = null;
        userEntity = handleUserSessionAndReturnUserEntity(request, response, logger);
        logger.info("Updating Leave for user ID: {}", userEntity != null ? userEntity.getUserId() : "Unknown");
        boolean isSuccess = myLeaveService.updateMyLeaveRequest(leaveRequest);
        if (isSuccess)
            logger.info("Leave updated for user ID: {}", userEntity != null ? userEntity.getUserId() : "Unknown");
        return leaveRequest;
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
