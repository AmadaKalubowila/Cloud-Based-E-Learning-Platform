package com.edu.elearning.controller.module;


import com.edu.elearning.dto.module.request.ModuleCreate;
import com.edu.elearning.dto.module.request.ModuleUpdate;
import com.edu.elearning.dto.module.response.ModuleResponse;
import com.edu.elearning.service.module.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping("/create")
    public ModuleResponse createModule(
            @RequestBody ModuleCreate moduleCreate) {

        return moduleService.createModules(moduleCreate);
    }


    @PutMapping("update")
    public ModuleResponse updateModule(
            @RequestBody ModuleUpdate moduleUpdate) {

        return moduleService.updateModule(moduleUpdate);
    }


    @GetMapping("/getById/{id}")
    public ModuleResponse getModuleById(
            @PathVariable Long id) {

        return moduleService.getModuleById(id);
    }


    @GetMapping("/getAll")
    public Page<ModuleResponse> getAllModules(
            @RequestParam Map<String, String> filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "DESC") String sortDirection) {


        return moduleService.getAllModules(
                filters,
                page,
                size,
                sortField,
                sortDirection
        );
    }
}