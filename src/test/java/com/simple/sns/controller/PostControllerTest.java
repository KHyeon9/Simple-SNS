package com.simple.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simple.sns.controller.request.PostCreateRequest;
import com.simple.sns.controller.request.PostModifyRequest;
import com.simple.sns.exception.ErrorCode;
import com.simple.sns.exception.SnsApplicationException;
import com.simple.sns.fixture.PostEntityFixture;
import com.simple.sns.model.Post;
import com.simple.sns.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @Test
    @WithMockUser
    void 포스트_작성() throws Exception {
        // Given
        String title = "title";
        String content = "content";

        // When & Then
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, content))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void 포스트_작성시_로그인하지_않는_경우_에러_발생() throws Exception {
        // Given
        String title = "title";
        String content = "content";

        // When & Then
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, content))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @Test
    @WithMockUser
    void 포스트_수정() throws Exception {
        // Given
        String title = "title";
        String content = "content";

        // When
        when(postService.modify(eq(title), eq(content), any(), any()))
                .thenReturn(Post.fromEntity(PostEntityFixture.get("userName", 1, 1)));

        // Then
        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, content))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void 포스트_수정시_로그인하지_않은_경우_에러_발생 () throws Exception {
        // Given
        String title = "title";
        String content = "content";

        // When & Then
        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, content))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @Test
    @WithMockUser
    void 포스트_수정시_본인이_작성한_글이_아닌경우_에러_발생 () throws Exception {
        // Given
        String title = "title";
        String content = "content";

        // When
        doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION))
                .when(postService).modify(eq(title), eq(content), any(), eq(1));

        // Then
        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, content))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));
    }

    @Test
    @WithMockUser
    void 포스트_수정시_수정하려는_글이_없는경우_에러_발생 () throws Exception {
        // Given
        String title = "title";
        String content = "content";

        // When
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND))
                .when(postService).modify(eq(title), eq(content), any(), eq(1));

        // Then
        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostModifyRequest(title, content))))
                .andDo(print())
                .andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()));
    }

    @Test
    @WithMockUser
    void 포스트_삭제() throws Exception {
        // Given

        // When

        // Then
        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void 포스트_삭제사_로그인하지_않은_경우_에러_발생() throws Exception {
        // Given

        // When

        // Then
        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @Test
    @WithMockUser
    void 포스트_삭제시_작성자와_삭제_요청자가_다를_경우_에러_발생() throws Exception {
        // Given

        // When
        doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).delete(any(), any());

        // Then
        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));
    }

    @Test
    @WithMockUser
    void 포스트_삭제시_삭제하려는_포스트가_없는_경우_에러_발생() throws Exception {
        // Given

        // When
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).delete(any(), any());

        // Then
        mockMvc.perform(delete("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()));
    }

    @Test
    @WithMockUser
    void 피드_목록_조회() throws Exception {
        // Given
        Page<Post> list = postService.list(any());

        // When
        when(list).thenReturn(Page.empty());

        // Then
        mockMvc.perform(get("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void 피드_목록_요청시_로그인하지_않은_경우_에러_발생() throws Exception {
        // Given
        Page<Post> list = postService.list(any());

        // When
        when(list).thenReturn(Page.empty());

        // Then
        mockMvc.perform(get("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }

    @Test
    @WithMockUser
    void 내_피드_목록_조회() throws Exception {
        // Given
        Page<Post> my = postService.my(any(), any());

        // When
        when(my).thenReturn(Page.empty());

        // Then
        mockMvc.perform(get("/api/v1/posts/my")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void 내_피드_목록_요청시_로그인하지_않은_경우_에러_발생() throws Exception {
        // Given
        Page<Post> my = postService.my(any(), any());

        // When
        when(my).thenReturn(Page.empty());

        // Then
        mockMvc.perform(get("/api/v1/posts/my")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_TOKEN.getStatus().value()));
    }
}
