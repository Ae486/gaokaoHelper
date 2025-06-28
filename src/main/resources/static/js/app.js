// API 基础URL
const API_BASE_URL = '';

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    // 检查是否已登录
    checkLoginStatus();

    // 绑定表单提交事件
    document.getElementById('loginFormElement').addEventListener('submit', handleLogin);
    document.getElementById('registerFormElement').addEventListener('submit', handleRegister);

    // 绑定实时验证事件
    document.getElementById('registerUsername').addEventListener('input', validateUsername);
    document.getElementById('registerPassword').addEventListener('input', validatePassword);
    document.getElementById('registerPassword').addEventListener('focus', showPasswordRequirements);
    document.getElementById('registerPassword').addEventListener('blur', hidePasswordRequirements);
    document.getElementById('confirmPassword').addEventListener('input', validatePasswordMatch);


});

// 显示登录表单
function showLoginForm() {
    // 隐藏用户信息页面
    document.getElementById('userInfo').style.display = 'none';

    // 显示登录表单，隐藏注册表单
    document.getElementById('loginForm').style.display = 'block';
    document.getElementById('loginForm').classList.add('active');
    document.getElementById('registerForm').style.display = 'none';
    document.getElementById('registerForm').classList.remove('active');

    // 重置登录表单状态
    resetLoginForm();

    clearMessage();
}

// 显示注册表单
function showRegisterForm() {
    // 隐藏用户信息页面
    document.getElementById('userInfo').style.display = 'none';

    // 显示注册表单，隐藏登录表单
    document.getElementById('registerForm').style.display = 'block';
    document.getElementById('registerForm').classList.add('active');
    document.getElementById('loginForm').style.display = 'none';
    document.getElementById('loginForm').classList.remove('active');

    // 重置注册表单状态
    resetRegisterForm();

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
            // 登录成功，保存token和用户信息
            localStorage.setItem('token', result.data.token);
            localStorage.setItem('userInfo', JSON.stringify(result.data.userInfo));
            localStorage.setItem('username', result.data.userInfo.username);
            localStorage.setItem('userId', result.data.userInfo.id);

            showMessage('登录成功！正在跳转到主界面...', 'success');

            // 跳转到主界面
            setTimeout(() => {
                window.location.href = '/main.html';
            }, 1500);
            
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
            showMessage('注册成功！正在跳转到登录页面...', 'success');

            // 保存用户名用于自动填入
            const registeredUsername = registerData.username;

            // 切换到登录表单
            setTimeout(() => {
                showLoginForm();
                // 自动填入用户名
                document.getElementById('loginUsername').value = registeredUsername;
                // 聚焦到密码输入框
                document.getElementById('loginPassword').focus();
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

// 实时验证用户名
function validateUsername() {
    const usernameInput = document.getElementById('registerUsername');
    const username = usernameInput.value;
    const hint = usernameInput.parentNode.querySelector('.input-hint');

    // 清除之前的样式
    usernameInput.classList.remove('valid', 'invalid');

    if (username.length === 0) {
        hint.textContent = '用户名将作为您的唯一标识';
        hint.className = 'input-hint';
        return;
    }

    // 验证用户名格式
    const usernamePattern = /^[a-zA-Z0-9_]{4,20}$/;
    if (username.length < 4) {
        usernameInput.classList.add('invalid');
        hint.textContent = '用户名至少需要4个字符';
        hint.className = 'input-hint error';
    } else if (username.length > 20) {
        usernameInput.classList.add('invalid');
        hint.textContent = '用户名不能超过20个字符';
        hint.className = 'input-hint error';
    } else if (!usernamePattern.test(username)) {
        usernameInput.classList.add('invalid');
        hint.textContent = '用户名只能包含字母、数字和下划线';
        hint.className = 'input-hint error';
    } else {
        usernameInput.classList.add('valid');
        hint.textContent = '';  // 符合规范时不显示文字提示
        hint.className = 'input-hint';

        // 检查用户名是否已存在
        checkUsernameAvailability(username);
    }
}

// 检查用户名是否可用
async function checkUsernameAvailability(username) {
    try {
        const response = await fetch(`${API_BASE_URL}/api/auth/check-username?username=${encodeURIComponent(username)}`);
        const result = await response.json();

        const usernameInput = document.getElementById('registerUsername');
        const hint = usernameInput.parentNode.querySelector('.input-hint');

        if (result.code === 200) {
            if (result.data.exists) {
                usernameInput.classList.remove('valid');
                usernameInput.classList.add('invalid');
                hint.textContent = '用户名已存在，请选择其他用户名';
                hint.className = 'input-hint error';
            } else {
                usernameInput.classList.remove('invalid');
                usernameInput.classList.add('valid');
                hint.textContent = '';  // 可用时不显示文字提示
                hint.className = 'input-hint';
            }
        }
    } catch (error) {
        console.error('检查用户名失败:', error);
    }
}

// 显示密码规范
function showPasswordRequirements() {
    const requirements = document.getElementById('passwordRequirements');
    requirements.classList.add('show');
}

// 隐藏密码规范（当密码输入框失去焦点且密码为空时）
function hidePasswordRequirements() {
    const password = document.getElementById('registerPassword').value;
    const requirements = document.getElementById('passwordRequirements');

    // 只有当密码为空时才隐藏规范显示
    if (password.length === 0) {
        requirements.classList.remove('show');
    }
}

// 实时验证密码
function validatePassword() {
    const passwordInput = document.getElementById('registerPassword');
    const password = passwordInput.value;

    // 清除之前的样式
    passwordInput.classList.remove('valid', 'invalid');

    // 显示密码规范
    showPasswordRequirements();

    // 检查各项规范
    const hasLength = password.length >= 8 && password.length <= 20;
    const hasUpper = /[A-Z]/.test(password);
    const hasLower = /[a-z]/.test(password);
    const hasNumber = /\d/.test(password);

    // 更新规范显示状态
    updateRequirement('req-length', hasLength);
    updateRequirement('req-uppercase', hasUpper);
    updateRequirement('req-lowercase', hasLower);
    updateRequirement('req-number', hasNumber);

    // 设置输入框样式
    if (hasLength && hasUpper && hasLower && hasNumber) {
        passwordInput.classList.add('valid');
    } else if (password.length > 0) {
        passwordInput.classList.add('invalid');
    }

    // 如果确认密码已输入，重新验证密码匹配
    const confirmPassword = document.getElementById('confirmPassword').value;
    if (confirmPassword) {
        validatePasswordMatch();
    }
}

// 更新单个规范的显示状态
function updateRequirement(requirementId, isValid) {
    const requirement = document.getElementById(requirementId);
    const icon = requirement.querySelector('.requirement-icon');

    requirement.classList.remove('valid', 'invalid');

    if (isValid) {
        requirement.classList.add('valid');
        icon.textContent = '✓';
    } else {
        requirement.classList.add('invalid');
        icon.textContent = '○';
    }
}

// 验证密码匹配
function validatePasswordMatch() {
    const password = document.getElementById('registerPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const confirmInput = document.getElementById('confirmPassword');
    const hint = confirmInput.parentNode.querySelector('.input-hint');

    // 清除之前的样式
    confirmInput.classList.remove('valid', 'invalid');

    if (confirmPassword.length === 0) {
        if (hint) {
            hint.textContent = '';
            hint.className = 'input-hint';
        }
        confirmInput.setCustomValidity('');
        return;
    }

    if (password !== confirmPassword) {
        confirmInput.classList.add('invalid');
        confirmInput.setCustomValidity('密码不匹配');
        if (hint) {
            hint.textContent = '两次输入的密码不一致';
            hint.className = 'input-hint error';
        }
    } else {
        confirmInput.classList.add('valid');
        confirmInput.setCustomValidity('');
        if (hint) {
            hint.textContent = '';  // 匹配时不显示文字提示
            hint.className = 'input-hint';
        }
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

    // 检查是否为管理员用户，显示管理系统入口
    checkAdminPermission(data.userInfo.username);

    clearMessage();
}

// 退出登录
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo');

    // 显示登录表单
    showLoginForm();

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

// 重置登录表单状态
function resetLoginForm() {
    // 清空表单
    document.getElementById('loginFormElement').reset();

    // 移除所有输入框的样式类
    const loginInputs = document.querySelectorAll('#loginForm input');
    loginInputs.forEach(input => {
        input.classList.remove('valid', 'invalid');
    });


}

// 重置注册表单状态
function resetRegisterForm() {
    // 清空表单
    document.getElementById('registerFormElement').reset();

    // 移除所有输入框的样式类
    const registerInputs = document.querySelectorAll('#registerForm input');
    registerInputs.forEach(input => {
        input.classList.remove('valid', 'invalid');
    });

    // 重置所有提示信息
    const hints = document.querySelectorAll('#registerForm .input-hint');
    hints.forEach(hint => {
        hint.textContent = '';
        hint.className = 'input-hint';
    });

    // 重置用户名提示为默认状态
    const usernameHint = document.querySelector('#registerUsername').parentNode.querySelector('.input-hint');
    usernameHint.textContent = '用户名将作为您的唯一标识';
    usernameHint.className = 'input-hint';

    // 隐藏密码规范显示
    const requirements = document.getElementById('passwordRequirements');
    requirements.classList.remove('show');

    // 重置密码规范状态
    const requirementItems = document.querySelectorAll('.requirement');
    requirementItems.forEach(item => {
        item.classList.remove('valid', 'invalid');
        const icon = item.querySelector('.requirement-icon');
        icon.textContent = '○';
    });
}

// 工具函数：获取JWT token
function getToken() {
    return localStorage.getItem('token');
}

// 工具函数：设置Authorization头
function getAuthHeaders() {
    const token = getToken();
    return token ? { 'Authorization': `Bearer ${token}` } : {};
}

// 检查管理员权限
async function checkAdminPermission(username) {
    const adminButton = document.getElementById('adminButton');

    // 只有PLeiA用户才显示管理按钮
    if (username === 'PLeiA') {
        try {
            const response = await fetch(`${API_BASE_URL}/api/admin/check`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    ...getAuthHeaders()
                }
            });

            if (response.ok) {
                const result = await response.json();
                if (result.data && result.data.isAdmin) {
                    adminButton.style.display = 'block';
                }
            }
        } catch (error) {
            console.log('检查管理员权限失败:', error);
        }
    } else {
        adminButton.style.display = 'none';
    }
}

// 跳转到管理系统
function goToAdmin() {
    window.location.href = '/admin.html';
}
