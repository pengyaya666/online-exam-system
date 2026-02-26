package com.exam.controller;

import com.exam.dto.ExamDTO;
import com.exam.dto.SubmitExamDTO;
import com.exam.entity.User;
import com.exam.service.ExamService;
import com.exam.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/exam")
public class ExamController {
    
    @Autowired
    private ExamService examService;
    
    @GetMapping("/page")
    public Result<PageResult<ExamDetailVO>> getExamPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        
        Integer role = (Integer) request.getAttribute("role");
        Long userId = (Long) request.getAttribute("userId");
        
        // 教师只能看到自己创建的考试，管理员可以看到所有
        Long createBy = (role != null && role == User.ROLE_TEACHER) ? userId : null;
        
        PageResult<ExamDetailVO> result = examService.getExamPage(page, size, status, keyword, createBy);
        return Result.success(result);
    }
    
    @GetMapping("/active")
    public Result<List<ExamDetailVO>> getActiveExams(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<ExamDetailVO> list = examService.getActiveExams(userId);
        return Result.success(list);
    }
    
    @GetMapping("/{id}")
    public Result<ExamDetailVO> getExamById(@PathVariable Long id, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        Long userId = (Long) request.getAttribute("userId");
        
        ExamDetailVO examDetailVO;
        if (role != null && role == User.ROLE_STUDENT) {
            examDetailVO = examService.getExamDetailForStudent(id, userId);
        } else {
            examDetailVO = examService.getExamDetail(id);
        }
        return Result.success(examDetailVO);
    }
    
    @PostMapping
    public Result<Void> createExam(@RequestBody ExamDTO examDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        examService.createExam(examDTO, userId);
        return Result.success("创建成功");
    }
    
    @PutMapping
    public Result<Void> updateExam(@RequestBody ExamDTO examDTO) {
        examService.updateExam(examDTO);
        return Result.success("更新成功");
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return Result.success("删除成功");
    }
    
    @PutMapping("/{id}/publish")
    public Result<Void> publishExam(@PathVariable Long id) {
        examService.publishExam(id);
        return Result.success("发布成功");
    }
    
    @PostMapping("/{id}/start")
    public Result<ExamRecordVO> startExam(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ExamRecordVO recordVO = examService.startExam(id, userId);
        return Result.success("开始考试", recordVO);
    }
    
    @PostMapping("/submit")
    public Result<Void> submitExam(@RequestBody SubmitExamDTO submitDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        examService.submitExam(submitDTO, userId);
        return Result.success("提交成功");
    }
    
    @GetMapping("/record/{recordId}")
    public Result<ExamRecordVO> getExamResult(@PathVariable Long recordId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        ExamRecordVO recordVO = examService.getExamResult(recordId, userId);
        return Result.success(recordVO);
    }
    
    @GetMapping("/records")
    public Result<PageResult<ExamRecordVO>> getExamRecords(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long examId,
            HttpServletRequest request) {
        
        Integer role = (Integer) request.getAttribute("role");
        Long userId = (Long) request.getAttribute("userId");
        
        // 学生只能看到自己的记录
        Long studentId = (role != null && role == User.ROLE_STUDENT) ? userId : null;
        
        PageResult<ExamRecordVO> result = examService.getExamRecords(page, size, examId, studentId);
        return Result.success(result);
    }
    
    @GetMapping("/{id}/statistics")
    public Result<ScoreStatisticsVO> getScoreStatistics(@PathVariable Long id) {
        ScoreStatisticsVO statistics = examService.getScoreStatistics(id);
        return Result.success(statistics);
    }
}
