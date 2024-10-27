package com.wavemaker.employee.repository.impl.hibernatedatasource;

import com.wavemaker.employee.exception.LeaveDaysExceededException;
import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.LeaveType;
import com.wavemaker.employee.repository.LeaveTypeRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.HibernateException;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("leaveTypeRepositoryInHDS")
public class LeaveTypeRepositoryImplInHDS implements LeaveTypeRepository {

    private static final String GET_ALL_LEAVE_TYPES_QUERY = "FROM LeaveType lt WHERE lt.applicableForGender = 'BOTH' OR lt.applicableForGender = :gender";
    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Override
    public List<LeaveType> getAllLeaveTypes(String gender) throws ServerUnavailableException {
        List<LeaveType> leaveTypes = null;
        try {
            leaveTypes = hibernateTemplate.execute(session -> {
                Query<LeaveType> query = session.createQuery(GET_ALL_LEAVE_TYPES_QUERY, LeaveType.class);
                query.setParameter("gender", gender.toUpperCase());
                return query.list();
            });
        } catch (HibernateException e) {
            throw new ServerUnavailableException("Unable to retrieve leave type information", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        return leaveTypes;
    }


    @Override
    public boolean isLeaveTypeWithInRange(int leaveTypeId, int totalDays) throws ServerUnavailableException, LeaveDaysExceededException {
        try {
            LeaveType leaveType = hibernateTemplate.get(LeaveType.class, leaveTypeId);
            int maxAllowedDays = leaveType.getMaxNoOfLeaves();
            return totalDays < maxAllowedDays;
        } catch (HibernateException e) {
            throw new ServerUnavailableException("Failed to retrieve maximum allowed days for leave type.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
