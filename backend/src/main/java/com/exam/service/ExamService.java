package com.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.dto.ExamDTO;
import com.exam.dto.SubmitExamDTO;
import com.exam.entity.Exam;
import com.exam.vo.ExamDetailVO;
import com.exam.vo.ExamRecordVO;
import com.exam.vo.PageResult;
import com.exam.vo.ScoreStatisticsVO;

import java.util.List;

public interface ExamService extends IService<Exam> {
    
    void createExam(ExamDTO examDTO, Long userId);
    
    void updateExam(ExamDTO examDTO);
    
    void deleteExam(Long id);
    
    void publishExam(Long id);
    
    ExamDetailVO getExamDetail(Long id);
    
    ExamDetailVO getExamDetailForStudent(Long id, Long studentId);
    
    PageResult<ExamDetailVO> getExamPage(Integer page, Integer size, Integer status, String keyword, Long createBy);
    
    List<ExamDetailVO> getActiveExams(Long studentId);
    
    ExamRecordVO startExam(Long examId, Long studentId);
    
    void submitExam(SubmitExamDTO submitDTO, Long studentId);
    
    void autoGrade(Long recordId);
    
    ExamRecordVO getExamResult(Long recordId, Long studentId);
    
    PageResult<ExamRecordVO> getExamRecords(Integer page, Integer size, Long examId, Long studentId);
    
    ScoreStatisticsVO getScoreStatistics(Long examId);
    
    void updateExamStatus();
}
