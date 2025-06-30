// MBTI测试JavaScript
class MBTITest {
    constructor() {
        this.questions = [];
        this.answers = [];
        this.currentQuestionIndex = 0;
        this.startTime = null;
        this.testResult = null;

        this.init();
    }

    async init() {
        try {
            await this.loadQuestions();
        } catch (error) {
            console.error('初始化失败:', error);
            this.showError('加载测试题目失败，请刷新页面重试');
        }
    }

    async loadQuestions() {
        try {
            const response = await fetch('/api/personality-test/mbti/questions');
            const result = await response.json();

            if (result.code === 200) {
                this.questions = result.data;
                console.log(`加载了 ${this.questions.length} 道MBTI测试题`);
            } else {
                throw new Error(result.message || '加载题目失败');
            }
        } catch (error) {
            console.error('加载题目失败:', error);
            throw error;
        }
    }

    startTest() {
        if (this.questions.length === 0) {
            this.showError('题目尚未加载完成，请稍候再试');
            return;
        }

        this.startTime = Date.now();
        this.answers = new Array(this.questions.length).fill(null);
        this.currentQuestionIndex = 0;

        this.showPage('test-page');
        this.displayQuestion();
    }



    displayQuestion() {
        const question = this.questions[this.currentQuestionIndex];
        if (!question) return;

        // 更新题目信息
        document.getElementById('question-number').textContent = `第 ${this.currentQuestionIndex + 1} 题`;
        document.getElementById('question-text').textContent = question.questionText;
        document.getElementById('option-a-text').textContent = question.optionA;
        document.getElementById('option-b-text').textContent = question.optionB;

        // 更新进度
        const progress = ((this.currentQuestionIndex + 1) / this.questions.length) * 100;
        document.getElementById('progress-fill').style.width = `${progress}%`;
        document.getElementById('progress-text').textContent = `${this.currentQuestionIndex + 1} / ${this.questions.length}`;

        // 清除选择状态
        this.clearSelection();

        // 恢复之前的选择
        const previousAnswer = this.answers[this.currentQuestionIndex];
        if (previousAnswer) {
            this.selectOption(previousAnswer, false);
        }

        // 更新导航按钮状态
        this.updateNavigationButtons();
    }

    selectOption(option, updateAnswer = true) {
        console.log('MBTI selectOption called with:', option, updateAnswer);

        // 清除之前的选择
        this.clearSelection();

        // 设置新的选择
        const optionElement = document.getElementById(`option-${option.toLowerCase()}`);
        if (optionElement) {
            optionElement.classList.add('selected');
            console.log('Option element found and selected:', optionElement);
        } else {
            console.error('Option element not found for:', `option-${option.toLowerCase()}`);
        }

        if (updateAnswer) {
            this.answers[this.currentQuestionIndex] = option;
            this.updateNavigationButtons();

            // 添加选择动画效果
            if (optionElement) {
                optionElement.style.transform = 'scale(1.05)';
                setTimeout(() => {
                    optionElement.style.transform = 'scale(1)';
                }, 150);
            }

            console.log('Setting timeout for auto advance...');
            // 自动跳转到下一题（延迟500ms以显示选择效果）
            setTimeout(() => {
                console.log('Auto advance timeout triggered');
                this.autoNextQuestion();
            }, 500);
        }
    }

    autoNextQuestion() {
        console.log('autoNextQuestion called, current index:', this.currentQuestionIndex, 'total questions:', this.questions.length);

        // 如果不是最后一题，自动跳转到下一题
        if (this.currentQuestionIndex < this.questions.length - 1) {
            console.log('Moving to next question...');
            this.currentQuestionIndex++;
            this.displayQuestion();

            // 添加页面切换动画
            const questionContainer = document.querySelector('.question-container');
            if (questionContainer) {
                questionContainer.style.opacity = '0.7';
                questionContainer.style.transform = 'translateX(20px)';
                setTimeout(() => {
                    questionContainer.style.opacity = '1';
                    questionContainer.style.transform = 'translateX(0)';
                }, 100);
            }
        } else {
            console.log('Last question, submitting test...');
            // 最后一题，自动提交测试
            setTimeout(() => {
                this.submitTest();
            }, 300);
        }
    }

    clearSelection() {
        document.getElementById('option-a').classList.remove('selected');
        document.getElementById('option-b').classList.remove('selected');
    }

    updateNavigationButtons() {
        const prevBtn = document.getElementById('prev-btn');
        const nextBtn = document.getElementById('next-btn');

        // 上一题按钮
        prevBtn.disabled = this.currentQuestionIndex === 0;

        // 下一题按钮
        const hasAnswer = this.answers[this.currentQuestionIndex] !== null;
        nextBtn.disabled = !hasAnswer;

        // 如果是最后一题，改变按钮文本
        if (this.currentQuestionIndex === this.questions.length - 1) {
            nextBtn.innerHTML = '<i class="fas fa-check"></i> 完成测试';
        } else {
            nextBtn.innerHTML = '下一题 <i class="fas fa-chevron-right"></i>';
        }
    }

    previousQuestion() {
        if (this.currentQuestionIndex > 0) {
            this.currentQuestionIndex--;
            this.displayQuestion();
        }
    }

    nextQuestion() {
        if (this.answers[this.currentQuestionIndex] === null) {
            return;
        }

        if (this.currentQuestionIndex < this.questions.length - 1) {
            this.currentQuestionIndex++;
            this.displayQuestion();
        } else {
            // 完成测试
            this.submitTest();
        }
    }

    async submitTest() {
        try {
            this.showPage('loading-page');
            this.startLoadingAnimation();

            const testDuration = Math.floor((Date.now() - this.startTime) / 1000);
            
            const requestData = {
                testType: 'MBTI',
                answers: this.questions.map((question, index) => ({
                    questionId: question.id,
                    answer: this.answers[index]
                })),
                testDuration: testDuration
            };

            const response = await fetch('/api/personality-test/mbti/submit', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestData)
            });

            const result = await response.json();

            if (result.code === 200) {
                this.testResult = result.data;
                setTimeout(() => {
                    this.showResult();
                }, 2000); // 延迟显示结果，增加仪式感
            } else {
                throw new Error(result.message || '提交测试失败');
            }
        } catch (error) {
            console.error('提交测试失败:', error);
            this.showError('提交测试失败，请重试');
        }
    }

    startLoadingAnimation() {
        const loadingFill = document.getElementById('loading-fill');
        const loadingText = document.getElementById('loading-text');
        
        const messages = [
            '分析你的性格维度...',
            '计算MBTI类型...',
            '匹配适合的专业...',
            '生成个性化报告...'
        ];
        
        let messageIndex = 0;
        const interval = setInterval(() => {
            if (messageIndex < messages.length) {
                loadingText.textContent = messages[messageIndex];
                messageIndex++;
            } else {
                clearInterval(interval);
            }
        }, 500);
    }

    showResult() {
        this.showPage('result-page');
        this.displayTestResult();
    }

    displayTestResult() {
        if (!this.testResult) return;

        // 显示MBTI类型
        document.getElementById('mbti-type').textContent = this.testResult.testResult;
        document.getElementById('type-name').textContent = this.testResult.typeName || '性格类型';
        document.getElementById('type-description').textContent = this.testResult.typeDescription || '正在分析你的性格特征...';

        // 显示维度分析
        this.displayDimensionAnalysis();

        // 显示推荐专业
        this.displayRecommendedMajors();
    }

    displayDimensionAnalysis() {
        const dimensionBars = document.getElementById('dimension-bars');
        const dimensions = this.testResult.dimensionPercentages || {};
        
        const dimensionNames = {
            'EI': { name: '外向 vs 内向', positive: '外向(E)', negative: '内向(I)' },
            'SN': { name: '感觉 vs 直觉', positive: '感觉(S)', negative: '直觉(N)' },
            'TF': { name: '思考 vs 情感', positive: '思考(T)', negative: '情感(F)' },
            'JP': { name: '判断 vs 感知', positive: '判断(J)', negative: '感知(P)' }
        };

        dimensionBars.innerHTML = '';
        
        Object.entries(dimensionNames).forEach(([key, info]) => {
            const score = this.testResult.dimensionScores[key] || 0;
            const percentage = Math.abs(score) / 15 * 100;
            const isPositive = score > 0;
            
            const barHTML = `
                <div class="dimension-bar">
                    <div class="dimension-info">
                        <span class="dimension-title">${info.name}</span>
                        <span class="dimension-result">${isPositive ? info.positive : info.negative}</span>
                    </div>
                    <div class="bar-container">
                        <div class="bar-fill ${isPositive ? 'positive' : 'negative'}" 
                             style="width: ${percentage}%"></div>
                    </div>
                    <span class="percentage">${Math.round(percentage)}%</span>
                </div>
            `;
            dimensionBars.innerHTML += barHTML;
        });
    }

    displayRecommendedMajors() {
        const majorsList = document.getElementById('majors-list');
        const majors = this.testResult.recommendedMajors || [];
        
        if (majors.length === 0) {
            majorsList.innerHTML = '<p class="no-majors">正在为你匹配适合的专业...</p>';
            return;
        }

        majorsList.innerHTML = majors.map(major => `
            <div class="major-card">
                <div class="major-header">
                    <h4>${major.majorName}</h4>
                    <span class="match-score">${Math.round(major.matchScore * 100)}%匹配</span>
                </div>
                <p class="major-category">${major.category}</p>
                <p class="major-reason">${major.reason}</p>
            </div>
        `).join('');
    }

    showPage(pageId) {
        document.querySelectorAll('.page').forEach(page => {
            page.classList.remove('active');
        });
        document.getElementById(pageId).classList.add('active');
    }

    showError(message) {
        alert(message); // 简单的错误提示，可以后续优化
    }

    goBack() {
        this.showPage('intro-page');
    }

    retakeTest() {
        this.answers = [];
        this.currentQuestionIndex = 0;
        this.testResult = null;
        this.showPage('intro-page');
    }

    async viewDetailedReport() {
        if (!this.testResult) {
            alert('没有测试结果数据');
            return;
        }

        try {
            // 显示加载状态
            this.showReportModal();
            this.showReportLoading();

            console.log('开始生成详细报告...');
            const startTime = Date.now();

            // 调用后端API生成详细报告
            const response = await fetch('/api/personality-test/detailed-report', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token') || sessionStorage.getItem('token')}`
                },
                body: JSON.stringify(this.testResult)
            });

            const result = await response.json();
            const endTime = Date.now();
            const duration = endTime - startTime;

            console.log(`详细报告生成完成，耗时: ${duration}ms`);

            if (result.code === 200) {
                // 显示报告内容
                this.showReportContent(result.data);
            } else {
                throw new Error(result.message || '生成报告失败');
            }
        } catch (error) {
            console.error('生成详细报告失败:', error);
            let errorMessage = '生成报告失败，请稍后重试';

            // 根据错误类型提供更具体的提示
            if (error.message.includes('timeout') || error.message.includes('超时')) {
                errorMessage = '报告生成超时，AI服务繁忙，请稍后重试';
            } else if (error.message.includes('网络')) {
                errorMessage = '网络连接异常，请检查网络后重试';
            } else if (error.message.includes('认证') || error.message.includes('token')) {
                errorMessage = '登录已过期，请重新登录后重试';
            }

            this.showReportError(errorMessage);
        }
    }

    goToRecommendation() {
        window.location.href = '/recommendation-new.html';
    }

    showReportModal() {
        showReportModal();
    }

    showReportLoading() {
        showReportLoading();
    }

    showReportContent(content) {
        showReportContent(content);
    }

    showReportError(message) {
        showReportError(message);
    }
}

// 全局函数，供HTML调用
let mbtiTest;

document.addEventListener('DOMContentLoaded', function() {
    mbtiTest = new MBTITest();
});

function startTest() {
    mbtiTest.startTest();
}

function selectOption(option) {
    console.log('Global selectOption called with:', option);
    if (!mbtiTest) {
        console.error('mbtiTest is not initialized!');
        return;
    }
    mbtiTest.selectOption(option);
}

function previousQuestion() {
    mbtiTest.previousQuestion();
}

function nextQuestion() {
    mbtiTest.nextQuestion();
}

function goBack() {
    mbtiTest.goBack();
}

function retakeTest() {
    mbtiTest.retakeTest();
}

function viewDetailedReport() {
    mbtiTest.viewDetailedReport();
}

function goToRecommendation() {
    mbtiTest.goToRecommendation();
}

// 报告弹窗相关函数
function showReportModal() {
    const modal = document.getElementById('report-modal');
    if (modal) {
        modal.style.display = 'flex';
    }
}

function hideReportModal() {
    const modal = document.getElementById('report-modal');
    if (modal) {
        modal.style.display = 'none';
    }
}

function showReportLoading() {
    const loadingDiv = document.getElementById('report-loading');
    const contentDiv = document.getElementById('report-content');
    const errorDiv = document.getElementById('report-error');

    if (loadingDiv) loadingDiv.style.display = 'block';
    if (contentDiv) contentDiv.style.display = 'none';
    if (errorDiv) errorDiv.style.display = 'none';
}

function showReportContent(content) {
    const loadingDiv = document.getElementById('report-loading');
    const contentDiv = document.getElementById('report-content');
    const errorDiv = document.getElementById('report-error');

    if (loadingDiv) loadingDiv.style.display = 'none';
    if (contentDiv) {
        contentDiv.style.display = 'block';
        // 将AI生成的报告内容转换为HTML格式
        const formattedContent = formatReportContent(content);
        contentDiv.innerHTML = formattedContent;
    }
    if (errorDiv) errorDiv.style.display = 'none';
}

function showReportError(message) {
    const loadingDiv = document.getElementById('report-loading');
    const contentDiv = document.getElementById('report-content');
    const errorDiv = document.getElementById('report-error');

    if (loadingDiv) loadingDiv.style.display = 'none';
    if (contentDiv) contentDiv.style.display = 'none';
    if (errorDiv) {
        errorDiv.style.display = 'block';
        errorDiv.innerHTML = `<p class="error-message">${message}</p>`;
    }
}

function formatReportContent(content) {
    // 将纯文本转换为HTML格式
    return content
        .replace(/\n\n/g, '</p><p>')
        .replace(/\n/g, '<br>')
        .replace(/^/, '<p>')
        .replace(/$/, '</p>')
        .replace(/(\d+\.\s)/g, '<strong>$1</strong>')
        .replace(/【([^】]+)】/g, '<h3>$1</h3>');
}
