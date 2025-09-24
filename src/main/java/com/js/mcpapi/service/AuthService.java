package com.js.mcpapi.service;

import com.js.mcpapi.dto.*;
import com.js.mcpapi.mapper.UserMapper;
import com.js.mcpapi.util.JwtTokenUtil;
import com.js.mcpapi.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {
        try {
            log.info("회원가입 요청: userLoginId={}, userNm={}, userEmail={}",
                    request.getUserLoginId(), request.getUserNm(), request.getUserEmail());

            // userLoginId로 중복 체크
            if (userMapper.checkUserExists(request.getUserLoginId()) > 0) {
                log.warn("중복된 사용자 ID로 회원가입 시도: {}", request.getUserLoginId());
                return AuthResponseDto.builder()
                        .success(false)
                        .message("이미 존재하는 사용자 ID입니다.")
                        .build();
            }

            // 비밀번호 미리 암호화
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            log.debug("비밀번호 암호화 완료");

            // 사용자 기본 정보 생성
            int userResult = userMapper.insertUser(request);
            log.debug("사용자 기본 정보 생성 결과: {}, 생성된 userId: {}", userResult, request.getUserId());

            if (userResult > 0) {
                // 생성된 userId(sequence)를 사용해서 auth 테이블에 삽입
                int authResult = userMapper.insertUserAuth(request.getUserId(), encodedPassword);
                log.debug("사용자 인증 정보 생성 결과: {}", authResult);

                if (authResult > 0) {
                    log.info("회원가입 성공: userId={}, userLoginId={}", request.getUserId(), request.getUserLoginId());
                    return AuthResponseDto.builder()
                            .success(true)
                            .message("회원가입이 성공적으로 완료되었습니다.")
                            .build();
                } else {
                    log.error("인증 정보 생성 실패: userId={}", request.getUserId());
                    throw new RuntimeException("인증 정보 생성에 실패했습니다.");
                }
            } else {
                log.error("사용자 기본 정보 생성 실패: userLoginId={}", request.getUserLoginId());
                return AuthResponseDto.builder()
                        .success(false)
                        .message("회원가입에 실패했습니다.")
                        .build();
            }

        } catch (Exception e) {
            log.error("회원가입 처리 중 오류 발생: userLoginId={}, error={}",
                    request.getUserLoginId(), e.getMessage(), e);
            return AuthResponseDto.builder()
                    .success(false)
                    .message("회원가입 처리 중 오류가 발생했습니다.")
                    .build();
        }
    }

    @Transactional
    public AuthResponseDto login(LoginRequestDto request) {
        try {
            log.info("로그인 요청: userLoginId={}", request.getUserLoginId());

            // userLoginId로 비밀번호 조회
            String storedPassword = userMapper.selectUserPassword(request.getUserLoginId());

            if (storedPassword == null) {
                log.warn("존재하지 않는 사용자로 로그인 시도: {}", request.getUserLoginId());
                return AuthResponseDto.builder()
                        .success(false)
                        .message("존재하지 않는 사용자입니다.")
                        .build();
            }

            if (!passwordEncoder.matches(request.getPassword(), storedPassword)) {
                log.warn("비밀번호 불일치: {}", request.getUserLoginId());
                return AuthResponseDto.builder()
                        .success(false)
                        .message("비밀번호가 일치하지 않습니다.")
                        .build();
            }

            // userLoginId로 사용자 정보 조회
            UserInfoDto userInfo = userMapper.selectUserInfoByLoginId(request.getUserLoginId());
            log.debug("사용자 정보 조회 완료: userId={}", userInfo.getUserId());

            if (userInfo.getUserFirstLoginDtm() == null) {
                log.debug("첫 로그인 시간 업데이트: {}", request.getUserLoginId());
                userMapper.updateFirstLoginTime(request.getUserLoginId());
                userInfo = userMapper.selectUserInfoByLoginId(request.getUserLoginId());
            }

            userMapper.updateLastLoginTime(request.getUserLoginId());
            log.debug("마지막 로그인 시간 업데이트 완료");

            String userId = String.valueOf(userInfo.getUserId());
            log.info("JWT 토큰에 들어갈 userId: {}", userId);
            String token = jwtTokenUtil.generateToken(userId);
            log.info("로그인 성공: userLoginId={}, 토큰에 포함된 userId={}", request.getUserLoginId(), userId);

            return AuthResponseDto.builder()
                    .success(true)
                    .message("로그인이 성공적으로 완료되었습니다.")
                    .token(token)
                    .data(userInfo)
                    .build();

        } catch (Exception e) {
            log.error("로그인 처리 중 오류 발생: userLoginId={}, error={}",
                    request.getUserLoginId(), e.getMessage(), e);
            return AuthResponseDto.builder()
                    .success(false)
                    .message("로그인 처리 중 오류가 발생했습니다.")
                    .build();
        }
    }

    public AuthResponseDto logout() {
        try {
            log.info("로그아웃 요청");

            return AuthResponseDto.builder()
                    .success(true)
                    .message("로그아웃이 성공적으로 완료되었습니다.")
                    .build();

        } catch (Exception e) {
            log.error("로그아웃 처리 중 오류 발생: error={}", e.getMessage(), e);
            return AuthResponseDto.builder()
                    .success(false)
                    .message("로그아웃 처리 중 오류가 발생했습니다.")
                    .build();
        }
    }
}