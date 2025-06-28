# 测试一分一段查询API的PowerShell脚本

Write-Host "=== 测试一分一段查询API ===" -ForegroundColor Green

# 1. 测试分数统计接口
Write-Host "`n1. 测试分数统计接口..." -ForegroundColor Yellow
$statisticsUrl = "http://localhost:8080/api/score-ranking/statistics?year=2024&provinceId=1&subjectCategoryId=1"
try {
    $response = Invoke-RestMethod -Uri $statisticsUrl -Method GET
    Write-Host "✅ 分数统计接口测试成功" -ForegroundColor Green
    Write-Host "最高分: $($response.data.maxScore)" -ForegroundColor Cyan
    Write-Host "最低分: $($response.data.minScore)" -ForegroundColor Cyan
    Write-Host "总人数: $($response.data.totalCount)" -ForegroundColor Cyan
    Write-Host "记录数: $($response.data.recordCount)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ 分数统计接口测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 2. 测试分数转位次接口
Write-Host "`n2. 测试分数转位次接口..." -ForegroundColor Yellow
$scoreToRankUrl = "http://localhost:8080/api/score-ranking/score-to-rank"
$scoreToRankData = @{
    year = 2024
    provinceId = 1
    subjectCategoryId = 1
    score = 550
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri $scoreToRankUrl -Method POST -Body $scoreToRankData -ContentType "application/json"
    Write-Host "✅ 分数转位次接口测试成功" -ForegroundColor Green
    Write-Host "输入分数: $($response.data.inputScore)" -ForegroundColor Cyan
    Write-Host "对应位次: $($response.data.rank)" -ForegroundColor Cyan
    Write-Host "本段人数: $($response.data.countAtScore)" -ForegroundColor Cyan
    Write-Host "是否精确匹配: $($response.data.exactMatch)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ 分数转位次接口测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 3. 测试位次转分数接口
Write-Host "`n3. 测试位次转分数接口..." -ForegroundColor Yellow
$rankToScoreUrl = "http://localhost:8080/api/score-ranking/rank-to-score"
$rankToScoreData = @{
    year = 2024
    provinceId = 1
    subjectCategoryId = 1
    rank = 10000
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri $rankToScoreUrl -Method POST -Body $rankToScoreData -ContentType "application/json"
    Write-Host "✅ 位次转分数接口测试成功" -ForegroundColor Green
    Write-Host "输入位次: $($response.data.inputRank)" -ForegroundColor Cyan
    Write-Host "对应分数: $($response.data.score)" -ForegroundColor Cyan
    Write-Host "本段人数: $($response.data.countAtScore)" -ForegroundColor Cyan
    Write-Host "是否精确匹配: $($response.data.exactMatch)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ 位次转分数接口测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 4. 测试分数分布查询接口
Write-Host "`n4. 测试分数分布查询接口..." -ForegroundColor Yellow
$distributionUrl = "http://localhost:8080/api/score-ranking/distribution"
$distributionData = @{
    year = 2024
    provinceId = 1
    subjectCategoryId = 1
    minScore = 600
    maxScore = 650
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri $distributionUrl -Method POST -Body $distributionData -ContentType "application/json"
    Write-Host "✅ 分数分布查询接口测试成功" -ForegroundColor Green
    Write-Host "查询年份: $($response.data.queryInfo.year)" -ForegroundColor Cyan
    Write-Host "省份名称: $($response.data.queryInfo.provinceName)" -ForegroundColor Cyan
    Write-Host "科类名称: $($response.data.queryInfo.subjectCategoryName)" -ForegroundColor Cyan
    Write-Host "分数分布记录数: $($response.data.scoreDistributions.Count)" -ForegroundColor Cyan
    
    if ($response.data.scoreDistributions.Count -gt 0) {
        Write-Host "前3条分数分布数据:" -ForegroundColor Cyan
        for ($i = 0; $i -lt [Math]::Min(3, $response.data.scoreDistributions.Count); $i++) {
            $item = $response.data.scoreDistributions[$i]
            Write-Host "  分数: $($item.score), 本段人数: $($item.countAtScore), 累计人数: $($item.cumulativeCount)" -ForegroundColor White
        }
    }
} catch {
    Write-Host "❌ 分数分布查询接口测试失败: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green
