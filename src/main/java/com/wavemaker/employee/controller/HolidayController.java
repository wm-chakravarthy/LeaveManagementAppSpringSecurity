package com.wavemaker.employee.controller;

import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.Holiday;
import com.wavemaker.employee.service.HolidayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/employee/holiday")
public class HolidayController {

    private static final Logger logger = LoggerFactory.getLogger(HolidayController.class);

    @Autowired
    private HolidayService holidayService;

    @GetMapping
    public Holiday getHolidayById(
            @RequestParam(value = "holidayId", required = false) String holidayId,
            HttpServletRequest request, HttpServletResponse response) {

        logger.info("Fetching holiday details for holidayId: {}", holidayId);
        Holiday holiday = null;
        if (holidayId != null) {
            holiday = holidayService.getHolidayById(Integer.parseInt(holidayId));
            logger.info("Holiday details fetched: {}", holiday);
        }
        return holiday;
    }

    @GetMapping("/upcoming")
    public List<Holiday> getUpcommingHolidayList() throws ServerUnavailableException {
        List<Holiday> holidayList = null;
        logger.info("Fetching upcoming holidays");
        holidayList = holidayService.getUpcommingHolidayList();
        logger.info("Upcoming holidays fetched: {}", holidayList);
        return holidayList;
    }
}
