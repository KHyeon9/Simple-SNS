package com.simple.sns.service;

import com.simple.sns.model.Alarm;
import com.simple.sns.model.entity.UserEntity;
import com.simple.sns.exception.ErrorCode;
import com.simple.sns.exception.SnsApplicationException;
import com.simple.sns.model.User;
import com.simple.sns.repository.AlarmEntityRepository;
import com.simple.sns.repository.UserCacheRepository;
import com.simple.sns.repository.UserEntityRepository;
import com.simple.sns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final AlarmEntityRepository alarmEntityRepository;
    private final BCryptPasswordEncoder encoder;
    private final UserCacheRepository userCacheRepository;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;

    public User loadUserByUserName(String userName) {
        return userCacheRepository.getUser(userName).orElseGet(
                () -> userEntityRepository.findByUserName(userName).map(User::fromEntity).orElseThrow(
                        () -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName))
                ));
    }

    @Transactional
    public User join(String userName, String password) {
        // 회원 가입하는 username 중복 체크
        userEntityRepository.findByUserName(userName).ifPresent(it -> {
                throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", userName));
        });

        // 회원 가입 진행 -> user 등록
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName, encoder.encode(password)));

        return User.fromEntity(userEntity);
    }

    public String login(String userName, String password) {
        // 회원가입 체크
        User user = loadUserByUserName(userName);
        userCacheRepository.setUser(user);

        // 비밀번호 체크
        if (!encoder.matches(password, user.getPassword())) {
            throw new SnsApplicationException(ErrorCode.INVALID_PASSWORD);
        }
        
        // 토큰 생성
        String token = JwtTokenUtils.generateToken(userName, secretKey, expiredTimeMs);

        return token;
    }

    public Page<Alarm> alarmList(Integer userId, Pageable pageable) {
        // UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() ->
        // new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));

        return alarmEntityRepository.findAllByUserId(userId, pageable).map(Alarm::fromEntity);
    }
}
