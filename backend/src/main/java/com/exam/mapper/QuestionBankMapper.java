package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.QuestionBank;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface QuestionBankMapper extends BaseMapper<QuestionBank> {
    
    @Select("SELECT COUNT(*) FROM question WHERE bank_id = #{bankId} AND deleted = 0")
    Long countQuestionsByBankId(@Param("bankId") Long bankId);
}
