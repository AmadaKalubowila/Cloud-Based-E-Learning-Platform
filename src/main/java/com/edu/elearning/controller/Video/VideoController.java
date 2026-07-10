package com.edu.elearning.controller.Video;

import com.edu.elearning.dto.video.request.VideoCreate;
import com.edu.elearning.dto.video.request.VideoUpdate;
import com.edu.elearning.dto.video.response.VideoResponse;
import com.edu.elearning.service.video.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;


    @PostMapping("/create")
    public VideoResponse createVideo(
            @RequestBody VideoCreate videoCreate) {

        return videoService.createVideo(videoCreate);
    }

    @PutMapping("/update")
    public VideoResponse updateVideo(
            @RequestBody VideoUpdate videoUpdate) {

        return videoService.updateVideo(videoUpdate);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteVideo(
            @PathVariable Long id) {

        videoService.deleteVideo(id);

        return "Video deleted successfully";
    }

    @GetMapping("/getById/{id}")
    public VideoResponse getVideoById(
            @PathVariable Long id) {

        return videoService.getVideoById(id);
    }

    @GetMapping("/getAll")
    public Page<VideoResponse> getAllVideos(
            @RequestParam Map<String,String> filters,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "DESC") String sortDirection) {


        return videoService.getAllVideos(
                filters,
                page,
                size,
                sortField,
                sortDirection
        );
    }
}
