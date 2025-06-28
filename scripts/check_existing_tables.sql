-- 检查现有表结构的安全脚本
-- 此脚本只查询，不会修改任何数据

USE gaokaodb;

-- 1. 显示所有现有表
SELECT '=== 现有数据库表 ===' as info;
SHOW TABLES;

-- 2. 检查是否存在性格测试相关的表（避免冲突）
SELECT '=== 检查性格测试表是否已存在 ===' as info;

SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN CONCAT('⚠️  表 ', table_name, ' 已存在！')
        ELSE CONCAT('✅ 表 ', 'mbti_questions', ' 不存在，可以安全创建')
    END as status
FROM information_schema.tables 
WHERE table_schema = 'gaokaodb' AND table_name = 'mbti_questions'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN CONCAT('⚠️  表 ', table_name, ' 已存在！')
        ELSE CONCAT('✅ 表 ', 'holland_questions', ' 不存在，可以安全创建')
    END as status
FROM information_schema.tables 
WHERE table_schema = 'gaokaodb' AND table_name = 'holland_questions'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN CONCAT('⚠️  表 ', table_name, ' 已存在！')
        ELSE CONCAT('✅ 表 ', 'majors', ' 不存在，可以安全创建')
    END as status
FROM information_schema.tables 
WHERE table_schema = 'gaokaodb' AND table_name = 'majors'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN CONCAT('⚠️  表 ', table_name, ' 已存在！')
        ELSE CONCAT('✅ 表 ', 'mbti_major_mapping', ' 不存在，可以安全创建')
    END as status
FROM information_schema.tables 
WHERE table_schema = 'gaokaodb' AND table_name = 'mbti_major_mapping'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN CONCAT('⚠️  表 ', table_name, ' 已存在！')
        ELSE CONCAT('✅ 表 ', 'holland_major_mapping', ' 不存在，可以安全创建')
    END as status
FROM information_schema.tables 
WHERE table_schema = 'gaokaodb' AND table_name = 'holland_major_mapping'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN CONCAT('⚠️  表 ', table_name, ' 已存在！')
        ELSE CONCAT('✅ 表 ', 'personality_test_records', ' 不存在，可以安全创建')
    END as status
FROM information_schema.tables 
WHERE table_schema = 'gaokaodb' AND table_name = 'personality_test_records'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN CONCAT('⚠️  表 ', table_name, ' 已存在！')
        ELSE CONCAT('✅ 表 ', 'test_answers', ' 不存在，可以安全创建')
    END as status
FROM information_schema.tables 
WHERE table_schema = 'gaokaodb' AND table_name = 'test_answers'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN CONCAT('⚠️  表 ', table_name, ' 已存在！')
        ELSE CONCAT('✅ 表 ', 'mbti_descriptions', ' 不存在，可以安全创建')
    END as status
FROM information_schema.tables 
WHERE table_schema = 'gaokaodb' AND table_name = 'mbti_descriptions'
UNION ALL
SELECT 
    CASE 
        WHEN COUNT(*) > 0 THEN CONCAT('⚠️  表 ', table_name, ' 已存在！')
        ELSE CONCAT('✅ 表 ', 'holland_descriptions', ' 不存在，可以安全创建')
    END as status
FROM information_schema.tables 
WHERE table_schema = 'gaokaodb' AND table_name = 'holland_descriptions';

-- 3. 显示现有表的记录数量（确保不会意外删除数据）
SELECT '=== 现有表的数据量统计 ===' as info;

SELECT 
    table_name as '表名',
    table_rows as '大约行数',
    ROUND(((data_length + index_length) / 1024 / 1024), 2) as '大小(MB)'
FROM information_schema.tables 
WHERE table_schema = 'gaokaodb' 
ORDER BY table_rows DESC;
