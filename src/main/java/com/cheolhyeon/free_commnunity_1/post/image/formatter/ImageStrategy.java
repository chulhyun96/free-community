package com.cheolhyeon.free_commnunity_1.post.image.formatter;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageStrategy {
    String formatToSave(List<MultipartFile> images) throws JsonProcessingException;

    String formatToSave(List<MultipartFile> images, List<String> deletedImages, String existingImages) throws JsonProcessingException;
}
