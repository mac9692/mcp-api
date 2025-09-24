package com.js.mcpapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.js.mcpapi.dto.*;
import com.js.mcpapi.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_Success() throws Exception {
        // Given
        RegisterRequestDto request = new RegisterRequestDto("testuser", "testpass123", "테스트사용자", "test@example.com", "01012345678", "1994-10-06");
        AuthResponseDto mockResponse = AuthResponseDto.builder()
                .success(true)
                .message("회원가입이 성공적으로 완료되었습니다.")
                .build();

        given(authService.register(any(RegisterRequestDto.class))).willReturn(mockResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원가입이 성공적으로 완료되었습니다."));
    }

    @Test
    void register_AlreadyExists() throws Exception {
        // Given
        RegisterRequestDto request = new RegisterRequestDto("existuser", "password123", "기존사용자", "exist@example.com", "01087654321", "1990-01-01");
        AuthResponseDto mockResponse = AuthResponseDto.builder()
                .success(false)
                .message("이미 존재하는 사용자 ID입니다.")
                .build();

        given(authService.register(any(RegisterRequestDto.class))).willReturn(mockResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("이미 존재하는 사용자 ID입니다."));
    }

    @Test
    void login_Success() throws Exception {
        // Given
        LoginRequestDto request = new LoginRequestDto("testuser", "testpass123");
        UserInfoDto userInfo = new UserInfoDto("testuser", "테스트사용자", "test@example.com", "01012345678", LocalDate.of(1994, 10, 6), null);
        AuthResponseDto mockResponse = AuthResponseDto.builder()
                .success(true)
                .message("로그인이 성공적으로 완료되었습니다.")
                .token("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTYzMzY0MDQwMCwiZXhwIjoxNjMzNjQ0MDAwfQ.test")
                .data(userInfo)
                .build();

        given(authService.login(any(LoginRequestDto.class))).willReturn(mockResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그인이 성공적으로 완료되었습니다."))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.data.userId").value("testuser"))
                .andExpect(jsonPath("$.data.userNm").value("테스트사용자"));
    }

    @Test
    void login_UserNotFound() throws Exception {
        // Given
        LoginRequestDto request = new LoginRequestDto("nonexist", "password123");
        AuthResponseDto mockResponse = AuthResponseDto.builder()
                .success(false)
                .message("존재하지 않는 사용자입니다.")
                .build();

        given(authService.login(any(LoginRequestDto.class))).willReturn(mockResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("존재하지 않는 사용자입니다."));
    }

    @Test
    void login_WrongPassword() throws Exception {
        // Given
        LoginRequestDto request = new LoginRequestDto("testuser", "wrongpass");
        AuthResponseDto mockResponse = AuthResponseDto.builder()
                .success(false)
                .message("비밀번호가 일치하지 않습니다.")
                .build();

        given(authService.login(any(LoginRequestDto.class))).willReturn(mockResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."));
    }
}