package com.rtvnewsnetwork.file.controller;

import com.rtvnewsnetwork.config.model.UploadedFile;
import com.rtvnewsnetwork.file.model.VideoAndThumbnailResponse;
import com.rtvnewsnetwork.file.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/rtv")
@RequiredArgsConstructor
public class FileProxyController {

    private final FileUploadService rtvService;

    @PostMapping("/file/upload")
    public ResponseEntity<UploadedFile> uploadFile(
            @RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(rtvService.uploadFile(file));
    }

    @PostMapping("/file/upload/multiple")
    public ResponseEntity<List<UploadedFile>> uploadFiles(
            @RequestParam("files") MultipartFile[] files) throws Exception {

        return ResponseEntity.ok(rtvService.uploadFiles(files));
    }


    @PostMapping("/videoAndThumbnailUpload")
    public ResponseEntity<VideoAndThumbnailResponse> uploadVideoAndThumbnail(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "videoPath", defaultValue = "MEDIA_VIDEOS") String videoPath,
            @RequestParam(value = "thumbnailPath", defaultValue = "MEDIA_IMAGES") String thumbnailPath
    ) throws Exception {
        return ResponseEntity.ok(rtvService.uploadVideoAndThumbnail(file, videoPath, thumbnailPath));
    }



}
