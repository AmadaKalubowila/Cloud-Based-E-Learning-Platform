package com.edu.elearning.dto.module.response;

import com.edu.elearning.dto.video.response.VideoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuleResponse {
    private Long id;
    private String moduleCode;
    private String moduleName;
    private String moduleDescription;
    private String moduleCredits;
    private String status;
    private List<VideoResponse> videos;
}
