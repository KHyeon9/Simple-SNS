package com.simple.sns.service;

import com.simple.sns.exception.ErrorCode;
import com.simple.sns.exception.SnsApplicationException;
import com.simple.sns.model.Comment;
import com.simple.sns.model.Post;
import com.simple.sns.model.entity.CommentEntity;
import com.simple.sns.model.entity.LikeEntity;
import com.simple.sns.model.entity.PostEntity;
import com.simple.sns.model.entity.UserEntity;
import com.simple.sns.repository.CommentEntityRepository;
import com.simple.sns.repository.LikeEntityRepository;
import com.simple.sns.repository.PostEntityRepository;
import com.simple.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final LikeEntityRepository likeEntityRepository;
    private final CommentEntityRepository commentEntityRepository;

    @Transactional
    public void create(String title, String content, String userName) {
        UserEntity userEntity = getUserEntityOrException(userName);

        // post save
        postEntityRepository.save(PostEntity.of(title, content, userEntity));
    }

    @Transactional
    public Post modify(String title, String content, String userName, Integer postId) {
        UserEntity userEntity = getUserEntityOrException(userName);
        PostEntity postEntity = getPostEntityOrException(postId);

        // post permission
        if (postEntity.getUser() != userEntity) {
           throw new SnsApplicationException(
                   ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }

        postEntity.setTitle(title);
        postEntity.setBody(content);

        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));
    }

    @Transactional
    public void delete(String userName, Integer postId) {
        UserEntity userEntity = getUserEntityOrException(userName);
        PostEntity postEntity = getPostEntityOrException(postId);

        // post permission
        if (postEntity.getUser() != userEntity) {
            throw new SnsApplicationException(
                    ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }

        postEntityRepository.delete(postEntity);
    }

    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    public Page<Post> my(String userName, Pageable pageable) {
        UserEntity userEntity = getUserEntityOrException(userName);

        return postEntityRepository.findAllByUser(userEntity, pageable).map(Post::fromEntity);
    }

    @Transactional
    public void like(Integer postId, String userName) {
        UserEntity userEntity = getUserEntityOrException(userName);
        PostEntity postEntity = getPostEntityOrException(postId);

        // check liked -> throw
        likeEntityRepository.findByUserAndPost(userEntity, postEntity).ifPresent(likeEntity -> {
            throw new SnsApplicationException(
                    ErrorCode.ALREADY_LIKED, String.format("Username %s already like post %d", userName, postId));
        });

        // like save
        likeEntityRepository.save(LikeEntity.of(userEntity, postEntity));
    }

    public int likeCount(Integer postId) {
        PostEntity postEntity = getPostEntityOrException(postId);

        // count like 
        // List<LikeEntity> likeEntitys = likeEntityRepository.findAllByPost(postEntity); 이건 entity를 가져오므로 좋지않음

        return likeEntityRepository.countByPost(postEntity);
    }

    @Transactional
    public void comment(Integer postId, String userName, String comment) {
        UserEntity userEntity = getUserEntityOrException(userName);
        PostEntity postEntity = getPostEntityOrException(postId);
        System.out.println("DAta 확인~~~~~~~~~~~~~~~~~~~ : " + userEntity.getUserName());

        // comment save
        commentEntityRepository.save(CommentEntity.of(userEntity, postEntity, comment));
    }

    public Page<Comment> getComments(Integer postId, Pageable pageable) {
        PostEntity postEntity = getPostEntityOrException(postId);

        return commentEntityRepository.findAllByPost(postEntity, pageable).map(Comment::fromEntity);
    }

    private PostEntity getPostEntityOrException(Integer postId) {
        // post find
       return postEntityRepository.findById(postId).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));
    }

    private UserEntity getUserEntityOrException(String userName) {
        // user find
        return userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
    }
}
