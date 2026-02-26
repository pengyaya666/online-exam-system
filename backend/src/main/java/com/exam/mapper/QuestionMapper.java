package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.Question;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface QuestionMapper extends BaseMapper<Question> {
    
    @Select("SELECT * FROM question WHERE bank_id = #{bankId} AND type = #{type} AND deleted = 0 ORDER BY RAND() LIMIT #{limit}")
    List<Question> selectRandomByBankIdAndType(@Param("bankId") Long bankId, @Param("type") Integer type, @Param("limit") Integer limit);
    
    @Select("SELECT COUNT(*) FROM question WHERE bank_id = #{bankId} AND type = #{type} AND deleted = 0")
    Long countByBankIdAndType(@Param("bankId") Long bankId, @Param("type") Integer type);
    
    @Select("SELECT * FROM question WHERE bank_id = #{bankId} AND deleted = 0")
    List<Question> selectByBankId(@Param("bankId") Long bankId);
}
