package com.cheolhyeon.free_commnunity_1.user.controller.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserHistoryResponseTest {

    @Test
    @DisplayName("PostHistory를 Map에 저장한다")
    void addPostHistory() {
        //given
        PostHistoryResponse postHistoryResponse = PostHistoryResponse.of(1L, "test", 1L);
        UserHistoryResponse userHistory = new UserHistoryResponse();
        //when
        userHistory.addPostHistory(postHistoryResponse);

        //then
        assertThat(userHistory.getPostHistoryMap()).hasSize(1);
        assertThat(userHistory.getPostTitle(1L)).isEqualTo("test");
    }

    @Test
    @DisplayName("CommentHistory를 Map에 저장한다")
    void addCommentHistory() {
        //given
        CommentHistoryResponse commentHistoryResponse = CommentHistoryResponse.of(1L, "test", "content");
        UserHistoryResponse userHistory = new UserHistoryResponse();
        //when
        userHistory.addCommentHistory(commentHistoryResponse);

        //then
        assertThat(userHistory.getCommentHistoryMap()).hasSize(1);
    }

    @Test
    @DisplayName("PostTitle 검색 시에 postId가 없다면 빈 문자열을 반환한다")
    void getPostTitleReturnEmpty() {
        UserHistoryResponse userHistoryResponse = new UserHistoryResponse();

        String postTitle = userHistoryResponse.getPostTitle(null);

        assertThat(postTitle).isEqualTo("");
    }

    @Test
    @DisplayName("PostTitle 검색 시에 postId가 있다면 해당 Post Title을 반환한다")
    void getPostTitle() {
        UserHistoryResponse userHistoryResponse = new UserHistoryResponse();
        userHistoryResponse.addPostHistory(PostHistoryResponse.of(1L, "test", 1L));


        String postTitle = userHistoryResponse.getPostTitle(1L);

        assertThat(postTitle).isEqualTo("test");
    }
}