package com.edu.elearning.service.impl.video;

import com.edu.elearning.dto.video.request.VideoCreate;
import com.edu.elearning.dto.video.request.VideoUpdate;
import com.edu.elearning.dto.video.response.VideoResponse;
import com.edu.elearning.entity.Modules;
import com.edu.elearning.entity.Video;
import com.edu.elearning.enums.Status;
import com.edu.elearning.exception.ElearningException;
import com.edu.elearning.repository.ModulesRepository;
import com.edu.elearning.repository.VideoRepository;
import com.edu.elearning.service.video.VideoService;
import com.edu.elearning.specification.CommonSpecifications;
import com.edu.elearning.utility.Sorting;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final ModulesRepository modulesRepository;


    @Override
    public VideoResponse createVideo(VideoCreate videoCreate) {

        Modules module = modulesRepository.findById(videoCreate.getModuleId())
                .orElseThrow(() -> new ElearningException("Module not found"));

        Video video = Video.builder()
                .title(videoCreate.getTitle())
                .description(videoCreate.getDescription())
                .videoUrl(videoCreate.getVideoUrl())
                .durationMinutes(videoCreate.getDurationMinutes())
                .displayOrder(videoCreate.getDisplayOrder())
                .status(Status.ACTIVE)
                .module(module)
                .build();

        Video savedVideo = videoRepository.save(video);

        return convertToDTO(savedVideo);
    }


    @Override
    public VideoResponse updateVideo(VideoUpdate videoUpdate) {

        if (videoUpdate.getId() == null) {
            throw new ElearningException("Invalid Video ID");
        }

        Video video = videoRepository.findById(videoUpdate.getId())
                .orElseThrow(() -> new ElearningException("Video not found"));


        if (videoUpdate.getTitle() != null) {
            video.setTitle(videoUpdate.getTitle());
        }

        if (videoUpdate.getDescription() != null) {
            video.setDescription(videoUpdate.getDescription());
        }

        if (videoUpdate.getVideoUrl() != null) {
            video.setVideoUrl(videoUpdate.getVideoUrl());
        }

        if (videoUpdate.getDurationMinutes() != null) {
            video.setDurationMinutes(videoUpdate.getDurationMinutes());
        }

        if (videoUpdate.getDisplayOrder() != null) {
            video.setDisplayOrder(videoUpdate.getDisplayOrder());
        }

        if (videoUpdate.getStatus() != null) {
            video.setStatus(Status.valueOf(videoUpdate.getStatus()));
        }


        if (videoUpdate.getModuleId() != null) {

            Modules module = modulesRepository.findById(videoUpdate.getModuleId())
                    .orElseThrow(() -> new ElearningException("Module not found"));

            video.setModule(module);
        }


        Video updatedVideo = videoRepository.save(video);

        return convertToDTO(updatedVideo);
    }


    @Override
    public void deleteVideo(Long id) {

        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ElearningException("Video not found"));

        videoRepository.delete(video);
    }


    @Override
    public VideoResponse getVideoById(Long id) {

        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ElearningException("Video not found"));

        return convertToDTO(video);
    }


    @Override
    public Page<VideoResponse> getAllVideos(
            Map<String, String> filters,
            int page,
            int size,
            String sortField,
            String sortDirection) {


        Specification<Video> specification =
                CommonSpecifications.getSpecification(filters, Video.class);


        Page<Video> videos =
                videoRepository.findAll(
                        specification,
                        Sorting.sorting(page, size, sortField, sortDirection)
                );


        return videos.map(this::convertToDTO);
    }


    private VideoResponse convertToDTO(Video video) {

        return VideoResponse.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .videoUrl(video.getVideoUrl())
                .durationMinutes(video.getDurationMinutes())
                .displayOrder(video.getDisplayOrder())
                .status(video.getStatus().name())
                .moduleId(video.getModule().getId())
                .moduleName(video.getModule().getModuleName())
                .build();
    }
}
