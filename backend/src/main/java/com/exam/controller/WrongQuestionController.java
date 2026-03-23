package com.exam.controller;

import com.exam.entity.WrongQuestion;
import com.exam.service.WrongQuestionService;
import com.exam.vo.PageResult;
import com.exam.vo.Result;
import com.exam.vo.WrongQuestionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/wrong")
public class WrongQuestionController {

    @Autowired
    private WrongQuestionService wrongQuestionService;

    @GetMapping("/page")
    public Result<PageResult<WrongQuestionVO>> getWrongQuestionPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long bankId,
            HttpServletRequest request) {

        Long studentId = (Long) request.getAttribute("userId");

        PageResult<WrongQuestionVO> result = wrongQuestionService.getWrongQuestionPage(page, size, studentId, bankId);
        return Result.success(result);
    }

    @GetMapping("/count")
    public Result<Integer> getWrongCount(HttpServletRequest request) {
        Long studentId = (Long) request.getAttribute("userId");
        Integer count = wrongQuestionService.getWrongCountByStudent(studentId);
        return Result.success(count);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteWrongQuestion(@PathVariable Long id, HttpServletRequest request) {
        Long studentId = (Long) request.getAttribute("userId");
        WrongQuestion wrong = wrongQuestionService.getById(id);
        if (wrong == null || !wrong.getStudentId().equals(studentId)) {
            return Result.error("无权操作");
        }
        wrongQuestionService.removeById(id);
        return Result.success("删除成功");
    }

    @DeleteMapping("/clear")
    public Result<Void> clearWrongQuestions(HttpServletRequest request) {
        Long studentId = (Long) request.getAttribute("userId");
        wrongQuestionService.remove(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.exam.entity.WrongQuestion>()
                        .eq(com.exam.entity.WrongQuestion::getStudentId, studentId)
        );
        return Result.success("清空成功");
    }
}