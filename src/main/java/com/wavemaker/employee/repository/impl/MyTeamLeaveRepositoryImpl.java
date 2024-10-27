package com.wavemaker.employee.repository.impl;

import com.wavemaker.employee.constants.LeaveRequestStatus;
import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.LeaveRequest;
import com.wavemaker.employee.pojo.dto.LeaveRequestVO;
import com.wavemaker.employee.repository.MyTeamLeaveRepository;
import com.wavemaker.employee.util.DBConnector;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository("myTeamLeaveRepositoryInDB")
public class MyTeamLeaveRepositoryImpl implements MyTeamLeaveRepository {

    private static final String SQL_UPDATE_LEAVE_REQUEST_APPROVE_REJECT =
            "UPDATE LEAVE_REQUEST lr "
                    + "JOIN EMPLOYEE e ON lr.EMP_ID = e.EMP_ID "
                    + "SET lr.LEAVE_STATUS = ?, lr.DATE_OF_ACTION = ? "
                    + "WHERE lr.LEAVE_REQUEST_ID = ? AND e.MANAGER_ID = ?";

    private static final String GET_TEAM_LEAVE_REQUESTS =
            "SELECT lr.LEAVE_REQUEST_ID, lr.EMP_ID, e.NAME, lr.LEAVE_TYPE_ID, lr.LEAVE_REASON, " +
                    "lr.FROM_DATE, lr.TO_DATE, lr.DATE_OF_APPLICATION, lr.LEAVE_STATUS, lr.DATE_OF_ACTION, lr.TOTAL_DAYS " +
                    "FROM LEAVE_REQUEST lr " +
                    "JOIN EMPLOYEE e ON lr.EMP_ID = e.EMP_ID " +
                    "JOIN LEAVE_TYPE lt ON lr.LEAVE_TYPE_ID = lt.LEAVE_TYPE_ID " +
                    "WHERE e.MANAGER_ID = ?";

    private static final String ORDER_BY_QUERY =
            " ORDER BY CASE WHEN lr.LEAVE_STATUS = 'PENDING' THEN 1 ELSE 2 END, lr.DATE_OF_APPLICATION DESC";
    private static final Logger logger = LoggerFactory.getLogger(MyTeamLeaveRepositoryImpl.class);

    private Connection connection;

    public MyTeamLeaveRepositoryImpl() {
        try {
            connection = DBConnector.getConnectionInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<LeaveRequest> getMyTeamLeaveRequests(int empId, List<String> statusList) throws ServerUnavailableException {
        List<LeaveRequestVO> leaveRequestVOList = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder(GET_TEAM_LEAVE_REQUESTS);

        if (statusList != null && !statusList.isEmpty()) {
            String placeholders = String.join(",", Collections.nCopies(statusList.size(), "?"));
            queryBuilder.append(" AND lr.LEAVE_STATUS IN (").append(placeholders).append(")");
        }

        queryBuilder.append(ORDER_BY_QUERY);

        String query = queryBuilder.toString();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, empId);

            if (statusList != null && !statusList.isEmpty()) {
                for (int i = 0; i < statusList.size(); i++) {
                    preparedStatement.setString(i + 2, statusList.get(i).toUpperCase());
                }
            }

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    LeaveRequestVO leaveRequestVO = new LeaveRequestVO();
                    leaveRequestVO.setLeaveRequestId(rs.getInt("LEAVE_REQUEST_ID"));
                    leaveRequestVO.setEmpId(rs.getInt("EMP_ID"));
                    leaveRequestVO.setEmpName(rs.getString("NAME"));
                    leaveRequestVO.setLeaveTypeId(rs.getInt("LEAVE_TYPE_ID"));
                    leaveRequestVO.setLeaveReason(rs.getString("LEAVE_REASON"));
                    leaveRequestVO.setFromDate(rs.getDate("FROM_DATE"));
                    leaveRequestVO.setToDate(rs.getDate("TO_DATE"));
                    leaveRequestVO.setDateOfApplication(rs.getDate("DATE_OF_APPLICATION"));
                    leaveRequestVO.setLeaveStatus(LeaveRequestStatus.valueOf(rs.getString("LEAVE_STATUS")));
                    leaveRequestVO.setDateOfApproved(rs.getDate("DATE_OF_ACTION"));
                    leaveRequestVO.setTotalNoOfDays(rs.getInt("TOTAL_DAYS"));
                    leaveRequestVOList.add(leaveRequestVO);
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavailableException("Unable to retrieve leave requests for team members.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return null; //this should return LEAVE_REQUESTS_VO
    }

    @Override
    public boolean approveOrRejectTeamLeaveRequest(int leaveRequestId, int approvingEmpId, LeaveRequestStatus approveOrReject) throws ServerUnavailableException {
        if (approveOrReject != LeaveRequestStatus.APPROVED && approveOrReject != LeaveRequestStatus.REJECTED) {
            throw new IllegalArgumentException("Invalid status. Only APPROVED and REJECTED are allowed.");
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE_LEAVE_REQUEST_APPROVE_REJECT)) {
            preparedStatement.setString(1, approveOrReject.name());
            preparedStatement.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            preparedStatement.setInt(3, leaveRequestId);
            preparedStatement.setInt(4, approvingEmpId);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new ServerUnavailableException("Unable to update leave request status.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}
