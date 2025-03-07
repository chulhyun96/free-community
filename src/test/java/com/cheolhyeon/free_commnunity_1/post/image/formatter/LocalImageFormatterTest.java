package com.cheolhyeon.free_commnunity_1.post.image.formatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class LocalImageFormatterTest {



    @Value("${image.base-path}")
    private String basePath;
    private LocalImageFormatter formatter;
    private ObjectMapper mapper;


    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        formatter = new LocalImageFormatter(mapper);
    }

    /*
    1. 기존이미지가 있는 상태에서 새로운 이미지의 변경없이 null로 들어올 경우
    2. 기존 이미지가 없는 상태에서 새로운 이미지가 들어올 경우
    3. 기존 이미지가 있는 상태에서 새로운 이미지가 추가가 될 경우 ex) 기존 이미지 2장, 새로운 이미지 3장 + 총 5장이 저장되어야 하는 경우
    4. 사용자가 기존 이미지를 완전히 비운 상태에서 새로운 이미지를 추가한 경우
    5. 사용자가 기존 2장의 이미지 중에서 1장을 삭제하고 새로운 1장을 추가한 경우*/
    @Test
    @DisplayName("기존 이미지가 있는 상태에서 이미지의 변경없이 null로 들어올 경우")
    void keepExistingImage() throws JsonProcessingException {
        String existingImages = toJson(List.of(basePath + "/image1.jpg", basePath + "/image2.jpg"));
        String result = formatter.formatToSave(null, null, existingImages);
        assertThat(result).isEqualTo(existingImages);
    }
    @Test
    @DisplayName("기존 이미지가 [] 상태에서 새로운 이미지가 들어올 경우")
    void addNewImageWhenNoExistingImages() {
        //given
        List<MultipartFile> newImages = List.of(
                new MockMultipartFile("image", "new1.jpg", "image/jpeg", "new1.jpg".getBytes()),
                new MockMultipartFile("image", "new2.jpg", "image/jpeg", "new2.jpg".getBytes())
        );
        //when
        String result = formatter.formatToSave(newImages, null, "[]");
        log.info("Result : {}", result);
        String expected = toJson(List.of(basePath + "/new1.jpg", basePath + "/new2.jpg"));
        log.info("Expected : {}", expected);
        //then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("기존 이미지를 유지 + 새로운 이미지 추가")
    void addNewImagesToExistingImages() throws JsonProcessingException {
        //given
        List<MultipartFile> newImages = List.of(
                new MockMultipartFile("image", "new1.jpg", "image/jpeg", "new1.jpg".getBytes()),
                new MockMultipartFile("image", "new2.jpg", "image/jpeg", "new2.jpg".getBytes())
        );
        //when
        String existingImages = toJson(List.of(basePath + "/image1.jpg", basePath + "/image2.jpg"));
        log.info("ExistingImages : {}", existingImages);
        String result = formatter.formatToSave(newImages, null, existingImages);
        log.info("Result : {}", result);
        //then
        assertThat(result).isEqualTo(toJson(List.of(
                basePath + "/image1.jpg",
                basePath + "/image2.jpg",
                basePath + "/new1.jpg",
                basePath + "/new2.jpg"
        )));
    }
    @Test
    @DisplayName("기존 이미지 전부 삭제 후 새로운 이미지 추가")
    void deleteAllAndAddNewImages() throws JsonProcessingException {
        //given
        List<MultipartFile> newImages = List.of(
                new MockMultipartFile("image", "new1.jpg", "image/jpeg", "new1.jpg".getBytes()),
                new MockMultipartFile("image", "new2.jpg", "image/jpeg", "new2.jpg".getBytes())
        );
        List<String> existingImages = List.of(basePath + "/image1.jpg", basePath + "/image2.jpg");
        String existingImagesAsJson = toJson(existingImages);
        //when
        String result = formatter.formatToSave(newImages, existingImages, existingImagesAsJson);
        log.info("Result : {}", result);

        //then
        assertThat(result).isEqualTo(toJson(List.of(
                basePath + "/new1.jpg",
                basePath + "/new2.jpg"
        )));
    }

    @Test
    @DisplayName("기존 이미지 일부를 삭제한 후 새로운 이미지 추가")
    void deleteSomeAndAddNewImages() {
        //given
        List<MultipartFile> newImages = List.of(
                new MockMultipartFile("image", "new1.jpg", "image/jpeg", "new1.jpg".getBytes()),
                new MockMultipartFile("image", "new2.jpg", "image/jpeg", "new2.jpg".getBytes())
        );
        String existingImages = toJson(List.of(basePath + "/image1.jpg", basePath + "/image2.jpg"));
        List<String> deletedImages = List.of(basePath + "/image1.jpg");
        //when
        String result = formatter.formatToSave(newImages, deletedImages, existingImages);
        log.info("Result : {}", result);
        //then
        assertThat(result).isEqualTo(toJson(List.of(
                basePath + "/image2.jpg",
                basePath + "/new1.jpg",
                basePath + "/new2.jpg"
        )));
    }

    private String toJson(List<String> list) {
        try {
            return mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert to JSON", e);
        }
    }
}