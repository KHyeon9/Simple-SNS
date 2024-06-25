package com.simple.sns.service;

import com.simple.sns.entity.PostEntity;
import com.simple.sns.entity.UserEntity;
import com.simple.sns.exception.ErrorCode;
import com.simple.sns.exception.SnsApplicationException;
import com.simple.sns.fixture.PostEntityFixture;
import com.simple.sns.fixture.UserEntityFixture;
import com.simple.sns.repository.PostEntityRepository;
import com.simple.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        String body = "body";
        String userName = "userName";

        // When
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mock(UserEntity.class)));
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        // Then
        assertDoesNotThrow(() -> postService.create(title, body, userName));
    }

    @Test
    void 포스트_작성시_요청한_유저가_존재하자_않는_경우_에러_발생() {
        // Given
        String title = "title";
        String body = "body";
        String userName = "userName";

        // When
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(postEntityRepository.save(any())).thenReturn(mock(PostEntity.class));

        // Then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> postService.create(title, body, userName));
        assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 포스트_수정이_성공한_경우() {
        // Given
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        // When
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(postEntityRepository.saveAndFlush(any())).thenReturn(postEntity);

        // Then
        assertDoesNotThrow(() -> postService.modify(title, body, userName, postId));
    }

    @Test
    void 포스트_수정시_포스트가_존재하지_않는_경우_에러_발생() {
        // Given
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        // When
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        // Then
        SnsApplicationException e = assertThrows(
                SnsApplicationException.class, () -> postService.modify(title, body, userName, postId));
        assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 포스트_수정시_권한이_없는_경우_에러_발생() {
        // Given
        String title = "title";
        String body = "body";
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity writer = UserEntityFixture.get("testUserName", "password", 2);

        // When
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        // Then
        SnsApplicationException e = assertThrows(
                SnsApplicationException.class, () -> postService.modify(title, body, userName, postId));
        assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }

    @Test
    void 포스트_삭제가_성공한_경우() {
        // Given
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        // When
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        // Then
        assertDoesNotThrow(() -> postService.delete(userName, 1));
    }

    @Test
    void 포스트_삭제시_포스트가_존재하지_않는_경우_에러_발생() {
        // Given
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity userEntity = postEntity.getUser();

        // When
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.empty());

        // Then
        SnsApplicationException e = assertThrows(
                SnsApplicationException.class, () -> postService.delete(userName, 1));
        assertEquals(ErrorCode.POST_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 포스트_삭제시_권한이_없는_경우_에러_발생() {
        // Given
        String userName = "userName";
        Integer postId = 1;

        PostEntity postEntity = PostEntityFixture.get(userName, postId, 1);
        UserEntity writer = UserEntityFixture.get("testUserName", "password", 2);

        // When
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(writer));
        when(postEntityRepository.findById(postId)).thenReturn(Optional.of(postEntity));

        // Then
        SnsApplicationException e = assertThrows(
                SnsApplicationException.class, () -> postService.delete(userName, 1));
        assertEquals(ErrorCode.INVALID_PERMISSION, e.getErrorCode());
    }

    @Test
    void 피드목록_조회가_성공한_경우() {
        // Given
        Pageable pageable = mock(Pageable.class);

        // When
        when(postEntityRepository.findAll(pageable)).thenReturn(Page.empty());

        // Then
        assertDoesNotThrow(() -> postService.list(pageable));
    }

    @Test
    void 내_피드목록_조회가_성공한_경우() {
        // Given
        Pageable pageable = mock(Pageable.class);
        UserEntity user = mock(UserEntity.class);

        // When
        when(userEntityRepository.findByUserName(any())).thenReturn(Optional.of(user));
        when(postEntityRepository.findAllByUser(user, pageable)).thenReturn(Page.empty());

        // Then
        assertDoesNotThrow(() -> postService.my("", pageable));
    }
}
