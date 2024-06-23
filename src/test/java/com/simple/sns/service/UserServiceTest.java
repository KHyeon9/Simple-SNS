package com.simple.sns.service;

import com.simple.sns.entity.UserEntity;
import com.simple.sns.exception.ErrorCode;
import com.simple.sns.exception.SnsApplicationException;
import com.simple.sns.fixture.UserEntityFixture;
import com.simple.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.simple.sns.fixture.UserEntityFixture.get;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserEntityRepository userEntityRepository;

    @MockBean
    private BCryptPasswordEncoder encoder;

    @Test
    void 회원가입이_정상적으로_동작하는_경우() {
        // Given
        String userName = "userName";
        String password = "password";
        UserEntity fixture = get(userName, password);

        // When
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(encoder.encode(password)).thenReturn("encrypt_password");
        when(userEntityRepository.save(any())).thenReturn(UserEntityFixture.get(userName, password));

        // Then
        assertDoesNotThrow(() -> userService.join(userName, password));
    }

    @Test
    void 회원가입시_userName으로_회원가입한_유저가_이미_있는경우() {
        // Given
        String userName = "userName";
        String password = "password";
        UserEntity fixture = get(userName, password);

        // When
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        when(encoder.encode(password)).thenReturn("encrypt_password");
        when(userEntityRepository.save(any())).thenReturn(Optional.of(fixture));

        // Then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> userService.join(userName, password));
        assertEquals(ErrorCode.DUPLICATED_USER_NAME, e.getErrorCode());
    }

    @Test
    void 로그인이_정상적으로_동작하는_경우() {
        // Given
        String userName = "userName";
        String password = "password";
        UserEntity fixture = get(userName, password);

        // When
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        when(encoder.matches(password, fixture.getPassword())).thenReturn(true);

        // Then
        assertDoesNotThrow(() -> userService.login(userName, password));
    }

    @Test
    void 로그인시_userName으로_회원가입한_유저가_없는_경우() {
        // Given
        String userName = "userName";
        String password = "password";

        // When
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());

        // Then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> userService.login(userName, password));
        assertEquals(ErrorCode.USER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    void 로그인시_패스워드가_틀린_경우() {
        // Given
        String userName = "userName";
        String password = "password";
        String wrongPassword = "password";
        UserEntity fixture = get(userName, password);

        // When
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));

        // Then
        SnsApplicationException e = assertThrows(SnsApplicationException.class, () -> userService.login(userName, wrongPassword));
        assertEquals(ErrorCode.INVALID_PASSWORD, e.getErrorCode());
    }
}