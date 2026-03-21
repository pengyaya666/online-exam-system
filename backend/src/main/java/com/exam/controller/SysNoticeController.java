package com.exam.controller;

import com.exam.dto.SysNoticeDTO;
import com.exam.service.SysNoticeService;
import com.exam.vo.PageResult;
import com.exam.vo.Result;
import com.exam.vo.SysNoticeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/notice")
public class SysNoticeController {

    @Autowired
    private SysNoticeService sysNoticeService;

    @GetMapping("/page")
    public Result<PageResult<SysNoticeVO>> getNoticePage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {

        PageResult<SysNoticeVO> result = sysNoticeService.getNoticePage(page, size, status, keyword);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<SysNoticeVO> getNoticeById(@PathVariable Long id) {
        SysNoticeVO vo = sysNoticeService.getNoticeById(id);
        return Result.success(vo);
    }

    @GetMapping("/current")
    public Result<SysNoticeVO> getCurrentNotice() {
        SysNoticeVO vo = sysNoticeService.getCurrentNotice();
        return Result.success(vo);
    }

    @PostMapping
    public Result<Void> saveNotice(@RequestBody SysNoticeDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        sysNoticeService.saveNotice(dto, userId);
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<Void> updateNotice(@RequestBody SysNoticeDTO dto) {
        sysNoticeService.updateNotice(dto);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteNotice(@PathVariable Long id) {
        sysNoticeService.deleteNotice(id);
        return Result.success("删除成功");
    }
}