-- 在线考试系统数据库初始化脚本
-- 数据库: online_exam
-- 创建日期: 2024年

-- 创建数据库
CREATE DATABASE IF NOT EXISTS online_exam DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE online_exam;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(MD5加密)',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    role TINYINT NOT NULL DEFAULT 2 COMMENT '角色(0管理员 1教师 2学生)',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0禁用 1启用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0未删除 1已删除)',
    UNIQUE KEY uk_username (username),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 题库表
CREATE TABLE IF NOT EXISTS question_bank (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '题库ID',
    name VARCHAR(100) NOT NULL COMMENT '题库名称',
    description TEXT COMMENT '题库描述',
    create_by BIGINT NOT NULL COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_create_by (create_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题库表';

-- 题目表
CREATE TABLE IF NOT EXISTS question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '题目ID',
    bank_id BIGINT NOT NULL COMMENT '所属题库ID',
    type TINYINT NOT NULL COMMENT '题目类型(1单选 2多选 3判断 4填空)',
    content TEXT NOT NULL COMMENT '题目内容',
    options TEXT COMMENT '选项(JSON格式)',
    answer VARCHAR(500) NOT NULL COMMENT '正确答案',
    score INT NOT NULL DEFAULT 5 COMMENT '分值',
    analysis TEXT COMMENT '答案解析',
    knowledge_point VARCHAR(200) COMMENT '知识点',
    difficulty TINYINT DEFAULT 1 COMMENT '难度(1简单 2中等 3困难)',
    create_by BIGINT NOT NULL COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_bank_id (bank_id),
    INDEX idx_type (type),
    INDEX idx_difficulty (difficulty)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目表';

-- 考试表
CREATE TABLE IF NOT EXISTS exam (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '考试ID',
    title VARCHAR(200) NOT NULL COMMENT '考试标题',
    description TEXT COMMENT '考试说明',
    bank_id BIGINT NOT NULL COMMENT '关联题库ID',
    duration INT NOT NULL COMMENT '考试时长(分钟)',
    total_score INT NOT NULL COMMENT '总分',
    pass_score INT NOT NULL COMMENT '及格分数',
    single_count INT NOT NULL DEFAULT 0 COMMENT '单选题数量',
    single_score INT NOT NULL DEFAULT 0 COMMENT '单选题分值',
    multiple_count INT NOT NULL DEFAULT 0 COMMENT '多选题数量',
    multiple_score INT NOT NULL DEFAULT 0 COMMENT '多选题分值',
    judge_count INT NOT NULL DEFAULT 0 COMMENT '判断题数量',
    judge_score INT NOT NULL DEFAULT 0 COMMENT '判断题分值',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    allow_multiple TINYINT DEFAULT 0 COMMENT '允许多次参加(0否 1是)',
    random_order TINYINT DEFAULT 1 COMMENT '题目乱序(0否 1是)',
    status TINYINT DEFAULT 0 COMMENT '状态(0草稿 1已发布 2已结束)',
    create_by BIGINT NOT NULL COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_bank_id (bank_id),
    INDEX idx_status (status),
    INDEX idx_start_time (start_time),
    INDEX idx_end_time (end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试表';

-- 考试题目关联表
CREATE TABLE IF NOT EXISTS exam_question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    sequence INT NOT NULL COMMENT '题目顺序',
    score INT NOT NULL COMMENT '本题分值',
    UNIQUE KEY uk_exam_question (exam_id, question_id),
    INDEX idx_exam_id (exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试题目关联表';

-- 考试记录表
CREATE TABLE IF NOT EXISTS exam_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    status TINYINT DEFAULT 1 COMMENT '状态(0未开始 1进行中 2已提交 3已自动批改 4已人工批改)',
    switch_count INT DEFAULT 0 COMMENT '切屏次数',
    score INT COMMENT '总得分',
    objective_score INT COMMENT '客观题得分',
    subjective_score INT COMMENT '主观题得分',
    is_pass TINYINT COMMENT '是否通过(0否 1是)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_exam_id (exam_id),
    INDEX idx_student_id (student_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试记录表';

-- 答题记录表
CREATE TABLE IF NOT EXISTS answer_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    record_id BIGINT NOT NULL COMMENT '考试记录ID',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    question_id BIGINT NOT NULL COMMENT '题目ID',
    answer VARCHAR(500) COMMENT '学生答案',
    is_correct TINYINT DEFAULT -1 COMMENT '是否正确(-1待批改 0错误 1正确)',
    score INT COMMENT '得分',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_record_question (record_id, question_id),
    INDEX idx_record_id (record_id),
    INDEX idx_exam_id (exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答题记录表';

-- 插入默认管理员账号 (密码: admin123)
INSERT INTO sys_user (username, password, real_name, email, role, status) VALUES
('admin', '0192023a7bbd73250516f069df18b500', '系统管理员', 'admin@exam.com', 0, 1);

-- 插入测试教师账号 (密码: teacher123)
INSERT INTO sys_user (username, password, real_name, email, role, status) VALUES
('teacher', '8d788385431273d11e8b44bbdaa5dd04', '测试教师', 'teacher@exam.com', 1, 1);

-- 插入测试学生账号 (密码: student123)
INSERT INTO sys_user (username, password, real_name, email, role, status) VALUES
('student', 'cd73502828457d15655bbd7a63fb0bc8', '测试学生', 'student@exam.com', 2, 1),
('2021001', 'e10adc3949ba59abbe56e057f20f883e', '张三', 'zhangsan@exam.com', 2, 1),
('2021002', 'e10adc3949ba59abbe56e057f20f883e', '李四', 'lisi@exam.com', 2, 1),
('2021003', 'e10adc3949ba59abbe56e057f20f883e', '王五', 'wangwu@exam.com', 2, 1);

-- 插入示例题库
INSERT INTO question_bank (name, description, create_by) VALUES
('计算机基础', '计算机基础知识的题库', 2),
('Java程序设计', 'Java编程相关的题目', 2),
('数据库原理', '数据库相关知识点', 2);

-- 插入示例题目 - 计算机基础
INSERT INTO question (bank_id, type, content, options, answer, score, analysis, knowledge_point, difficulty, create_by) VALUES
(1, 1, '计算机中，1KB等于多少字节？', '{"A":"1000","B":"1024","C":"512","D":"2048"}', 'B', 5, '1KB = 1024字节', '计算机基础', 1, 2),
(1, 1, '计算机的核心部件是？', '{"A":"内存","B":"硬盘","C":"CPU","D":"显卡"}', 'C', 5, 'CPU是计算机的核心部件', '计算机组成', 1, 2),
(1, 3, '计算机病毒是一种程序。', '', 'true', 5, '计算机病毒确实是一种恶意程序', '计算机安全', 1, 2),
(1, 2, '以下哪些是操作系统？', '{"A":"Windows","B":"Linux","C":"Word","D":"Android"}', 'ABD', 10, 'Word是办公软件，不是操作系统', '操作系统', 2, 2);

-- 插入示例题目 - Java程序设计
INSERT INTO question (bank_id, type, content, options, answer, score, analysis, knowledge_point, difficulty, create_by) VALUES
(2, 1, 'Java中，String类位于哪个包？', '{"A":"java.io","B":"java.lang","C":"java.util","D":"java.net"}', 'B', 5, 'String类在java.lang包中，无需导入', 'Java基础', 1, 2),
(2, 1, '以下哪个不是Java的基本数据类型？', '{"A":"int","B":"String","C":"boolean","D":"char"}', 'B', 5, 'String是类，不是基本数据类型', 'Java基础', 1, 2),
(2, 3, 'Java是面向对象的编程语言。', '', 'true', 5, 'Java是纯面向对象的编程语言', 'Java特性', 1, 2),
(2, 2, '以下哪些是Java的访问修饰符？', '{"A":"public","B":"private","C":"protected","D":"static"}', 'ABC', 10, 'static不是访问修饰符', 'Java基础', 2, 2);

-- 插入示例题目 - 数据库原理
INSERT INTO question (bank_id, type, content, options, answer, score, analysis, knowledge_point, difficulty, create_by) VALUES
(3, 1, 'SQL中，用于查询数据的关键字是？', '{"A":"INSERT","B":"UPDATE","C":"SELECT","D":"DELETE"}', 'C', 5, 'SELECT用于查询数据', 'SQL基础', 1, 2),
(3, 1, '以下哪个不是关系型数据库？', '{"A":"MySQL","B":"MongoDB","C":"Oracle","D":"SQL Server"}', 'B', 5, 'MongoDB是文档型非关系数据库', '数据库类型', 2, 2),
(3, 3, '数据库事务具有ACID特性。', '', 'true', 5, 'ACID是事务的四个基本特性', '事务', 2, 2);

-- 密码说明:
-- admin: admin123 -> 0192023a7bbd73250516f069df18b500
-- teacher: teacher123 -> 8d788385431273d11e8b44bbdaa5dd04
-- student: student123 -> cd73502828457d15655bbd7a63fb0bc8
-- 2021001-2021003: 123456 -> e10adc3949ba59abbe56e057f20f883e
