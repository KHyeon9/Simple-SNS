package com.simple.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.sns.controller.request.UserJoinRequest;
import com.simple.sns.controller.request.UserLoginRequest;
import com.simple.sns.exception.ErrorCode;
import com.simple.sns.exception.SnsApplicationException;
import com.simple.sns.model.User;
import com.simple.sns.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void 회원가입() throws Exception {
        // Given
        String userName = "userName";
        String password = "password";

        // When
        when(userService.join(userName, password)).thenReturn(mock(User.class));

        // then
        mockMvc.perform(post("/api/v1/users/join")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(new UserJoinRequest("userName", "password"))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void 회원가입시_이미_회원가입된_username으로_회원가입을_하는경우_에러_반환() throws Exception {
        // Given
        String userName = "userName";
        String password = "password";

        // When
        when(userService.join(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME));

        //then
        mockMvc.perform(post("/api/v1/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest("userName", "password")))
                ).andDo(print())
                .andExpect(status().is(ErrorCode.DUPLICATED_USER_NAME.getStatus().value()));
    }

    @Test
    void 로그인() throws Exception {
        // Given
        String userName = "userName";
        String password = "password";

        // When
        when(userService.login(userName, password)).thenReturn("test_token");

        // then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest("userName", "password")))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void 로그인시_회원가입이_안된_userName을_입력할_경우_에러_반환() throws Exception {
        // Given
        String userName = "userName";
        String password = "password";

        // When
        when(userService.login(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.USER_NOT_FOUND));

        // then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest("userName", "password")))
                ).andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void 로그인시_틀린_password를_입력할_경우_에러_반환() throws Exception {
        // Given
        String userName = "userName";
        String password = "password";

        // When
        when(userService.login(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.INVALID_PASSWORD));

        // then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest("userName", "password")))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
