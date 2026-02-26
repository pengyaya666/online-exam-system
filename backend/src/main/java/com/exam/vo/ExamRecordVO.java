package com.exam.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamRecordVO {
    
    private Long id;
    
    private Long examId;
    
    private String examTitle;
    
    private Integer duration;
    
    private Integer totalScore;
    
    private Integer passScore;
    
    private Long studentId;
    
    private String studentName;
    
    private String realName;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Integer useTime;
    
    private Integer status;
    
    private String statusName;
    
    private Integer switchCount;
    
    private Integer score;
    
    private Integer objectiveScore;
    
    private Integer isPass;
    
    private String passStatus;
    
    private LocalDateTime createTime;
    
    private List<AnswerRecordVO> answers;
}
