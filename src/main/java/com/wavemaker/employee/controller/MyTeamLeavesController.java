package com.wavemaker.employee.controller;

import com.wavemaker.employee.constants.LeaveRequestStatus;
import com.wavemaker.employee.pojo.EmployeePassword;
import com.wavemaker.employee.pojo.LeaveRequest;
import com.wavemaker.employee.pojo.UserEntity;
import com.wavemaker.employee.repository.EmployeePasswordRepository;
import com.wavemaker.employee.service.MyTeamLeaveService;
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
@RequestMapping("/employee/my-team-leave")
public class MyTeamLeavesController {

    private static final Logger logger = LoggerFactory.getLogger(MyLeavesController.class);

    @Autowired
    private MyTeamLeaveService myTeamLeaveService;

    @Autowired
    private EmployeePasswordRepository employeePasswordRepository;
    @GetMapping
    public List<LeaveRequest> getMyTeamLeaveRequests(
            @RequestParam(value = "status", required = false) String statusParam,
            HttpServletRequest request, HttpServletResponse response) {

        List<LeaveRequest> leaveRequestList = null;
        UserEntity userEntity = null;
        List<String> statusList = new ArrayList<>();
        if (statusParam != null && !statusParam.isEmpty()) {
            statusList = Arrays.asList(statusParam.split(","));
        } else {
            statusList.add("APPROVED");
            statusList.add("REJECTED");
            statusList.add("PENDING");
            statusList.add("CANCELLED");
        }
        logger.info("Fetching Leave details for user ID: {}", userEntity != null ? userEntity.getUserId() : "Unknown");
        userEntity = handleUserSessionAndReturnUserEntity(request, response, logger);
        leaveRequestList = myTeamLeaveService.getMyTeamLeaveRequests(userEntity.getEmpId(), statusList);
        logger.info("Leave details fetched: {}", leaveRequestList);
        return leaveRequestList;
    }

    @PostMapping
    public boolean approveOrRejectTeamLeaveRequest(
            @RequestParam(value = "leaveRequestId", required = false) String leaveRequestId,
            @RequestParam(value = "approveOrReject", required = false) String approveOrReject,
            HttpServletRequest request, HttpServletResponse response) {

        boolean isSuccess = false;
        UserEntity userEntity = null;
        userEntity = handleUserSessionAndReturnUserEntity(request, response, logger);
        if (leaveRequestId != null && approveOrReject != null) {
            logger.info("Approving or Rejecting Leave request for user ID: {}", userEntity != null ? userEntity.getUserId() : "Unknown");
            isSuccess = myTeamLeaveService.approveOrRejectTeamLeaveRequest(Integer.parseInt(leaveRequestId), userEntity.getEmpId(), LeaveRequestStatus.valueOf(approveOrReject));
            if (isSuccess)
                logger.info("Leave request approved or rejected for user ID: {}", userEntity != null ? userEntity.getUserId() : "Unknown");
        }
        return isSuccess;
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
