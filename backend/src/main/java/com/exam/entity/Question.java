package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("question")
public class Question {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long bankId;
    
    @TableField(exist = false)
    private String bankName;
    
    private Integer type;
    
    private String content;
    
    private String options;
    
    private String answer;
    
    private Integer score;
    
    private String analysis;
    
    private String knowledgePoint;
    
    private Integer difficulty;
    
    private Long createBy;
    
    @TableField(exist = false)
    private String createByName;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
    
    public static final int TYPE_SINGLE = 1;
    public static final int TYPE_MULTIPLE = 2;
    public static final int TYPE_JUDGE = 3;
    public static final int TYPE_FILL = 4;
    
    public static final int DIFFICULTY_EASY = 1;
    public static final int DIFFICULTY_MEDIUM = 2;
    public static final int DIFFICULTY_HARD = 3;
}
