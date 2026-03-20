package com.exam.dto;

import lombok.Data;
import java.util.List;

@Data
public class SubmitExamDTO {

    private Long recordId;

    private List<AnswerDTO> answers;

    private Integer switchCount;  // 切屏次数

    @Data
    public static class AnswerDTO {
        private Long questionId;
        private String answer;
    }
}