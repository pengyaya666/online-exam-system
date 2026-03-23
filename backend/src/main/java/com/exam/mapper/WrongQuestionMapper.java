package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.WrongQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Delete;

@Mapper
public interface WrongQuestionMapper extends BaseMapper<WrongQuestion> {

    @Delete("DELETE FROM wrong_question WHERE student_id = #{studentId} AND question_id = #{questionId}")
    int deleteByStudentAndQuestion(@Param("studentId") Long studentId, @Param("questionId") Long questionId);
}