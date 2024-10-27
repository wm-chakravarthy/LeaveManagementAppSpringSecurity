package com.wavemaker.employee.repository.impl.hibernatedatasource;

import com.wavemaker.employee.exception.LeaveDaysExceededException;
import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.Employee;
import com.wavemaker.employee.pojo.EmployeeLeaveSummary;
import com.wavemaker.employee.repository.EmployeeLeaveSummaryRepository;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("employeeLeaveSummaryRepositoryInHDS")
public class EmployeeLeaveSummaryRepositoryImplHDS implements EmployeeLeaveSummaryRepository {

    private static final String UPDATE_QUERY =
            "UPDATE EmployeeLeaveSummary els " +
                    "SET els.pendingLeaves = els.pendingLeaves - :pendingLeaves, " +
                    "els.totalLeavesTaken = els.totalLeavesTaken + :totalLeavesTaken, " +
                    "els.lastUpdated = CURRENT_TIMESTAMP " +
                    "WHERE els.empId = :empId AND els.leaveTypeId = :leaveTypeId";
    private static final String HQL_SELECT_EMPLOYEE_LEAVE_SUMMARY =
            "FROM EmployeeLeaveSummary els WHERE els.empId = :empId";
    private static final String HQL_SELECT_PENDING_LEAVES =
            "SELECT els.pendingLeaves FROM EmployeeLeaveSummary els WHERE els.empId = :empId AND els.leaveTypeId = :leaveTypeId";
    private static final String GET_EMPLOYEE_BY_ID =
            "FROM Employee emp WHERE emp.managerId = :managerId";
    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Override
    public boolean updateEmployeeLeaveSummary(int empId, int leaveTypeId, int totalDays) throws ServerUnavailableException {
        try {
            hibernateTemplate.execute(session -> {
                Query query = session.createQuery(UPDATE_QUERY);
                query.setParameter("pendingLeaves", totalDays);
                query.setParameter("totalLeavesTaken", totalDays);
                query.setParameter("empId", empId);
                query.setParameter("leaveTypeId", leaveTypeId);

                return query.executeUpdate();
            });
            return true;
        } catch (Exception e) {
            throw new ServerUnavailableException("Unable to update leave summary", 500);
        }
    }


    public List<EmployeeLeaveSummary> getEmployeeLeaveSummariesById(int empId) throws ServerUnavailableException {
        List<EmployeeLeaveSummary> summaries = null;
        try {
            summaries = hibernateTemplate.execute(session -> {
                Query<EmployeeLeaveSummary> query = session.createQuery(HQL_SELECT_EMPLOYEE_LEAVE_SUMMARY, EmployeeLeaveSummary.class);
                query.setParameter("empId", empId);
                return query.list();
            });
        } catch (Exception e) {
            throw new ServerUnavailableException("Unable to retrieve employee leave summaries", 500);
        }
        return summaries;
    }

    @Override
    public Map<Employee, List<EmployeeLeaveSummary>> getMyTeamEmployeeLeaveSummaries(int empId) throws ServerUnavailableException {
        Map<Employee, List<EmployeeLeaveSummary>> resultMap = new HashMap<>();
        List<Employee> employees = null; //
        try {
            employees = hibernateTemplate.execute(session -> {
                Query<Employee> query = session.createQuery(GET_EMPLOYEE_BY_ID, Employee.class);
                query.setParameter("managerId", empId);
                return query.list();
            });
            if (employees != null) {
                for (Employee employee : employees) {
                    resultMap.put(employee, getEmployeeLeaveSummariesById(employee.getEmpId()));
                }
            }
        } catch (Exception e) {
            throw new ServerUnavailableException("Unable to retrieve employee leave summaries", 500);
        }
        return resultMap;
    }

    @Override
    public boolean isLeaveTypeWithinRange(int empId, int leaveTypeId, int totalDays) throws LeaveDaysExceededException, ServerUnavailableException {
        Integer pendingLeaves = -1;
        try {
            pendingLeaves = hibernateTemplate.execute(session -> {
                Query<Integer> query = session.createQuery(HQL_SELECT_PENDING_LEAVES, Integer.class);
                query.setParameter("empId", empId);
                query.setParameter("leaveTypeId", leaveTypeId);

                return query.getSingleResult();
            });
            if (pendingLeaves == null) {
                return false;
            }
            return totalDays <= pendingLeaves;
        } catch (DataAccessException e) {
            throw new ServerUnavailableException("Unable to check leave type within range", 500);
        }
    }
}
