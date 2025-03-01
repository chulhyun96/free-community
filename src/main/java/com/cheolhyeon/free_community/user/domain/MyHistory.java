package com.cheolhyeon.free_community.user.domain;

import com.cheolhyeon.free_community.post.domain.Post;
import lombok.Getter;

import java.util.Deque;
import java.util.LinkedList;

@Getter
public class MyHistory {
    private static final int MAX_SIZE = 20;
    private final Deque<Post> posts = new LinkedList<>();
//    private Deque<Comment> comments = new ArrayList<>();


    //    사용자 활동 이력 관리(최근 작성한 게시글/댓글 관리) → 최신꺼 20개씩만 나머지는 안보여짐
    public void addPost(Post post) {
        if (posts.size() == MAX_SIZE) {
            posts.removeLast();
        }
        posts.addFirst(post);
    }


    /*public void addComment(Comment comment) {
        comments.add(comment)
    }*/

}
