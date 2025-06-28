USE gaokaodb;

-- 1. MBTI测试题表
CREATE TABLE IF NOT EXISTS mbti_questions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    question_text TEXT NOT NULL,
    option_a VARCHAR(255) NOT NULL,
    option_b VARCHAR(255) NOT NULL,
    dimension ENUM('EI', 'SN', 'TF', 'JP') NOT NULL,
    a_score TINYINT NOT NULL,
    b_score TINYINT NOT NULL,
    question_order INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_dimension (dimension),
    INDEX idx_order (question_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 霍兰德测试题表
CREATE TABLE IF NOT EXISTS holland_questions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    question_text TEXT NOT NULL,
    question_type ENUM('ACTIVITY', 'ABILITY', 'OCCUPATION') NOT NULL,
    dimension ENUM('R', 'I', 'A', 'S', 'E', 'C') NOT NULL,
    question_order INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_dimension (dimension),
    INDEX idx_type (question_type),
    INDEX idx_order (question_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 专业信息表
CREATE TABLE IF NOT EXISTS majors (
    id INT PRIMARY KEY AUTO_INCREMENT,
    major_name VARCHAR(100) NOT NULL,
    major_code VARCHAR(20),
    category VARCHAR(50) NOT NULL,
    description TEXT,
    career_prospects TEXT,
    core_courses TEXT,
    skill_requirements TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_name (major_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
