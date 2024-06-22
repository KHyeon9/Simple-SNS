package com.simple.sns.controller;

import com.simple.sns.controller.request.UserJoinRequest;
import com.simple.sns.controller.response.Response;
import com.simple.sns.controller.response.UserJoinResponse;
import com.simple.sns.model.User;
import com.simple.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    private final UserService userService;

    // TODO: implement
    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
        // join
        User user = userService.join(request.getUserName(), request.getPassword());

        return Response.success(UserJoinResponse.fromUser(user));
    }
}
