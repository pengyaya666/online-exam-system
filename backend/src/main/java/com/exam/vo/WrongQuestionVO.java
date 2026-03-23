package com.exam.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WrongQuestionVO {
    private Long id;
    private Long studentId;
    private Long examId;
    private String examTitle;
    private Long questionId;
    private Long bankId;
    private String bankName;
    private String questionContent;
    private Integer questionType;
    private String questionTypeName;
    private String questionOptions;
    private String correctAnswer;
    private String studentAnswer;
    private String knowledgePoint;
    private Integer difficulty;
    private String difficultyName;
    private String analysis;
    private LocalDateTime wrongTime;
}