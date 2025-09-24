package com.js.mcpapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
    private String userId;
    private String userLoginId;
    private String userNm;
    private String userEmail;
    private String userPhone;
    private LocalDate userBirthDt;
    private LocalDateTime userFirstLoginDtm;
    private BigDecimal pointBalance;
    private BigDecimal coinBalance;
}