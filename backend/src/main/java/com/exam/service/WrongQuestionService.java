package com.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.entity.WrongQuestion;
import com.exam.vo.PageResult;
import com.exam.vo.WrongQuestionVO;

public interface WrongQuestionService extends IService<WrongQuestion> {

    PageResult<WrongQuestionVO> getWrongQuestionPage(Integer page, Integer size, Long studentId, Long bankId);

    void deleteByStudentAndQuestion(Long studentId, Long questionId);

    Integer getWrongCountByStudent(Long studentId);
}