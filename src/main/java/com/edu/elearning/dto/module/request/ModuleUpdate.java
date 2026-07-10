package com.edu.elearning.dto.module.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuleUpdate {
    private Long id;
    private String moduleCode;
    private String moduleName;
    private String moduleDescription;
    private String moduleCredits;
    private String status;
}
