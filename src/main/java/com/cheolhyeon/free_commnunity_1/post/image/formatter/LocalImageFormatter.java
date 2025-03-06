package com.cheolhyeon.free_commnunity_1.post.image.formatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LocalImageFormatter implements ImageStrategy {
    private final static String BASE_PATH = "/Users/cheolhyeon/desktop"; // S3 이용시 S3 엔드포인트로 변경
    private final ObjectMapper mapper;

    @Override
    public String formatToSave(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return "[]";
        }
        return getJsonPathsList(images).toString();
    }

    @Override
    public String formatToSave(List<MultipartFile> images, List<String> deletedImages, String existingImages) {
        // 기존 이미지 JSON을 List<String>으로 변환
        List<String> existingImagePaths = parseJsonToList(existingImages);

        // 1. 기존 이미지에서 삭제할 이미지 제거
        if (deletedImages != null && !deletedImages.isEmpty()) {
            existingImagePaths.removeAll(deletedImages);
        }

        // 2. 새로운 이미지 추가
        if (images != null && !images.isEmpty()) {
            List<String> newImagePaths = getJsonPathsList(images);
            existingImagePaths.addAll(newImagePaths);
        }

        // 3. 최종 리스트 JSON으로 변환하여 반환
        return toJson(existingImagePaths);
    }

    private List<String> getJsonPathsList(List<MultipartFile> images) {
        return images.stream()
                .map(image -> BASE_PATH + File.separator + image.getOriginalFilename())
                .toList();
    }

    private List<String> parseJsonToList(String json) {
        try {
            return mapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }

    private String toJson(List<String> paths) {
        try {
            return mapper.writeValueAsString(paths);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert to JSON", e);
        }
    }
}
