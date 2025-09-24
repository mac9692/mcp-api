package com.js.mcpapi.service;

import com.js.mcpapi.dto.*;
import com.js.mcpapi.mapper.UserMapper;
import com.js.mcpapi.util.JwtTokenUtil;
import com.js.mcpapi.util.PasswordEncoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_Success() {
        // Given
        RegisterRequestDto request = new RegisterRequestDto("testuser", "testpass123", "테스트사용자", "test@example.com", "01012345678", "1994-10-06");

        given(userMapper.checkUserExists("testuser")).willReturn(0);
        given(userMapper.insertUser(any(RegisterRequestDto.class))).willReturn(1);
        given(passwordEncoder.encode("testpass123")).willReturn("encodedPassword");
        given(userMapper.insertUserAuth("testuser", "encodedPassword")).willReturn(1);

        // When
        AuthResponseDto result = authService.register(request);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessage()).isEqualTo("회원가입이 성공적으로 완료되었습니다.");

        verify(userMapper).checkUserExists("testuser");
        verify(userMapper).insertUser(request);
        verify(passwordEncoder).encode("testpass123");
        verify(userMapper).insertUserAuth("testuser", "encodedPassword");
    }

    @Test
    void register_UserAlreadyExists() {
        // Given
        RegisterRequestDto request = new RegisterRequestDto("existuser", "password123", "기존사용자", "exist@example.com", "01087654321", "1990-01-01");
        given(userMapper.checkUserExists("existuser")).willReturn(1);

        // When
        AuthResponseDto result = authService.register(request);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("이미 존재하는 사용자 ID입니다.");

        verify(userMapper).checkUserExists("existuser");
        verify(userMapper, never()).insertUser(any());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userMapper, never()).insertUserAuth(anyString(), anyString());
    }

    @Test
    void register_InsertUserFailed() {
        // Given
        RegisterRequestDto request = new RegisterRequestDto("testuser", "testpass123", "테스트사용자", "test@example.com", "01012345678", "1994-10-06");

        given(userMapper.checkUserExists("testuser")).willReturn(0);
        given(userMapper.insertUser(any(RegisterRequestDto.class))).willReturn(0);

        // When
        AuthResponseDto result = authService.register(request);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("회원가입에 실패했습니다.");

        verify(userMapper).checkUserExists("testuser");
        verify(userMapper).insertUser(request);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userMapper, never()).insertUserAuth(anyString(), anyString());
    }

    @Test
    void register_InsertUserAuthFailed() {
        // Given
        RegisterRequestDto request = new RegisterRequestDto("testuser", "testpass123", "테스트사용자", "test@example.com", "01012345678", "1994-10-06");

        given(userMapper.checkUserExists("testuser")).willReturn(0);
        given(userMapper.insertUser(any(RegisterRequestDto.class))).willReturn(1);
        given(passwordEncoder.encode("testpass123")).willReturn("encodedPassword");
        given(userMapper.insertUserAuth("testuser", "encodedPassword")).willReturn(0);

        // When
        AuthResponseDto result = authService.register(request);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("회원가입 처리 중 오류가 발생했습니다.");

        verify(userMapper).checkUserExists("testuser");
        verify(userMapper).insertUser(request);
        verify(passwordEncoder).encode("testpass123");
        verify(userMapper).insertUserAuth("testuser", "encodedPassword");
    }

    @Test
    void login_Success() {
        // Given
        LoginRequestDto request = new LoginRequestDto("testuser", "testpass123");
        UserInfoDto userInfo = new UserInfoDto("testuser", "테스트사용자", "test@example.com", "01012345678", LocalDate.of(1994, 10, 6), LocalDateTime.now());

        given(userMapper.selectUserPassword("testuser")).willReturn("encodedPassword");
        given(passwordEncoder.matches("testpass123", "encodedPassword")).willReturn(true);
        given(userMapper.selectUserInfo("testuser")).willReturn(userInfo);
        given(jwtTokenUtil.generateToken("testuser")).willReturn("mockToken");

        // When
        AuthResponseDto result = authService.login(request);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessage()).isEqualTo("로그인이 성공적으로 완료되었습니다.");
        assertThat(result.getToken()).isEqualTo("mockToken");
        assertThat(result.getData()).isEqualTo(userInfo);

        verify(userMapper).selectUserPassword("testuser");
        verify(passwordEncoder).matches("testpass123", "encodedPassword");
        verify(userMapper).selectUserInfo("testuser");
        verify(userMapper).updateLastLoginTime("testuser");
        verify(jwtTokenUtil).generateToken("testuser");
    }

    @Test
    void login_UserNotFound() {
        // Given
        LoginRequestDto request = new LoginRequestDto("nonexist", "password123");
        given(userMapper.selectUserPassword("nonexist")).willReturn(null);

        // When
        AuthResponseDto result = authService.login(request);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("존재하지 않는 사용자입니다.");

        verify(userMapper).selectUserPassword("nonexist");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userMapper, never()).selectUserInfo(anyString());
        verify(jwtTokenUtil, never()).generateToken(anyString());
    }

    @Test
    void login_WrongPassword() {
        // Given
        LoginRequestDto request = new LoginRequestDto("testuser", "wrongpass");
        given(userMapper.selectUserPassword("testuser")).willReturn("encodedPassword");
        given(passwordEncoder.matches("wrongpass", "encodedPassword")).willReturn(false);

        // When
        AuthResponseDto result = authService.login(request);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("비밀번호가 일치하지 않습니다.");

        verify(userMapper).selectUserPassword("testuser");
        verify(passwordEncoder).matches("wrongpass", "encodedPassword");
        verify(userMapper, never()).selectUserInfo(anyString());
        verify(jwtTokenUtil, never()).generateToken(anyString());
    }

    @Test
    void login_FirstLogin() {
        // Given
        LoginRequestDto request = new LoginRequestDto("testuser", "testpass123");
        UserInfoDto userInfoBeforeUpdate = new UserInfoDto("testuser", "테스트사용자", "test@example.com", "01012345678", LocalDate.of(1994, 10, 6), null);
        UserInfoDto userInfoAfterUpdate = new UserInfoDto("testuser", "테스트사용자", "test@example.com", "01012345678", LocalDate.of(1994, 10, 6), LocalDateTime.now());

        given(userMapper.selectUserPassword("testuser")).willReturn("encodedPassword");
        given(passwordEncoder.matches("testpass123", "encodedPassword")).willReturn(true);
        given(userMapper.selectUserInfo("testuser"))
                .willReturn(userInfoBeforeUpdate)
                .willReturn(userInfoAfterUpdate);
        given(jwtTokenUtil.generateToken("testuser")).willReturn("mockToken");

        // When
        AuthResponseDto result = authService.login(request);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessage()).isEqualTo("로그인이 성공적으로 완료되었습니다.");
        assertThat(result.getToken()).isEqualTo("mockToken");

        verify(userMapper).selectUserPassword("testuser");
        verify(passwordEncoder).matches("testpass123", "encodedPassword");
        verify(userMapper, times(2)).selectUserInfo("testuser");
        verify(userMapper).updateFirstLoginTime("testuser");
        verify(userMapper).updateLastLoginTime("testuser");
        verify(jwtTokenUtil).generateToken("testuser");
    }
}