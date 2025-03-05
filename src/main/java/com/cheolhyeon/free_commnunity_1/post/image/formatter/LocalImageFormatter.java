package com.cheolhyeon.free_commnunity_1.post.image.formatter;

import com.fasterxml.jackson.core.JsonProcessingException;
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
        return getJsonPathsToSave(images);
    }

    private String getJsonPathsToSave(List<MultipartFile> images) {
        List<String> jsonPaths = images.stream()
                .map(image -> BASE_PATH + File.separator + image.getOriginalFilename())
                .toList();
        try {
            return mapper.writeValueAsString(jsonPaths);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
