package com.edu.elearning.service.module;

import com.edu.elearning.dto.module.request.ModuleCreate;
import com.edu.elearning.dto.module.request.ModuleUpdate;
import com.edu.elearning.dto.module.response.ModuleResponse;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ModuleService {
    ModuleResponse createModules(ModuleCreate moduleCreate);

    ModuleResponse updateModule(ModuleUpdate moduleUpdate);

    ModuleResponse getModuleById(Long id);

    Page<ModuleResponse> getAllModules(
            Map<String, String> filters, int page, int size, String sortField, String sortDirection);
}
