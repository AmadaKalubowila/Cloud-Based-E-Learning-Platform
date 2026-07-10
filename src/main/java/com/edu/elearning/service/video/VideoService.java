package com.edu.elearning.service.video;

import com.edu.elearning.dto.video.request.VideoCreate;
import com.edu.elearning.dto.video.request.VideoUpdate;
import com.edu.elearning.dto.video.response.VideoResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface VideoService {
    VideoResponse createVideo(VideoCreate videoCreate);

    VideoResponse updateVideo(VideoUpdate videoUpdate);

    VideoResponse getVideoById(Long id);

    Page<VideoResponse> getAllVideos(
            Map<String, String> filters,
            int page,
            int size,
            String sortField,
            String sortDirection);

    VideoResponse uploadVideo(
            MultipartFile file,
            String title,
            String description,
            Integer durationMinutes,
            Integer displayOrder,
            Long moduleId);
    void deleteVideo(Long id);
}
