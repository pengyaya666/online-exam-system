package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.entity.QuestionBank;
import com.exam.entity.User;
import com.exam.mapper.QuestionBankMapper;
import com.exam.mapper.UserMapper;
import com.exam.service.QuestionBankService;
import com.exam.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank> implements QuestionBankService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public void createBank(QuestionBank bank, Long userId) {
        bank.setCreateBy(userId);
        baseMapper.insert(bank);
    }
    
    @Override
    public void updateBank(QuestionBank bank) {
        QuestionBank existBank = baseMapper.selectById(bank.getId());
        if (existBank == null) {
            throw new RuntimeException("题库不存在");
        }
        
        baseMapper.updateById(bank);
    }
    
    @Override
    public void deleteBank(Long id) {
        baseMapper.deleteById(id);
    }
    
    @Override
    public QuestionBank getBankDetail(Long id) {
        QuestionBank bank = baseMapper.selectById(id);
        if (bank == null) {
            throw new RuntimeException("题库不存在");
        }
        
        User user = userMapper.selectById(bank.getCreateBy());
        if (user != null) {
            bank.setCreateByName(user.getRealName());
        }
        
        return bank;
    }
    
    @Override
    public PageResult<QuestionBank> getBankPage(Integer page, Integer size, String keyword, Long createBy) {
        LambdaQueryWrapper<QuestionBank> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(QuestionBank::getName, keyword);
        }
        
        if (createBy != null) {
            wrapper.eq(QuestionBank::getCreateBy, createBy);
        }
        
        wrapper.orderByDesc(QuestionBank::getCreateTime);
        
        Page<QuestionBank> pageResult = baseMapper.selectPage(new Page<>(page, size), wrapper);
        
        List<QuestionBank> records = pageResult.getRecords().stream()
                .map(bank -> {
                    User user = userMapper.selectById(bank.getCreateBy());
                    if (user != null) {
                        bank.setCreateByName(user.getRealName());
                    }
                    return bank;
                })
                .collect(Collectors.toList());
        
        return new PageResult<>(pageResult.getTotal(), records, pageResult.getCurrent(), pageResult.getSize());
    }
    
    @Override
    public List<QuestionBank> getBankList(Long createBy) {
        LambdaQueryWrapper<QuestionBank> wrapper = new LambdaQueryWrapper<>();
        
        if (createBy != null) {
            wrapper.eq(QuestionBank::getCreateBy, createBy);
        }
        
        wrapper.orderByDesc(QuestionBank::getCreateTime);
        
        return baseMapper.selectList(wrapper).stream()
                .map(bank -> {
                    User user = userMapper.selectById(bank.getCreateBy());
                    if (user != null) {
                        bank.setCreateByName(user.getRealName());
                    }
                    return bank;
                })
                .collect(Collectors.toList());
    }
}
