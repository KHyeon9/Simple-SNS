package com.simple.sns.controller;

import com.simple.sns.controller.request.UserJoinRequest;
import com.simple.sns.controller.request.UserLoginRequest;
import com.simple.sns.controller.response.AlarmResponse;
import com.simple.sns.controller.response.Response;
import com.simple.sns.controller.response.UserJoinResponse;
import com.simple.sns.controller.response.UserLoginResponse;
import com.simple.sns.exception.ErrorCode;
import com.simple.sns.exception.SnsApplicationException;
import com.simple.sns.model.User;
import com.simple.sns.service.UserService;
import com.simple.sns.util.ClassUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
        // join
        User user = userService.join(request.getName(), request.getPassword());

        return Response.success(UserJoinResponse.fromUser(user));
    }

    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        String token = userService.login(request.getName(), request.getPassword());
        return Response.success(new UserLoginResponse(token));
    }

    @GetMapping("/alarm")
    public Response<Page<AlarmResponse>> alarm(Pageable pageable, Authentication authentication) {
        User user = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), User.class)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "Casting to User class fails."));
        return Response.success(userService.alarmList(user.getId(), pageable).map(AlarmResponse::fromAlarm));
    }
}
