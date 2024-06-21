package com.simple.sns.fixture;

import com.simple.sns.entity.UserEntity;

public class UserEntityFixture {

    public static UserEntity get(String userName, String password) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setUserName(userName);
        userEntity.setPassword(password);

        return userEntity;
    }
}
