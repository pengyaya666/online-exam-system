package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.ExamRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ExamRecordMapper extends BaseMapper<ExamRecord> {
    
    @Select("SELECT * FROM exam_record WHERE exam_id = #{examId} AND student_id = #{studentId} ORDER BY create_time DESC LIMIT 1")
    ExamRecord selectLatestByExamAndStudent(@Param("examId") Long examId, @Param("studentId") Long studentId);
    
    @Select("SELECT * FROM exam_record WHERE exam_id = #{examId} ORDER BY create_time DESC")
    List<ExamRecord> selectByExamId(@Param("examId") Long examId);
    
    @Select("SELECT * FROM exam_record WHERE student_id = #{studentId} ORDER BY create_time DESC")
    List<ExamRecord> selectByStudentId(@Param("studentId") Long studentId);
    
    @Update("UPDATE exam_record SET switch_count = switch_count + 1 WHERE id = #{recordId}")
    void incrementSwitchCount(@Param("recordId") Long recordId);
    
    @Select("SELECT COUNT(*) FROM exam_record WHERE exam_id = #{examId} AND status >= 2")
    Long countSubmittedByExamId(@Param("examId") Long examId);
    
    @Select("SELECT AVG(score) FROM exam_record WHERE exam_id = #{examId} AND status >= 3 AND score IS NOT NULL")
    Double averageScoreByExamId(@Param("examId") Long examId);
    
    @Select("SELECT MAX(score) FROM exam_record WHERE exam_id = #{examId} AND status >= 3 AND score IS NOT NULL")
    Integer maxScoreByExamId(@Param("examId") Long examId);
    
    @Select("SELECT MIN(score) FROM exam_record WHERE exam_id = #{examId} AND status >= 3 AND score IS NOT NULL")
    Integer minScoreByExamId(@Param("examId") Long examId);
    
    @Select("SELECT COUNT(*) FROM exam_record WHERE exam_id = #{examId} AND status >= 3 AND score >= #{passScore}")
    Long countPassedByExamId(@Param("examId") Long examId, @Param("passScore") Integer passScore);
}
