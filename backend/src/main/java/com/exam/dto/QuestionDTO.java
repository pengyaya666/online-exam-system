package com.exam.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class QuestionDTO {
    
    private Long id;
    
    @NotNull(message = "题库ID不能为空")
    private Long bankId;
    
    @NotNull(message = "题目类型不能为空")
    private Integer type;
    
    @NotBlank(message = "题目内容不能为空")
    private String content;
    
    private String options;
    
    @NotBlank(message = "答案不能为空")
    private String answer;
    
    @NotNull(message = "分值不能为空")
    private Integer score;
    
    private String analysis;
    
    private String knowledgePoint;
    
    @NotNull(message = "难度不能为空")
    private Integer difficulty;
}
