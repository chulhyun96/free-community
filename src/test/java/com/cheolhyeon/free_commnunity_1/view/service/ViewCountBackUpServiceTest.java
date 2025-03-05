package com.cheolhyeon.free_commnunity_1.view.service;

import com.cheolhyeon.free_commnunity_1.view.repository.ViewCountBackUpRepository;
import com.cheolhyeon.free_commnunity_1.view.repository.entity.ViewCountEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ViewCountBackUpServiceTest {
    @Mock
    ViewCountBackUpRepository viewCountBackUpRepository;

    @InjectMocks
    ViewCountBackUpService viewCountBackUpService;

    @Test
    @DisplayName("updateViewCount가 발생하여 1을 반환할 경우 save()가 실행되지 않는다.")
    void backUpProcessingSuccess() {
        //given
        int alreadyExist = 1;
        Long postId = 1L;
        Long viewCount = 50L;
        given(viewCountBackUpRepository.updateViewCount(
                postId, viewCount)).willReturn(alreadyExist);
        //when
        viewCountBackUpService.backUp(postId, viewCount);

        //then
        then(viewCountBackUpRepository).should().updateViewCount(postId, viewCount);
        then(viewCountBackUpRepository).should(never()).findById(postId);
        then(viewCountBackUpRepository).should(never()).save(any(ViewCountEntity.class));
    }

    @Test
    @DisplayName("updateViewCount가 발생하여 0을 반환할 경우 초기 데이터를 저장하는 save()가 실행된다")
    void notBackUpp() {
        //given
        int alreadyExist = 0;
        Long postId = 1L;
        Long viewCount = 50L;
        given(viewCountBackUpRepository.updateViewCount(
                postId, viewCount)).willReturn(alreadyExist);
        //when
        viewCountBackUpService.backUp(postId, viewCount);

        //then
        then(viewCountBackUpRepository).should(times(1)).updateViewCount(postId, viewCount);
        then(viewCountBackUpRepository).should(times(1)).findById(postId);
        then(viewCountBackUpRepository).should(times(1)).save(any(ViewCountEntity.class));
    }

}