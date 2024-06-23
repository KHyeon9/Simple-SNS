package com.simple.sns.service;

import com.simple.sns.entity.PostEntity;
import com.simple.sns.entity.UserEntity;
import com.simple.sns.exception.ErrorCode;
import com.simple.sns.exception.SnsApplicationException;
import com.simple.sns.repository.PostEntityRepository;
import com.simple.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockBean
    private PostEntityRepository postEntityRepository;

    @MockBean
    private UserEntityRepository userEntityRepository;

    @Test
    void 포스트_작성이_성공한_경우() {
        // Given
        String title = "title";
        String content = "content";
        String userName = "userName";

        // When
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mock(UserEntity.class)));
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        // Then
        assertDoesNotThrow(() -> postService.create(title, content, userName));
    }

    @Test
    void 포스트_작성시_요청한_유저가_존재하자_않는_경우() {
        // Given
        String title = "title";
        String content = "content";
        String userName = "userName";

        // When
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        // Then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.create(title, content, userName));
        assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }
}
