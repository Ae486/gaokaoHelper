# 🎓 高考志愿填报助手系统

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![License](https://img.shields.io/badge/License-MIT-green)
![Status](https://img.shields.io/badge/Status-Active%20Development-yellow)

**为高考生提供科学、智能的志愿填报决策支持系统**

[功能特性](#-功能特性) • [快速开始](#-快速开始) • [技术架构](#-技术架构) • [API文档](#-api文档) • [贡献指南](#-贡献指南)

</div>

---

## 📋 项目简介

高考志愿填报助手是一个基于Spring Boot + MySQL的全栈Web应用系统，专为高考生和家长设计，提供科学的志愿填报建议和全方位的升学指导服务。系统集成了录取概率预测、智能推荐、性格测试、AI对话等多项核心功能，帮助考生做出最优的升学决策。

### 🎯 设计理念

- **数据驱动**: 基于历年真实录取数据进行科学分析
- **个性化推荐**: 结合考生成绩、兴趣、性格进行精准匹配
- **智能化服务**: 集成AI对话助手提供24/7咨询服务
- **用户友好**: 简洁直观的界面设计，操作简单易懂

## 🚀 功能特性

### 核心功能模块

| 功能模块 | 描述 | 状态 |
|---------|------|------|
| 🎯 **录取概率预测** | 基于历史数据和统计模型预测院校录取概率 | ✅ 已实现 |
| 🏫 **智能高校推荐** | 多维度评分推荐最适合的院校和专业 | ✅ 已实现 |
| 🧠 **性格测试** | MBTI/霍兰德测试推荐匹配专业 | ✅ 已实现 |
| 🔍 **高校查询** | 全面的院校信息检索和对比功能 | ✅ 已实现 |
| 📊 **一分一段** | 各省份分数段人数分布统计分析 | ✅ 已实现 |
| 👥 **同分去向** | 同等分数考生录取去向分析 | ✅ 已实现 |
| 🤖 **AI对话助手** | 基于DeepSeek的智能问答服务 | ✅ 已实现 |
| 👤 **用户管理** | 完整的用户注册、登录、权限管理 | ✅ 已实现 |
| 📱 **响应式界面** | 支持PC、平板、手机多端访问 | ✅ 已实现 |

### 技术特色

- **🔬 科学算法**: 多模型融合的预测算法，提高预测准确性
- **⚡ 高性能**: 数据库索引优化、缓存策略、异步处理
- **🔒 安全可靠**: JWT认证、密码加密、API限流保护
- **📈 可扩展**: 模块化设计，支持功能扩展和性能扩容
- **🎨 现代化UI**: 基于现代CSS技术的美观界面设计

## 🛠 技术架构

### 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| **Java** | 17 | 核心开发语言 |
| **Spring Boot** | 2.7.18 | 应用框架 |
| **Spring Data JPA** | - | 数据访问层 |
| **Spring Security** | - | 安全认证 |
| **MySQL** | 8.0 | 主数据库 |
| **HikariCP** | - | 数据库连接池 |
| **JWT** | 0.11.5 | 身份认证 |
| **Swagger** | 1.7.0 | API文档 |
| **Lombok** | - | 代码简化 |
| **MapStruct** | 1.5.5 | 对象映射 |
| **Hutool** | 5.8.22 | 工具类库 |

### 前端技术栈

- **HTML5/CSS3**: 现代化页面结构和样式
- **JavaScript ES6+**: 交互逻辑实现
- **GSAP**: 动画效果库
- **Font Awesome**: 图标库
- **响应式设计**: 支持多设备适配

### 数据库设计

系统包含16个核心数据表：

| 表名 | 描述 | 主要字段 |
|------|------|----------|
| `users` | 用户信息表 | id, username, password, email, province_id |
| `provinces` | 省份信息表 | id, name, province_exam_type |
| `subject_categories` | 科类信息表 | id, name, code, description |
| `schools` | 院校信息表 | id, name, code, type, level, location |
| `admission_scores` | 录取分数表 | id, school_id, province_id, year, min_score |
| `provincial_rankings` | 一分一段表 | id, province_id, year, score, count_at_score |
| `university_rankings` | 大学排名表 | id, school_id, ranking_type, rank_value |
| `mbti_questions` | MBTI测试题目 | id, question_text, dimension, options |
| `holland_questions` | 霍兰德测试题目 | id, question_text, category, weight |
| `personality_test_records` | 测试记录表 | id, user_id, test_type, results |
| `chat_sessions` | 聊天会话表 | id, user_id, session_id, created_at |
| `chat_messages` | 聊天消息表 | id, session_id, message_type, content |
| `majors` | 专业信息表 | id, name, category, description |
| `mbti_major_mapping` | MBTI专业匹配表 | id, mbti_type, major_id, match_score |
| `mbti_descriptions` | MBTI类型描述表 | id, mbti_type, description, traits |
| `admin_logs` | 管理员日志表 | id, admin_id, action, details |

## 📁 项目结构

```
gaokaoHelper/
├── src/main/java/com/gaokao/helper/
│   ├── GaokaoHelperApplication.java    # 🚀 应用启动类
│   ├── algorithm/                      # 🧮 核心算法引擎
│   │   ├── PredictionAlgorithm.java   # 录取概率预测算法
│   │   └── RecommendationEngine.java  # 智能推荐算法
│   ├── annotation/                     # 📝 自定义注解
│   ├── aspect/                         # 🔍 AOP切面
│   ├── common/                         # 🔧 公共组件
│   │   ├── Result.java                # 统一返回结果
│   │   └── PageResult.java            # 分页结果封装
│   ├── config/                         # ⚙️ 配置类
│   │   ├── SecurityConfig.java        # 安全配置
│   │   ├── SwaggerConfig.java         # API文档配置
│   │   └── WebConfig.java             # Web配置
│   ├── controller/                     # 🎮 控制器层
│   │   ├── AuthController.java        # 认证控制器
│   │   ├── PredictionController.java  # 预测控制器
│   │   ├── RecommendationController.java # 推荐控制器
│   │   ├── PersonalityTestController.java # 性格测试控制器
│   │   ├── ChatController.java        # AI对话控制器
│   │   └── ...                        # 其他控制器
│   ├── dto/                           # 📦 数据传输对象
│   │   ├── request/                   # 请求DTO
│   │   └── response/                  # 响应DTO
│   ├── entity/                        # 🗃️ 实体类
│   │   ├── User.java                  # 用户实体
│   │   ├── School.java                # 学校实体
│   │   ├── AdmissionScore.java        # 录取分数实体
│   │   └── ...                        # 其他实体
│   ├── exception/                     # ❌ 异常处理
│   ├── repository/                    # 🗄️ 数据访问层
│   ├── service/                       # 💼 业务逻辑层
│   │   └── impl/                      # 业务逻辑实现
│   └── util/                          # 🛠️ 工具类
├── src/main/resources/
│   ├── application.yml                # 主配置文件
│   ├── application-dev.yml           # 开发环境配置
│   ├── application-prod.yml          # 生产环境配置
│   ├── data/                         # 初始化数据
│   └── static/                       # 静态资源
│       ├── index.html                # 登录页面
│       ├── main.html                 # 主界面
│       ├── css/                      # 样式文件
│       ├── js/                       # JavaScript文件
│       └── images/                   # 图片资源
├── scripts/                          # 🔧 脚本文件
│   ├── create_tables_step1.sql       # 数据库建表脚本
│   ├── insert_test_data.sql          # 测试数据脚本
│   └── backup_database.bat           # 数据库备份脚本
├── docs/                             # 📚 项目文档
├── rawDataFiles/                     # 📊 原始数据文件
├── pom.xml                           # Maven配置文件
└── README.md                         # 项目说明文档
```

## 🚀 快速开始

### 环境要求

| 软件 | 版本要求 | 说明 |
|------|----------|------|
| **Java** | JDK 17+ | 核心运行环境 |
| **Maven** | 3.6+ | 项目构建工具 |
| **MySQL** | 8.0+ | 主数据库 |
| **Node.js** | 16+ (可选) | 前端开发工具 |

### 🔧 安装步骤

#### 1. 克隆项目
```bash
git clone https://github.com/Ae486/gaokaoHelper.git
cd gaokaoHelper
```

#### 2. 数据库配置
```sql
-- 创建数据库
CREATE DATABASE gaokaodb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户（可选）
CREATE USER 'gaokao_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON gaokaodb.* TO 'gaokao_user'@'localhost';
FLUSH PRIVILEGES;
```

#### 3. 修改配置文件
编辑 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/gaokaodb?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8
    username: root  # 修改为你的数据库用户名
    password: your_password  # 修改为你的数据库密码
```

#### 4. 初始化数据库
```bash
# 执行数据库脚本
mysql -u root -p gaokaodb < scripts/create_tables_step1.sql
mysql -u root -p gaokaodb < scripts/insert_test_data.sql
```

#### 5. 编译运行
```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/gaokao-helper-1.0.0.jar
```

#### 6. 访问应用
- **主应用**: http://localhost:8080
- **API文档**: http://localhost:8080/swagger-ui.html
- **健康检查**: http://localhost:8080/actuator/health

## ⚙️ 配置说明

### 核心配置项

#### 数据库配置
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/gaokaodb?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8
    username: root
    password: your_password
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000
      max-lifetime: 1200000
      connection-timeout: 20000
```

#### JPA配置
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
```

#### AI对话配置
```yaml
gaokao:
  helper:
    ai:
      deepseek:
        api-key: your_deepseek_api_key
        base-url: https://api.deepseek.com
        model: deepseek-chat
        max-tokens: 2000
        temperature: 0.7
```

#### JWT配置
```yaml
jwt:
  secret: your_jwt_secret_key
  expiration: 86400000  # 24小时
```

### 环境配置

项目支持多环境配置：
- `application.yml` - 基础配置
- `application-dev.yml` - 开发环境
- `application-prod.yml` - 生产环境

启动时指定环境：
```bash
# 开发环境
java -jar app.jar --spring.profiles.active=dev

# 生产环境
java -jar app.jar --spring.profiles.active=prod
```

## 📚 API文档

### 主要API接口

| 模块 | 接口路径 | 描述 |
|------|----------|------|
| **认证** | `/api/auth/*` | 用户注册、登录、JWT验证 |
| **预测** | `/api/prediction/*` | 录取概率预测相关接口 |
| **推荐** | `/api/recommendation/*` | 智能推荐相关接口 |
| **测试** | `/api/personality/*` | 性格测试相关接口 |
| **查询** | `/api/schools/*` | 院校查询相关接口 |
| **排名** | `/api/ranking/*` | 一分一段相关接口 |
| **对话** | `/api/chat/*` | AI对话相关接口 |

### 接口示例

#### 用户登录
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

#### 录取概率预测
```http
POST /api/prediction/calculate
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "schoolId": 1,
  "provinceId": 1,
  "subjectCategoryId": 1,
  "userScore": 650,
  "year": 2024
}
```

#### AI对话
```http
POST /api/chat/message
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "sessionId": "session_123",
  "message": "请推荐一些适合理科生的大学"
}
```

## 🏗️ 开发规范

### 代码规范

- **命名规范**: 使用驼峰命名法，类名首字母大写，方法名首字母小写
- **注释规范**: 所有公共方法必须添加JavaDoc注释
- **异常处理**: 统一使用BusinessException处理业务异常
- **日志规范**: 使用SLF4J进行日志记录，合理设置日志级别
- **代码格式**: 使用IDE自动格式化，保持代码风格一致

### 数据库规范

- **表名**: 使用下划线命名，如 `user_test_results`
- **字段名**: 使用下划线命名，如 `created_at`
- **主键**: 统一使用自增ID作为主键
- **索引**: 为查询频繁的字段添加索引
- **注释**: 所有表和重要字段必须添加注释

### API设计规范

- **RESTful**: 遵循RESTful设计原则
- **统一前缀**: 所有API使用 `/api` 前缀
- **统一响应**: 使用Result类封装所有响应
- **错误处理**: 统一的错误码和错误信息
- **版本控制**: 支持API版本控制

## 📊 项目状态

### 已完成功能 ✅

- [x] **基础架构**: Spring Boot项目搭建，数据库设计
- [x] **用户系统**: 注册、登录、JWT认证、权限管理
- [x] **录取预测**: 基于历史数据的概率预测算法
- [x] **智能推荐**: 多维度评分的院校推荐系统
- [x] **性格测试**: MBTI和霍兰德测试及专业匹配
- [x] **院校查询**: 全面的院校信息检索功能
- [x] **数据分析**: 一分一段、同分去向分析
- [x] **AI对话**: 基于DeepSeek的智能问答
- [x] **前端界面**: 响应式Web界面设计
- [x] **管理后台**: 管理员功能和日志记录

### 开发中功能 🚧

- [ ] **数据可视化**: 图表展示和数据分析报告
- [ ] **移动端适配**: 移动端专用界面优化
- [ ] **缓存优化**: Redis缓存策略完善
- [ ] **性能监控**: 应用性能监控和告警
- [ ] **单元测试**: 完善的测试用例覆盖

### 计划功能 📋

- [ ] **微信小程序**: 小程序版本开发
- [ ] **数据导入工具**: 批量数据导入和更新工具
- [ ] **报告生成**: PDF格式的分析报告生成
- [ ] **消息推送**: 重要信息推送功能
- [ ] **多语言支持**: 国际化支持

## 🚀 部署指南

### Docker部署（推荐）

#### 1. 构建镜像
```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY target/gaokao-helper-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Xmx2g", "-Xms1g", "app.jar"]
```

#### 2. Docker Compose部署
```yaml
# docker-compose.yml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/gaokaodb
    depends_on:
      - mysql

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: your_password
      MYSQL_DATABASE: gaokaodb
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

#### 3. 启动服务
```bash
docker-compose up -d
```

### 传统部署

#### 1. 服务器要求
- **操作系统**: Linux (Ubuntu 20.04+ / CentOS 7+)
- **内存**: 最低2GB，推荐4GB+
- **存储**: 最低10GB可用空间
- **网络**: 稳定的网络连接

#### 2. 环境安装
```bash
# 安装Java 17
sudo apt update
sudo apt install openjdk-17-jdk

# 安装MySQL
sudo apt install mysql-server

# 配置MySQL
sudo mysql_secure_installation
```

#### 3. 应用部署
```bash
# 上传jar包到服务器
scp target/gaokao-helper-*.jar user@server:/opt/gaokao-helper/

# 创建启动脚本
cat > /opt/gaokao-helper/start.sh << 'EOF'
#!/bin/bash
cd /opt/gaokao-helper
nohup java -jar -Xmx2g -Xms1g gaokao-helper-*.jar --spring.profiles.active=prod > app.log 2>&1 &
echo $! > app.pid
EOF

chmod +x /opt/gaokao-helper/start.sh

# 启动应用
./start.sh
```

#### 4. 配置Nginx反向代理
```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## 🤝 贡献指南

我们欢迎所有形式的贡献！无论是bug报告、功能建议、代码贡献还是文档改进。

### 如何贡献

1. **Fork项目**
   ```bash
   git clone https://github.com/your-username/gaokaoHelper.git
   ```

2. **创建特性分支**
   ```bash
   git checkout -b feature/amazing-feature
   ```

3. **提交更改**
   ```bash
   git commit -m 'Add some amazing feature'
   ```

4. **推送到分支**
   ```bash
   git push origin feature/amazing-feature
   ```

5. **创建Pull Request**

### 贡献类型

- 🐛 **Bug修复**: 发现并修复系统bug
- ✨ **新功能**: 添加新的功能特性
- 📚 **文档**: 改进项目文档
- 🎨 **界面**: 优化用户界面设计
- ⚡ **性能**: 提升系统性能
- 🔧 **配置**: 改进配置和部署

### 代码贡献规范

- 遵循项目的代码规范和风格
- 添加必要的单元测试
- 更新相关文档
- 确保所有测试通过
- 提供清晰的提交信息

## 📞 支持与反馈

### 获取帮助

- 📖 **文档**: 查看项目文档和API说明
- 💬 **讨论**: 在GitHub Discussions中提问
- 🐛 **Bug报告**: 在GitHub Issues中报告问题
- 💡 **功能建议**: 在GitHub Issues中提出建议

### 联系方式

- **项目维护者**: PLeiA
- **GitHub**: [https://github.com/Ae486/gaokaoHelper](https://github.com/Ae486/gaokaoHelper)


### 技术支持

- [Spring Boot](https://spring.io/projects/spring-boot) - 强大的Java应用框架
- [MySQL](https://www.mysql.com/) - 可靠的关系型数据库
- [DeepSeek](https://www.deepseek.com/) - AI对话服务支持
- [Font Awesome](https://fontawesome.com/) - 优秀的图标库
- [GSAP](https://greensock.com/gsap/) - 专业的动画库

### 数据来源

- 各省教育考试院官方数据
- 高校官方招生信息
- 权威教育统计数据

---

