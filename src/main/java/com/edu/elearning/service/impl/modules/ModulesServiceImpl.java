package com.edu.elearning.service.impl.modules;

import com.edu.elearning.dto.module.request.ModuleCreate;
import com.edu.elearning.dto.module.request.ModuleUpdate;
import com.edu.elearning.dto.module.response.ModuleResponse;
import com.edu.elearning.dto.video.response.VideoResponse;
import com.edu.elearning.entity.Modules;
import com.edu.elearning.enums.Status;
import com.edu.elearning.exception.ElearningException;
import com.edu.elearning.repository.ModulesRepository;
import com.edu.elearning.service.module.ModuleService;
import com.edu.elearning.specification.CommonSpecifications;
import com.edu.elearning.utility.Sorting;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ModulesServiceImpl implements ModuleService {

    private final ModulesRepository modulesRepository;


    @Override
    public ModuleResponse createModules(ModuleCreate moduleCreate) {

        Modules module = modulesRepository.save(buildModule(moduleCreate));

        return convertToDTO(module);
    }


    @Override
    public ModuleResponse updateModule(ModuleUpdate moduleUpdate) {

        if (moduleUpdate.getId() == null) {
            throw new ElearningException("Invalid Inputs");
        }


        Modules module = modulesRepository.findById(moduleUpdate.getId())
                .orElseThrow(() -> new ElearningException("Module not found"));


        if (moduleUpdate.getModuleCode() != null) {
            module.setModuleCode(moduleUpdate.getModuleCode());
        }


        if (moduleUpdate.getModuleName() != null) {
            module.setModuleName(moduleUpdate.getModuleName());
        }


        if (moduleUpdate.getModuleDescription() != null) {
            module.setModuleDescription(moduleUpdate.getModuleDescription());
        }


        if (moduleUpdate.getModuleCredits() != null) {
            module.setModuleCredit(moduleUpdate.getModuleCredits());
        }


        if (moduleUpdate.getStatus() != null) {
            module.setStatus(Status.valueOf(moduleUpdate.getStatus()));
        }


        Modules updatedModule = modulesRepository.save(module);

        return convertToDTO(updatedModule);
    }



    @Override
    public ModuleResponse getModuleById(Long id) {

        Modules module = modulesRepository.findById(id)
                .orElseThrow(() -> new ElearningException("Module not found"));

        return convertToDTO(module);
    }



    @Override
    public Page<ModuleResponse> getAllModules(
            Map<String, String> filters,
            int page,
            int size,
            String sortField,
            String sortDirection) {


        Specification<Modules> specification =
                CommonSpecifications.getSpecification(filters, Modules.class);


        Page<Modules> modules =
                modulesRepository.findAll(
                        specification,
                        Sorting.sorting(page, size, sortField, sortDirection)
                );


        return modules.map(this::convertToDTO);
    }



    private Modules buildModule(ModuleCreate moduleCreate) {

        return Modules.builder()
                .moduleCode(moduleCreate.getModuleCode())
                .moduleName(moduleCreate.getModuleName())
                .moduleDescription(moduleCreate.getModuleDescription())
                .moduleCredit(moduleCreate.getModuleCredit())
                .status(Status.ACTIVE)
                .build();
    }



    private ModuleResponse convertToDTO(Modules module) {


        List<VideoResponse> videos = module.getVideos() == null
                ? Collections.emptyList()
                : module.getVideos()
                .stream()
                .map(video -> VideoResponse.builder()
                        .id(video.getId())
                        .title(video.getTitle())
                        .description(video.getDescription())
                        .videoUrl(video.getVideoUrl())
                        .durationMinutes(video.getDurationMinutes())
                        .displayOrder(video.getDisplayOrder())
                        .status(video.getStatus().name())
                        .build())
                .toList();



        return ModuleResponse.builder()
                .id(module.getId())
                .moduleCode(module.getModuleCode())
                .moduleName(module.getModuleName())
                .moduleDescription(module.getModuleDescription())
                .moduleCredits(module.getModuleCredit())
                .status(module.getStatus().name())
                .videos(videos)
                .build();
    }
}