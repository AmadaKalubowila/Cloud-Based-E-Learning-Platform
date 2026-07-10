package com.edu.elearning.dto.video.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VideoResponse {

    private Long id;

    private String title;

    private String description;

    private String videoUrl;

    private Integer durationMinutes;

    private Integer displayOrder;

    private String status;

    private Long moduleId;

    private String moduleName;
}
