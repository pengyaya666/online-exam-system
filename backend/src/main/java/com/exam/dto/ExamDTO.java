package com.exam.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ExamDTO {
    
    private Long id;
    
    @NotBlank(message = "考试标题不能为空")
    private String title;
    
    private String description;
    
    @NotNull(message = "题库ID不能为空")
    private Long bankId;
    
    @NotNull(message = "考试时长不能为空")
    private Integer duration;
    
    @NotNull(message = "总分不能为空")
    private Integer totalScore;
    
    @NotNull(message = "及格分数不能为空")
    private Integer passScore;
    
    @NotNull(message = "单选题数量不能为空")
    private Integer singleCount;
    
    @NotNull(message = "单选题分值不能为空")
    private Integer singleScore;
    
    @NotNull(message = "多选题数量不能为空")
    private Integer multipleCount;
    
    @NotNull(message = "多选题分值不能为空")
    private Integer multipleScore;
    
    @NotNull(message = "判断题数量不能为空")
    private Integer judgeCount;
    
    @NotNull(message = "判断题分值不能为空")
    private Integer judgeScore;
    
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
    
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
    
    private Integer allowMultiple;
    
    private Integer randomOrder;
}
