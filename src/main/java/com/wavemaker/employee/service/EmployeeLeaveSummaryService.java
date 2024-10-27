package com.wavemaker.employee.service;

import com.wavemaker.employee.exception.LeaveDaysExceededException;
import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.Employee;
import com.wavemaker.employee.pojo.EmployeeLeaveSummary;

import java.util.List;
import java.util.Map;

public interface EmployeeLeaveSummaryService {
    public boolean updateEmployeeLeaveSummary(int empId, int leaveTypeId, int totalDays) throws ServerUnavailableException;

    public List<EmployeeLeaveSummary> getEmployeeLeaveSummariesById(int empId) throws ServerUnavailableException;

    public Map<Employee, List<EmployeeLeaveSummary>> getMyTeamEmployeeLeaveSummaries(int empId) throws ServerUnavailableException;

    public boolean isLeaveTypeWithinRange(int empId, int leaveTypeId, int totalDays) throws LeaveDaysExceededException, ServerUnavailableException;

}
