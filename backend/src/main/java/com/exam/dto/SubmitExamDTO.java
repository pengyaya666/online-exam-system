package com.exam.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubmitExamDTO {
    
    private Long recordId;
    
    private List<AnswerDTO> answers;
    
    @Data
    public static class AnswerDTO {
        private Long questionId;
        private String answer;
    }
}
