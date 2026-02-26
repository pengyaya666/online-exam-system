package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.AnswerRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AnswerRecordMapper extends BaseMapper<AnswerRecord> {
    
    @Select("SELECT * FROM answer_record WHERE record_id = #{recordId}")
    List<AnswerRecord> selectByRecordId(@Param("recordId") Long recordId);
    
    @Select("SELECT * FROM answer_record WHERE record_id = #{recordId} AND question_id = #{questionId}")
    AnswerRecord selectByRecordAndQuestion(@Param("recordId") Long recordId, @Param("questionId") Long questionId);
    
    @Select("SELECT COUNT(*) FROM answer_record WHERE record_id = #{recordId} AND is_correct = 1")
    Long countCorrectByRecordId(@Param("recordId") Long recordId);
    
    @Select("SELECT COUNT(*) FROM answer_record ar JOIN exam_record er ON ar.record_id = er.id " +
            "WHERE er.exam_id = #{examId} AND ar.question_id = #{questionId} AND ar.is_correct = 1")
    Long countCorrectByExamAndQuestion(@Param("examId") Long examId, @Param("questionId") Long questionId);
}
