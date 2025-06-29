# AI对话助手简单测试脚本

Write-Host "=== AI对话助手测试 ===" -ForegroundColor Green

$baseUrl = "http://localhost:8080"
$username = "testuser"
$password = "Test123456"

# 1. 登录获取Token
Write-Host "`n1. 用户登录..." -ForegroundColor Yellow
$loginData = @{
    username = $username
    password = $password
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -Body $loginData -ContentType "application/json"
    if ($loginResponse.code -eq 200) {
        $token = $loginResponse.data.token
        Write-Host "登录成功" -ForegroundColor Green
    } else {
        Write-Host "登录失败: $($loginResponse.message)" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "登录请求失败: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 2. 发送AI对话消息
Write-Host "`n2. 发送AI对话消息..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$messageData = @{
    message = "你好，我想了解计算机专业"
    useContext = $true
    contextSize = 10
} | ConvertTo-Json

try {
    $messageResponse = Invoke-RestMethod -Uri "$baseUrl/api/chat/message" -Method POST -Body $messageData -Headers $headers
    if ($messageResponse.code -eq 200) {
        Write-Host "消息发送成功" -ForegroundColor Green
        Write-Host "AI回复: $($messageResponse.data.reply)" -ForegroundColor Cyan
        Write-Host "会话ID: $($messageResponse.data.sessionId)" -ForegroundColor Yellow
    } else {
        Write-Host "消息发送失败: $($messageResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "消息发送请求失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green
