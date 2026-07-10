package com.edu.elearning.dto.video.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoCreate {

    private String title;

    private String description;

    private String videoUrl;

    private Integer durationMinutes;

    private Integer displayOrder;

    private Long moduleId;
}