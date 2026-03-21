package com.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.dto.SysConfigDTO;
import com.exam.entity.SysConfig;
import com.exam.vo.PageResult;
import com.exam.vo.SysConfigVO;
import java.util.Map;

public interface SysConfigService extends IService<SysConfig> {

    PageResult<SysConfigVO> getConfigPage(Integer page, Integer size, String keyword);

    SysConfigVO getConfigById(Long id);

    void saveConfig(SysConfigDTO dto);

    void updateConfig(SysConfigDTO dto);

    void deleteConfig(Long id);

    Map<String, String> getAllConfig();
}