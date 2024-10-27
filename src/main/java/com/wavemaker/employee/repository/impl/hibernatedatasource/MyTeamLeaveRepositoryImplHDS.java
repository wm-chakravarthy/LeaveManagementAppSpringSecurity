package com.wavemaker.employee.repository.impl.hibernatedatasource;

import com.wavemaker.employee.constants.LeaveRequestStatus;
import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.LeaveRequest;
import com.wavemaker.employee.repository.MyTeamLeaveRepository;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository("myTeamLeaveRepositoryHDS")
public class MyTeamLeaveRepositoryImplHDS implements MyTeamLeaveRepository {

    private static final String HQL_GET_TEAM_LEAVE_REQUESTS =
            "SELECT lr " +
                    "FROM LeaveRequest lr " +
                    "JOIN lr.employeeByEmpId e " +
                    "WHERE e.managerId = :managerEmpId";
    private static final String HQL_UPDATE_LEAVE_REQUEST_APPROVE_REJECT =
            "UPDATE LeaveRequest lr " +
                    "SET lr.leaveRequestStatus = :status, lr.dateOfApproved = :actionDate " +
                    "WHERE lr.leaveRequestId = :leaveRequestId AND lr.empId IN ( " +
                    "SELECT e.empId FROM Employee e WHERE e.managerId = :managerId)";
    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Override
    public List<LeaveRequest> getMyTeamLeaveRequests(int managerEmpId, List<String> statusList) throws ServerUnavailableException {
        List<LeaveRequestStatus> status = convertToLeaveRequestStatusList(statusList);
        try {
            return hibernateTemplate.execute(session -> {
                StringBuilder queryBuilder = new StringBuilder(HQL_GET_TEAM_LEAVE_REQUESTS);

                if (statusList != null && !statusList.isEmpty()) {
                    queryBuilder.append(" AND lr.leaveRequestStatus IN (:statusList)");
                }

                Query<LeaveRequest> query = session.createQuery(queryBuilder.toString(), LeaveRequest.class);
                query.setParameter("managerEmpId", managerEmpId);

                if (statusList != null && !statusList.isEmpty()) {
                    query.setParameterList("statusList", status);
                }

                return query.list();
            });
        } catch (Exception e) {
            throw new ServerUnavailableException("Unable to retrieve leave requests for team members.", 500);
        }
    }

    private List<LeaveRequestStatus> convertToLeaveRequestStatusList(List<String> statusList) {
        List<LeaveRequestStatus> leaveRequestStatusList = new ArrayList<>();
        if (statusList != null && !statusList.isEmpty()) {
            for (String status : statusList) {
                leaveRequestStatusList.add(LeaveRequestStatus.valueOf(status));
            }
        }
        return leaveRequestStatusList;
    }


    @Override
    public boolean approveOrRejectTeamLeaveRequest(int leaveRequestId, int approvingEmpId, LeaveRequestStatus approveOrRejectOrCancel) throws ServerUnavailableException {
        if (approveOrRejectOrCancel != LeaveRequestStatus.APPROVED &&
                approveOrRejectOrCancel != LeaveRequestStatus.REJECTED &&
                approveOrRejectOrCancel != LeaveRequestStatus.CANCELLED) {
            throw new IllegalArgumentException("Invalid status. Only APPROVED, REJECTED, and CANCELLED are allowed.");
        }

        try {
            Integer rowsAffected = hibernateTemplate.execute(session -> {
                Query query = session.createQuery(HQL_UPDATE_LEAVE_REQUEST_APPROVE_REJECT);
                query.setParameter("status", approveOrRejectOrCancel);
                query.setParameter("actionDate", new Date());
                query.setParameter("leaveRequestId", leaveRequestId);
                query.setParameter("managerId", approvingEmpId);
                return query.executeUpdate();
            });

            return rowsAffected != null && rowsAffected > 0;
        } catch (Exception e) {
            throw new ServerUnavailableException("Unable to update leave request status.", 500);
        }
    }

}
