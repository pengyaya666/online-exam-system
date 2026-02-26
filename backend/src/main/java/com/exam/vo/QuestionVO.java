package com.exam.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionVO {
    
    private Long id;
    
    private Long bankId;
    
    private String bankName;
    
    private Integer type;
    
    private String typeName;
    
    private String content;
    
    private List<String> optionList;
    
    private String answer;
    
    private Integer score;
    
    private String analysis;
    
    private String knowledgePoint;
    
    private Integer difficulty;
    
    private String difficultyName;
    
    private Long createBy;
    
    private String createByName;
    
    private LocalDateTime createTime;
    
    private Integer sequence;
    
    private String studentAnswer;
    
    private Integer isCorrect;
}
