package com.exam.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("wrong_question")
public class WrongQuestion {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long examId;

    private Long questionId;

    private Long bankId;

    private String questionContent;

    private Integer questionType;

    private String questionOptions;

    private String correctAnswer;

    private String studentAnswer;

    private String knowledgePoint;

    private Integer difficulty;

    private String analysis;

    private LocalDateTime wrongTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}