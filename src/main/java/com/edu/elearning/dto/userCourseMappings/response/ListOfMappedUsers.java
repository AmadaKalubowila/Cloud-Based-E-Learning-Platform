package com.edu.elearning.dto.userCourseMappings.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListOfMappedUsers {
    private List<CourseMappingsUser> users;
}
