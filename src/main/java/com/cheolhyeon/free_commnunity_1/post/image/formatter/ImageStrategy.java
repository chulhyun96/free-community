package com.cheolhyeon.free_commnunity_1.post.image.formatter;


import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageStrategy {
    String formatToSave(List<MultipartFile> images);

    String formatToSave(List<MultipartFile> images, List<String> deletedImages, String existingImages);
}
