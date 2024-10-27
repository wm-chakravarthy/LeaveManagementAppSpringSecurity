package com.wavemaker.employee.service;

import com.wavemaker.employee.constants.LeaveRequestStatus;
import com.wavemaker.employee.exception.LeaveDaysExceededException;
import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.LeaveRequest;
import com.wavemaker.employee.pojo.dto.EmployeeLeaveRequestVO;

import java.util.List;

public interface MyLeaveService {

    public LeaveRequest applyForLeave(LeaveRequest leaveRequest) throws ServerUnavailableException, LeaveDaysExceededException;

    public boolean cancelMyLeaveRequest(int leaveRequestId, int approvingEmpId) throws ServerUnavailableException;

    public List<EmployeeLeaveRequestVO> getMyLeaveRequests(int empId, List<LeaveRequestStatus> leaveRequestStatus) throws ServerUnavailableException;

    public LeaveRequest getMyLeaveRequest(int leaveRequestId) throws ServerUnavailableException;

    public boolean updateMyLeaveRequest(LeaveRequest leaveRequest) throws ServerUnavailableException;

    public int getEmployeeIdByLeaveRequestId(int leaveRequestId) throws ServerUnavailableException;

    public List<Integer> getLeaveTypeIdAndTotalDaysByLeaveRequestId(int leaveRequestId) throws ServerUnavailableException;
}
