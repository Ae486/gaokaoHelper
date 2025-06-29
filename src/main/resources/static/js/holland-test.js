// 霍兰德测试JavaScript
class HollandTest {
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
            const response = await fetch('/api/personality-test/holland/questions');
            const result = await response.json();

            if (result.code === 200) {
                this.questions = result.data;
                console.log(`加载了 ${this.questions.length} 道霍兰德测试题`);
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
        console.log('Holland selectOption called with:', option, updateAnswer);

        // 清除之前的选择
        this.clearSelection();

        // 设置新的选择 - 修复ID匹配问题
        const optionElement = option === 'YES' ?
            document.getElementById('option-yes') :
            document.getElementById('option-no');

        if (optionElement) {
            optionElement.classList.add('selected');
            console.log('Holland option element found and selected:', optionElement);
        } else {
            console.error('Holland option element not found for:', option);
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

            console.log('Holland setting timeout for auto advance...');
            // 自动跳转到下一题（延迟500ms以显示选择效果）
            setTimeout(() => {
                console.log('Holland auto advance timeout triggered');
                this.autoNextQuestion();
            }, 500);
        }
    }

    autoNextQuestion() {
        console.log('Holland autoNextQuestion called, current index:', this.currentQuestionIndex, 'total questions:', this.questions.length);

        // 如果不是最后一题，自动跳转到下一题
        if (this.currentQuestionIndex < this.questions.length - 1) {
            console.log('Holland moving to next question...');
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
            console.log('Holland last question, submitting test...');
            // 最后一题，自动提交测试
            setTimeout(() => {
                this.submitTest();
            }, 300);
        }
    }

    clearSelection() {
        document.getElementById('option-yes').classList.remove('selected');
        document.getElementById('option-no').classList.remove('selected');
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
                testType: 'HOLLAND',
                answers: this.questions.map((question, index) => ({
                    questionId: question.id,
                    answer: this.answers[index]
                })),
                testDuration: testDuration
            };

            const response = await fetch('/api/personality-test/holland/submit', {
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
            '分析你的兴趣偏好...',
            '计算霍兰德代码...',
            '匹配职业方向...',
            '生成兴趣报告...'
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

        // 显示霍兰德代码
        const hollandCode = this.testResult.testResult;
        document.getElementById('holland-code').textContent = hollandCode;
        
        // 生成代码描述
        const codeDescription = this.generateCodeDescription(hollandCode);
        document.getElementById('code-description').textContent = codeDescription;
        document.getElementById('type-summary').textContent = this.testResult.typeDescription || '正在分析你的兴趣特征...';

        // 显示兴趣分析
        this.displayInterestChart();

        // 显示推荐专业
        this.displayRecommendedMajors();
    }

    generateCodeDescription(code) {
        const typeNames = {
            'R': '现实型',
            'I': '研究型',
            'A': '艺术型',
            'S': '社会型',
            'E': '企业型',
            'C': '常规型'
        };

        return code.split('').map(char => typeNames[char] || char).join('-');
    }

    displayInterestChart() {
        const radarChart = document.getElementById('radar-chart');
        const scores = this.testResult.dimensionScores || {};
        
        const typeInfo = {
            'R': { name: '现实型', color: '#ff9a9e' },
            'I': { name: '研究型', color: '#a8edea' },
            'A': { name: '艺术型', color: '#ffecd2' },
            'S': { name: '社会型', color: '#ff8a80' },
            'E': { name: '企业型', color: '#667eea' },
            'C': { name: '常规型', color: '#f093fb' }
        };

        radarChart.innerHTML = '';
        
        Object.entries(typeInfo).forEach(([key, info]) => {
            const score = scores[key] || 0;
            const percentage = (score / 10) * 100; // 每个维度最多10分
            
            const barHTML = `
                <div class="interest-bar">
                    <div class="interest-info">
                        <span class="interest-label">${key}</span>
                        <span class="interest-name">${info.name}</span>
                    </div>
                    <div class="bar-container">
                        <div class="bar-fill" 
                             style="width: ${percentage}%; background: ${info.color}"></div>
                    </div>
                    <span class="score">${score}/10</span>
                </div>
            `;
            radarChart.innerHTML += barHTML;
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

            // 调用后端API生成详细报告
            const response = await fetch('/api/personality-test/detailed-report', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(this.testResult)
            });

            const result = await response.json();

            if (result.code === 200) {
                // 显示报告内容
                this.showReportContent(result.data);
            } else {
                throw new Error(result.message || '生成报告失败');
            }
        } catch (error) {
            console.error('生成详细报告失败:', error);
            this.showReportError('生成报告失败，请稍后重试');
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
let hollandTest;

document.addEventListener('DOMContentLoaded', function() {
    hollandTest = new HollandTest();
});

function startTest() {
    hollandTest.startTest();
}

function selectOption(option) {
    console.log('Global Holland selectOption called with:', option);
    if (!hollandTest) {
        console.error('hollandTest is not initialized!');
        return;
    }
    hollandTest.selectOption(option);
}

function previousQuestion() {
    hollandTest.previousQuestion();
}

function nextQuestion() {
    hollandTest.nextQuestion();
}

function goBack() {
    hollandTest.goBack();
}

function retakeTest() {
    hollandTest.retakeTest();
}

function viewDetailedReport() {
    hollandTest.viewDetailedReport();
}

function goToRecommendation() {
    hollandTest.goToRecommendation();
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
