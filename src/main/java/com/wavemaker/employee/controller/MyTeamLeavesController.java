package com.wavemaker.employee.controller;

import com.wavemaker.employee.constants.LeaveRequestStatus;
import com.wavemaker.employee.pojo.LeaveRequest;
import com.wavemaker.employee.pojo.UserEntity;
import com.wavemaker.employee.service.MyTeamLeaveService;
import com.wavemaker.employee.util.UserSessionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
        userEntity = UserSessionHandler.handleUserSessionAndReturnUserEntity(request, response, logger);
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
        userEntity = UserSessionHandler.handleUserSessionAndReturnUserEntity(request, response, logger);
        if (leaveRequestId != null && approveOrReject != null) {
            logger.info("Approving or Rejecting Leave request for user ID: {}", userEntity != null ? userEntity.getUserId() : "Unknown");
            isSuccess = myTeamLeaveService.approveOrRejectTeamLeaveRequest(Integer.parseInt(leaveRequestId), userEntity.getEmpId(), LeaveRequestStatus.valueOf(approveOrReject));
            if (isSuccess)
                logger.info("Leave request approved or rejected for user ID: {}", userEntity != null ? userEntity.getUserId() : "Unknown");
        }
        return isSuccess;
    }
}
