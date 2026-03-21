package com.exam.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysNoticeDTO {
    private Long id;
    private String title;
    private String content;
    private Integer type;
    private Integer status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}