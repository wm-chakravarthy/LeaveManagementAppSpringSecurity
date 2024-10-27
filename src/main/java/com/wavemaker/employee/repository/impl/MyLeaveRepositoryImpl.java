package com.wavemaker.employee.repository.impl;

import com.wavemaker.employee.constants.LeaveRequestStatus;
import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.LeaveRequest;
import com.wavemaker.employee.pojo.dto.EmployeeLeaveRequestVO;
import com.wavemaker.employee.repository.MyLeaveRepository;
import com.wavemaker.employee.util.DBConnector;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

//@Repository("myLeaveRepositoryInDB")
public class MyLeaveRepositoryImpl implements MyLeaveRepository {

    private static final String INSERT_LEAVE_REQUEST_QUERY = "INSERT INTO LEAVE_REQUEST" +
            " (EMP_ID, LEAVE_TYPE_ID, LEAVE_REASON, FROM_DATE, TO_DATE, DATE_OF_APPLICATION, " +
            "LEAVE_STATUS, DATE_OF_ACTION, TOTAL_DAYS) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";

    private static final String UPDATE_LEAVE_REQUEST_SQL =
            "UPDATE leave_request SET EMP_ID = ?, LEAVE_TYPE_ID = ?, LEAVE_REASON = ?, " +
                    "FROM_DATE = ?, TO_DATE = ?, DATE_OF_APPLICATION = ?, LEAVE_STATUS = ?, " +
                    "DATE_OF_ACTION = ?, TOTAL_DAYS = ? WHERE LEAVE_REQUEST_ID = ?";

    private static final String SQL_SELECT_EMP_ID_BY_LEAVE_REQUEST_ID =
            "SELECT EMP_ID FROM leave_request WHERE LEAVE_REQUEST_ID = ?";

    private static final String GET_LEAVE_REQUESTS_BY_MULTIPLE_STATUSES =
            "SELECT LEAVE_REQUEST_ID, LEAVE_TYPE.LEAVE_TYPE, LEAVE_REASON, FROM_DATE, TO_DATE, DATE_OF_APPLICATION, LEAVE_STATUS, DATE_OF_ACTION, TOTAL_DAYS " +
                    "FROM leave_request " +
                    "JOIN leave_type ON leave_request.LEAVE_TYPE_ID = leave_type.LEAVE_TYPE_ID " +
                    "WHERE EMP_ID = ? AND LEAVE_STATUS IN (%s) " +
                    "ORDER BY CASE WHEN LEAVE_STATUS = 'PENDING' THEN 1 ELSE 2 END, DATE_OF_APPLICATION DESC";

    private static final String GET_LEAVE_TYPE_ID_AND_TOTAL_DAYS_QUERY =
            "SELECT LEAVE_TYPE_ID, TOTAL_DAYS FROM leave_request WHERE LEAVE_REQUEST_ID = ?";

    private Connection connection;

    public MyLeaveRepositoryImpl() {
        try {
            connection = DBConnector.getConnectionInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public LeaveRequest applyForLeave(LeaveRequest leaveRequest) throws ServerUnavailableException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_LEAVE_REQUEST_QUERY, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, leaveRequest.getEmpId());
            preparedStatement.setInt(2, leaveRequest.getLeaveTypeId());
            preparedStatement.setString(3, leaveRequest.getLeaveReason());
            preparedStatement.setDate(4, new java.sql.Date(leaveRequest.getFromDate().getTime()));
            preparedStatement.setDate(5, new java.sql.Date(leaveRequest.getToDate().getTime()));
            preparedStatement.setDate(6, new java.sql.Date(leaveRequest.getDateOfApplication().getTime()));
            preparedStatement.setString(7, leaveRequest.getLeaveStatus().name());
            preparedStatement.setDate(8, leaveRequest.getDateOfApproved() != null ? new java.sql.Date(leaveRequest.getDateOfApproved().getTime()) : null);
            preparedStatement.setInt(9, leaveRequest.getTotalNoOfDays());
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        leaveRequest.setLeaveRequestId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating leave request failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavailableException("Unexpected exception", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return leaveRequest;
    }

    @Override
    public boolean cancelMyLeaveRequest(int leaveRequestId, int empId) throws ServerUnavailableException {
        String query = "UPDATE LEAVE_REQUEST "
                + "SET LEAVE_STATUS = ?, DATE_OF_ACTION = ? "
                + "WHERE LEAVE_REQUEST_ID = ? AND EMP_ID = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, LeaveRequestStatus.CANCELLED.name()); // Set the leave status to CANCELLED
            preparedStatement.setDate(2, new java.sql.Date(System.currentTimeMillis())); // Set the current date as DATE_OF_ACTION
            preparedStatement.setInt(3, leaveRequestId); // Set the leave request ID
            preparedStatement.setInt(4, empId); // Set the employee ID (EMP_ID)

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new ServerUnavailableException("Unable to update leave request status.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public List<EmployeeLeaveRequestVO> getMyLeaveRequests(int empId, List<LeaveRequestStatus> statusList) throws ServerUnavailableException {
        List<String> statusListStr = statusList.stream().map(Enum::name).collect(Collectors.toList());
        List<EmployeeLeaveRequestVO> leaveRequests = new ArrayList<>();
        String placeholders = String.join(",", Collections.nCopies(statusList.size(), "?"));
        String query = String.format(GET_LEAVE_REQUESTS_BY_MULTIPLE_STATUSES, placeholders);

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, empId);
            for (int i = 0; i < statusList.size(); i++) {
                preparedStatement.setString(i + 2, statusListStr.get(i)); // Index starts from 2 as 1 is for empId
            }
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    EmployeeLeaveRequestVO leaveRequest = new EmployeeLeaveRequestVO();
                    leaveRequest.setLeaveRequestId(rs.getInt("LEAVE_REQUEST_ID"));
                    leaveRequest.setLeaveType(rs.getString("LEAVE_TYPE"));
                    leaveRequest.setLeaveReason(rs.getString("LEAVE_REASON"));
                    leaveRequest.setFromDate(rs.getDate("FROM_DATE"));
                    leaveRequest.setToDate(rs.getDate("TO_DATE"));
                    leaveRequest.setDateOfApplication(rs.getDate("DATE_OF_APPLICATION"));
                    leaveRequest.setLeaveRequestStatus(LeaveRequestStatus.valueOf(rs.getString("LEAVE_STATUS")));
                    leaveRequest.setDateOfApproved(rs.getDate("DATE_OF_ACTION"));
                    leaveRequest.setTotalDays(rs.getInt("TOTAL_DAYS"));
                    leaveRequests.add(leaveRequest);
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavailableException("Unable to retrieve leave requests.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return leaveRequests;
    }

    @Override
    public LeaveRequest getMyLeaveRequest(int leaveRequestId) throws ServerUnavailableException {
        return null;  //implemented this method in hibernate
    }

    @Override
    public boolean updateMyLeaveRequest(LeaveRequest leaveRequest) throws ServerUnavailableException {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_LEAVE_REQUEST_SQL);
            preparedStatement.setInt(1, leaveRequest.getEmpId());
            preparedStatement.setInt(2, leaveRequest.getLeaveTypeId());
            preparedStatement.setString(3, leaveRequest.getLeaveReason());
            preparedStatement.setDate(4, new Date(leaveRequest.getFromDate().getTime()));
            preparedStatement.setDate(5, new Date(leaveRequest.getToDate().getTime()));
            preparedStatement.setDate(6, new Date(leaveRequest.getDateOfApplication().getTime()));
            preparedStatement.setDate(8, new Date(leaveRequest.getDateOfApproved().getTime()));
            preparedStatement.setInt(9, leaveRequest.getLeaveRequestId());
            preparedStatement.setInt(10, leaveRequest.getTotalNoOfDays());
            int rowsUpdated = preparedStatement.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            throw new ServerUnavailableException("Unable to Update leave requests.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public int getEmployeeIdByLeaveRequestId(int leaveRequestId) throws ServerUnavailableException {
        int empId = -1;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(SQL_SELECT_EMP_ID_BY_LEAVE_REQUEST_ID);
            preparedStatement.setInt(1, leaveRequestId);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                empId = resultSet.getInt("EMP_ID");
            }
        } catch (SQLException e) {
            throw new ServerUnavailableException("Error fetching employee ID by leave request ID", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return empId;
    }

    @Override
    public List<Integer> getLeaveTypeIdAndTotalDaysByLeaveRequestId(int leaveRequestId) throws ServerUnavailableException {
        List<Integer> result = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(GET_LEAVE_TYPE_ID_AND_TOTAL_DAYS_QUERY);
            preparedStatement.setInt(1, leaveRequestId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int leaveTypeId = resultSet.getInt("LEAVE_TYPE_ID");
                    int totalDays = resultSet.getInt("TOTAL_DAYS");
                    result.add(leaveTypeId);
                    result.add(totalDays);
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavailableException("Error fetching leave type ID and total days", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return result;
    }

}
