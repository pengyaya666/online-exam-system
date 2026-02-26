package com.exam.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamDetailVO {
    
    private Long id;
    
    private String title;
    
    private String description;
    
    private Long bankId;
    
    private String bankName;
    
    private Integer duration;
    
    private Integer totalScore;
    
    private Integer passScore;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Integer status;
    
    private String statusName;
    
    private Long createBy;
    
    private String createByName;
    
    private LocalDateTime createTime;
    
    private List<QuestionVO> questions;
    
    private Integer questionCount;
    
    private Integer isParticipated;
    
    private Integer participatedCount;
}
