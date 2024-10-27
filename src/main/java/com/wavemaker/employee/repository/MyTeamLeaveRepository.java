package com.wavemaker.employee.repository;

import com.wavemaker.employee.constants.LeaveRequestStatus;
import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.LeaveRequest;

import java.util.List;

public interface MyTeamLeaveRepository {
    public List<LeaveRequest> getMyTeamLeaveRequests(int managerEmpId, List<String> statusList) throws ServerUnavailableException;

    public boolean approveOrRejectTeamLeaveRequest(int leaveRequestId, int approvingEmpId, LeaveRequestStatus approveOrRejectOrCancel) throws ServerUnavailableException;

}
