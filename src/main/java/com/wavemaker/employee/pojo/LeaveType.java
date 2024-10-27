package com.wavemaker.employee.pojo;

import jakarta.persistence.*;

@Entity
@Table(name = "LEAVE_TYPE")
public class LeaveType {

    @Id
    @Column(name = "LEAVE_TYPE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int leaveTypeId;

    @Column(name = "LEAVE_TYPE")
    private String leaveType;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "MAX_LEAVE_DAYS_ALLOWED")
    private int maxNoOfLeaves;

    @Column(name = "APPLICABLE_GENDER")
    private String applicableForGender;

    public int getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(int leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxNoOfLeaves() {
        return maxNoOfLeaves;
    }

    public void setMaxNoOfLeaves(int maxNoOfLeaves) {
        this.maxNoOfLeaves = maxNoOfLeaves;
    }

    public String getApplicableForGender() {
        return applicableForGender;
    }

    public void setApplicableForGender(String applicableForGender) {
        this.applicableForGender = applicableForGender;
    }
}
