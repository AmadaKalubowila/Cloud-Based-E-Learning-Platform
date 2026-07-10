package com.edu.elearning.service.video;

import com.edu.elearning.dto.video.request.VideoCreate;
import com.edu.elearning.dto.video.request.VideoUpdate;
import com.edu.elearning.dto.video.response.VideoResponse;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface VideoService {
    VideoResponse createVideo(VideoCreate videoCreate);

    VideoResponse updateVideo(VideoUpdate videoUpdate);

    void deleteVideo(Long id);

    VideoResponse getVideoById(Long id);

    Page<VideoResponse> getAllVideos(
            Map<String, String> filters,
            int page,
            int size,
            String sortField,
            String sortDirection);
}
