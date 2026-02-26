package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("exam_record")
public class ExamRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long examId;
    
    @TableField(exist = false)
    private Exam exam;
    
    private Long studentId;
    
    @TableField(exist = false)
    private String studentName;
    
    @TableField(exist = false)
    private String realName;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Integer status;
    
    private Integer switchCount;
    
    private Integer score;
    
    private Integer objectiveScore;
    
    private Integer subjectiveScore;
    
    private Integer isPass;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    public static final int STATUS_NOT_STARTED = 0;
    public static final int STATUS_IN_PROGRESS = 1;
    public static final int STATUS_SUBMITTED = 2;
    public static final int STATUS_AUTO_GRADED = 3;
    public static final int STATUS_MANUAL_GRADED = 4;
}
