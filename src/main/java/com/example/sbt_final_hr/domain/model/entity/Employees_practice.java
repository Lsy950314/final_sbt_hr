package com.example.sbt_final_hr.domain.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class Employees_practice {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employees_practice_seq")
    @SequenceGenerator(name = "employees_practice_seq", sequenceName = "Employees_practice_SEQ", allocationSize = 1)
    private Long employeeID;
    private String name;
    private String address;
    @Temporal(TemporalType.DATE)
    private Date lastProjectEndDate;
    @Temporal(TemporalType.DATE)
    private Date currentProjectEndDate;

    @Override
    public String toString() {
        return "Employees_practice [EmployeeID=" + employeeID + ", Name=" + name + ", Address=" + address + ", lastProjectEndDate=" + lastProjectEndDate + ", currentProjectEndDate=" + currentProjectEndDate + "]";
    }


}
