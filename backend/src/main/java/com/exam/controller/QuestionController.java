package com.exam.controller;

import com.exam.dto.QuestionDTO;
import com.exam.entity.User;
import com.exam.service.QuestionService;
import com.exam.vo.PageResult;
import com.exam.vo.QuestionVO;
import com.exam.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {
    
    @Autowired
    private QuestionService questionService;
    
    @GetMapping("/page")
    public Result<PageResult<QuestionVO>> getQuestionPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long bankId,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) String keyword) {
        
        PageResult<QuestionVO> result = questionService.getQuestionPage(page, size, bankId, type, keyword);
        return Result.success(result);
    }
    
    @GetMapping("/list")
    public Result<List<QuestionVO>> getQuestionList(@RequestParam Long bankId) {
        List<QuestionVO> list = questionService.getQuestionsByBankId(bankId);
        return Result.success(list);
    }
    
    @GetMapping("/{id}")
    public Result<QuestionVO> getQuestionById(@PathVariable Long id) {
        QuestionVO questionVO = questionService.getQuestionDetail(id);
        return Result.success(questionVO);
    }
    
    @PostMapping
    public Result<Void> addQuestion(@RequestBody QuestionDTO questionDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        questionService.addQuestion(questionDTO, userId);
        return Result.success("添加成功");
    }
    
    @PutMapping
    public Result<Void> updateQuestion(@RequestBody QuestionDTO questionDTO) {
        questionService.updateQuestion(questionDTO);
        return Result.success("更新成功");
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return Result.success("删除成功");
    }
    
    @PostMapping("/import")
    public Result<Void> batchImport(
            @RequestParam Long bankId,
            @RequestParam MultipartFile file,
            HttpServletRequest request) throws IOException {
        
        Long userId = (Long) request.getAttribute("userId");
        questionService.batchImport(bankId, file, userId);
        return Result.success("导入成功");
    }
    
    @GetMapping("/template")
    public void exportTemplate(HttpServletResponse response) throws IOException {
        questionService.exportTemplate(response);
    }
}
