package com.edu.elearning.dto.courseManagement.response;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CourseResponse {

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