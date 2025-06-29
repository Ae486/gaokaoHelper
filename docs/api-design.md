# 高考志愿填报助手 API 接口设计

## 1. 用户认证模块

### 1.1 用户注册
```
POST /api/auth/register
Content-Type: application/json

Request Body:
{
    "username": "string",
    "password": "string",
    "email": "string",
    "phone": "string",
    "realName": "string",
    "provinceId": "integer",
    "subjectTypeId": "integer",
    "examYear": "integer"
}

Response:
{
    "code": 200,
    "message": "注册成功",
    "data": {
        "userId": "integer",
        "username": "string"
    }
}
```

### 1.2 用户登录
```
POST /api/auth/login
Content-Type: application/json

Request Body:
{
    "username": "string",
    "password": "string"
}

Response:
{
    "code": 200,
    "message": "登录成功",
    "data": {
        "token": "string",
        "userInfo": {
            "userId": "integer",
            "username": "string",
            "realName": "string",
            "provinceId": "integer",
            "subjectTypeId": "integer"
        }
    }
}
```

## 2. 录取概率预测模块

### 2.1 预测录取概率
```
POST /api/prediction/calculate
Authorization: Bearer {token}
Content-Type: application/json

Request Body:
{
    "universityId": "integer",
    "majorId": "integer",
    "userScore": "integer",
    "provinceId": "integer",
    "subjectTypeId": "integer",
    "year": "integer"
}

Response:
{
    "code": 200,
    "message": "预测成功",
    "data": {
        "probability": "float",
        "level": "string", // 冲刺/稳妥/保底
        "userRank": "integer",
        "historicalData": {
            "minScore": "integer",
            "avgScore": "integer",
            "minRank": "integer",
            "avgRank": "integer"
        },
        "analysis": "string"
    }
}
```

### 2.2 批量预测
```
POST /api/prediction/batch
Authorization: Bearer {token}
Content-Type: application/json

Request Body:
{
    "userScore": "integer",
    "provinceId": "integer",
    "subjectTypeId": "integer",
    "targets": [
        {
            "universityId": "integer",
            "majorId": "integer"
        }
    ]
}

Response:
{
    "code": 200,
    "message": "批量预测成功",
    "data": [
        {
            "universityId": "integer",
            "majorId": "integer",
            "probability": "float",
            "level": "string"
        }
    ]
}
```

## 3. 智能高校推荐模块

### 3.1 获取推荐高校
```
POST /api/recommendation/universities
Authorization: Bearer {token}
Content-Type: application/json

Request Body:
{
    "userScore": "integer",
    "provinceId": "integer",
    "subjectTypeId": "integer",
    "preferences": {
        "regions": ["string"],
        "universityTypes": ["string"],
        "majors": ["string"]
    },
    "recommendationType": "string", // all/rush/stable/safe
    "limit": "integer"
}

Response:
{
    "code": 200,
    "message": "推荐成功",
    "data": {
        "rush": [
            {
                "universityId": "integer",
                "universityName": "string",
                "majorId": "integer",
                "majorName": "string",
                "probability": "float",
                "score": "float",
                "reason": "string"
            }
        ],
        "stable": [...],
        "safe": [...]
    }
}
```

### 3.2 保存用户偏好
```
POST /api/recommendation/preferences
Authorization: Bearer {token}
Content-Type: application/json

Request Body:
{
    "preferredRegions": "string",
    "preferredUniversityTypes": "string",
    "preferredMajors": "string",
    "minScoreExpectation": "integer",
    "maxScoreExpectation": "integer"
}

Response:
{
    "code": 200,
    "message": "偏好保存成功"
}
```

## 4. 性格测试推荐专业模块

### 4.1 获取测试题目
```
GET /api/personality/test/{testType}
Authorization: Bearer {token}

Parameters:
- testType: mbti/holland/bigfive

Response:
{
    "code": 200,
    "message": "获取成功",
    "data": {
        "testId": "integer",
        "testName": "string",
        "description": "string",
        "questions": [
            {
                "id": "integer",
                "question": "string",
                "options": [
                    {
                        "id": "string",
                        "text": "string"
                    }
                ]
            }
        ]
    }
}
```

### 4.2 提交测试答案
```
POST /api/personality/test/submit
Authorization: Bearer {token}
Content-Type: application/json

Request Body:
{
    "testId": "integer",
    "answers": [
        {
            "questionId": "integer",
            "answerId": "string"
        }
    ]
}

Response:
{
    "code": 200,
    "message": "测试完成",
    "data": {
        "personalityType": "string",
        "scores": {
            "dimension1": "float",
            "dimension2": "float"
        },
        "recommendedMajors": [
            {
                "majorId": "integer",
                "majorName": "string",
                "matchScore": "float",
                "reason": "string"
            }
        ]
    }
}
```

## 5. 高校查询模块

### 5.1 高校搜索
```
GET /api/universities/search
Authorization: Bearer {token}

Parameters:
- keyword: string (可选)
- provinceId: integer (可选)
- universityType: string (可选)
- level: string (可选)
- page: integer (默认1)
- size: integer (默认20)

Response:
{
    "code": 200,
    "message": "查询成功",
    "data": {
        "total": "integer",
        "page": "integer",
        "size": "integer",
        "universities": [
            {
                "id": "integer",
                "name": "string",
                "code": "string",
                "type": "string",
                "level": "string",
                "location": "string",
                "website": "string",
                "description": "string"
            }
        ]
    }
}
```

### 5.2 高校详情
```
GET /api/universities/{id}
Authorization: Bearer {token}

Response:
{
    "code": 200,
    "message": "获取成功",
    "data": {
        "id": "integer",
        "name": "string",
        "code": "string",
        "type": "string",
        "level": "string",
        "location": "string",
        "website": "string",
        "description": "string",
        "majors": [
            {
                "majorId": "integer",
                "majorName": "string",
                "category": "string"
            }
        ],
        "rankings": [
            {
                "rankingType": "string",
                "year": "integer",
                "position": "integer",
                "source": "string"
            }
        ]
    }
}
```

## 6. 一分一段模块

### 6.1 查询分数排名
```
GET /api/score-ranking/query
Authorization: Bearer {token}

Parameters:
- year: integer
- provinceId: integer
- subjectCategoryId: integer
- minScore: integer (可选)
- maxScore: integer (可选)

Response:
{
    "code": 200,
    "message": "查询成功",
    "data": [
        {
            "score": "integer",
            "countAtScore": "integer",
            "cumulativeCount": "integer",
            "percentage": "float"
        }
    ]
}
```

### 6.2 分数位次转换
```
GET /api/score-ranking/convert
Authorization: Bearer {token}

Parameters:
- year: integer
- provinceId: integer
- subjectCategoryId: integer
- score: integer

Response:
{
    "code": 200,
    "message": "转换成功",
    "data": {
        "score": "integer",
        "rank": "integer",
        "percentage": "float",
        "totalCount": "integer"
    }
}
```

## 7. 同分去向分析模块

### 7.1 同分去向统计
```
GET /api/similar-score/analysis
Authorization: Bearer {token}

Parameters:
- score: integer
- provinceId: integer
- subjectCategoryId: integer
- year: integer (可选，默认最近年份)

Response:
{
    "code": 200,
    "message": "分析成功",
    "data": {
        "targetScore": "integer",
        "totalCount": "integer",
        "universityDistribution": [
            {
                "universityId": "integer",
                "universityName": "string",
                "count": "integer",
                "percentage": "float"
            }
        ],
        "majorDistribution": [
            {
                "majorId": "integer",
                "majorName": "string",
                "count": "integer",
                "percentage": "float"
            }
        ],
        "regionDistribution": [
            {
                "region": "string",
                "count": "integer",
                "percentage": "float"
            }
        ]
    }
}
```

## 8. AI对话助手模块

### 8.1 创建对话会话
```
POST /api/chat/session
Authorization: Bearer {token}

Response:
{
    "code": 200,
    "message": "会话创建成功",
    "data": {
        "sessionId": "string"
    }
}
```

### 8.2 发送消息
```
POST /api/chat/message
Authorization: Bearer {token}
Content-Type: application/json

Request Body:
{
    "sessionId": "string",
    "message": "string"
}

Response:
{
    "code": 200,
    "message": "回复成功",
    "data": {
        "messageId": "string",
        "reply": "string",
        "suggestions": ["string"]
    }
}
```

### 8.3 获取对话历史
```
GET /api/chat/history/{sessionId}
Authorization: Bearer {token}

Parameters:
- page: integer (默认1)
- size: integer (默认50)

Response:
{
    "code": 200,
    "message": "获取成功",
    "data": {
        "messages": [
            {
                "id": "string",
                "type": "user/assistant",
                "content": "string",
                "timestamp": "datetime"
            }
        ]
    }
}
```

## 通用响应格式

### 成功响应
```json
{
    "code": 200,
    "message": "操作成功",
    "data": {},
    "timestamp": "2024-01-01T00:00:00Z"
}
```

### 错误响应
```json
{
    "code": 400,
    "message": "参数错误",
    "error": "详细错误信息",
    "timestamp": "2024-01-01T00:00:00Z"
}
```

## HTTP状态码说明
- 200: 成功
- 400: 请求参数错误
- 401: 未授权
- 403: 禁止访问
- 404: 资源不存在
- 500: 服务器内部错误
