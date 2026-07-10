package com.edu.elearning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "modules")
@SuperBuilder
public class Modules extends BaseEntity {
    @Column(name = "module_code")
    private String moduleCode;

    @Column(name = "module_name")
    private String moduleName;

    @Column(name = "module_description")
    private String moduleDescription;

    @Column(name = "module_credit")
    private String moduleCredit;

    @OneToMany(mappedBy = "module",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Video> videos = new ArrayList<>();
}
