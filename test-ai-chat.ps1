# AI对话助手API测试脚本
# 使用PowerShell测试DeepSeek集成的AI对话功能

Write-Host "=== AI对话助手API测试 ===" -ForegroundColor Green

$baseUrl = "http://localhost:8080"
$username = "testuser"
$password = "Test123456"
$token = $null
$sessionId = $null

# 1. 用户登录获取Token
Write-Host "`n1. 用户登录..." -ForegroundColor Yellow
$loginData = @{
    username = $username
    password = $password
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method POST -Body $loginData -ContentType "application/json"
    if ($loginResponse.code -eq 200) {
        $token = $loginResponse.data.token
        Write-Host "✅ 登录成功，获取到Token" -ForegroundColor Green
        Write-Host "Token: $($token.Substring(0, 20))..." -ForegroundColor Cyan
    } else {
        Write-Host "❌ 登录失败: $($loginResponse.message)" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ 登录请求失败: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 2. 创建聊天会话
Write-Host "`n2. 创建聊天会话..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

try {
    $sessionResponse = Invoke-RestMethod -Uri "$baseUrl/api/chat/session" -Method POST -Headers $headers
    if ($sessionResponse.code -eq 200) {
        $sessionId = $sessionResponse.data.sessionId
        Write-Host "✅ 会话创建成功" -ForegroundColor Green
        Write-Host "会话ID: $sessionId" -ForegroundColor Cyan
        Write-Host "会话标题: $($sessionResponse.data.title)" -ForegroundColor Cyan
    } else {
        Write-Host "❌ 创建会话失败: $($sessionResponse.message)" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "❌ 创建会话请求失败: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 3. 发送测试消息
Write-Host "`n3. 发送测试消息..." -ForegroundColor Yellow

$testMessages = @(
    "你好，我是一名高考生，想了解一下计算机专业",
    "计算机专业的就业前景如何？",
    "推荐一些计算机专业比较好的大学"
)

foreach ($message in $testMessages) {
    Write-Host "`n发送消息: $message" -ForegroundColor White
    
    $messageData = @{
        sessionId = $sessionId
        message = $message
        useContext = $true
        contextSize = 10
    } | ConvertTo-Json

    try {
        $messageResponse = Invoke-RestMethod -Uri "$baseUrl/api/chat/message" -Method POST -Body $messageData -Headers $headers
        if ($messageResponse.code -eq 200) {
            Write-Host "✅ 消息发送成功" -ForegroundColor Green
            Write-Host "AI回复: $($messageResponse.data.reply)" -ForegroundColor Cyan
            
            if ($messageResponse.data.suggestions -and $messageResponse.data.suggestions.Count -gt 0) {
                Write-Host "建议问题:" -ForegroundColor Yellow
                foreach ($suggestion in $messageResponse.data.suggestions) {
                    Write-Host "  - $suggestion" -ForegroundColor White
                }
            }
        } else {
            Write-Host "❌ 消息发送失败: $($messageResponse.message)" -ForegroundColor Red
        }
    } catch {
        Write-Host "❌ 消息发送请求失败: $($_.Exception.Message)" -ForegroundColor Red
    }
    
    # 等待一下再发送下一条消息
    Start-Sleep -Seconds 2
}

# 4. 获取会话列表
Write-Host "`n4. 获取会话列表..." -ForegroundColor Yellow
try {
    $sessionsResponse = Invoke-RestMethod -Uri "$baseUrl/api/chat/sessions" -Method GET -Headers $headers
    if ($sessionsResponse.code -eq 200) {
        Write-Host "✅ 获取会话列表成功" -ForegroundColor Green
        Write-Host "会话数量: $($sessionsResponse.data.Count)" -ForegroundColor Cyan
        
        foreach ($session in $sessionsResponse.data) {
            Write-Host "会话: $($session.title) (ID: $($session.sessionId), 消息数: $($session.messageCount))" -ForegroundColor White
        }
    } else {
        Write-Host "❌ 获取会话列表失败: $($sessionsResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ 获取会话列表请求失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 5. 获取聊天历史
Write-Host "`n5. 获取聊天历史..." -ForegroundColor Yellow
try {
    $historyResponse = Invoke-RestMethod -Uri "$baseUrl/api/chat/history/$sessionId" -Method GET -Headers $headers
    if ($historyResponse.code -eq 200) {
        Write-Host "✅ 获取聊天历史成功" -ForegroundColor Green
        Write-Host "历史消息数量: $($historyResponse.data.Count)" -ForegroundColor Cyan
        
        foreach ($msg in $historyResponse.data) {
            $role = if ($msg.messageType -eq "user") { "用户" } else { "AI" }
            $content = if ($msg.content.Length -gt 50) { $msg.content.Substring(0, 50) + "..." } else { $msg.content }
            Write-Host "$role: $content" -ForegroundColor White
        }
    } else {
        Write-Host "❌ 获取聊天历史失败: $($historyResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ 获取聊天历史请求失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 6. 更新会话标题
Write-Host "`n6. 更新会话标题..." -ForegroundColor Yellow
$titleData = @{
    title = "计算机专业咨询"
} | ConvertTo-Json

try {
    $titleResponse = Invoke-RestMethod -Uri "$baseUrl/api/chat/session/$sessionId/title" -Method PUT -Body $titleData -Headers $headers
    if ($titleResponse.code -eq 200) {
        Write-Host "✅ 会话标题更新成功" -ForegroundColor Green
    } else {
        Write-Host "❌ 更新会话标题失败: $($titleResponse.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ 更新会话标题请求失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green
Write-Host "注意事项:" -ForegroundColor Yellow
Write-Host "1. 确保已配置DeepSeek API密钥" -ForegroundColor White
Write-Host "2. 确保数据库中存在测试用户" -ForegroundColor White
Write-Host "3. 确保聊天相关数据表已创建" -ForegroundColor White
Write-Host "4. 可以访问 http://localhost:8080/ai-chat-test.html 进行Web界面测试" -ForegroundColor White
