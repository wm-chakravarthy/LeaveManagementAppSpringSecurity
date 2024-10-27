package com.wavemaker.employee.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "EMPLOYEE")
public class Employee {

    @Id
    @Column(name = "EMP_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer empId;

    @Column(name = "MANAGER_ID")
    private Integer managerId;

    @Column(name = "NAME")
    private String empName;

    @Column(name = "DOB")
    private Date empDateOfBirth;

    @Column(name = "PHONE_NUMBER")
    private Long phoneNumber;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "GENDER")
    private String gender;

    @Column(name = "ROLE")
    private String role;

    @JsonIgnoreProperties("employeeByManagerId")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MANAGER_ID", insertable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Employee employeeByManagerId;

    public Employee getEmployeeByManagerId() {
        return employeeByManagerId;
    }

    public void setEmployeeByManagerId(Employee employeeByManagerId) {
        this.employeeByManagerId = employeeByManagerId;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getEmpDateOfBirth() {
        return empDateOfBirth;
    }

    public void setEmpDateOfBirth(Date empDateOfBirth) {
        this.empDateOfBirth = empDateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Employee employee = (Employee) object;
        return empId == employee.empId && managerId == employee.managerId && phoneNumber == employee.phoneNumber && Objects.equals(empName, employee.empName) && Objects.equals(empDateOfBirth, employee.empDateOfBirth) && Objects.equals(email, employee.email) && Objects.equals(gender, employee.gender) && Objects.equals(role, employee.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(empId, managerId, empName, empDateOfBirth, phoneNumber, email, gender, role);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "empId=" + empId +
                ", managerId=" + managerId +
                ", empName='" + empName + '\'' +
                ", empDateOfBirth=" + empDateOfBirth +
                ", phoneNumber=" + phoneNumber +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
