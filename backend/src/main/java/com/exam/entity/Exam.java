package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("exam")
public class Exam {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String description;
    
    private Long bankId;
    
    @TableField(exist = false)
    private String bankName;
    
    private Integer duration;
    
    private Integer totalScore;
    
    private Integer passScore;
    
    private Integer singleCount;
    
    private Integer singleScore;
    
    private Integer multipleCount;
    
    private Integer multipleScore;
    
    private Integer judgeCount;
    
    private Integer judgeScore;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Integer allowMultiple;
    
    private Integer randomOrder;
    
    private Integer status;
    
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
    
    public static final int STATUS_DRAFT = 0;
    public static final int STATUS_PUBLISHED = 1;
    public static final int STATUS_ENDED = 2;
}
