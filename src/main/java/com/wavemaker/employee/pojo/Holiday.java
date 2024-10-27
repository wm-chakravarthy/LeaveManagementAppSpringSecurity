package com.wavemaker.employee.pojo;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "HOLIDAYS")
public class Holiday {

    @Id
    @Column(name = "HOLIDAY_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int holidayId;

    @Column(name = "NAME")
    private String holidayName;

    @Column(name = "HOLIDAY_DATE")
    private Date holidayDate;

    @Column(name = "DESCRIPTION")
    private String description;

    public int getHolidayId() {
        return holidayId;
    }

    public void setHolidayId(int holidayId) {
        this.holidayId = holidayId;
    }

    public String getHolidayName() {
        return holidayName;
    }

    public void setHolidayName(String holidayName) {
        this.holidayName = holidayName;
    }

    public Date getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(Date holidayDate) {
        this.holidayDate = holidayDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Holiday holiday = (Holiday) object;
        return holidayId == holiday.holidayId && Objects.equals(holidayName, holiday.holidayName) && Objects.equals(holidayDate, holiday.holidayDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(holidayId, holidayName, holidayDate);
    }

    @Override
    public String toString() {
        return "Holiday{" +
                "holidayId=" + holidayId +
                ", holidayName='" + holidayName + '\'' +
                ", holidayDate=" + holidayDate +
                ", description='" + description + '\'' +
                '}';
    }
}
