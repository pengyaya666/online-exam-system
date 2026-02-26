package com.exam.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {
    
    private Long id;
    
    private String username;
    
    private String realName;
    
    private String email;
    
    private String phone;
    
    private Integer role;
    
    private String roleName;
    
    private Integer status;
    
    private String statusName;
    
    private LocalDateTime createTime;
}
