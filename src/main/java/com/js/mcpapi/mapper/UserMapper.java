package com.js.mcpapi.mapper;

import com.js.mcpapi.dto.UserInfoDto;
import com.js.mcpapi.dto.RegisterRequestDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    UserInfoDto selectUserInfo(@Param("userId") String userId);

    UserInfoDto selectUserInfoByLoginId(@Param("userLoginId") String userLoginId);

    int insertUser(RegisterRequestDto registerRequest);

    int insertUserAuth(@Param("userId") Long userId, @Param("password") String password);

    int checkUserExists(@Param("userId") String userId);

    String selectUserPassword(@Param("userId") String userId);

    int updateFirstLoginTime(@Param("userId") String userId);

    int updateLastLoginTime(@Param("userId") String userId);
}