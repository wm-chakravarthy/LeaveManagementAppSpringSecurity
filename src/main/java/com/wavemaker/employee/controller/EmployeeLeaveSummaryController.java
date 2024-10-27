package com.wavemaker.employee.controller;

import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.EmployeeLeaveSummary;
import com.wavemaker.employee.pojo.UserEntity;
import com.wavemaker.employee.service.EmployeeLeaveSummaryService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/employee/leave/summary")
public class EmployeeLeaveSummaryController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeLeaveSummaryController.class);

    @Autowired
    private EmployeeLeaveSummaryService employeeLeaveSummaryService;


    @GetMapping
    public List<EmployeeLeaveSummary> getLoggedInEmployeeLeaveSummaries(HttpServletRequest request, HttpServletResponse response) {
        UserEntity userEntity = null;
        List<EmployeeLeaveSummary> employeeLeaveSummaryList = null;
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication authentication = context.getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                userEntity = (UserEntity) userDetails;
            }
            logger.info("Fetching leave summary for employee : {}", userEntity);
            employeeLeaveSummaryList = employeeLeaveSummaryService.getEmployeeLeaveSummariesById(3);
        } catch (ServerUnavailableException e) {
            logger.error("Error fetching Leave details for user ID: {}", userEntity != null ? userEntity.getUserId() : "Unknown", e);
        } catch (Exception e) {
            logger.error("Server error occurred while processing GET request", e);
        }
        return employeeLeaveSummaryList;
    }
}
