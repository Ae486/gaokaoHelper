# 高考志愿填报助手系统

## 项目简介

高考志愿填报助手系统是一个基于Spring Boot + MySQL的Web应用，旨在为高考生提供科学的志愿填报建议和相关查询服务。

## 技术栈

- **后端框架**: Spring Boot 2.7.18
- **数据库**: MySQL 8.0
- **缓存**: Redis 6.x
- **Java版本**: JDK 17
- **构建工具**: Maven 3.x
- **ORM框架**: Spring Data JPA
- **安全框架**: Spring Security
- **API文档**: Swagger/OpenAPI 3

## 核心功能模块

1. **录取概率预测** - 基于历史数据预测考生被特定院校录取的可能性
2. **智能高校推荐** - 根据考生分数和偏好推荐合适的高校
3. **性格测试推荐专业** - 通过心理测评推荐适合的专业方向
4. **高校查询** - 提供高校基本信息的检索功能
5. **一分一段** - 展示各省份分数段人数分布情况
6. **同分去向** - 分析同等分数考生的录取去向
7. **AI对话助手** - 提供智能问答服务

## 数据库表结构

系统包含7个核心数据表：

1. `provinces` - 省份信息表
2. `subject_categories` - 考生科类表
3. `schools` - 院校信息表
4. `university_rankings` - 大学排名表
5. `provincial_rankings` - 省份一分一段表
6. `admission_scores` - 历年专业录取分数表
7. `users` - 用户核心信息表

## 项目结构

```
src/main/java/com/gaokao/helper/
├── GaokaoHelperApplication.java    # 启动类
├── config/                         # 配置类
├── entity/                         # 实体类
├── repository/                     # 数据访问层
├── service/                        # 业务逻辑层
│   └── impl/                       # 业务逻辑实现类
├── controller/                     # 控制器层
├── dto/                           # 数据传输对象
│   ├── request/                   # 请求DTO
│   └── response/                  # 响应DTO
├── common/                        # 公共类
├── util/                          # 工具类
└── algorithm/                     # 算法引擎
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 安装步骤

1. **克隆项目**
   ```bash
   git clone [项目地址]
   cd gaokao-helper
   ```

2. **配置数据库**
   - 创建MySQL数据库 `gaokao_helper`
   - 修改 `application.yml` 中的数据库连接配置

3. **配置Redis**
   - 启动Redis服务
   - 修改 `application.yml` 中的Redis连接配置

4. **编译运行**
   ```bash
   mvn clean compile
   mvn spring-boot:run
   ```

5. **访问应用**
   - 应用地址: http://localhost:8080
   - API文档: http://localhost:8080/swagger-ui.html

## 配置说明

### 数据库配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/gaokao_helper?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: root
    password: 123456
```

### Redis配置

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
```

## 开发规范

### 代码规范

- 使用Lombok简化代码
- 统一使用Result类封装返回结果
- 异常处理使用BusinessException
- 所有接口使用RESTful风格

### 数据库规范

- 表名使用下划线命名
- 字段名使用下划线命名
- 主键统一使用自增ID
- 必要字段添加注释

### API规范

- 统一使用 `/api` 前缀
- 使用HTTP状态码表示请求结果
- 返回数据统一使用Result包装
- 分页查询使用PageResult

## 开发状态

当前项目处于框架搭建阶段，已完成：

- ✅ 项目基础框架搭建
- ✅ 数据库实体类设计
- ✅ Repository接口定义
- ✅ 基础目录结构创建
- ⏳ 业务逻辑实现（待开发）
- ⏳ API接口实现（待开发）
- ⏳ 前端界面开发（待开发）

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

- 作者: PLeiA
- 邮箱: [您的邮箱]
- 项目地址: [项目仓库地址]
