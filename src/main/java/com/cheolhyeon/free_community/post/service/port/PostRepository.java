package com.cheolhyeon.free_community.post.service.port;

import com.cheolhyeon.free_community.post.domain.Post;
import com.cheolhyeon.free_community.post.repository.entity.PostEntity;
import com.cheolhyeon.free_community.user.repository.entity.UserEntity;

public interface PostRepository {

    Post save(Post newPost);
}
