package com.exam.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysNoticeVO {
    private Long id;
    private String title;
    private String content;
    private Integer type;
    private String typeName;
    private Integer status;
    private String statusName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long createBy;
    private String createByName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}