package com.cheolhyeon.free_commnunity_1.comment.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class CommentTest {

    @Test
    @DisplayName("delete 메서드가 호출 되고 나서 상태가 변경된다")
    void delete() {
        // given
        Comment content = Comment.builder()
                .commentId(1L)
                .content("content")
                .deleted(false)
                .build();
        
        // when
        content.delete();
        //then
        Assertions.assertThat(content)
                .extracting(
                        Comment::getDeleted,
                        Comment::getContent
                ).containsExactly(true, "사용자 요청에 의해 삭제된 댓글입니다.");
    }
}