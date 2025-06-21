// API 基础URL
const API_BASE_URL = '';

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    // 检查是否已登录
    checkLoginStatus();
    
    // 绑定表单提交事件
    document.getElementById('loginFormElement').addEventListener('submit', handleLogin);
    document.getElementById('registerFormElement').addEventListener('submit', handleRegister);
    
    // 绑定密码确认验证
    document.getElementById('confirmPassword').addEventListener('input', validatePasswordMatch);
    document.getElementById('registerPassword').addEventListener('input', validatePasswordMatch);
});

// 显示登录表单
function showLoginForm() {
    document.getElementById('loginForm').classList.add('active');
    document.getElementById('registerForm').classList.remove('active');
    clearMessage();
}

// 显示注册表单
function showRegisterForm() {
    document.getElementById('registerForm').classList.add('active');
    document.getElementById('loginForm').classList.remove('active');
    clearMessage();
}

// 处理登录
async function handleLogin(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const loginData = {
        username: formData.get('username'),
        password: formData.get('password')
    };
    
    try {
        showMessage('正在登录...', 'info');
        
        const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(loginData)
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
            // 登录成功，保存token
            localStorage.setItem('token', result.data.token);
            localStorage.setItem('userInfo', JSON.stringify(result.data.userInfo));
            
            showMessage('登录成功！', 'success');
            
            // 显示用户信息
            setTimeout(() => {
                showUserInfo(result.data);
            }, 1000);
            
        } else {
            showMessage(result.message || '登录失败', 'error');
        }
        
    } catch (error) {
        console.error('登录错误:', error);
        showMessage('网络错误，请稍后重试', 'error');
    }
}

// 处理注册
async function handleRegister(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const registerData = {
        username: formData.get('username'),
        password: formData.get('password'),
        confirmPassword: formData.get('confirmPassword')
    };
    
    // 验证密码匹配
    if (registerData.password !== registerData.confirmPassword) {
        showMessage('两次输入的密码不一致', 'error');
        return;
    }
    
    // 验证密码强度
    if (!validatePasswordStrength(registerData.password)) {
        showMessage('密码必须包含8-20个字符，至少包含一个大写字母、一个小写字母和一个数字', 'error');
        return;
    }
    
    try {
        showMessage('正在注册...', 'info');
        
        const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(registerData)
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
            showMessage('注册成功！请登录', 'success');
            
            // 清空注册表单
            document.getElementById('registerFormElement').reset();
            
            // 切换到登录表单
            setTimeout(() => {
                showLoginForm();
                // 自动填入用户名
                document.getElementById('loginUsername').value = registerData.username;
            }, 1500);
            
        } else {
            showMessage(result.message || '注册失败', 'error');
        }
        
    } catch (error) {
        console.error('注册错误:', error);
        showMessage('网络错误，请稍后重试', 'error');
    }
}

// 验证密码强度
function validatePasswordStrength(password) {
    const pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{8,20}$/;
    return pattern.test(password);
}

// 验证密码匹配
function validatePasswordMatch() {
    const password = document.getElementById('registerPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const confirmInput = document.getElementById('confirmPassword');
    
    if (confirmPassword && password !== confirmPassword) {
        confirmInput.style.borderColor = '#e74c3c';
        confirmInput.setCustomValidity('密码不匹配');
    } else {
        confirmInput.style.borderColor = '#e1e5e9';
        confirmInput.setCustomValidity('');
    }
}

// 显示用户信息
function showUserInfo(data) {
    document.getElementById('loginForm').style.display = 'none';
    document.getElementById('registerForm').style.display = 'none';
    document.getElementById('userInfo').style.display = 'block';
    
    document.getElementById('userId').textContent = data.userInfo.userId;
    document.getElementById('username').textContent = data.userInfo.username;
    document.getElementById('loginTime').textContent = new Date().toLocaleString('zh-CN');
    
    clearMessage();
}

// 退出登录
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo');
    
    document.getElementById('userInfo').style.display = 'none';
    document.getElementById('loginForm').style.display = 'block';
    document.getElementById('loginForm').classList.add('active');
    
    // 清空表单
    document.getElementById('loginFormElement').reset();
    document.getElementById('registerFormElement').reset();
    
    showMessage('已退出登录', 'info');
}

// 检查登录状态
function checkLoginStatus() {
    const token = localStorage.getItem('token');
    const userInfo = localStorage.getItem('userInfo');
    
    if (token && userInfo) {
        try {
            const userData = JSON.parse(userInfo);
            showUserInfo({
                userInfo: userData
            });
        } catch (error) {
            // 如果数据损坏，清除本地存储
            localStorage.removeItem('token');
            localStorage.removeItem('userInfo');
        }
    }
}

// 显示消息
function showMessage(text, type = 'info') {
    const messageElement = document.getElementById('message');
    messageElement.textContent = text;
    messageElement.className = `message ${type}`;
    messageElement.style.display = 'block';
    
    // 3秒后自动隐藏成功和信息消息
    if (type === 'success' || type === 'info') {
        setTimeout(() => {
            clearMessage();
        }, 3000);
    }
}

// 清除消息
function clearMessage() {
    const messageElement = document.getElementById('message');
    messageElement.style.display = 'none';
    messageElement.textContent = '';
    messageElement.className = 'message';
}
