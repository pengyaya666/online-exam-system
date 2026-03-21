package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.dto.SysConfigDTO;
import com.exam.entity.SysConfig;
import com.exam.mapper.SysConfigMapper;
import com.exam.service.SysConfigService;
import com.exam.vo.PageResult;
import com.exam.vo.SysConfigVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Override
    public PageResult<SysConfigVO> getConfigPage(Integer page, Integer size, String keyword) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysConfig::getConfigKey, keyword)
                    .or()
                    .like(SysConfig::getDescription, keyword);
        }

        wrapper.orderByDesc(SysConfig::getCreateTime);

        Page<SysConfig> pageResult = baseMapper.selectPage(new Page<>(page, size), wrapper);

        List<SysConfigVO> records = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(pageResult.getTotal(), records, pageResult.getCurrent(), pageResult.getSize());
    }

    @Override
    public SysConfigVO getConfigById(Long id) {
        SysConfig config = baseMapper.selectById(id);
        if (config == null) {
            throw new RuntimeException("配置不存在");
        }
        return convertToVO(config);
    }

    @Override
    public void saveConfig(SysConfigDTO dto) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, dto.getConfigKey());
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("配置键已存在");
        }

        SysConfig config = new SysConfig();
        BeanUtils.copyProperties(dto, config);
        baseMapper.insert(config);
    }

    @Override
    public void updateConfig(SysConfigDTO dto) {
        SysConfig existConfig = baseMapper.selectById(dto.getId());
        if (existConfig == null) {
            throw new RuntimeException("配置不存在");
        }

        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, dto.getConfigKey())
                .ne(SysConfig::getId, dto.getId());
        if (baseMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("配置键已存在");
        }

        BeanUtils.copyProperties(dto, existConfig);
        baseMapper.updateById(existConfig);
    }

    @Override
    public void deleteConfig(Long id) {
        baseMapper.deleteById(id);
    }

    @Override
    public Map<String, String> getAllConfig() {
        List<SysConfig> list = baseMapper.selectList(null);
        Map<String, String> configMap = new HashMap<>();
        for (SysConfig config : list) {
            configMap.put(config.getConfigKey(), config.getConfigValue());
        }
        return configMap;
    }

    private SysConfigVO convertToVO(SysConfig config) {
        SysConfigVO vo = new SysConfigVO();
        BeanUtils.copyProperties(config, vo);
        return vo;
    }
}