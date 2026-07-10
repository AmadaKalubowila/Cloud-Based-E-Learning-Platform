package com.edu.elearning.dto.courseManagement.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseCreate {

    private String courseCode;

    private String courseName;

    private String description;

    private String thumbnailUrl;

    private Integer durationHours;

    private String departments;

    private List<Long> moduleIds;
}