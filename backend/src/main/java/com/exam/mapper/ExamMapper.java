package com.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exam.entity.Exam;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ExamMapper extends BaseMapper<Exam> {
    
    @Select("SELECT * FROM exam WHERE status = 1 AND deleted = 0 AND start_time <= NOW() AND end_time >= NOW()")
    List<Exam> selectActiveExams();
    
    @Select("SELECT COUNT(*) FROM exam_record WHERE exam_id = #{examId} AND status >= 2")
    Long countParticipants(@Param("examId") Long examId);
    
    @Select("SELECT COUNT(*) FROM exam_record WHERE exam_id = #{examId} AND student_id = #{studentId} AND status >= 2")
    Long countStudentParticipation(@Param("examId") Long examId, @Param("studentId") Long studentId);
    
    @Update("UPDATE exam SET status = 2 WHERE end_time < NOW() AND status = 1")
    void updateEndedExams();
}
