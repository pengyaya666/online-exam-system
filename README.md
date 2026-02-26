# 在线考试系统

基于 Spring Boot + Vue3 的在线考试系统，支持学生在线考试、教师题库管理、管理员用户管理等功能。

## 技术栈

### 后端
- Spring Boot 2.7.18
- Java 8
- MyBatis Plus 3.5.3.1
- MySQL 8.0.44
- JWT 认证
- Maven 3.9.9

### 前端
- Vue 3.3.4
- Element Plus 2.4.8
- Axios 1.6.8
- Vue Router 4
- Vuex 4
- ECharts 5

## 功能模块

### 学生端
- 查看待考考试
- 在线考试（防切屏监控）
- 查看考试成绩
- 答题详情分析

### 教师端
- 题库管理（增删改查）
- 题目管理（支持Excel批量导入）
- 考试管理（组卷、发布）
- 成绩统计分析（图表展示）

### 管理员端
- 用户管理（增删改查）
- 系统监控

## 本地启动步骤

### 1. 数据库初始化

```bash
# 登录MySQL
mysql -u root -p

# 执行初始化脚本
source database/init.sql
```

或直接使用MySQL客户端工具（如Navicat）导入 `database/init.sql` 文件。

**默认账号密码：**
- 管理员：admin / admin123
- 教师：teacher / teacher123
- 学生：student / student123

### 2. 后端启动

```bash
# 进入后端目录
cd backend

# 使用Maven打包（首次运行需要下载依赖）
mvn clean install

# 运行项目
mvn spring-boot:run
```

后端服务默认运行在 http://localhost:8080

### 3. 前端启动

```bash
# 进入前端目录
cd frontend

# 安装依赖（使用 --legacy-peer-deps 解决依赖冲突）
npm install --legacy-peer-deps

# 启动开发服务器
npm run serve
```

前端服务默认运行在 http://localhost:3000

### 4. 访问系统

打开浏览器访问 http://localhost:3000

## 项目结构

```
online-exam-system/
├── backend/                 # 后端项目
│   ├── src/
│   │   └── main/
│   │       ├── java/com/exam/
│   │       │   ├── config/      # 配置类
│   │       │   ├── controller/  # 控制器
│   │       │   ├── dto/         # 数据传输对象
│   │       │   ├── entity/      # 实体类
│   │       │   ├── exception/   # 异常处理
│   │       │   ├── interceptor/ # 拦截器
│   │       │   ├── mapper/      # MyBatis Mapper
│   │       │   ├── service/     # 业务逻辑
│   │       │   ├── util/        # 工具类
│   │       │   └── vo/          # 视图对象
│   │       └── resources/
│   │           └── application.yml
│   └── pom.xml
├── frontend/                # 前端项目
│   ├── src/
│   │   ├── api/            # API接口
│   │   ├── components/     # 公共组件
│   │   ├── router/         # 路由配置
│   │   ├── store/          # Vuex状态管理
│   │   ├── utils/          # 工具函数
│   │   ├── views/          # 页面组件
│   │   │   ├── admin/      # 管理员页面
│   │   │   ├── dashboard/  # 首页
│   │   │   ├── login/      # 登录注册
│   │   │   ├── profile/    # 个人中心
│   │   │   ├── student/    # 学生页面
│   │   │   └── teacher/    # 教师页面
│   │   ├── App.vue
│   │   └── main.js
│   ├── package.json
│   └── vue.config.js
├── database/
│   └── init.sql            # 数据库初始化脚本
└── README.md
```

## 核心功能说明

### 1. 用户认证
- 基于 JWT 的无状态认证
- 支持登录、注册、密码修改
- 角色权限控制（管理员/教师/学生）

### 2. 题库管理
- 题库增删改查
- 题目支持单选、多选、判断三种类型
- 支持 Excel 批量导入题目
- 题目难度分级（简单/中等/困难）

### 3. 考试管理
- 自动组卷（从题库随机抽取题目）
- 考试时间控制
- 防切屏监控
- 题目乱序

### 4. 自动评分
- 客观题自动批改
- 实时计算得分
- 成绩统计分析

### 5. 成绩统计
- 分数段分布图表
- 各题正确率分析
- 成绩导出

## 数据库表结构

| 表名 | 说明 |
|------|------|
| sys_user | 用户表 |
| question_bank | 题库表 |
| question | 题目表 |
| exam | 考试表 |
| exam_question | 考试题目关联表 |
| exam_record | 考试记录表 |
| answer_record | 答题记录表 |

## 注意事项

1. **MySQL版本**：建议使用 MySQL 8.0 及以上版本
2. **Java版本**：必须使用 Java 8
3. **Node版本**：建议使用 Node.js 18+，测试通过版本 22.12.0
4. **npm安装**：前端依赖安装请使用 `npm install --legacy-peer-deps`
5. **跨域配置**：后端已配置CORS，前端代理配置在 vue.config.js 中

## 常见问题

### 1. 后端启动失败
- 检查MySQL服务是否启动
- 检查application.yml中的数据库连接配置
- 确保MySQL用户名密码正确（默认root/123456）

### 2. 前端依赖安装失败
```bash
# 清除缓存后重试
npm cache clean --force
npm install --legacy-peer-deps
```

### 3. 登录失败
- 检查后端服务是否正常运行
- 检查前端vue.config.js中的代理配置
- 确认数据库中已初始化用户数据

## 开发团队

- 开发：彭俊娅
- 指导教师：朱秋云
- 学校：电子科技大学中山学院

## 许可证

本项目仅供学习交流使用。
