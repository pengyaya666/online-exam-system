package com.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.dto.ExamDTO;
import com.exam.dto.SubmitExamDTO;
import com.exam.entity.*;
import com.exam.mapper.*;
import com.exam.service.ExamService;
import com.exam.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements ExamService {

    @Autowired
    private QuestionBankMapper bankMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private ExamQuestionMapper examQuestionMapper;

    @Autowired
    private ExamRecordMapper examRecordMapper;

    @Autowired
    private AnswerRecordMapper answerRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public void createExam(ExamDTO examDTO, Long userId) {
        QuestionBank bank = bankMapper.selectById(examDTO.getBankId());
        if (bank == null) {
            throw new RuntimeException("题库不存在");
        }

        // 验证题目数量
        validateQuestionCount(examDTO);

        Exam exam = new Exam();
        BeanUtils.copyProperties(examDTO, exam);
        exam.setCreateBy(userId);
        exam.setStatus(Exam.STATUS_DRAFT);

        baseMapper.insert(exam);

        // 生成考试题目
        generateExamQuestions(exam);
    }

    @Override
    @Transactional
    public void updateExam(ExamDTO examDTO) {
        Exam existExam = baseMapper.selectById(examDTO.getId());
        if (existExam == null) {
            throw new RuntimeException("考试不存在");
        }

        if (existExam.getStatus() == Exam.STATUS_PUBLISHED) {
            throw new RuntimeException("已发布的考试不能修改");
        }

        validateQuestionCount(examDTO);

        BeanUtils.copyProperties(examDTO, existExam);
        baseMapper.updateById(existExam);

        // 重新生成考试题目
        examQuestionMapper.deleteByExamId(existExam.getId());
        generateExamQuestions(existExam);
    }

    @Override
    @Transactional
    public void deleteExam(Long id) {
        Exam exam = baseMapper.selectById(id);
        if (exam == null) {
            throw new RuntimeException("考试不存在");
        }

        if (exam.getStatus() == Exam.STATUS_PUBLISHED) {
            throw new RuntimeException("已发布的考试不能删除");
        }

        examQuestionMapper.deleteByExamId(id);
        baseMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void publishExam(Long id) {
        Exam exam = baseMapper.selectById(id);
        if (exam == null) {
            throw new RuntimeException("考试不存在");
        }

        if (exam.getStatus() != Exam.STATUS_DRAFT) {
            throw new RuntimeException("只能发布草稿状态的考试");
        }

        exam.setStatus(Exam.STATUS_PUBLISHED);
        baseMapper.updateById(exam);
    }

    @Override
    public ExamDetailVO getExamDetail(Long id) {
        Exam exam = baseMapper.selectById(id);
        if (exam == null) {
            throw new RuntimeException("考试不存在");
        }

        return convertToDetailVO(exam, false);
    }

    @Override
    public ExamDetailVO getExamDetailForStudent(Long id, Long studentId) {
        Exam exam = baseMapper.selectById(id);
        if (exam == null) {
            throw new RuntimeException("考试不存在");
        }

        if (exam.getStatus() != Exam.STATUS_PUBLISHED) {
            throw new RuntimeException("考试未发布");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exam.getStartTime())) {
            throw new RuntimeException("考试未开始");
        }

        if (now.isAfter(exam.getEndTime())) {
            throw new RuntimeException("考试已结束");
        }

        ExamDetailVO vo = convertToDetailVO(exam, true);

        // 检查是否已参加
        Long participationCount = baseMapper.countStudentParticipation(id, studentId);
        vo.setIsParticipated(participationCount > 0 ? 1 : 0);

        return vo;
    }

    @Override
    public PageResult<ExamDetailVO> getExamPage(Integer page, Integer size, Integer status, String keyword, Long createBy) {
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(Exam::getStatus, status);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Exam::getTitle, keyword);
        }

        if (createBy != null) {
            wrapper.eq(Exam::getCreateBy, createBy);
        }

        wrapper.orderByDesc(Exam::getCreateTime);

        Page<Exam> pageResult = baseMapper.selectPage(new Page<>(page, size), wrapper);

        List<ExamDetailVO> records = pageResult.getRecords().stream()
                .map(exam -> convertToDetailVO(exam, false))
                .collect(Collectors.toList());

        return new PageResult<>(pageResult.getTotal(), records, pageResult.getCurrent(), pageResult.getSize());
    }

    @Override
    public List<ExamDetailVO> getActiveExams(Long studentId) {
        updateExamStatus();

        List<Exam> exams = baseMapper.selectActiveExams();

        return exams.stream()
                .map(exam -> {
                    ExamDetailVO vo = convertToDetailVO(exam, true);
                    Long participationCount = baseMapper.countStudentParticipation(exam.getId(), studentId);
                    vo.setIsParticipated(participationCount > 0 ? 1 : 0);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ExamRecordVO startExam(Long examId, Long studentId) {
        Exam exam = baseMapper.selectById(examId);
        if (exam == null) {
            throw new RuntimeException("考试不存在");
        }

        if (exam.getStatus() != Exam.STATUS_PUBLISHED) {
            throw new RuntimeException("考试未发布");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exam.getStartTime())) {
            throw new RuntimeException("考试未开始");
        }

        if (now.isAfter(exam.getEndTime())) {
            throw new RuntimeException("考试已结束");
        }

        // 检查是否已参加
        if (exam.getAllowMultiple() == null || exam.getAllowMultiple() == 0) {
            Long participationCount = baseMapper.countStudentParticipation(examId, studentId);
            if (participationCount > 0) {
                throw new RuntimeException("您已参加过该考试");
            }
        }

        // 创建考试记录
        ExamRecord record = new ExamRecord();
        record.setExamId(examId);
        record.setStudentId(studentId);
        record.setStartTime(now);
        record.setStatus(ExamRecord.STATUS_IN_PROGRESS);
        record.setSwitchCount(0);

        examRecordMapper.insert(record);

        return convertToRecordVO(record);
    }

    @Override
    @Transactional
    public void submitExam(SubmitExamDTO submitDTO, Long studentId) {
        ExamRecord record = examRecordMapper.selectById(submitDTO.getRecordId());
        if (record == null) {
            throw new RuntimeException("考试记录不存在");
        }

        if (!record.getStudentId().equals(studentId)) {
            throw new RuntimeException("无权操作");
        }

        if (record.getStatus() != ExamRecord.STATUS_IN_PROGRESS) {
            throw new RuntimeException("考试已提交");
        }

        Exam exam = baseMapper.selectById(record.getExamId());

        // 保存答题记录
        for (SubmitExamDTO.AnswerDTO answerDTO : submitDTO.getAnswers()) {
            AnswerRecord answerRecord = new AnswerRecord();
            answerRecord.setRecordId(record.getId());
            answerRecord.setExamId(record.getExamId());
            answerRecord.setQuestionId(answerDTO.getQuestionId());
            answerRecord.setAnswer(answerDTO.getAnswer());
            answerRecord.setIsCorrect(AnswerRecord.CORRECT_UNGRADED);

            answerRecordMapper.insert(answerRecord);
        }

        // 更新切屏次数
        if (submitDTO.getSwitchCount() != null) {
            record.setSwitchCount(submitDTO.getSwitchCount());
            System.out.println("保存切屏次数: " + submitDTO.getSwitchCount());
        }

        record.setEndTime(LocalDateTime.now());
        record.setStatus(ExamRecord.STATUS_SUBMITTED);
        examRecordMapper.updateById(record);

        // 自动批改
        autoGrade(record.getId());
    }

    @Override
    @Transactional
    public void autoGrade(Long recordId) {
        ExamRecord record = examRecordMapper.selectById(recordId);
        if (record == null) {
            return;
        }

        List<AnswerRecord> answers = answerRecordMapper.selectByRecordId(recordId);

        int totalScore = 0;
        int objectiveScore = 0;

        for (AnswerRecord answer : answers) {
            Question question = questionMapper.selectById(answer.getQuestionId());
            if (question == null) {
                continue;
            }

            ExamQuestion examQuestion = examQuestionMapper.selectOne(
                    new LambdaQueryWrapper<ExamQuestion>()
                            .eq(ExamQuestion::getExamId, record.getExamId())
                            .eq(ExamQuestion::getQuestionId, question.getId())
            );

            int score = examQuestion != null ? examQuestion.getScore() : question.getScore();

            boolean isCorrect = false;

            if (question.getType() == Question.TYPE_SINGLE ||
                    question.getType() == Question.TYPE_JUDGE) {
                isCorrect = question.getAnswer().trim().equalsIgnoreCase(
                        answer.getAnswer() != null ? answer.getAnswer().trim() : "");
            } else if (question.getType() == Question.TYPE_MULTIPLE) {
                String correctAnswer = question.getAnswer().trim().toUpperCase();
                String studentAnswer = answer.getAnswer() != null ?
                        answer.getAnswer().trim().toUpperCase() : "";

                // 排序后比较
                char[] correctArr = correctAnswer.toCharArray();
                char[] studentArr = studentAnswer.toCharArray();
                Arrays.sort(correctArr);
                Arrays.sort(studentArr);

                isCorrect = Arrays.equals(correctArr, studentArr);
            }

            if (isCorrect) {
                answer.setIsCorrect(AnswerRecord.CORRECT_YES);
                answer.setScore(score);
                objectiveScore += score;
            } else {
                answer.setIsCorrect(AnswerRecord.CORRECT_NO);
                answer.setScore(0);
            }

            answerRecordMapper.updateById(answer);
        }

        totalScore = objectiveScore;

        record.setScore(totalScore);
        record.setObjectiveScore(objectiveScore);
        record.setStatus(ExamRecord.STATUS_AUTO_GRADED);

        Exam exam = baseMapper.selectById(record.getExamId());
        if (exam != null) {
            record.setIsPass(totalScore >= exam.getPassScore() ? 1 : 0);
        }

        examRecordMapper.updateById(record);
    }

    @Override
    public ExamRecordVO getExamResult(Long recordId, Long studentId) {
        ExamRecord record = examRecordMapper.selectById(recordId);
        if (record == null) {
            throw new RuntimeException("考试记录不存在");
        }

        if (!record.getStudentId().equals(studentId)) {
            throw new RuntimeException("无权查看");
        }

        return convertToRecordVO(record);
    }

    @Override
    public PageResult<ExamRecordVO> getExamRecords(Integer page, Integer size, Long examId, Long studentId) {
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();

        if (examId != null) {
            wrapper.eq(ExamRecord::getExamId, examId);
        }

        if (studentId != null) {
            wrapper.eq(ExamRecord::getStudentId, studentId);
        }

        wrapper.orderByDesc(ExamRecord::getCreateTime);

        Page<ExamRecord> pageResult = examRecordMapper.selectPage(new Page<>(page, size), wrapper);

        List<ExamRecordVO> records = pageResult.getRecords().stream()
                .map(this::convertToRecordVO)
                .collect(Collectors.toList());

        return new PageResult<>(pageResult.getTotal(), records, pageResult.getCurrent(), pageResult.getSize());
    }

    @Override
    public ScoreStatisticsVO getScoreStatistics(Long examId) {
        Exam exam = baseMapper.selectById(examId);
        if (exam == null) {
            throw new RuntimeException("考试不存在");
        }

        ScoreStatisticsVO vo = new ScoreStatisticsVO();
        vo.setExamId(examId);
        vo.setExamTitle(exam.getTitle());

        Long submittedCount = examRecordMapper.countSubmittedByExamId(examId);
        vo.setSubmittedCount(submittedCount.intValue());

        Double averageScore = examRecordMapper.averageScoreByExamId(examId);
        vo.setAverageScore(averageScore != null ? averageScore : 0.0);

        Integer maxScore = examRecordMapper.maxScoreByExamId(examId);
        vo.setMaxScore(maxScore != null ? maxScore : 0);

        Integer minScore = examRecordMapper.minScoreByExamId(examId);
        vo.setMinScore(minScore != null ? minScore : 0);

        Long passCount = examRecordMapper.countPassedByExamId(examId, exam.getPassScore());
        vo.setPassRate(submittedCount > 0 ? (double) passCount / submittedCount * 100 : 0.0);

        // 分数段分布
        List<Map<String, Object>> distribution = new ArrayList<>();
        int[][] ranges = {{0, 59}, {60, 69}, {70, 79}, {80, 89}, {90, 100}};
        String[] labels = {"不及格", "及格", "中等", "良好", "优秀"};

        for (int i = 0; i < ranges.length; i++) {
            LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ExamRecord::getExamId, examId);
            wrapper.ge(ExamRecord::getScore, ranges[i][0]);
            wrapper.le(ExamRecord::getScore, ranges[i][1]);

            Long count = examRecordMapper.selectCount(wrapper);

            Map<String, Object> item = new HashMap<>();
            item.put("range", labels[i]);
            item.put("count", count);
            item.put("percentage", submittedCount > 0 ? (double) count / submittedCount * 100 : 0.0);
            distribution.add(item);
        }
        vo.setScoreDistribution(distribution);

        // 各题正确率
        List<ExamQuestion> examQuestions = examQuestionMapper.selectByExamId(examId);
        List<Map<String, Object>> questionRates = new ArrayList<>();

        for (ExamQuestion eq : examQuestions) {
            Question question = questionMapper.selectById(eq.getQuestionId());
            if (question == null) continue;

            Long correctCount = answerRecordMapper.countCorrectByExamAndQuestion(examId, eq.getQuestionId());

            Map<String, Object> item = new HashMap<>();
            item.put("questionId", eq.getQuestionId());
            item.put("content", question.getContent().length() > 20 ?
                    question.getContent().substring(0, 20) + "..." : question.getContent());
            item.put("correctRate", submittedCount > 0 ? (double) correctCount / submittedCount * 100 : 0.0);
            questionRates.add(item);
        }
        vo.setQuestionCorrectRate(questionRates);

        return vo;
    }

    @Override
    public void updateExamStatus() {
        baseMapper.updateEndedExams();
    }

    private void validateQuestionCount(ExamDTO examDTO) {
        Long singleCount = questionMapper.countByBankIdAndType(examDTO.getBankId(), Question.TYPE_SINGLE);
        Long multipleCount = questionMapper.countByBankIdAndType(examDTO.getBankId(), Question.TYPE_MULTIPLE);
        Long judgeCount = questionMapper.countByBankIdAndType(examDTO.getBankId(), Question.TYPE_JUDGE);

        if (singleCount < examDTO.getSingleCount()) {
            throw new RuntimeException("题库中单选题数量不足，当前有" + singleCount + "道");
        }

        if (multipleCount < examDTO.getMultipleCount()) {
            throw new RuntimeException("题库中多选题数量不足，当前有" + multipleCount + "道");
        }

        if (judgeCount < examDTO.getJudgeCount()) {
            throw new RuntimeException("题库中判断题数量不足，当前有" + judgeCount + "道");
        }
    }

    private void generateExamQuestions(Exam exam) {
        int sequence = 1;

        // 单选题
        List<Question> singleQuestions = questionMapper.selectRandomByBankIdAndType(
                exam.getBankId(), Question.TYPE_SINGLE, exam.getSingleCount());
        for (Question q : singleQuestions) {
            ExamQuestion eq = new ExamQuestion();
            eq.setExamId(exam.getId());
            eq.setQuestionId(q.getId());
            eq.setSequence(sequence++);
            eq.setScore(exam.getSingleScore());
            examQuestionMapper.insert(eq);
        }

        // 多选题
        List<Question> multipleQuestions = questionMapper.selectRandomByBankIdAndType(
                exam.getBankId(), Question.TYPE_MULTIPLE, exam.getMultipleCount());
        for (Question q : multipleQuestions) {
            ExamQuestion eq = new ExamQuestion();
            eq.setExamId(exam.getId());
            eq.setQuestionId(q.getId());
            eq.setSequence(sequence++);
            eq.setScore(exam.getMultipleScore());
            examQuestionMapper.insert(eq);
        }

        // 判断题
        List<Question> judgeQuestions = questionMapper.selectRandomByBankIdAndType(
                exam.getBankId(), Question.TYPE_JUDGE, exam.getJudgeCount());
        for (Question q : judgeQuestions) {
            ExamQuestion eq = new ExamQuestion();
            eq.setExamId(exam.getId());
            eq.setQuestionId(q.getId());
            eq.setSequence(sequence++);
            eq.setScore(exam.getJudgeScore());
            examQuestionMapper.insert(eq);
        }
    }

    private ExamDetailVO convertToDetailVO(Exam exam, boolean forStudent) {
        ExamDetailVO vo = new ExamDetailVO();
        BeanUtils.copyProperties(exam, vo);

        QuestionBank bank = bankMapper.selectById(exam.getBankId());
        if (bank != null) {
            vo.setBankName(bank.getName());
        }

        User user = userMapper.selectById(exam.getCreateBy());
        if (user != null) {
            vo.setCreateByName(user.getRealName());
        }

        switch (exam.getStatus()) {
            case 0:
                vo.setStatusName("草稿");
                break;
            case 1:
                vo.setStatusName("已发布");
                break;
            case 2:
                vo.setStatusName("已结束");
                break;
            default:
                vo.setStatusName("未知");
        }

        Long participantCount = baseMapper.countParticipants(exam.getId());
        vo.setParticipatedCount(participantCount != null ? participantCount.intValue() : 0);

        // 获取题目
        List<ExamQuestion> examQuestions = examQuestionMapper.selectByExamId(exam.getId());
        vo.setQuestionCount(examQuestions.size());

        // 学生端也需要题目，但不需要显示答案等敏感信息
        List<QuestionVO> questionVOs = examQuestions.stream()
                .map(eq -> {
                    Question question = questionMapper.selectById(eq.getQuestionId());
                    if (question == null) return null;

                    QuestionVO qvo = new QuestionVO();
                    // 只复制学生需要的基本信息
                    qvo.setId(question.getId());
                    qvo.setType(question.getType());
                    qvo.setTypeName(getTypeName(question.getType()));
                    qvo.setContent(question.getContent());
                    qvo.setScore(eq.getScore());
                    qvo.setDifficulty(question.getDifficulty());
                    qvo.setDifficultyName(getDifficultyName(question.getDifficulty()));
                    qvo.setKnowledgePoint(question.getKnowledgePoint());

                    // 解析选项
                    if (org.springframework.util.StringUtils.hasText(question.getOptions())) {
                        qvo.setOptionList(parseOptions(question.getOptions()));
                    }

                    return qvo;
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(QuestionVO::getId))
                .collect(Collectors.toList());

        vo.setQuestions(questionVOs);

        return vo;
    }

    private ExamRecordVO convertToRecordVO(ExamRecord record) {
        ExamRecordVO vo = new ExamRecordVO();
        BeanUtils.copyProperties(record, vo);

        Exam exam = baseMapper.selectById(record.getExamId());
        if (exam != null) {
            vo.setExamTitle(exam.getTitle());
            vo.setDuration(exam.getDuration());
            vo.setTotalScore(exam.getTotalScore());
            vo.setPassScore(exam.getPassScore());
        }

        User student = userMapper.selectById(record.getStudentId());
        if (student != null) {
            vo.setStudentName(student.getUsername());
            vo.setRealName(student.getRealName());
        }

        if (record.getStartTime() != null && record.getEndTime() != null) {
            vo.setUseTime((int) Duration.between(record.getStartTime(), record.getEndTime()).getSeconds());
        }

        switch (record.getStatus()) {
            case 0:
                vo.setStatusName("未开始");
                break;
            case 1:
                vo.setStatusName("进行中");
                break;
            case 2:
                vo.setStatusName("已提交");
                break;
            case 3:
                vo.setStatusName("已自动批改");
                break;
            case 4:
                vo.setStatusName("已人工批改");
                break;
            default:
                vo.setStatusName("未知");
        }

        vo.setPassStatus(record.getIsPass() != null && record.getIsPass() == 1 ? "通过" : "未通过");

        // 获取答题记录
        List<AnswerRecord> answers = answerRecordMapper.selectByRecordId(record.getId());
        List<AnswerRecordVO> answerVOs = answers.stream()
                .map(a -> {
                    AnswerRecordVO avo = new AnswerRecordVO();
                    BeanUtils.copyProperties(a, avo);

                    Question question = questionMapper.selectById(a.getQuestionId());
                    if (question != null) {
                        QuestionVO qvo = new QuestionVO();
                        BeanUtils.copyProperties(question, qvo);
                        qvo.setContent(question.getContent());

                        // 查找考试题目分值
                        ExamQuestion eq = examQuestionMapper.selectOne(
                                new LambdaQueryWrapper<ExamQuestion>()
                                        .eq(ExamQuestion::getExamId, record.getExamId())
                                        .eq(ExamQuestion::getQuestionId, question.getId())
                        );
                        avo.setFullScore(eq != null ? eq.getScore() : question.getScore());
                        avo.setCorrectAnswer(question.getAnswer());

                        // 解析选项
                        if (org.springframework.util.StringUtils.hasText(question.getOptions())) {
                            qvo.setOptionList(parseOptions(question.getOptions()));
                        }

                        avo.setQuestion(qvo);
                    }

                    avo.setCorrectStatus(a.getIsCorrect() == 1 ? "正确" :
                            (a.getIsCorrect() == 0 ? "错误" : "待批改"));

                    return avo;
                })
                .collect(Collectors.toList());

        vo.setAnswers(answerVOs);

        return vo;
    }

    private List<String> parseOptions(String optionsJson) {
        List<String> options = new ArrayList<>();
        try {
            optionsJson = optionsJson.trim();
            if (optionsJson.startsWith("{") && optionsJson.endsWith("}")) {
                optionsJson = optionsJson.substring(1, optionsJson.length() - 1);
                String[] pairs = optionsJson.split(",");
                for (String pair : pairs) {
                    String[] kv = pair.split(":");
                    if (kv.length == 2) {
                        String key = kv[0].trim().replace("\"", "");
                        String value = kv[1].trim().replace("\"", "");
                        options.add(key + ". " + value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return options;
    }

    private String getTypeName(Integer type) {
        switch (type) {
            case 1: return "单选题";
            case 2: return "多选题";
            case 3: return "判断题";
            case 4: return "填空题";
            default: return "未知";
        }
    }

    private String getDifficultyName(Integer difficulty) {
        switch (difficulty) {
            case 1: return "简单";
            case 2: return "中等";
            case 3: return "困难";
            default: return "未知";
        }
    }
}