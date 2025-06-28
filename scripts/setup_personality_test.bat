@echo off
chcp 65001 >nul
echo ========================================
echo 🧠 性格测试系统数据库安装脚本
echo ========================================
echo.

set DB_USER=root
set DB_PASSWORD=114514
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=gaokaodb

echo 📋 安装步骤：
echo 1. 备份现有数据库
echo 2. 检查现有表结构
echo 3. 创建新表（安全模式）
echo 4. 导入测试数据
echo.

echo ⚠️  重要提醒：
echo - 此脚本使用 IF NOT EXISTS 确保不会覆盖现有表
echo - 会先备份您的数据库
echo - 只会添加新表，不会修改现有数据
echo.

set /p confirm="确认继续安装？(y/N): "
if /i not "%confirm%"=="y" (
    echo 安装已取消
    pause
    exit /b 0
)

echo.
echo ========================================
echo 第1步：备份现有数据库
echo ========================================

call backup_database.bat
if %errorlevel% neq 0 (
    echo ❌ 数据库备份失败，安装终止
    pause
    exit /b 1
)

echo.
echo ========================================
echo 第2步：检查现有表结构
echo ========================================

echo 正在检查现有表...
mysql -u %DB_USER% -p%DB_PASSWORD% -h %DB_HOST% -P %DB_PORT% < scripts\check_existing_tables.sql

if %errorlevel% neq 0 (
    echo ❌ 检查表结构失败
    pause
    exit /b 1
)

echo.
echo ⚠️  请查看上面的检查结果：
echo - 如果看到 "⚠️  表 XXX 已存在"，表示该表已存在，不会被覆盖
echo - 如果看到 "✅ 表 XXX 不存在"，表示可以安全创建
echo.

set /p continue="查看检查结果后，确认继续？(y/N): "
if /i not "%continue%"=="y" (
    echo 安装已取消
    pause
    exit /b 0
)

echo.
echo ========================================
echo 第3步：创建表结构
echo ========================================

echo 正在创建性格测试相关表...
mysql -u %DB_USER% -p%DB_PASSWORD% -h %DB_HOST% -P %DB_PORT% < src\main\resources\data\personality_test_schema.sql

if %errorlevel% neq 0 (
    echo ❌ 创建表结构失败
    pause
    exit /b 1
)

echo ✅ 表结构创建完成

echo.
echo ========================================
echo 第4步：检查数据导入状态
echo ========================================

echo 正在检查哪些表需要导入数据...
mysql -u %DB_USER% -p%DB_PASSWORD% -h %DB_HOST% -P %DB_PORT% < scripts\safe_import_data.sql

echo.
echo ⚠️  请查看上面的检查结果，确定哪些表需要导入数据
echo.

set /p import_data="是否导入测试数据？(y/N): "
if /i not "%import_data%"=="y" (
    echo 跳过数据导入，只创建了表结构
    goto :success
)

echo.
echo ========================================
echo 第5步：导入测试数据
echo ========================================

echo 正在导入MBTI测试题...
mysql -u %DB_USER% -p%DB_PASSWORD% -h %DB_HOST% -P %DB_PORT% < src\main\resources\data\mbti_questions.sql
if %errorlevel% neq 0 (
    echo ⚠️  MBTI测试题导入失败（可能已存在数据）
)

echo 正在导入霍兰德测试题...
mysql -u %DB_USER% -p%DB_PASSWORD% -h %DB_HOST% -P %DB_PORT% < src\main\resources\data\holland_questions.sql
if %errorlevel% neq 0 (
    echo ⚠️  霍兰德测试题导入失败（可能已存在数据）
)

echo 正在导入专业信息和匹配数据...
mysql -u %DB_USER% -p%DB_PASSWORD% -h %DB_HOST% -P %DB_PORT% < src\main\resources\data\major_personality_mapping.sql
if %errorlevel% neq 0 (
    echo ⚠️  专业数据导入失败（可能已存在数据）
)

:success
echo.
echo ========================================
echo ✅ 安装完成！
echo ========================================
echo.
echo 📊 安装结果：
echo - 数据库已备份到 backup\ 目录
echo - 性格测试相关表已创建
echo - 测试数据已导入（如果表为空）
echo.
echo 🚀 下一步：
echo 1. 检查应用程序是否能正常连接数据库
echo 2. 开始开发性格测试功能的后端代码
echo 3. 创建前端测试页面
echo.

pause
