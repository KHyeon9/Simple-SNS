package com.simple.sns.fixture;

import com.simple.sns.model.entity.PostEntity;
import com.simple.sns.model.entity.UserEntity;

public class PostEntityFixture {

    public static PostEntity get(String userName, Integer postId, Integer userId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUserName(userName);

        PostEntity postEntity = new PostEntity();
        postEntity.setUser(userEntity);
        postEntity.setId(postId);

        return postEntity;
    }
}
