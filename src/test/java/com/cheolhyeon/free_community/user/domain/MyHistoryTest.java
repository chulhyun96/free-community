package com.cheolhyeon.free_community.user.domain;

import com.cheolhyeon.free_community.post.domain.Post;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MyHistoryTest {

    @Test
    void addPost() {
        //given
        MyHistory myHistory = new MyHistory();

        //when
        for (int i = 1; i <= 21; i++) {
            myHistory.addPost(Post.builder()
                    .title("title" + i)
                    .content("content" + i)
                    .imageUrl("imageUrl" + i)
                    .build());
        }
        //then
        String lastTitle = myHistory.getPosts().getLast().getTitle();
        String firstTitle = myHistory.getPosts().getFirst().getTitle();

        assertThat(myHistory.getPosts().size()).isEqualTo(20);
        assertThat(firstTitle).isEqualTo("title21");
        assertThat(lastTitle).isEqualTo("title2");
    }

}