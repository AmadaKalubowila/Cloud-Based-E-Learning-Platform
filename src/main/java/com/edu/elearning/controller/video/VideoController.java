package com.edu.elearning.controller.video;

import com.edu.elearning.dto.userCourseMappings.response.ListOfMappedUsers;
import com.edu.elearning.dto.video.request.VideoCreate;
import com.edu.elearning.dto.video.request.VideoUpdate;
import com.edu.elearning.dto.video.response.VideoResponse;
import com.edu.elearning.service.video.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;

    @PostMapping("/create")
    public VideoResponse createVideo(@RequestHeader("Authorization") String token,
            @RequestBody VideoCreate videoCreate) {

        return videoService.createVideo(videoCreate);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VideoResponse uploadVideo(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "durationMinutes", required = false) Integer durationMinutes,
            @RequestParam(value = "displayOrder", required = false) Integer displayOrder,
            @RequestParam("moduleId") Long moduleId) {

        return videoService.uploadVideo(
                file, title, description, durationMinutes, displayOrder, moduleId);
    }

    @PutMapping("/update")
    public VideoResponse updateVideo(
            @RequestHeader("Authorization") String token,
            @RequestBody VideoUpdate videoUpdate) {

        return videoService.updateVideo(videoUpdate);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteVideo(@RequestHeader("Authorization") String token,
            @PathVariable Long id) {

        videoService.deleteVideo(id);

        return "Video deleted successfully";
    }

    @GetMapping("/getById/{id}")
    public VideoResponse getVideoById(@RequestHeader("Authorization") String token,
            @PathVariable Long id) {

        return videoService.getVideoById(id);
    }

    @GetMapping("/getAllByUsers/{id}")
    public ListOfMappedUsers getAllByUsers(
                                           @PathVariable Long id) {
        return videoService.getCourseUsers(id);
    }

    @GetMapping("/getAll")
    public Page<VideoResponse> getAllVideos(
            @RequestHeader("Authorization") String token,
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
