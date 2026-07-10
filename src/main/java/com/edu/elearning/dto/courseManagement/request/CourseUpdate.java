package com.edu.elearning.dto.courseManagement.request;


import lombok.Data;

import java.util.List;

@Data
public class CourseUpdate {

    private Long id;

    private String courseCode;

    private String courseName;

    private String description;

    private String thumbnailUrl;

    private Integer durationHours;

    private String courseStatus;

    private String status;

    private String departments;

    private List<Long> moduleIds;
}