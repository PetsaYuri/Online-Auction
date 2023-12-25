package com.OnlineAuction.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MainService {

    private final ImageUploadService imageUploadService;

    @Autowired
    public MainService(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }

    public String uploadImage(MultipartFile file) {
        String filename = imageUploadService.getUniqueFilename(file.getOriginalFilename() + " ");
        imageUploadService.saveImage(file, filename);
        return filename;
    }
}
