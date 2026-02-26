package com.exam.vo;

import lombok.Data;

@Data
public class LoginVO {
    
    private String token;
    
    private String refreshToken;
    
    private Long expireTime;
    
    private UserVO userInfo;
}
