package com.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exam.dto.QuestionDTO;
import com.exam.entity.Question;
import com.exam.vo.PageResult;
import com.exam.vo.QuestionVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface QuestionService extends IService<Question> {

    void addQuestion(QuestionDTO questionDTO, Long userId);

    void updateQuestion(QuestionDTO questionDTO);

    void deleteQuestion(Long id);

    QuestionVO getQuestionDetail(Long id);

    PageResult<QuestionVO> getQuestionPage(Integer page, Integer size, Long bankId, Integer type, String keyword);

    // 修改这里：返回 Map 类型
    Map<String, Integer> batchImport(Long bankId, MultipartFile file, Long userId) throws IOException;

    void exportTemplate(HttpServletResponse response) throws IOException;

    List<QuestionVO> getQuestionsByBankId(Long bankId);

    List<Question> selectRandomQuestions(Long bankId, Integer type, Integer count);
}