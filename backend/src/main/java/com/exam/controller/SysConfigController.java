package com.exam.controller;

import com.exam.dto.SysConfigDTO;
import com.exam.service.SysConfigService;
import com.exam.vo.PageResult;
import com.exam.vo.Result;
import com.exam.vo.SysConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/config")
public class SysConfigController {

    @Autowired
    private SysConfigService sysConfigService;

    @GetMapping("/page")
    public Result<PageResult<SysConfigVO>> getConfigPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {

        PageResult<SysConfigVO> result = sysConfigService.getConfigPage(page, size, keyword);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<SysConfigVO> getConfigById(@PathVariable Long id) {
        SysConfigVO vo = sysConfigService.getConfigById(id);
        return Result.success(vo);
    }

    @GetMapping("/all")
    public Result<Map<String, String>> getAllConfig() {
        Map<String, String> config = sysConfigService.getAllConfig();
        return Result.success(config);
    }

    @PostMapping
    public Result<Void> saveConfig(@RequestBody SysConfigDTO dto) {
        sysConfigService.saveConfig(dto);
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<Void> updateConfig(@RequestBody SysConfigDTO dto) {
        sysConfigService.updateConfig(dto);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteConfig(@PathVariable Long id) {
        sysConfigService.deleteConfig(id);
        return Result.success("删除成功");
    }
}