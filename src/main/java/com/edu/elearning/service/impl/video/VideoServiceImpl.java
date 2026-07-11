package com.edu.elearning.service.impl.video;

import com.edu.elearning.dto.userCourseMappings.response.CourseMappingsUser;
import com.edu.elearning.dto.userCourseMappings.response.ListOfMappedUsers;
import com.edu.elearning.dto.video.request.VideoCreate;
import com.edu.elearning.dto.video.request.VideoUpdate;
import com.edu.elearning.dto.video.response.VideoResponse;
import com.edu.elearning.entity.*;
import com.edu.elearning.enums.Status;
import com.edu.elearning.exception.ElearningException;
import com.edu.elearning.repository.*;
import com.edu.elearning.service.video.VideoService;
import com.edu.elearning.specification.CommonSpecifications;
import com.edu.elearning.utility.Sorting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private static final String S3_KEY_PREFIX = "videos/";
    private final VideoRepository videoRepository;
    private final ModulesRepository modulesRepository;
    private final S3StorageService s3StorageService;
    private final CourseEnrollmentRepository curEnrollmentRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final CourseRepository courseRepository;


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
    public VideoResponse uploadVideo(
            MultipartFile file,
            String title,
            String description,
            Integer durationMinutes,
            Integer displayOrder,
            Long moduleId) {

        Modules module = modulesRepository.findById(moduleId)
                .orElseThrow(() -> new ElearningException("Module not found"));

        String s3Key = s3StorageService.uploadVideo(file, moduleId);

        Video video = Video.builder()
                .title(title)
                .description(description)
                .videoUrl(s3Key)
                .durationMinutes(durationMinutes)
                .displayOrder(displayOrder)
                .status(Status.ACTIVE)
                .module(module)
                .build();

        Video savedVideo = videoRepository.save(video);

        List<CourseMappingsUser> enrolledUsers = List.of();

            ListOfMappedUsers users = getCourseUsers(video.getId());
            if (users != null && users.getUsers() != null) {
                enrolledUsers = users.getUsers();

            }

        VideoResponse response = convertToDTO(savedVideo);
        response.setEnrolledUsers(enrolledUsers);

        return response;
    }

    @Override
    public void deleteVideo(Long id) {

        Video video = videoRepository.findById(id)
                .orElseThrow(() -> new ElearningException("Video not found"));

        if (video.getVideoUrl() != null && video.getVideoUrl().startsWith(S3_KEY_PREFIX)) {
            s3StorageService.deleteVideo(video.getVideoUrl());
        }

        videoRepository.delete(video);
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

        String playbackUrl = video.getVideoUrl();
        if (playbackUrl != null && !playbackUrl.isBlank()) {
            playbackUrl = s3StorageService.generatePlaybackUrl(video.getVideoUrl());
        }

        return VideoResponse.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .videoUrl(playbackUrl)
                .durationMinutes(video.getDurationMinutes())
                .displayOrder(video.getDisplayOrder())
                .status(video.getStatus().name())
                .moduleId(video.getModule().getId())
                .moduleName(video.getModule().getModuleName())
                .build();
    }

    public ListOfMappedUsers getCourseUsers(Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new ElearningException("Video not found"));

        if (video.getModule() == null) {
            throw new ElearningException("Video is not linked to a module");
        }

        List<Courses> courses = courseRepository.findAllByModuleId(video.getModule().getId());

        List<CourseEnrollment> courseEnrollments = curEnrollmentRepository.findByCourseIn(courses);

        List<UserDetails> users = courseEnrollments.stream()
                .map(CourseEnrollment::getUser)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<CourseMappingsUser> courseMappingsUsers = users.stream()
                .map(this::buildCourseMappingsUser)
                .collect(Collectors.toList());

        return convertToDTO(courseMappingsUsers);
    }

    private CourseMappingsUser buildCourseMappingsUser(UserDetails user) {
        return CourseMappingsUser.builder()
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }

    private ListOfMappedUsers convertToDTO(List<CourseMappingsUser> courseMappingsUsers) {
        return ListOfMappedUsers.builder()
                .users(courseMappingsUsers)
                .build();
    }
}
