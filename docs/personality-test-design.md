# 性格推荐专业系统设计方案

## 📊 数据库表设计

### 1. MBTI测试题表 (mbti_questions)
```sql
CREATE TABLE mbti_questions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    question_text TEXT NOT NULL COMMENT '题目内容',
    option_a VARCHAR(255) NOT NULL COMMENT '选项A',
    option_b VARCHAR(255) NOT NULL COMMENT '选项B',
    dimension ENUM('EI', 'SN', 'TF', 'JP') NOT NULL COMMENT '测试维度',
    a_score TINYINT NOT NULL COMMENT '选择A的得分(-1或1)',
    b_score TINYINT NOT NULL COMMENT '选择B的得分(-1或1)',
    question_order INT NOT NULL COMMENT '题目顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2. 霍兰德测试题表 (holland_questions)
```sql
CREATE TABLE holland_questions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    question_text TEXT NOT NULL COMMENT '题目内容',
    question_type ENUM('ACTIVITY', 'ABILITY', 'OCCUPATION') NOT NULL COMMENT '题目类型',
    dimension ENUM('R', 'I', 'A', 'S', 'E', 'C') NOT NULL COMMENT '兴趣维度',
    question_order INT NOT NULL COMMENT '题目顺序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 3. 专业信息表 (majors)
```sql
CREATE TABLE majors (
    id INT PRIMARY KEY AUTO_INCREMENT,
    major_name VARCHAR(100) NOT NULL COMMENT '专业名称',
    major_code VARCHAR(20) COMMENT '专业代码',
    category VARCHAR(50) NOT NULL COMMENT '专业类别',
    description TEXT COMMENT '专业描述',
    career_prospects TEXT COMMENT '就业前景',
    core_courses TEXT COMMENT '核心课程',
    skill_requirements TEXT COMMENT '技能要求',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 4. MBTI-专业匹配表 (mbti_major_mapping)
```sql
CREATE TABLE mbti_major_mapping (
    id INT PRIMARY KEY AUTO_INCREMENT,
    mbti_type CHAR(4) NOT NULL COMMENT 'MBTI类型',
    major_id INT NOT NULL COMMENT '专业ID',
    match_score DECIMAL(3,2) NOT NULL COMMENT '匹配度(0-1)',
    reason TEXT COMMENT '匹配原因',
    FOREIGN KEY (major_id) REFERENCES majors(id)
);
```

### 5. 霍兰德-专业匹配表 (holland_major_mapping)
```sql
CREATE TABLE holland_major_mapping (
    id INT PRIMARY KEY AUTO_INCREMENT,
    holland_code VARCHAR(3) NOT NULL COMMENT '霍兰德代码(如RIA)',
    major_id INT NOT NULL COMMENT '专业ID',
    match_score DECIMAL(3,2) NOT NULL COMMENT '匹配度(0-1)',
    reason TEXT COMMENT '匹配原因',
    FOREIGN KEY (major_id) REFERENCES majors(id)
);
```

### 6. 测试记录表 (personality_test_records)
```sql
CREATE TABLE personality_test_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT COMMENT '用户ID(可为空)',
    test_type ENUM('MBTI', 'HOLLAND') NOT NULL COMMENT '测试类型',
    test_result VARCHAR(10) NOT NULL COMMENT '测试结果',
    detailed_scores JSON COMMENT '详细得分',
    test_duration INT COMMENT '测试时长(秒)',
    ip_address VARCHAR(45) COMMENT 'IP地址',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🧠 算法设计

### MBTI计分算法
```
E/I维度: 外向性得分 = Σ(EI题目得分)
S/N维度: 感知方式得分 = Σ(SN题目得分)  
T/F维度: 决策方式得分 = Σ(TF题目得分)
J/P维度: 生活方式得分 = Σ(JP题目得分)

最终类型 = (E/I > 0 ? 'E' : 'I') + (S/N > 0 ? 'S' : 'N') + 
          (T/F > 0 ? 'T' : 'F') + (J/P > 0 ? 'J' : 'P')
```

### 霍兰德计分算法
```
R维度得分 = Σ(R类题目选择数)
I维度得分 = Σ(I类题目选择数)
A维度得分 = Σ(A类题目选择数)
S维度得分 = Σ(S类题目选择数)
E维度得分 = Σ(E类题目选择数)
C维度得分 = Σ(C类题目选择数)

霍兰德代码 = 得分最高的前3个维度组合
```

## 🎯 功能模块

### 1. 测试模块
- 题目展示与答题
- 进度条显示
- 答案保存与验证
- 测试时长统计

### 2. 结果分析模块
- 性格类型解读
- 维度得分雷达图
- 性格特征描述
- 优势与发展建议

### 3. 专业推荐模块
- 基于性格的专业匹配
- 匹配度排序
- 专业详细信息
- 推荐理由说明

### 4. 报告生成模块
- 个性化测试报告
- PDF导出功能
- 分享功能
- 历史记录查看

## 📱 前端页面设计

### 1. 测试选择页
- MBTI测试入口
- 霍兰德测试入口
- 测试说明与注意事项

### 2. 测试进行页
- 题目展示区域
- 选项选择区域
- 进度指示器
- 上一题/下一题按钮

### 3. 结果展示页
- 性格类型大标题
- 特征描述卡片
- 维度得分图表
- 专业推荐列表

### 4. 专业详情页
- 专业基本信息
- 课程设置
- 就业方向
- 相关院校推荐

## 🔧 技术栈

### 后端
- Spring Boot (REST API)
- MySQL (数据存储)
- Redis (缓存)
- JPA (数据访问)

### 前端
- HTML5/CSS3/JavaScript
- Chart.js (图表展示)
- GSAP (动画效果)
- 响应式设计

## 📈 实施计划

### 第一阶段：基础框架
1. 数据库表创建
2. 实体类和Repository
3. 基础API接口
4. 前端页面框架

### 第二阶段：测试功能
1. 题库数据导入
2. 测试逻辑实现
3. 计分算法开发
4. 测试页面完善

### 第三阶段：推荐功能
1. 专业数据导入
2. 匹配算法实现
3. 推荐页面开发
4. 结果展示优化

### 第四阶段：完善优化
1. 报告生成功能
2. 数据统计分析
3. 性能优化
4. 用户体验提升
