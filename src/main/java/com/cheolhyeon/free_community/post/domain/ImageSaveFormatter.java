package com.cheolhyeon.free_community.post.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ImageSaveFormatter {
    private final static String BASE_PATH = "/Users/cheolhyeon/desktop"; // S3 이용시 S3 엔드포인트로 변경
    private final ObjectMapper mapper;

    public String formatToSave(List<MultipartFile> images) {
        List<String> filePaths = images.stream()
                .map(image -> BASE_PATH + File.separator + image.getOriginalFilename())
                .collect(Collectors.toList());
        try {
            return mapper.writeValueAsString(filePaths);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
