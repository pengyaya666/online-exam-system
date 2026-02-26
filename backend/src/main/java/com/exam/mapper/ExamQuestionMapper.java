package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.ExamQuestion;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ExamQuestionMapper extends BaseMapper<ExamQuestion> {
    
    @Select("SELECT * FROM exam_question WHERE exam_id = #{examId} ORDER BY sequence")
    List<ExamQuestion> selectByExamId(@Param("examId") Long examId);
    
    @Select("DELETE FROM exam_question WHERE exam_id = #{examId}")
    void deleteByExamId(@Param("examId") Long examId);
}
