package com.edu.elearning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "videos")
@SuperBuilder
public class Video extends BaseEntity{

    private String title;

    @Column(length = 1000)
    private String description;

    private String videoUrl;

    private Integer durationMinutes;

    private Integer displayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Modules module;
}
