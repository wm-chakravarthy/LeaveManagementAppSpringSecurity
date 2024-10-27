package com.wavemaker.employee.repository.impl.hibernatedatasource;

import com.wavemaker.employee.constants.LeaveRequestStatus;
import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.LeaveRequest;
import com.wavemaker.employee.pojo.dto.EmployeeLeaveRequestVO;
import com.wavemaker.employee.repository.MyLeaveRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.HibernateException;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository("myLeaveRepositoryImplInHDS")
public class MyLeaveRepositoryImplInHDS implements MyLeaveRepository {

    private static final String CANCEL_LEAVE_REQUEST_QUERY = "UPDATE LeaveRequest lr "
            + "SET lr.leaveRequestStatus = :status, lr.dateOfApproved = :date "
            + "WHERE lr.leaveRequestId = :requestId AND lr.empId = :empId";
    private static final String GET_ALL_LEAVE_REQUESTS_QUERY = "SELECT lr.leaveRequestId, lt.leaveType, lr.leaveReason, lr.fromDate, lr.toDate, " +
            "lr.dateOfApplication, lr.leaveRequestStatus, lr.dateOfApproved, lr.totalNoOfDays " +
            "FROM LeaveRequest lr " +
            "JOIN lr.leaveType lt " +
            "WHERE lr.empId = :empId AND lr.leaveRequestStatus IN (:statusList) " +
            "ORDER BY CASE WHEN lr.leaveRequestStatus = :pending THEN 1 ELSE 2 END, lr.dateOfApplication DESC";
    private static final String GET_EMPLOYEE_ID_BY_LEAVE_REQUEST_ID_QUERY = "SELECT lr.empId FROM LeaveRequest lr WHERE lr.leaveRequestId = :leaveRequestId";
    private static final String GET_LEAVE_TYPE_ID_TOTAL_DAYS_BY_LEAVE_REQUEST_ID_QUERY = "SELECT lt.leaveTypeId, lt.totalNoOfDays FROM LeaveRequest lr WHERE lr.leaveRequestId = :leaveRequestId";
    @Autowired
    private HibernateTemplate hibernateTemplate;

    private static EmployeeLeaveRequestVO getEmployeeLeaveRequestVO(Object[] row) {
        EmployeeLeaveRequestVO leaveRequest = new EmployeeLeaveRequestVO();
        leaveRequest.setLeaveRequestId((Integer) row[0]);
        leaveRequest.setLeaveType((String) row[1]);
        leaveRequest.setLeaveReason((String) row[2]);
        leaveRequest.setFromDate(new Date(((Timestamp) row[3]).getTime()));
        leaveRequest.setToDate(new Date(((Timestamp) row[4]).getTime()));
        leaveRequest.setDateOfApplication(new Date(((Timestamp) row[5]).getTime()));
        leaveRequest.setLeaveRequestStatus((LeaveRequestStatus) row[6]); // Directly cast to enum
        if (row[7] != null) leaveRequest.setDateOfApproved(new Date(((Timestamp) row[7]).getTime()));
        leaveRequest.setTotalDays((Integer) row[8]);
        return leaveRequest;
    }

    @Override
    public LeaveRequest applyForLeave(LeaveRequest leaveRequest) throws ServerUnavailableException {
        hibernateTemplate.save(leaveRequest);
        hibernateTemplate.flush();
        return leaveRequest;
    }

    @Override
    public boolean cancelMyLeaveRequest(int leaveRequestId, int approvingEmpId) throws ServerUnavailableException {
        try {
            Integer updatedRows = hibernateTemplate.execute(session -> {
                Query query = session.createQuery(CANCEL_LEAVE_REQUEST_QUERY);
                query.setParameter("status", LeaveRequestStatus.CANCELLED);
                query.setParameter("date", new java.sql.Date(System.currentTimeMillis()));
                query.setParameter("requestId", leaveRequestId);
                query.setParameter("empId", approvingEmpId);
                return query.executeUpdate();
            });

            return updatedRows != null && updatedRows > 0;

        } catch (HibernateException e) {
            throw new ServerUnavailableException("Unable to update leave request status.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<EmployeeLeaveRequestVO> getMyLeaveRequests(int empId, List<LeaveRequestStatus> statusList) throws ServerUnavailableException {
        try {
            return hibernateTemplate.execute(session -> {
                Query<Object[]> query = session.createQuery(GET_ALL_LEAVE_REQUESTS_QUERY, Object[].class);
                query.setParameter("empId", empId);
                query.setParameterList("statusList", statusList);
                query.setParameter("pending", LeaveRequestStatus.PENDING); // Parameter for the pending status

                List<Object[]> results = query.list();
                List<EmployeeLeaveRequestVO> leaveRequests = new ArrayList<>();

                for (Object[] row : results) {
                    EmployeeLeaveRequestVO leaveRequest = getEmployeeLeaveRequestVO(row);

                    leaveRequests.add(leaveRequest);
                }

                return leaveRequests;
            });
        } catch (HibernateException e) {
            throw new ServerUnavailableException("Unable to retrieve leave requests.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public LeaveRequest getMyLeaveRequest(int leaveRequestId) throws ServerUnavailableException {
        try {
            return hibernateTemplate.get(LeaveRequest.class, leaveRequestId);
        } catch (DataAccessException e) {
            throw new ServerUnavailableException("Unable to retrieve leave request", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean updateMyLeaveRequest(LeaveRequest leaveRequest) throws ServerUnavailableException {
        try {
            hibernateTemplate.update(leaveRequest);
            hibernateTemplate.flush();
            return true;
        } catch (DataAccessException e) {
            throw new ServerUnavailableException("Unable to update leave request", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public int getEmployeeIdByLeaveRequestId(int leaveRequestId) throws ServerUnavailableException {
        Integer empId = -1;
        try {
            empId = hibernateTemplate.execute(session -> {
                Query<Integer> query = session.createQuery(GET_EMPLOYEE_ID_BY_LEAVE_REQUEST_ID_QUERY, Integer.class);
                query.setParameter("leaveRequestId", leaveRequestId);
                return query.getSingleResult();
            });
            return empId != null ? empId : -1;
        } catch (DataAccessException e) {
            throw new ServerUnavailableException("Unable to retrieve employee ID by leave request ID", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Integer> getLeaveTypeIdAndTotalDaysByLeaveRequestId(int leaveRequestId) throws ServerUnavailableException {
        List<Integer> leaveTypeData = new ArrayList<>();
        try {
            leaveTypeData = hibernateTemplate.execute(session -> {
                Query<Integer> query = session.createQuery(GET_LEAVE_TYPE_ID_TOTAL_DAYS_BY_LEAVE_REQUEST_ID_QUERY, Integer.class);
                query.setParameter("leaveRequestId", leaveRequestId);
                return query.list();
            });
            return leaveTypeData;
        } catch (DataAccessException e) {
            throw new ServerUnavailableException("Unable to retrieve leave type ID and total days by leave request ID", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
