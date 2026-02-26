package com.exam.controller;

import com.exam.entity.QuestionBank;
import com.exam.entity.User;
import com.exam.service.QuestionBankService;
import com.exam.vo.PageResult;
import com.exam.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/bank")
public class QuestionBankController {
    
    @Autowired
    private QuestionBankService bankService;
    
    @GetMapping("/page")
    public Result<PageResult<QuestionBank>> getBankPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        
        Integer role = (Integer) request.getAttribute("role");
        Long userId = (Long) request.getAttribute("userId");
        
        // 教师只能看到自己的题库，管理员可以看到所有
        Long createBy = (role != null && role == User.ROLE_TEACHER) ? userId : null;
        
        PageResult<QuestionBank> result = bankService.getBankPage(page, size, keyword, createBy);
        return Result.success(result);
    }
    
    @GetMapping("/list")
    public Result<List<QuestionBank>> getBankList(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("role");
        Long userId = (Long) request.getAttribute("userId");
        
        Long createBy = (role != null && role == User.ROLE_TEACHER) ? userId : null;
        
        List<QuestionBank> list = bankService.getBankList(createBy);
        return Result.success(list);
    }
    
    @GetMapping("/{id}")
    public Result<QuestionBank> getBankById(@PathVariable Long id) {
        QuestionBank bank = bankService.getBankDetail(id);
        return Result.success(bank);
    }
    
    @PostMapping
    public Result<Void> createBank(@RequestBody QuestionBank bank, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        bankService.createBank(bank, userId);
        return Result.success("创建成功");
    }
    
    @PutMapping
    public Result<Void> updateBank(@RequestBody QuestionBank bank) {
        bankService.updateBank(bank);
        return Result.success("更新成功");
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> deleteBank(@PathVariable Long id) {
        bankService.deleteBank(id);
        return Result.success("删除成功");
    }
}
