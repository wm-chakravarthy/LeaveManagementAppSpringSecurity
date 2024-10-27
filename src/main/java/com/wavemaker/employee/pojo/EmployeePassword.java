package com.wavemaker.employee.pojo;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "EMPLOYEE_PASSWORD")
public class EmployeePassword implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMPLOYEE_PASSWORD_ID")
    private int employeePasswordId;

    @Column(name = "EMP_ID")
    private int empId;

    @Column(name = "EMAIL_ID")
    private String email;

    @Column(name = "PASSWORD")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getEmployeePasswordId() {
        return employeePasswordId;
    }

    public void setEmployeePasswordId(int employeePasswordId) {
        this.employeePasswordId = employeePasswordId;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
