package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.entity.Exam;
import com.exam.entity.QuestionBank;
import com.exam.entity.WrongQuestion;
import com.exam.mapper.ExamMapper;
import com.exam.mapper.QuestionBankMapper;
import com.exam.mapper.WrongQuestionMapper;
import com.exam.service.WrongQuestionService;
import com.exam.vo.PageResult;
import com.exam.vo.WrongQuestionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WrongQuestionServiceImpl extends ServiceImpl<WrongQuestionMapper, WrongQuestion> implements WrongQuestionService {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private QuestionBankMapper bankMapper;

    @Override
    public PageResult<WrongQuestionVO> getWrongQuestionPage(Integer page, Integer size, Long studentId, Long bankId) {
        LambdaQueryWrapper<WrongQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrongQuestion::getStudentId, studentId);

        if (bankId != null) {
            wrapper.eq(WrongQuestion::getBankId, bankId);
        }

        wrapper.orderByDesc(WrongQuestion::getWrongTime);

        Page<WrongQuestion> pageResult = baseMapper.selectPage(new Page<>(page, size), wrapper);

        List<WrongQuestionVO> records = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(pageResult.getTotal(), records, pageResult.getCurrent(), pageResult.getSize());
    }

    @Override
    public void deleteByStudentAndQuestion(Long studentId, Long questionId) {
        baseMapper.deleteByStudentAndQuestion(studentId, questionId);
    }

    @Override
    public Integer getWrongCountByStudent(Long studentId) {
        LambdaQueryWrapper<WrongQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrongQuestion::getStudentId, studentId);
        return Math.toIntExact(baseMapper.selectCount(wrapper));
    }

    private WrongQuestionVO convertToVO(WrongQuestion wrong) {
        WrongQuestionVO vo = new WrongQuestionVO();
        BeanUtils.copyProperties(wrong, vo);

        // 获取考试名称
        Exam exam = examMapper.selectById(wrong.getExamId());
        if (exam != null) {
            vo.setExamTitle(exam.getTitle());
        }

        // 获取题库名称
        QuestionBank bank = bankMapper.selectById(wrong.getBankId());
        if (bank != null) {
            vo.setBankName(bank.getName());
        }

        // 设置题型名称
        if (wrong.getQuestionType() != null) {
            switch (wrong.getQuestionType()) {
                case 1:
                    vo.setQuestionTypeName("单选题");
                    break;
                case 2:
                    vo.setQuestionTypeName("多选题");
                    break;
                case 3:
                    vo.setQuestionTypeName("判断题");
                    break;
                case 4:
                    vo.setQuestionTypeName("填空题");
                    break;
                default:
                    vo.setQuestionTypeName("未知");
            }
        }

        // 设置难度名称
        if (wrong.getDifficulty() != null) {
            switch (wrong.getDifficulty()) {
                case 1:
                    vo.setDifficultyName("简单");
                    break;
                case 2:
                    vo.setDifficultyName("中等");
                    break;
                case 3:
                    vo.setDifficultyName("困难");
                    break;
                default:
                    vo.setDifficultyName("未知");
            }
        }

        return vo;
    }
}