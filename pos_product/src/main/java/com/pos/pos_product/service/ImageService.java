package com.pos.pos_product.service;

import com.pos.pos_product.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Service s3Service;

    private String uploadImage(MultipartFile file, String folder) {

        validateFile(file);

        return s3Service.uploadFile(file, folder);
    }

    public String replaceImage(String oldKey, MultipartFile file, String folder) {

        String newKey = uploadImage(file, folder);

        if (oldKey != null && !oldKey.isBlank()) {
            s3Service.deleteFile(oldKey);
        }

        return newKey;
    }

    public void deleteImage(String key) {

        if (key == null || key.isBlank()) {
            return;
        }

        s3Service.deleteFile(key);
    }

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file must not be empty");
        }

        if (file.getSize() > 5_000_000) {
            throw new IllegalArgumentException("Image size exceeds 5MB limit");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }
    }
}