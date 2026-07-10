package com.edu.elearning.dto.video.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VideoUpdate {

    private Long id;

    private String title;

    private String description;

    private String videoUrl;

    private Integer durationMinutes;

    private Integer displayOrder;

    private String status;

    private Long moduleId;
}