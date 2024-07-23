package kw.practice.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "STAFF")
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STAFF_ID")
    private Long id;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "HIRE_DATE")
    private Date hireDate;

    @Column(name = "SALARY")
    private Double salary;

    public void setId(Long id) {
        
    }

    // getters and setters
}
