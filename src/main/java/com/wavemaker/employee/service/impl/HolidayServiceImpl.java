package com.wavemaker.employee.service.impl;

import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.Holiday;
import com.wavemaker.employee.repository.HolidayRepository;
import com.wavemaker.employee.service.HolidayService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class HolidayServiceImpl implements HolidayService {

    @Autowired
    @Qualifier("holidayRepositoryInDB")
    private static HolidayRepository holidayRepository;

    @Override
    public List<Holiday> getHolidayList() throws ServerUnavailableException {
        return holidayRepository.getHolidayList();
    }

    @Override
    public Holiday getHolidayById(int holidayId) throws ServerUnavailableException {
        return holidayRepository.getHolidayById(holidayId);
    }

    @Override
    public Holiday addHoliday(Holiday holiday) throws ServerUnavailableException {
        return holidayRepository.addHoliday(holiday);
    }

    @Override
    public List<Date> getListOfHolidayDates() throws ServerUnavailableException {
        return holidayRepository.getListOfHolidayDates();
    }

    @Override
    public List<Date> getUpcomingHolidayDatesList() throws ServerUnavailableException {
        return holidayRepository.getUpcomingHolidayDatesList();
    }

    @Override
    public List<Holiday> getUpcommingHolidayList() throws ServerUnavailableException {
        return holidayRepository.getUpcommingHolidayList();
    }
}
