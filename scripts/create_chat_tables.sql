-- 创建聊天相关数据表
-- 执行前请确保数据库连接正常

USE gaokaodb;

-- 创建聊天会话表
CREATE TABLE IF NOT EXISTS chat_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '会话ID',
    session_id VARCHAR(100) NOT NULL UNIQUE COMMENT '会话唯一标识符',
    title VARCHAR(200) DEFAULT '新对话' COMMENT '会话标题',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    status VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '会话状态：active-活跃, archived-归档, deleted-已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_session_id (session_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_updated_at (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天会话表';

-- 创建聊天消息表
CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    session_id BIGINT NOT NULL COMMENT '会话ID',
    message_type VARCHAR(20) NOT NULL COMMENT '消息类型：user-用户消息, assistant-AI助手消息, system-系统消息',
    role VARCHAR(20) DEFAULT NULL COMMENT '消息角色（用于AI API）',
    content TEXT COMMENT '消息内容',
    status VARCHAR(20) NOT NULL DEFAULT 'sent' COMMENT '消息状态：sent-已发送, delivered-已送达, failed-发送失败',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_session_id (session_id),
    INDEX idx_message_type (message_type),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (session_id) REFERENCES chat_sessions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天消息表';

-- 插入测试数据（可选）
-- INSERT INTO chat_sessions (session_id, title, user_id, status) VALUES 
-- ('test-session-001', '测试对话', 1, 'active');

-- INSERT INTO chat_messages (session_id, message_type, role, content, status) VALUES 
-- (1, 'user', 'user', '你好，我想了解一下计算机专业', 'sent'),
-- (1, 'assistant', 'assistant', '您好！计算机专业是一个非常热门的专业，涵盖了软件开发、人工智能、网络安全等多个方向。请问您想了解哪个具体方面呢？', 'sent');

-- 查看表结构
DESCRIBE chat_sessions;
DESCRIBE chat_messages;

-- 查看表数据
SELECT COUNT(*) as session_count FROM chat_sessions;
SELECT COUNT(*) as message_count FROM chat_messages;
