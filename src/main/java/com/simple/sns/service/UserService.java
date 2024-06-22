package com.simple.sns.service;

import com.simple.sns.entity.UserEntity;
import com.simple.sns.exception.ErrorCode;
import com.simple.sns.exception.SnsApplicationException;
import com.simple.sns.model.User;
import com.simple.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserEntityRepository userEntityRepository;

    // TODO: implement
    public User join(String userName, String password) {
        // 회원 가입하는 username 중복 체크
        userEntityRepository.findByUserName(userName).ifPresent(it -> {
                throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", userName));
        });

        // 회원 가입 진행 -> user 등록
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName, password));

        return User.from(userEntity);
    }

    // TODO: implement
    public String login(String userName, String password) {
        // 회원가입 체크
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() -> new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, ""));

        // 비밀번호 체크
        if (!userEntity.getPassword().equals(password)) {
            throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, "");
        }
        
        // 토큰 생성

        return "";
    }
}
