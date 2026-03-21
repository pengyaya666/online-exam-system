package com.exam.dto;

import lombok.Data;

@Data
public class SysConfigDTO {
    private Long id;
    private String configKey;
    private String configValue;
    private String description;
}