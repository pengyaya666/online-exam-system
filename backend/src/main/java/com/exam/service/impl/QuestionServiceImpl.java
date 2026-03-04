package com.exam.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exam.dto.QuestionDTO;
import com.exam.entity.Question;
import com.exam.entity.QuestionBank;
import com.exam.entity.User;
import com.exam.mapper.QuestionBankMapper;
import com.exam.mapper.QuestionMapper;
import com.exam.mapper.UserMapper;
import com.exam.service.QuestionService;
import com.exam.vo.PageResult;
import com.exam.vo.QuestionVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Autowired
    private QuestionBankMapper bankMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void addQuestion(QuestionDTO questionDTO, Long userId) {
        QuestionBank bank = bankMapper.selectById(questionDTO.getBankId());
        if (bank == null) {
            throw new RuntimeException("题库不存在");
        }

        Question question = new Question();
        BeanUtils.copyProperties(questionDTO, question);
        question.setCreateBy(userId);

        baseMapper.insert(question);
    }

    @Override
    public void updateQuestion(QuestionDTO questionDTO) {
        Question existQuestion = baseMapper.selectById(questionDTO.getId());
        if (existQuestion == null) {
            throw new RuntimeException("题目不存在");
        }

        BeanUtils.copyProperties(questionDTO, existQuestion);
        baseMapper.updateById(existQuestion);
    }

    @Override
    public void deleteQuestion(Long id) {
        baseMapper.deleteById(id);
    }

    @Override
    public QuestionVO getQuestionDetail(Long id) {
        Question question = baseMapper.selectById(id);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }

        return convertToVO(question);
    }

    @Override
    public PageResult<QuestionVO> getQuestionPage(Integer page, Integer size, Long bankId, Integer type, String keyword) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();

        if (bankId != null) {
            wrapper.eq(Question::getBankId, bankId);
        }

        if (type != null) {
            wrapper.eq(Question::getType, type);
        }

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Question::getContent, keyword);
        }

        wrapper.orderByDesc(Question::getCreateTime);

        Page<Question> pageResult = baseMapper.selectPage(new Page<>(page, size), wrapper);

        List<QuestionVO> records = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(pageResult.getTotal(), records, pageResult.getCurrent(), pageResult.getSize());
    }

    @Override
    public Map<String, Integer> batchImport(Long bankId, MultipartFile file, Long userId) throws IOException {
        Map<String, Integer> result = new HashMap<>();
        List<Question> questions = new ArrayList<>();
        int[] successCount = {0};
        int[] failCount = {0};

        QuestionBank bank = bankMapper.selectById(bankId);
        if (bank == null) {
            throw new RuntimeException("题库不存在");
        }

        try {
            EasyExcel.read(file.getInputStream())
                    .sheet()
                    .headRowNumber(1) // 第一行是表头
                    .registerReadListener(new ReadListener<Map<Integer, String>>() {
                        @Override
                        public void invoke(Map<Integer, String> data, AnalysisContext context) {
                            try {
                                // 打印读取到的数据，用于调试
                                System.out.println("读取到行数据: " + data);

                                Question question = new Question();
                                question.setBankId(bankId);

                                // 按列索引读取，确保和模板列顺序一致
                                question.setType(Integer.parseInt(data.get(0))); // 题目类型
                                question.setContent(data.get(1)); // 题目内容
                                question.setOptions(data.get(2)); // 选项
                                question.setAnswer(data.get(3)); // 答案
                                question.setScore(Integer.parseInt(data.get(4))); // 分值
                                question.setDifficulty(Integer.parseInt(data.get(5))); // 难度

                                if (data.containsKey(6) && data.get(6) != null) {
                                    question.setAnalysis(data.get(6)); // 解析
                                }
                                if (data.containsKey(7) && data.get(7) != null) {
                                    question.setKnowledgePoint(data.get(7)); // 知识点
                                }

                                question.setCreateBy(userId);

                                // 直接插入数据库
                                baseMapper.insert(question);
                                successCount[0]++;

                            } catch (Exception e) {
                                System.err.println("导入失败行: " + data + ", 错误: " + e.getMessage());
                                failCount[0]++;
                            }
                        }

                        @Override
                        public void doAfterAllAnalysed(AnalysisContext context) {
                            System.out.println("解析完成，成功：" + successCount[0] + "，失败：" + failCount[0]);
                        }
                    })
                    .doRead();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Excel解析失败: " + e.getMessage());
        }

        result.put("success", successCount[0]);
        result.put("fail", failCount[0]);
        return result;
    }

    @Override
    public void exportTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("题目导入模板", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        List<List<String>> head = Arrays.asList(
                Arrays.asList("题目类型(1单选2多选3判断4填空)"),
                Arrays.asList("题目内容"),
                Arrays.asList("选项(JSON格式,如:{\"A\":\"选项A\",\"B\":\"选项B\"})"),
                Arrays.asList("答案"),
                Arrays.asList("分值"),
                Arrays.asList("难度(1简单2中等3困难)"),
                Arrays.asList("解析"),
                Arrays.asList("知识点")
        );

        List<List<String>> data = new ArrayList<>();
        data.add(Arrays.asList("1", "示例单选题", "{\"A\":\"选项A\",\"B\":\"选项B\",\"C\":\"选项C\",\"D\":\"选项D\"}", "A", "5", "1", "这是解析", "知识点1"));
        data.add(Arrays.asList("2", "示例多选题", "{\"A\":\"选项A\",\"B\":\"选项B\",\"C\":\"选项C\",\"D\":\"选项D\"}", "AB", "10", "2", "这是解析", "知识点2"));
        data.add(Arrays.asList("3", "示例判断题", "", "true", "5", "1", "这是解析", "知识点3"));

        EasyExcel.write(response.getOutputStream())
                .head(head)
                .sheet("模板")
                .doWrite(data);
    }

    @Override
    public List<QuestionVO> getQuestionsByBankId(Long bankId) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getBankId, bankId);
        wrapper.orderByDesc(Question::getCreateTime);

        return baseMapper.selectList(wrapper).stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Question> selectRandomQuestions(Long bankId, Integer type, Integer count) {
        return baseMapper.selectRandomByBankIdAndType(bankId, type, count);
    }

    private QuestionVO convertToVO(Question question) {
        QuestionVO vo = new QuestionVO();
        BeanUtils.copyProperties(question, vo);

        QuestionBank bank = bankMapper.selectById(question.getBankId());
        if (bank != null) {
            vo.setBankName(bank.getName());
        }

        User user = userMapper.selectById(question.getCreateBy());
        if (user != null) {
            vo.setCreateByName(user.getRealName());
        }

        switch (question.getType()) {
            case 1:
                vo.setTypeName("单选题");
                break;
            case 2:
                vo.setTypeName("多选题");
                break;
            case 3:
                vo.setTypeName("判断题");
                break;
            case 4:
                vo.setTypeName("填空题");
                break;
            default:
                vo.setTypeName("未知");
        }

        switch (question.getDifficulty()) {
            case 1:
                vo.setDifficultyName("简单");
                break;
            case 2:
                vo.setDifficultyName("中等");
                break;
            case 3:
                vo.setDifficultyName("困难");
                break;
            default:
                vo.setDifficultyName("未知");
        }

        if (StringUtils.hasText(question.getOptions())) {
            vo.setOptionList(parseOptions(question.getOptions()));
        }

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
}