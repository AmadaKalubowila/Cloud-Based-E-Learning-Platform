package com.edu.elearning.dto.video.response;

import com.edu.elearning.dto.userCourseMappings.response.CourseMappingsUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    private List<CourseMappingsUser> enrolledUsers;
}
