package com.wavemaker.employee.service;

import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.Holiday;

import java.util.Date;
import java.util.List;

public interface HolidayService {
    public List<Holiday> getHolidayList() throws ServerUnavailableException;

    public Holiday getHolidayById(int holidayId) throws ServerUnavailableException;

    public Holiday addHoliday(Holiday holiday) throws ServerUnavailableException;

    public List<Date> getListOfHolidayDates() throws ServerUnavailableException;

    public List<Date> getUpcomingHolidayDatesList() throws ServerUnavailableException;

    public List<Holiday> getUpcommingHolidayList() throws ServerUnavailableException;

}
