package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.constants.LeaveRequestStatus;
import com.wavemaker.employee.exception.LeaveDaysExceededException;
import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.LeaveRequest;
import com.wavemaker.employee.repository.MyTeamLeaveRepository;
import com.wavemaker.employee.service.EmployeeLeaveSummaryService;
import com.wavemaker.employee.service.MyLeaveService;
import com.wavemaker.employee.service.MyTeamLeaveService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class MyTeamLeaveServiceImpl implements MyTeamLeaveService {

    @Autowired
    @Qualifier("myTeamLeaveRepositoryHDS")
    private MyTeamLeaveRepository myTeamLeaveRepository;

    @Autowired
    private MyLeaveService myLeaveService;

    @Autowired
    private EmployeeLeaveSummaryService employeeLeaveSummaryService;

    @Override
    public List<LeaveRequest> getMyTeamLeaveRequests(int managerEmpId, List<String> statusList) throws ServerUnavailableException {
        return myTeamLeaveRepository.getMyTeamLeaveRequests(managerEmpId, statusList);
    }

    @Override
    public boolean approveOrRejectTeamLeaveRequest(int leaveRequestId, int approvingEmpId, LeaveRequestStatus approveOrRejectOrCancel) throws ServerUnavailableException, LeaveDaysExceededException {
        Integer totalDays = -1;
        Integer leaveTypeId = -1;
        int empId = myLeaveService.getEmployeeIdByLeaveRequestId(leaveRequestId);
        if (approveOrRejectOrCancel.equals(LeaveRequestStatus.APPROVED)) {
            LeaveRequest leaveRequest = myLeaveService.getMyLeaveRequest(leaveRequestId);

            leaveTypeId = leaveRequest.getLeaveTypeId();
            totalDays = leaveRequest.getTotalNoOfDays();
            boolean isSuccess = employeeLeaveSummaryService.isLeaveTypeWithinRange(empId, leaveTypeId, totalDays);
            if (!isSuccess) {
                throw new LeaveDaysExceededException("Total days exceed the maximum allowed for this leave type.", HttpServletResponse.SC_BAD_REQUEST);
            }
            employeeLeaveSummaryService.updateEmployeeLeaveSummary(empId, leaveTypeId, totalDays);
        }
        return myTeamLeaveRepository.approveOrRejectTeamLeaveRequest(leaveRequestId, approvingEmpId, approveOrRejectOrCancel);
    }
}
