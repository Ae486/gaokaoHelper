-- 安全的数据导入脚本
-- ⚠️  此脚本会检查表是否为空，避免重复导入数据

USE gaokaodb;

-- 检查并导入MBTI测试题数据
SELECT '=== 检查 MBTI 测试题数据 ===' as info;

SELECT 
    CASE 
        WHEN COUNT(*) = 0 THEN '✅ MBTI测试题表为空，可以安全导入数据'
        ELSE CONCAT('⚠️  MBTI测试题表已有 ', COUNT(*), ' 条数据，跳过导入')
    END as status
FROM mbti_questions;

-- 只有在表为空时才导入MBTI数据
SET @mbti_count = (SELECT COUNT(*) FROM mbti_questions);

-- 检查并导入霍兰德测试题数据
SELECT '=== 检查霍兰德测试题数据 ===' as info;

SELECT 
    CASE 
        WHEN COUNT(*) = 0 THEN '✅ 霍兰德测试题表为空，可以安全导入数据'
        ELSE CONCAT('⚠️  霍兰德测试题表已有 ', COUNT(*), ' 条数据，跳过导入')
    END as status
FROM holland_questions;

-- 检查并导入专业信息数据
SELECT '=== 检查专业信息数据 ===' as info;

SELECT 
    CASE 
        WHEN COUNT(*) = 0 THEN '✅ 专业信息表为空，可以安全导入数据'
        ELSE CONCAT('⚠️  专业信息表已有 ', COUNT(*), ' 条数据，跳过导入')
    END as status
FROM majors;

-- 检查并导入MBTI类型描述数据
SELECT '=== 检查MBTI类型描述数据 ===' as info;

SELECT 
    CASE 
        WHEN COUNT(*) = 0 THEN '✅ MBTI类型描述表为空，可以安全导入数据'
        ELSE CONCAT('⚠️  MBTI类型描述表已有 ', COUNT(*), ' 条数据，跳过导入')
    END as status
FROM mbti_descriptions;

-- 检查并导入霍兰德类型描述数据
SELECT '=== 检查霍兰德类型描述数据 ===' as info;

SELECT 
    CASE 
        WHEN COUNT(*) = 0 THEN '✅ 霍兰德类型描述表为空，可以安全导入数据'
        ELSE CONCAT('⚠️  霍兰德类型描述表已有 ', COUNT(*), ' 条数据，跳过导入')
    END as status
FROM holland_descriptions;

-- 显示导入指导信息
SELECT '=== 数据导入指导 ===' as info;
SELECT '1. 如果所有表都为空，请按以下顺序导入数据：' as step;
SELECT '   a) 先导入基础数据：personality_test_schema.sql (MBTI和霍兰德类型描述)' as substep;
SELECT '   b) 再导入测试题：mbti_questions.sql' as substep;
SELECT '   c) 然后导入：holland_questions.sql' as substep;
SELECT '   d) 导入专业数据：major_personality_mapping.sql (专业信息)' as substep;
SELECT '   e) 最后导入匹配关系：major_personality_mapping.sql (匹配数据)' as substep;
SELECT '2. 如果表已有数据，请谨慎操作，避免重复导入' as step;
