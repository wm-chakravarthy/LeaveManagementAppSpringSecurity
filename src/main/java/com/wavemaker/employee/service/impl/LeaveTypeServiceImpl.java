package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.exception.LeaveDaysExceededException;
import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.LeaveType;
import com.wavemaker.employee.repository.LeaveTypeRepository;
import com.wavemaker.employee.service.LeaveTypeService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class LeaveTypeServiceImpl implements LeaveTypeService {

    @Autowired
    @Qualifier("leaveTypeRepositoryInHDS")
    private LeaveTypeRepository leaveTypeRepository;

    @Override
    public List<LeaveType> getAllLeaveTypes(String gender) throws ServerUnavailableException {
        return leaveTypeRepository.getAllLeaveTypes(gender);
    }

    @Override
    public boolean isLeaveTypeWithInRange(int leaveTypeId, int totalDays) throws ServerUnavailableException, LeaveDaysExceededException {
        return leaveTypeRepository.isLeaveTypeWithInRange(leaveTypeId, totalDays);
    }
}
