package com.edu.elearning.dto.userCourseMappings.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseMappingsUser {
    private String email;
    private String fullName;
}
