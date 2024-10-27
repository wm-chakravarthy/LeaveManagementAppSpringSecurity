package com.wavemaker.employee.repository.impl;

import com.wavemaker.employee.exception.ServerUnavailableException;
import com.wavemaker.employee.pojo.Holiday;
import com.wavemaker.employee.repository.HolidayRepository;
import com.wavemaker.employee.util.DBConnector;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository("holidayRepositoryInDB")
public class HolidayRepositoryImpl implements HolidayRepository {
    private static final String INSERT_HOLIDAY_QUERY = "INSERT INTO HOLIDAYS " +
            "(NAME, HOLIDAY_DATE, DESCRIPTION) VALUES (?, ?, ?)";
    private static final String SELECT_ALL_HOLIDAYS_QUERY =
            "SELECT HOLIDAY_ID, NAME, HOLIDAY_DATE, DESCRIPTION FROM HOLIDAYS";
    private static final String SELECT_HOLIDAY_BY_ID_QUERY =
            "SELECT HOLIDAY_ID, NAME, HOLIDAY_DATE, DESCRIPTION FROM HOLIDAYS WHERE HOLIDAY_ID = ?";
    private static final String DATE_FROM_HOLIDAYS_QUERY = "SELECT HOLIDAY_DATE FROM HOLIDAYS";
    private static final String SELECT_UPCOMING_HOLIDAY_LIST = "SELECT HOLIDAY_ID, NAME, HOLIDAY_DATE, DESCRIPTION " +
            "FROM holidays WHERE HOLIDAY_DATE >= CURDATE()";

    private Connection connection;

    public HolidayRepositoryImpl() {
        try {
            connection = DBConnector.getConnectionInstance();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Holiday addHoliday(Holiday holiday) throws ServerUnavailableException {
        try {
            PreparedStatement statement = connection.prepareStatement(INSERT_HOLIDAY_QUERY, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, holiday.getHolidayName());
            java.sql.Date sqlHolidayDate = new java.sql.Date(holiday.getHolidayDate().getTime());
            statement.setDate(2, sqlHolidayDate);
            statement.setString(3, holiday.getDescription());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        holiday.setHolidayId((int) generatedKeys.getLong(1));
                    }
                }
            }
            return holiday;

        } catch (SQLException e) {
            throw new ServerUnavailableException("Error adding holiday", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Date> getListOfHolidayDates() throws ServerUnavailableException {
        List<Date> holidayDates = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(DATE_FROM_HOLIDAYS_QUERY);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                holidayDates.add(resultSet.getDate("HOLIDAY_DATE"));
            }
        } catch (SQLException e) {
            throw new ServerUnavailableException("Error retrieving holiday dates", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return holidayDates;
    }

    public List<Date> getUpcomingHolidayDatesList() throws ServerUnavailableException {
        List<Date> upcomingHolidays = new ArrayList<>();
        String query = "SELECT HOLIDAY_DATE FROM holidays WHERE HOLIDAY_DATE >= CURDATE()";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Date holidayDate = rs.getDate("HOLIDAY_DATE");
                upcomingHolidays.add(holidayDate);
            }
        } catch (SQLException e) {
            throw new ServerUnavailableException("Error retrieving upcoming holidays", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return upcomingHolidays;
    }

    @Override
    public List<Holiday> getUpcommingHolidayList() throws ServerUnavailableException {
        List<Holiday> upcomingHolidays = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_UPCOMING_HOLIDAY_LIST);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Holiday holiday = new Holiday();
                holiday.setHolidayId(rs.getInt("HOLIDAY_ID"));
                holiday.setHolidayName(rs.getString("NAME"));
                holiday.setHolidayDate(rs.getDate("HOLIDAY_DATE"));
                holiday.setDescription(rs.getString("DESCRIPTION"));

                upcomingHolidays.add(holiday);
            }

        } catch (SQLException e) {
            throw new ServerUnavailableException("Error retrieving upcoming holidays", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return upcomingHolidays;
    }


    @Override
    public List<Holiday> getHolidayList() throws ServerUnavailableException {
        List<Holiday> holidays = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(SELECT_ALL_HOLIDAYS_QUERY);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Holiday holiday = new Holiday();
                holiday.setHolidayId(resultSet.getInt("HOLIDAY_ID"));
                holiday.setHolidayName(resultSet.getString("NAME"));
                holiday.setHolidayDate(resultSet.getDate("HOLIDAY_DATE"));
                holiday.setDescription(resultSet.getString("DESCRIPTION"));
                holidays.add(holiday);
            }
        } catch (SQLException e) {
            throw new ServerUnavailableException("Error retrieving holiday list", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return holidays;
    }

    @Override
    public Holiday getHolidayById(int holidayId) throws ServerUnavailableException {
        try {
            PreparedStatement statement = connection.prepareStatement(SELECT_HOLIDAY_BY_ID_QUERY);
            statement.setInt(1, holidayId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Holiday holiday = new Holiday();
                    holiday.setHolidayId(resultSet.getInt("HOLIDAY_ID"));
                    holiday.setHolidayName(resultSet.getString("NAME"));
                    holiday.setHolidayDate(resultSet.getDate("HOLIDAY_DATE"));
                    holiday.setDescription(resultSet.getString("DESCRIPTION"));
                    return holiday;
                }
            }
        } catch (SQLException e) {
            throw new ServerUnavailableException("Error retrieving holiday by ID", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return null;
    }
}
