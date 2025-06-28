@echo off
echo ========================================
echo 高考助手数据库备份脚本
echo ========================================

set BACKUP_DIR=backup
set DATE_TIME=%date:~0,4%%date:~5,2%%date:~8,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set DATE_TIME=%DATE_TIME: =0%
set BACKUP_FILE=%BACKUP_DIR%\gaokaodb_backup_%DATE_TIME%.sql

echo 创建备份目录...
if not exist %BACKUP_DIR% mkdir %BACKUP_DIR%

echo 开始备份数据库 gaokaodb...
echo 备份文件: %BACKUP_FILE%

mysqldump -u root -p114514 --single-transaction --routines --triggers gaokaodb > %BACKUP_FILE%

if %errorlevel% equ 0 (
    echo ✅ 数据库备份成功！
    echo 备份文件位置: %BACKUP_FILE%
    echo 文件大小:
    dir %BACKUP_FILE%
) else (
    echo ❌ 数据库备份失败！
    echo 请检查数据库连接和权限
    pause
    exit /b 1
)

echo ========================================
echo 备份完成，按任意键继续...
pause
