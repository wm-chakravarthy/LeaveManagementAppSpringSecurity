package com.wavemaker.employee.util;

import com.wavemaker.employee.exception.ServerUnavailableException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {
    public static int calculateTotalDaysExcludingWeekendsAndHolidays(Date fromDate, Date toDate) throws ServerUnavailableException {
        LocalDate start = convertToLocalDate(fromDate);
        LocalDate end = convertToLocalDate(toDate);

        int totalDays = 0;
        LocalDate currentDate = start;

        while (!currentDate.isAfter(end)) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                totalDays++;
            }
            currentDate = currentDate.plusDays(1);
        }

        return totalDays;
    }

//    private static int getTotalHolidays(LocalDate fromDate, LocalDate toDate) throws ServerUnavilableException {
//        List<Date> dateList = holidayService.getUpcomingHolidayDatesList();
//        int totalHolidays = 0;
//
//        for (Date holiday : dateList) {
//            LocalDate holidayDate = convertToLocalDate(holiday);
//
//            if ((holidayDate.isEqual(fromDate) || holidayDate.isAfter(fromDate)) &&
//                    (holidayDate.isEqual(toDate) || holidayDate.isBefore(toDate))) {
//                totalHolidays++;
//            }
//        }
//        return totalHolidays;
//    }


    private static LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}