package com.cheolhyeon.free_commnunity_1.post.repository;


import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageStrategy {
    String formatToSave(List<MultipartFile> images);
}
