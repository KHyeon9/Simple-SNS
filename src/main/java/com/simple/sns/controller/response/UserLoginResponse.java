package com.simple.sns.controller.response;

import com.simple.sns.model.User;
import com.simple.sns.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginResponse {

    private String token;
}
