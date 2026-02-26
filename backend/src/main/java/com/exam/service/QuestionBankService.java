package com.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.entity.QuestionBank;
import com.exam.vo.PageResult;

import java.util.List;

public interface QuestionBankService extends IService<QuestionBank> {
    
    void createBank(QuestionBank bank, Long userId);
    
    void updateBank(QuestionBank bank);
    
    void deleteBank(Long id);
    
    QuestionBank getBankDetail(Long id);
    
    PageResult<QuestionBank> getBankPage(Integer page, Integer size, String keyword, Long createBy);
    
    List<QuestionBank> getBankList(Long createBy);
}
