package com.simple.sns.fixture;

import com.simple.sns.entity.PostEntity;
import com.simple.sns.entity.UserEntity;

public class PostEntityFixture {

    public static PostEntity get(String userName, Integer postId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setUserName(userName);

        PostEntity postEntity = new PostEntity();
        postEntity.setUser(userEntity);
        postEntity.setId(postId);

        return postEntity;
    }
}
