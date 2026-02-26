package com.exam.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ScoreStatisticsVO {
    
    private Long examId;
    
    private String examTitle;
    
    private Integer totalCount;
    
    private Integer submittedCount;
    
    private Double averageScore;
    
    private Integer maxScore;
    
    private Integer minScore;
    
    private Double passRate;
    
    private List<Map<String, Object>> scoreDistribution;
    
    private List<Map<String, Object>> questionCorrectRate;
}
