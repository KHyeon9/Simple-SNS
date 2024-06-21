package com.simple.sns.service;

import com.simple.sns.entity.UserEntity;
import com.simple.sns.exception.SnsApplicationException;
import com.simple.sns.model.User;
import com.simple.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserEntityRepository userEntityRepository;

    // TODO: implement
    public User join(String userName, String password) {
        // 회원 가입하는 username 중복 체크
        Optional<UserEntity> userEntity = userEntityRepository.findByUserName(userName);

        // 회원 가입 진행 -> user 등록
        userEntityRepository.save(new UserEntity());

        return new User();
    }

    // TODO: implement
    public String login(String userName, String password) {
        // 회원가입 체크
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(SnsApplicationException::new);

        // 비밀번호 체크
        if (!userEntity.getPassword().equals(password)) {
            throw new SnsApplicationException();
        }
        
        // 토큰 생성

        return "";
    }
}
