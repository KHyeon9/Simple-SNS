package com.simple.sns.controller;

import com.simple.sns.controller.request.PostCreateRequest;
import com.simple.sns.controller.request.PostModifyRequest;
import com.simple.sns.controller.response.PostResponse;
import com.simple.sns.controller.response.Response;
import com.simple.sns.model.Post;
import com.simple.sns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostController {

    private final PostService postService;

    @PostMapping
    public Response<Void> create(@RequestBody PostCreateRequest request, Authentication authentication) {
        postService.create(request.getTitle(), request.getContent(), authentication.getName());

        return Response.success();
    }

    @PutMapping("/{postId}")
    public Response<PostResponse> modify(@PathVariable Integer postId, @RequestBody PostModifyRequest request, Authentication authentication) {
        Post post = postService.modify(request.getTitle(), request.getContent(), authentication.getName(), postId);

        return Response.success(PostResponse.fromPost(post));
    }
}
