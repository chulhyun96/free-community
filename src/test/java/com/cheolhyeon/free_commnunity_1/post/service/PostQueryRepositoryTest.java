package com.cheolhyeon.free_commnunity_1.post.service;

import com.cheolhyeon.free_commnunity_1.post.controller.response.PostSearchResponse;
import com.cheolhyeon.free_commnunity_1.post.controller.search.PostSearchCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PostQueryRepository.class)
class PostQueryRepositoryTest {


    @Autowired
    PostQueryRepository repository;


    @Test
    @DisplayName("Post의 title로 검색을 하면 해당 제목을 포함하는 게시물이 반환 된다.")
    void searchPostByTitle() {
        //given
        PostSearchCondition cond1 = new PostSearchCondition();
        cond1.setTitle("안녕하세요");
        PageRequest pageRequest = PageRequest.of(0, 10);
        String sort = "";
        //when
        Page<PostSearchResponse> response = repository.searchByCond(cond1, pageRequest, sort);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(cond1.getTitle()).isEqualTo(response.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("검색 조건의 title과 categoryName이 모두 null 이라면 전체가 조회되며 size는 20개가 반환된다.")
    void searchByNull() {
        //given
        PostSearchCondition cond1 = new PostSearchCondition();
        PageRequest pageRequest = PageRequest.of(1, 20);
        //when
        Page<PostSearchResponse> response = repository.searchByCond(cond1, pageRequest, "");
        //then
        assertThat(response).isNotNull();
        assertThat(response.getSize()).isEqualTo(20);
    }

    @Test
    @DisplayName("인피니티 스크롤 - 첫 페이지 : 다음페이지가 존재하는 경우 hasNext가 true가 된다.")
    void infiniteScrollHasNextTrue() {
        //given
        PostSearchCondition cond1 = new PostSearchCondition();
        PageRequest pageRequest = PageRequest.of(0, 1);
        //when
        Slice<PostSearchResponse> slice = repository.searchBySearchCondInfiniteScroll(cond1, pageRequest, "");
        //then
        assertThat(slice).isNotNull();
        assertThat(slice.getNumberOfElements()).isEqualTo(1);
        assertThat(slice.hasNext()).isTrue();
    }

    @Test
    @DisplayName("인피니티 스크롤 - 마지막 페치지 : 다음 페이지가 존재하지 않을 경우 hasNext가 false가 된다")
    void infiniteScrollHasNextFalse() {
        //given
        PostSearchCondition cond1 = new PostSearchCondition();
        PageRequest pageRequest = PageRequest.of(0, 2);
        //when
        Slice<PostSearchResponse> slice = repository.searchBySearchCondInfiniteScroll(cond1, pageRequest, "");
        //then
        assertThat(slice).isNotNull();
        assertThat(slice.getNumberOfElements()).isEqualTo(2);
        assertThat(slice.hasNext()).isFalse();
    }
}