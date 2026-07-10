package com.edu.elearning.entity;

import com.edu.elearning.enums.Departments;
import com.edu.elearning.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "user_details")
public class UserDetails extends BaseEntity {

    @Column(name= "register_no", unique = true)
    private String registerNo;

    @Column(name = "full_name")
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "department")
    private Departments departments;

    @Column(name = "email")
    private String email;

    @Column(name="batch")
    private String batch;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "registered_date")
    private LocalDateTime registeredDate;
}

