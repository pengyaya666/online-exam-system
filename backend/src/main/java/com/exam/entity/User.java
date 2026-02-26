package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class User {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    private String realName;
    
    private String email;
    
    private String phone;
    
    private Integer role;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
    
    public static final int ROLE_ADMIN = 0;
    public static final int ROLE_TEACHER = 1;
    public static final int ROLE_STUDENT = 2;
    
    public static final int STATUS_ENABLE = 1;
    public static final int STATUS_DISABLE = 0;
}
