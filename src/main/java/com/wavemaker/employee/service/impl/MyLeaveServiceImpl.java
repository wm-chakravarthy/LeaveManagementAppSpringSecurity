package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.constants.LeaveRequestStatus;
import com.wavemaker.employee.exception.LeaveDaysExceededException;
import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.LeaveRequest;
import com.wavemaker.employee.pojo.dto.EmployeeLeaveRequestVO;
import com.wavemaker.employee.repository.MyLeaveRepository;
import com.wavemaker.employee.service.EmployeeLeaveSummaryService;
import com.wavemaker.employee.service.MyLeaveService;
import com.wavemaker.employee.util.DateUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class MyLeaveServiceImpl implements MyLeaveService {

    @Autowired
    @Qualifier("myLeaveRepositoryImplInHDS")
    private MyLeaveRepository myLeaveRepository;

    @Autowired
    private EmployeeLeaveSummaryService employeeLeaveSummaryService;

    @Override
    public LeaveRequest applyForLeave(LeaveRequest leaveRequest) throws ServerUnavailableException, LeaveDaysExceededException {
        int totalDays = DateUtil.calculateTotalDaysExcludingWeekendsAndHolidays(leaveRequest.getFromDate(), leaveRequest.getToDate());
        int leaveTypeId = leaveRequest.getLeaveTypeId();
        boolean isSuccess = employeeLeaveSummaryService.isLeaveTypeWithinRange(leaveRequest.getEmpId(), leaveTypeId, totalDays);
        if (!isSuccess) {
            throw new LeaveDaysExceededException("Total days exceed the maximum allowed for this leave type.", HttpServletResponse.SC_BAD_REQUEST);
        }
        leaveRequest.setTotalNoOfDays(totalDays);
        employeeLeaveSummaryService.updateEmployeeLeaveSummary(leaveRequest.getEmpId(), leaveTypeId, totalDays);
        return myLeaveRepository.applyForLeave(leaveRequest);
    }

    @Override
    public boolean cancelMyLeaveRequest(int leaveRequestId, int approvingEmpId) throws ServerUnavailableException {
        return myLeaveRepository.cancelMyLeaveRequest(leaveRequestId, approvingEmpId);
    }

    @Override
    public List<EmployeeLeaveRequestVO> getMyLeaveRequests(int empId, List<LeaveRequestStatus> statusList) throws ServerUnavailableException {
        return myLeaveRepository.getMyLeaveRequests(empId, statusList);
    }

    @Override
    public LeaveRequest getMyLeaveRequest(int leaveRequestId) throws ServerUnavailableException {
        return myLeaveRepository.getMyLeaveRequest(leaveRequestId);
    }

    @Override
    public boolean updateMyLeaveRequest(LeaveRequest leaveRequest) throws ServerUnavailableException {
        int totalDays = DateUtil.calculateTotalDaysExcludingWeekendsAndHolidays(leaveRequest.getFromDate(), leaveRequest.getToDate());
        int leaveTypeId = leaveRequest.getLeaveTypeId();
        boolean isSuccess = employeeLeaveSummaryService.isLeaveTypeWithinRange(leaveRequest.getEmpId(), leaveTypeId, totalDays);
        if (!isSuccess) {
            throw new LeaveDaysExceededException("Total days exceed the maximum allowed for this leave type.", HttpServletResponse.SC_BAD_REQUEST);
        }
        leaveRequest.setTotalNoOfDays(totalDays);
        employeeLeaveSummaryService.updateEmployeeLeaveSummary(leaveRequest.getEmpId(), leaveTypeId, totalDays);
        return myLeaveRepository.updateMyLeaveRequest(leaveRequest);
    }

    @Override
    public int getEmployeeIdByLeaveRequestId(int leaveRequestId) throws ServerUnavailableException {
        return myLeaveRepository.getEmployeeIdByLeaveRequestId(leaveRequestId);
    }

    @Override
    public List<Integer> getLeaveTypeIdAndTotalDaysByLeaveRequestId(int leaveRequestId) throws ServerUnavailableException {
        return myLeaveRepository.getLeaveTypeIdAndTotalDaysByLeaveRequestId(leaveRequestId);
    }


}
