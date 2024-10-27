package com.wavemaker.employee.pojo;

import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "EMPLOYEE_LEAVE_SUMMARY")
public class EmployeeLeaveSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SUMMARY_ID")
    public int summaryId;

    @Column(name = "EMP_ID")
    public int empId;

    @Column(name = "LEAVE_TYPE_ID")
    public int leaveTypeId;

    @Column(name = "PENDING_LEAVES")
    public int pendingLeaves;

    @Column(name = "TOTAL_LEAVES_TAKEN")
    public int totalLeavesTaken;

    @Column(name = "LAST_UPDATED")
    public Date lastUpdated;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EMP_ID", insertable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Employee employee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LEAVE_TYPE_ID", insertable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private LeaveType leaveTypeByLeaveTypeId;

    public LeaveType getLeaveTypeByLeaveTypeId() {
        return leaveTypeByLeaveTypeId;
    }

    public void setLeaveTypeByLeaveTypeId(LeaveType leaveTypeByLeaveTypeId) {
        this.leaveTypeByLeaveTypeId = leaveTypeByLeaveTypeId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public int getSummaryId() {
        return summaryId;
    }

    public void setSummaryId(int summaryId) {
        this.summaryId = summaryId;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public int getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(int leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }

    public int getPendingLeaves() {
        return pendingLeaves;
    }

    public void setPendingLeaves(int pendingLeaves) {
        this.pendingLeaves = pendingLeaves;
    }

    public int getTotalLeavesTaken() {
        return totalLeavesTaken;
    }

    public void setTotalLeavesTaken(int totalLeavesTaken) {
        this.totalLeavesTaken = totalLeavesTaken;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EmployeeLeaveSummary that = (EmployeeLeaveSummary) object;
        return summaryId == that.summaryId && empId == that.empId && leaveTypeId == that.leaveTypeId && pendingLeaves == that.pendingLeaves && totalLeavesTaken == that.totalLeavesTaken && Objects.equals(lastUpdated, that.lastUpdated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(summaryId, empId, leaveTypeId, pendingLeaves, totalLeavesTaken, lastUpdated);
    }

    @Override
    public String toString() {
        return "EmployeeLeaveSummary{" +
                "summaryId=" + summaryId +
                ", empId=" + empId +
                ", leaveTypeId=" + leaveTypeId +
                ", pendingLeaves=" + pendingLeaves +
                ", totalLeavesTaken=" + totalLeavesTaken +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
