package com.exam.vo;

import lombok.Data;

@Data
public class AnswerRecordVO {
    
    private Long id;
    
    private Long questionId;
    
    private QuestionVO question;
    
    private String answer;
    
    private String correctAnswer;
    
    private Integer isCorrect;
    
    private String correctStatus;
    
    private Integer score;
    
    private Integer fullScore;
}
