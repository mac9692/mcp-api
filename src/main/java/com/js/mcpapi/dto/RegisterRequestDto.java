package com.js.mcpapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
    private Long userId; // 시퀀스 기반 내부 ID (자동 생성)
    private String userLoginId; // 로그인 계정 ID
    private String password;
    private String userNm;
    private String userEmail;
    private String userPhone;
    private String userBirthDt;
}