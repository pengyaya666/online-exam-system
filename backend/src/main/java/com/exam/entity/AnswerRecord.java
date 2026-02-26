package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("answer_record")
public class AnswerRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long recordId;
    
    private Long examId;
    
    private Long questionId;
    
    @TableField(exist = false)
    private Question question;
    
    private String answer;
    
    private Integer isCorrect;
    
    private Integer score;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    public static final int CORRECT_YES = 1;
    public static final int CORRECT_NO = 0;
    public static final int CORRECT_UNGRADED = -1;
}
