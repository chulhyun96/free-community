package com.cheolhyeon.free_commnunity_1.post.image.formatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LocalImageFormatter implements ImageStrategy {

    @Value("${image.base-path}")
    private String basePath;
    private final ObjectMapper mapper;

    @Override
    public String formatToSave(List<MultipartFile> images)  {
        if (images == null || images.isEmpty()) {
            return "[]";
        }
        return toJson(getJsonPathsList(images));
    }


    @Override
    public String formatToSave(List<MultipartFile> newImages, List<String> deletedImages, String existingImages)  {
        // 1.기존 이미지 String 타입의 JSON 포맷을 List<String>으로 변환
        List<String> existingImagePaths = parseJsonToList(existingImages);

        // 2. 기존 이미지에서 삭제할 이미지 제거, 삭제할 이미지가 없을 경우 String 리스트로 변환 후 제거
        List<String> safeDeletedImages = Optional.ofNullable(deletedImages).orElseGet(List::of);
        existingImagePaths.removeAll(safeDeletedImages);

        // 3. 새로운 이미지 추가, newImages null 일 경우 빈 리스트로 변환 후 새로운 이미지 추가
        List<MultipartFile> safeNewImages = Optional.ofNullable(newImages).orElseGet(List::of);
        if (notEmpty(safeNewImages)) {
            List<String> newImagePaths = getJsonPathsList(safeNewImages);
            existingImagePaths.addAll(newImagePaths);
        }

        // 3. 최종 String 리스트를 JSON 포맷으로 변환하여 반환
        return toJson(existingImagePaths);
    }

    private boolean notEmpty(List<MultipartFile> images) {
        return !images.isEmpty();
    }

    private List<String> getJsonPathsList(List<MultipartFile> images) {
        return images.stream()
                .map(image -> basePath + File.separator + image.getOriginalFilename())
                .toList();
    }

    private List<String> parseJsonToList(String json)  {
        try {
            return mapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String toJson(List<String> paths)  {
        try {
            return mapper.writeValueAsString(paths);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
