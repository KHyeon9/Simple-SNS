package com.simple.sns.service;

import com.simple.sns.entity.UserEntity;
import com.simple.sns.exception.SnsApplicationException;
import com.simple.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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

    @Test
    void 회원가입이_정상적으로_동작하는_경우() {
        // Given
        String userName = "userName";
        String password = "password";
        UserEntity fixture = get(userName, password);

        // When
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(userEntityRepository.save(any())).thenReturn(Optional.of(fixture));

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
        when(userEntityRepository.save(any())).thenReturn(Optional.of(fixture));

        // Then
        assertThrows(SnsApplicationException.class, () -> userService.join(userName, password));
    }

    @Test
    void 로그인이_정상적으로_동작하는_경우() {
        // Given
        String userName = "userName";
        String password = "password";
        UserEntity fixture = get(userName, password);

        // When
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));

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
        assertThrows(SnsApplicationException.class, () -> userService.login(userName, password));
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
        assertThrows(SnsApplicationException.class, () -> userService.login(userName, wrongPassword));
    }
}