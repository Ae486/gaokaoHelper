// 管理后台JavaScript
const API_BASE_URL = '';

// 页面加载时检查权限
document.addEventListener('DOMContentLoaded', function() {
    checkAdminAuth();
});

// 检查管理员权限
function checkAdminAuth() {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');

    // 检查是否登录
    if (!token || !username) {
        alert('请先登录！');
        window.location.href = '/index.html';
        return;
    }

    // 检查是否为PLeiA用户
    if (username !== 'PLeiA') {
        alert('您没有管理员权限！');
        window.location.href = '/main.html';
        return;
    }

    // 权限验证通过，初始化管理后台
    initAdmin();
}

// 初始化管理后台
function initAdmin() {
    // 显示用户名
    const userNameElement = document.querySelector('.user-name');
    if (userNameElement) {
        userNameElement.textContent = localStorage.getItem('username');
    }

    // 加载仪表盘数据
    loadDashboard();
}

// 全局变量
let currentSection = 'dashboard';
let currentPage = {
    users: 0,
    schools: 0,
    scores: 0,
    logs: 0
};
let pageSize = {
    users: 10,
    schools: 10,
    scores: 10,
    logs: 10
};

// 排序状态管理
let sortState = {
    users: { field: 'id', direction: 'desc' },
    schools: { field: 'id', direction: 'desc' },
    scores: { field: 'id', direction: 'desc' },
    logs: { field: 'id', direction: 'desc' }
};

// 防抖定时器
let searchTimers = {
    users: null,
    schools: null
};

// 排序功能
function toggleSort(module, field) {
    const currentSort = sortState[module];

    // 如果点击的是同一个字段，切换排序方向
    if (currentSort.field === field) {
        currentSort.direction = currentSort.direction === 'asc' ? 'desc' : 'asc';
    } else {
        // 如果点击的是不同字段，设置为降序
        currentSort.field = field;
        currentSort.direction = 'desc';
    }

    // 更新排序图标
    updateSortIcons(module, field, currentSort.direction);

    // 重新加载数据
    currentPage[module] = 0; // 重置到第一页
    switch(module) {
        case 'users':
            const userKeyword = document.getElementById('userSearch')?.value.trim() || '';
            loadUsersData(0, userKeyword);
            break;
        case 'schools':
            const schoolKeyword = document.getElementById('schoolSearch')?.value.trim() || '';
            loadSchoolsData(0, schoolKeyword);
            break;
        case 'scores':
            loadScoresData(0);
            break;
        case 'logs':
            loadLogsData(0);
            break;
    }
}

// 更新排序图标
function updateSortIcons(module, field, direction) {
    // 清除所有排序图标的active状态
    document.querySelectorAll('.sort-icons i').forEach(icon => {
        icon.classList.remove('active');
    });

    // 激活当前排序图标
    const iconId = `${module}-${field}-${direction}`;
    const activeIcon = document.getElementById(iconId);
    if (activeIcon) {
        activeIcon.classList.add('active');
    }
}

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    // 检查登录状态和管理员权限
    checkAdminAuth();
    
    // 初始化页面
    initializePage();
});

// 检查管理员权限
async function checkAdminAuth() {
    const token = localStorage.getItem('token');
    if (!token) {
        showMessage('请先登录', 'error');
        setTimeout(() => {
            window.location.href = '/index.html';
        }, 2000);
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/api/admin/check`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error('权限验证失败');
        }

        const result = await response.json();
        if (!result.data || !result.data.isAdmin) {
            showMessage('您没有管理员权限', 'error');
            setTimeout(() => {
                window.location.href = '/index.html';
            }, 2000);
            return;
        }

        // 权限验证通过，设置用户名
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
        document.getElementById('adminUsername').textContent = userInfo.username || 'PLeiA';

    } catch (error) {
        console.error('权限验证失败:', error);
        showMessage('权限验证失败，请重新登录', 'error');
        setTimeout(() => {
            window.location.href = '/index.html';
        }, 2000);
    }
}

// 初始化页面
function initializePage() {
    // 初始化排序图标
    initSortIcons();

    // 加载仪表盘数据
    loadDashboardData();

    // 设置默认显示仪表盘
    showSection('dashboard');
}

// 初始化排序图标
function initSortIcons() {
    // 为每个模块设置初始排序图标
    Object.keys(sortState).forEach(module => {
        const { field, direction } = sortState[module];
        updateSortIcons(module, field, direction);
    });
}

// 显示指定的内容区域
function showSection(sectionName) {
    // 隐藏所有内容区域
    const sections = document.querySelectorAll('.content-section');
    sections.forEach(section => {
        section.classList.remove('active');
    });

    // 移除所有导航项的active状态
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        item.classList.remove('active');
    });

    // 显示指定的内容区域
    const targetSection = document.getElementById(sectionName);
    if (targetSection) {
        targetSection.classList.add('active');
    }

    // 设置对应导航项为active状态
    const targetNavItem = document.querySelector(`a[href="#${sectionName}"]`).parentElement;
    if (targetNavItem) {
        targetNavItem.classList.add('active');
    }

    // 更新页面标题
    const titles = {
        'dashboard': '仪表盘',
        'users': '用户管理',
        'schools': '学校管理',
        'scores': '录取分数管理',
        'provinces': '省份管理',
        'logs': '操作日志'
    };
    document.getElementById('pageTitle').textContent = titles[sectionName] || '管理后台';

    // 记录当前区域
    currentSection = sectionName;

    // 根据区域加载对应数据
    switch (sectionName) {
        case 'dashboard':
            loadDashboardData();
            break;
        case 'users':
            loadUsersData();
            break;
        case 'schools':
            loadSchoolsData();
            break;
        case 'scores':
            loadScoresData();
            break;
        case 'provinces':
            loadProvincesData();
            break;
        case 'logs':
            loadLogsData();
            break;
    }
}

// 切换侧边栏
function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    const mainContent = document.getElementById('mainContent');
    
    if (window.innerWidth <= 768) {
        // 移动端：显示/隐藏侧边栏
        sidebar.classList.toggle('show');
    } else {
        // 桌面端：收缩/展开侧边栏
        sidebar.classList.toggle('collapsed');
        mainContent.classList.toggle('expanded');
    }
}

// 返回主页
function goBack() {
    window.location.href = '/index.html';
}

// 退出登录
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo');
    window.location.href = '/index.html';
}

// ==================== 仪表盘相关函数 ====================

// 加载仪表盘数据
async function loadDashboardData() {
    try {
        // 加载统计数据
        const statsResponse = await apiRequest('/api/admin/dashboard', 'GET');
        if (statsResponse.success) {
            updateDashboardStats(statsResponse.data);
        }

        // 加载最近操作日志
        const logsResponse = await apiRequest('/api/admin/logs/recent', 'GET');
        if (logsResponse.success) {
            updateRecentLogs(logsResponse.data);
        }

    } catch (error) {
        console.error('加载仪表盘数据失败:', error);
        showMessage('加载仪表盘数据失败', 'error');
    }
}

// 更新仪表盘统计数据
function updateDashboardStats(stats) {
    document.getElementById('userCount').textContent = stats.userCount || 0;
    document.getElementById('schoolCount').textContent = stats.schoolCount || 0;
    document.getElementById('scoreCount').textContent = stats.admissionScoreCount || 0;
    document.getElementById('operationCount').textContent = stats.totalOperations || 0;
}

// 更新最近操作日志
function updateRecentLogs(logs) {
    const container = document.getElementById('recentLogs');
    
    if (!logs || logs.length === 0) {
        container.innerHTML = '<div class="loading">暂无操作记录</div>';
        return;
    }

    const logsHtml = logs.map(log => `
        <div class="log-item">
            <div class="log-info">
                <span class="log-type ${log.operationType}">${getOperationTypeName(log.operationType)}</span>
                <div class="log-description">${log.description || '无描述'}</div>
                <div class="log-meta">表: ${log.tableName} | IP: ${log.ipAddress || '未知'}</div>
            </div>
            <div class="log-time">${formatDateTime(log.createdAt)}</div>
        </div>
    `).join('');

    container.innerHTML = logsHtml;
}

// ==================== 用户管理相关函数 ====================

// 加载用户数据
async function loadUsersData(page = 0, keyword = '') {
    try {
        const params = new URLSearchParams({
            page: page,
            size: pageSize.users,
            sort: sortState.users.field,
            direction: sortState.users.direction
        });
        
        if (keyword) {
            params.append('keyword', keyword);
        }

        const response = await apiRequest(`/api/admin/users?${params}`, 'GET');
        if (response.success) {
            updateUsersTable(response.data);
            updateUsersPagination(response.data, page);
            currentPage.users = page;
        }

    } catch (error) {
        console.error('加载用户数据失败:', error);
        showMessage('加载用户数据失败', 'error');
    }
}

// 更新用户表格
function updateUsersTable(pageData) {
    const tbody = document.getElementById('usersTableBody');

    if (!pageData.records || pageData.records.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="loading">暂无用户数据</td></tr>';
        return;
    }

    const usersHtml = pageData.records.map(user => `
        <tr>
            <td><input type="checkbox" value="${user.id}" onchange="toggleUserSelection()"></td>
            <td>${user.id}</td>
            <td>${user.username}</td>
            <td>${formatDateTime(user.createdAt)}</td>
            <td>
                <button class="btn btn-warning btn-sm" onclick="editUser(${user.id})">
                    <i class="fas fa-edit"></i> 编辑
                </button>
                ${user.username !== 'PLeiA' ? `
                <button class="btn btn-danger btn-sm" onclick="deleteUser(${user.id})">
                    <i class="fas fa-trash"></i> 删除
                </button>
                ` : ''}
            </td>
        </tr>
    `).join('');

    tbody.innerHTML = usersHtml;
}

// 更新用户分页
function updateUsersPagination(pageData, currentPage) {
    const container = document.getElementById('usersPagination');
    const totalPages = pageData.totalPages;
    
    if (totalPages <= 1) {
        container.innerHTML = '';
        return;
    }

    let paginationHtml = '';
    
    // 上一页按钮
    paginationHtml += `
        <button ${currentPage === 0 ? 'disabled' : ''} onclick="loadUsersData(${currentPage - 1})">
            <i class="fas fa-chevron-left"></i>
        </button>
    `;

    // 页码按钮
    for (let i = 0; i < totalPages; i++) {
        if (i === currentPage) {
            paginationHtml += `<button class="active">${i + 1}</button>`;
        } else {
            paginationHtml += `<button onclick="loadUsersData(${i})">${i + 1}</button>`;
        }
    }

    // 下一页按钮
    paginationHtml += `
        <button ${currentPage === totalPages - 1 ? 'disabled' : ''} onclick="loadUsersData(${currentPage + 1})">
            <i class="fas fa-chevron-right"></i>
        </button>
    `;

    container.innerHTML = paginationHtml;

    // 更新页码跳转输入框的最大值
    const jumpInput = document.getElementById('usersPageJump');
    if (jumpInput) {
        jumpInput.max = totalPages;
        jumpInput.placeholder = `1-${totalPages}`;
    }
}

// 防抖搜索函数
function debounceSearch(searchFunction, delay = 300) {
    return function(...args) {
        const context = this;
        clearTimeout(searchTimers[args[0]]);
        searchTimers[args[0]] = setTimeout(() => {
            searchFunction.apply(context, args.slice(1));
        }, delay);
    };
}

// 设置搜索状态
function setSearchingState(module, isSearching) {
    const searchBox = document.querySelector(`#${module}Search`).parentElement;
    const input = document.getElementById(`${module}Search`);

    if (isSearching) {
        searchBox.classList.add('searching');
        input.classList.add('searching');
    } else {
        searchBox.classList.remove('searching');
        input.classList.remove('searching');
    }
}

// 实际搜索用户函数
async function performUserSearch(keyword) {
    setSearchingState('user', true);
    currentPage.users = 0; // 重置到第一页
    try {
        await loadUsersData(0, keyword);
    } catch (error) {
        console.error('搜索用户失败:', error);
    } finally {
        setSearchingState('user', false);
    }
}

// 搜索用户（带防抖）
function searchUsers() {
    const keyword = document.getElementById('userSearch').value.trim();
    debounceSearch(performUserSearch, 300)('users', keyword);
}

// 处理搜索输入
function handleSearchInput(module) {
    const input = document.getElementById(`${module}Search`);
    const clearIcon = input.parentElement.querySelector('.clear-icon');

    if (input.value.trim()) {
        clearIcon.style.display = 'flex';
    } else {
        clearIcon.style.display = 'none';
    }
}

// 清空搜索
function clearSearch(module) {
    const input = document.getElementById(`${module}Search`);
    const clearIcon = input.parentElement.querySelector('.clear-icon');

    input.value = '';
    clearIcon.style.display = 'none';

    // 触发搜索以显示所有数据
    if (module === 'user') {
        searchUsers();
    } else if (module === 'school') {
        searchSchools();
    }
}

// 切换用户选择
function toggleUserSelection() {
    const checkboxes = document.querySelectorAll('#usersTable tbody input[type="checkbox"]');
    const checkedBoxes = document.querySelectorAll('#usersTable tbody input[type="checkbox"]:checked');
    const deleteBtn = document.getElementById('deleteUsersBtn');
    
    if (checkedBoxes.length > 0) {
        deleteBtn.style.display = 'inline-flex';
    } else {
        deleteBtn.style.display = 'none';
    }
}

// 切换所有用户选择
function toggleAllUsers(checkbox) {
    const checkboxes = document.querySelectorAll('#usersTable tbody input[type="checkbox"]');
    checkboxes.forEach(cb => {
        cb.checked = checkbox.checked;
    });
    toggleUserSelection();
}

// ==================== 工具函数 ====================

// API请求封装
async function apiRequest(url, method = 'GET', data = null) {
    const token = localStorage.getItem('token');
    const headers = {
        'Content-Type': 'application/json'
    };
    
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
        method: method,
        headers: headers
    };

    if (data && (method === 'POST' || method === 'PUT')) {
        config.body = JSON.stringify(data);
    }

    const response = await fetch(API_BASE_URL + url, config);
    const result = await response.json();

    if (!response.ok) {
        throw new Error(result.message || '请求失败');
    }

    return {
        success: response.ok,
        data: result.data,
        message: result.message
    };
}

// 显示消息提示
function showMessage(message, type = 'info') {
    const messageEl = document.getElementById('message');
    messageEl.textContent = message;
    messageEl.className = `message ${type} show`;

    setTimeout(() => {
        messageEl.classList.remove('show');
    }, 3000);
}

// 格式化日期时间
function formatDateTime(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}

// 获取操作类型名称
function getOperationTypeName(type) {
    const types = {
        'CREATE': '创建',
        'UPDATE': '更新',
        'DELETE': '删除',
        'VIEW': '查看'
    };
    return types[type] || type;
}

// 编辑用户
async function editUser(userId) {
    // 这里可以实现编辑用户的模态框
    showMessage('编辑用户功能待实现', 'info');
}

// 删除用户
async function deleteUser(userId) {
    if (!confirm('确定要删除这个用户吗？此操作不可恢复！')) {
        return;
    }

    try {
        const response = await apiRequest(`/api/admin/users/${userId}`, 'DELETE');
        if (response.success) {
            showMessage('删除用户成功', 'success');
            loadUsersData(currentPage.users);
        }
    } catch (error) {
        console.error('删除用户失败:', error);
        showMessage('删除用户失败: ' + error.message, 'error');
    }
}

// 批量删除用户
async function deleteSelectedUsers() {
    const checkedBoxes = document.querySelectorAll('#usersTable tbody input[type="checkbox"]:checked');
    if (checkedBoxes.length === 0) {
        showMessage('请选择要删除的用户', 'warning');
        return;
    }

    if (!confirm(`确定要删除选中的 ${checkedBoxes.length} 个用户吗？此操作不可恢复！`)) {
        return;
    }

    const userIds = Array.from(checkedBoxes).map(cb => parseInt(cb.value));

    try {
        const response = await apiRequest('/api/admin/users/batch', 'DELETE', userIds);
        if (response.success) {
            showMessage('批量删除用户成功', 'success');
            loadUsersData(currentPage.users);
            document.getElementById('deleteUsersBtn').style.display = 'none';
        }
    } catch (error) {
        console.error('批量删除用户失败:', error);
        showMessage('批量删除用户失败: ' + error.message, 'error');
    }
}

// ==================== 学校管理相关函数 ====================

// 加载学校数据
async function loadSchoolsData(page = 0, keyword = '') {
    try {
        const params = new URLSearchParams({
            page: page,
            size: pageSize.schools,
            sort: sortState.schools.field,
            direction: sortState.schools.direction
        });

        if (keyword) {
            params.append('keyword', keyword);
        }

        const response = await apiRequest(`/api/admin/schools?${params}`, 'GET');
        if (response.success) {
            updateSchoolsTable(response.data);
            updateSchoolsPagination(response.data, page);
            currentPage.schools = page;
        }

    } catch (error) {
        console.error('加载学校数据失败:', error);
        showMessage('加载学校数据失败', 'error');
    }
}

// 更新学校表格
function updateSchoolsTable(pageData) {
    const tbody = document.getElementById('schoolsTableBody');

    if (!pageData.records || pageData.records.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="loading">暂无学校数据</td></tr>';
        return;
    }

    const schoolsHtml = pageData.records.map(school => `
        <tr>
            <td><input type="checkbox" value="${school.id}" onchange="toggleSchoolSelection()"></td>
            <td>${school.id}</td>
            <td>${school.name}</td>
            <td>${school.provinceId || '-'}</td>
            <td>${school.schoolLevel || '-'}</td>
            <td>${school.schoolType || '-'}</td>
            <td>
                <button class="btn btn-warning btn-sm" onclick="editSchool(${school.id})">
                    <i class="fas fa-edit"></i> 编辑
                </button>
                <button class="btn btn-danger btn-sm" onclick="deleteSchool(${school.id})">
                    <i class="fas fa-trash"></i> 删除
                </button>
            </td>
        </tr>
    `).join('');

    tbody.innerHTML = schoolsHtml;
}

// 更新学校分页
function updateSchoolsPagination(pageData, currentPage) {
    const container = document.getElementById('schoolsPagination');
    const totalPages = pageData.totalPages;

    if (totalPages <= 1) {
        container.innerHTML = '';
        return;
    }

    let paginationHtml = '';

    paginationHtml += `
        <button ${currentPage === 0 ? 'disabled' : ''} onclick="loadSchoolsData(${currentPage - 1})">
            <i class="fas fa-chevron-left"></i>
        </button>
    `;

    for (let i = 0; i < totalPages; i++) {
        if (i === currentPage) {
            paginationHtml += `<button class="active">${i + 1}</button>`;
        } else {
            paginationHtml += `<button onclick="loadSchoolsData(${i})">${i + 1}</button>`;
        }
    }

    paginationHtml += `
        <button ${currentPage === totalPages - 1 ? 'disabled' : ''} onclick="loadSchoolsData(${currentPage + 1})">
            <i class="fas fa-chevron-right"></i>
        </button>
    `;

    container.innerHTML = paginationHtml;

    // 更新页码跳转输入框的最大值
    const jumpInput = document.getElementById('schoolsPageJump');
    if (jumpInput) {
        jumpInput.max = totalPages;
        jumpInput.placeholder = `1-${totalPages}`;
    }
}

// 实际搜索学校函数
async function performSchoolSearch(keyword) {
    setSearchingState('school', true);
    currentPage.schools = 0; // 重置到第一页
    try {
        await loadSchoolsData(0, keyword);
    } catch (error) {
        console.error('搜索学校失败:', error);
    } finally {
        setSearchingState('school', false);
    }
}

// 搜索学校（带防抖）
function searchSchools() {
    const keyword = document.getElementById('schoolSearch').value.trim();
    debounceSearch(performSchoolSearch, 300)('schools', keyword);
}

// 切换学校选择
function toggleSchoolSelection() {
    const checkedBoxes = document.querySelectorAll('#schoolsTable tbody input[type="checkbox"]:checked');
    const deleteBtn = document.getElementById('deleteSchoolsBtn');

    if (checkedBoxes.length > 0) {
        deleteBtn.style.display = 'inline-flex';
    } else {
        deleteBtn.style.display = 'none';
    }
}

// 切换所有学校选择
function toggleAllSchools(checkbox) {
    const checkboxes = document.querySelectorAll('#schoolsTable tbody input[type="checkbox"]');
    checkboxes.forEach(cb => {
        cb.checked = checkbox.checked;
    });
    toggleSchoolSelection();
}

// 显示添加学校模态框
function showAddSchoolModal() {
    showMessage('添加学校功能待实现', 'info');
}

// 编辑学校
function editSchool(schoolId) {
    showMessage('编辑学校功能待实现', 'info');
}

// 删除学校
async function deleteSchool(schoolId) {
    if (!confirm('确定要删除这个学校吗？此操作不可恢复！')) {
        return;
    }

    try {
        const response = await apiRequest(`/api/admin/schools/${schoolId}`, 'DELETE');
        if (response.success) {
            showMessage('删除学校成功', 'success');
            loadSchoolsData(currentPage.schools);
        }
    } catch (error) {
        console.error('删除学校失败:', error);
        showMessage('删除学校失败: ' + error.message, 'error');
    }
}

// 批量删除学校
async function deleteSelectedSchools() {
    const checkedBoxes = document.querySelectorAll('#schoolsTable tbody input[type="checkbox"]:checked');
    if (checkedBoxes.length === 0) {
        showMessage('请选择要删除的学校', 'warning');
        return;
    }

    if (!confirm(`确定要删除选中的 ${checkedBoxes.length} 个学校吗？此操作不可恢复！`)) {
        return;
    }

    const schoolIds = Array.from(checkedBoxes).map(cb => parseInt(cb.value));

    try {
        const response = await apiRequest('/api/admin/schools/batch', 'DELETE', schoolIds);
        if (response.success) {
            showMessage('批量删除学校成功', 'success');
            loadSchoolsData(currentPage.schools);
            document.getElementById('deleteSchoolsBtn').style.display = 'none';
        }
    } catch (error) {
        console.error('批量删除学校失败:', error);
        showMessage('批量删除学校失败: ' + error.message, 'error');
    }
}

// ==================== 录取分数管理相关函数 ====================

// 加载录取分数数据
async function loadScoresData(page = 0) {
    try {
        const yearFilter = document.getElementById('yearFilter')?.value || '';
        const provinceFilter = document.getElementById('provinceFilter')?.value || '';

        const params = new URLSearchParams({
            page: page,
            size: pageSize.scores,
            sort: sortState.scores.field,
            direction: sortState.scores.direction
        });

        if (yearFilter) params.append('year', yearFilter);
        if (provinceFilter) params.append('provinceId', provinceFilter);

        const response = await apiRequest(`/api/admin/admission-scores?${params}`, 'GET');
        if (response.success) {
            updateScoresTable(response.data);
            updateScoresPagination(response.data, page);
            currentPage.scores = page;
        }

    } catch (error) {
        console.error('加载录取分数数据失败:', error);
        showMessage('加载录取分数数据失败', 'error');
    }
}

// 更新录取分数表格
function updateScoresTable(pageData) {
    const tbody = document.getElementById('scoresTableBody');

    if (!pageData.records || pageData.records.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="loading">暂无录取分数数据</td></tr>';
        return;
    }

    const scoresHtml = pageData.records.map(score => `
        <tr>
            <td><input type="checkbox" value="${score.id}" onchange="toggleScoreSelection()"></td>
            <td>${score.id}</td>
            <td>${score.year}</td>
            <td>${score.schoolId}</td>
            <td>${score.provinceId}</td>
            <td>${score.minScore || '-'}</td>
            <td>${score.minRank || '-'}</td>
            <td>
                <button class="btn btn-warning btn-sm" onclick="editScore(${score.id})">
                    <i class="fas fa-edit"></i> 编辑
                </button>
                <button class="btn btn-danger btn-sm" onclick="deleteScore(${score.id})">
                    <i class="fas fa-trash"></i> 删除
                </button>
            </td>
        </tr>
    `).join('');

    tbody.innerHTML = scoresHtml;
}

// 更新录取分数分页
function updateScoresPagination(pageData, currentPage) {
    const container = document.getElementById('scoresPagination');
    const totalPages = pageData.totalPages;

    if (totalPages <= 1) {
        container.innerHTML = '';
        return;
    }

    let paginationHtml = '';

    paginationHtml += `
        <button ${currentPage === 0 ? 'disabled' : ''} onclick="loadScoresData(${currentPage - 1})">
            <i class="fas fa-chevron-left"></i>
        </button>
    `;

    for (let i = 0; i < totalPages; i++) {
        if (i === currentPage) {
            paginationHtml += `<button class="active">${i + 1}</button>`;
        } else {
            paginationHtml += `<button onclick="loadScoresData(${i})">${i + 1}</button>`;
        }
    }

    paginationHtml += `
        <button ${currentPage === totalPages - 1 ? 'disabled' : ''} onclick="loadScoresData(${currentPage + 1})">
            <i class="fas fa-chevron-right"></i>
        </button>
    `;

    container.innerHTML = paginationHtml;

    // 更新页码跳转输入框的最大值
    const jumpInput = document.getElementById('scoresPageJump');
    if (jumpInput) {
        jumpInput.max = totalPages;
        jumpInput.placeholder = `1-${totalPages}`;
    }
}

// 过滤录取分数
function filterScores() {
    loadScoresData(0);
}

// 切换录取分数选择
function toggleScoreSelection() {
    const checkedBoxes = document.querySelectorAll('#scoresTable tbody input[type="checkbox"]:checked');
    const deleteBtn = document.getElementById('deleteScoresBtn');

    if (checkedBoxes.length > 0) {
        deleteBtn.style.display = 'inline-flex';
    } else {
        deleteBtn.style.display = 'none';
    }
}

// 切换所有录取分数选择
function toggleAllScores(checkbox) {
    const checkboxes = document.querySelectorAll('#scoresTable tbody input[type="checkbox"]');
    checkboxes.forEach(cb => {
        cb.checked = checkbox.checked;
    });
    toggleScoreSelection();
}

// 显示添加录取分数模态框
function showAddScoreModal() {
    showMessage('添加录取分数功能待实现', 'info');
}

// 编辑录取分数
function editScore(scoreId) {
    showMessage('编辑录取分数功能待实现', 'info');
}

// 删除录取分数
async function deleteScore(scoreId) {
    if (!confirm('确定要删除这条录取分数记录吗？此操作不可恢复！')) {
        return;
    }

    try {
        const response = await apiRequest(`/api/admin/admission-scores/${scoreId}`, 'DELETE');
        if (response.success) {
            showMessage('删除录取分数记录成功', 'success');
            loadScoresData(currentPage.scores);
        }
    } catch (error) {
        console.error('删除录取分数失败:', error);
        showMessage('删除录取分数失败: ' + error.message, 'error');
    }
}

// 批量删除录取分数
async function deleteSelectedScores() {
    const checkedBoxes = document.querySelectorAll('#scoresTable tbody input[type="checkbox"]:checked');
    if (checkedBoxes.length === 0) {
        showMessage('请选择要删除的录取分数记录', 'warning');
        return;
    }

    if (!confirm(`确定要删除选中的 ${checkedBoxes.length} 条录取分数记录吗？此操作不可恢复！`)) {
        return;
    }

    const scoreIds = Array.from(checkedBoxes).map(cb => parseInt(cb.value));

    try {
        const response = await apiRequest('/api/admin/admission-scores/batch', 'DELETE', scoreIds);
        if (response.success) {
            showMessage('批量删除录取分数记录成功', 'success');
            loadScoresData(currentPage.scores);
            document.getElementById('deleteScoresBtn').style.display = 'none';
        }
    } catch (error) {
        console.error('批量删除录取分数失败:', error);
        showMessage('批量删除录取分数失败: ' + error.message, 'error');
    }
}

// ==================== 省份管理相关函数 ====================

// 加载省份数据
async function loadProvincesData() {
    try {
        const response = await apiRequest('/api/admin/provinces', 'GET');
        if (response.success) {
            updateProvincesTable(response.data);
        }

    } catch (error) {
        console.error('加载省份数据失败:', error);
        showMessage('加载省份数据失败', 'error');
    }
}

// 更新省份表格
function updateProvincesTable(provinces) {
    const tbody = document.getElementById('provincesTableBody');

    if (!provinces || provinces.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="loading">暂无省份数据</td></tr>';
        return;
    }

    const provincesHtml = provinces.map(province => `
        <tr>
            <td>${province.id}</td>
            <td>${province.name}</td>
            <td>${province.code || '-'}</td>
            <td>
                <button class="btn btn-warning btn-sm" onclick="editProvince(${province.id})">
                    <i class="fas fa-edit"></i> 编辑
                </button>
                <button class="btn btn-danger btn-sm" onclick="deleteProvince(${province.id})">
                    <i class="fas fa-trash"></i> 删除
                </button>
            </td>
        </tr>
    `).join('');

    tbody.innerHTML = provincesHtml;
}

// 显示添加省份模态框
function showAddProvinceModal() {
    showMessage('添加省份功能待实现', 'info');
}

// 编辑省份
function editProvince(provinceId) {
    showMessage('编辑省份功能待实现', 'info');
}

// 删除省份
async function deleteProvince(provinceId) {
    if (!confirm('确定要删除这个省份吗？此操作不可恢复！')) {
        return;
    }

    try {
        const response = await apiRequest(`/api/admin/provinces/${provinceId}`, 'DELETE');
        if (response.success) {
            showMessage('删除省份成功', 'success');
            loadProvincesData();
        }
    } catch (error) {
        console.error('删除省份失败:', error);
        showMessage('删除省份失败: ' + error.message, 'error');
    }
}

// ==================== 操作日志相关函数 ====================

// 加载操作日志数据
async function loadLogsData(page = 0) {
    try {
        const operationTypeFilter = document.getElementById('operationTypeFilter')?.value || '';
        const tableNameFilter = document.getElementById('tableNameFilter')?.value || '';

        const params = new URLSearchParams({
            page: page,
            size: pageSize.logs
        });

        if (operationTypeFilter) params.append('operationType', operationTypeFilter);
        if (tableNameFilter) params.append('tableName', tableNameFilter);

        const response = await apiRequest(`/api/admin/logs?${params}`, 'GET');
        if (response.success) {
            updateLogsTable(response.data);
            updateLogsPagination(response.data, page);
            currentPage.logs = page;
        }

    } catch (error) {
        console.error('加载操作日志失败:', error);
        showMessage('加载操作日志失败', 'error');
    }
}

// 更新操作日志表格
function updateLogsTable(pageData) {
    const tbody = document.getElementById('logsTableBody');

    if (!pageData.records || pageData.records.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="loading">暂无操作日志</td></tr>';
        return;
    }

    const logsHtml = pageData.records.map(log => `
        <tr>
            <td>${log.id}</td>
            <td><span class="log-type ${log.operationType}">${getOperationTypeName(log.operationType)}</span></td>
            <td>${log.tableName}</td>
            <td>${log.description || '-'}</td>
            <td>${log.ipAddress || '-'}</td>
            <td>${formatDateTime(log.createdAt)}</td>
        </tr>
    `).join('');

    tbody.innerHTML = logsHtml;
}

// 更新操作日志分页
function updateLogsPagination(pageData, currentPage) {
    const container = document.getElementById('logsPagination');
    const totalPages = pageData.totalPages;

    if (totalPages <= 1) {
        container.innerHTML = '';
        return;
    }

    let paginationHtml = '';

    paginationHtml += `
        <button ${currentPage === 0 ? 'disabled' : ''} onclick="loadLogsData(${currentPage - 1})">
            <i class="fas fa-chevron-left"></i>
        </button>
    `;

    for (let i = 0; i < totalPages; i++) {
        if (i === currentPage) {
            paginationHtml += `<button class="active">${i + 1}</button>`;
        } else {
            paginationHtml += `<button onclick="loadLogsData(${i})">${i + 1}</button>`;
        }
    }

    paginationHtml += `
        <button ${currentPage === totalPages - 1 ? 'disabled' : ''} onclick="loadLogsData(${currentPage + 1})">
            <i class="fas fa-chevron-right"></i>
        </button>
    `;

    container.innerHTML = paginationHtml;

    // 更新页码跳转输入框的最大值
    const jumpInput = document.getElementById('logsPageJump');
    if (jumpInput) {
        jumpInput.max = totalPages;
        jumpInput.placeholder = `1-${totalPages}`;
    }
}

// ==================== 分页控制函数 ====================

// 用户管理分页控制
function changeUsersPageSize() {
    const select = document.getElementById('usersPageSize');
    pageSize.users = parseInt(select.value);
    currentPage.users = 0;
    loadUsersData(0);
}

function jumpToUsersPage() {
    const input = document.getElementById('usersPageJump');
    const pageNum = parseInt(input.value);
    if (pageNum && pageNum > 0) {
        currentPage.users = pageNum - 1;
        loadUsersData(currentPage.users);
        input.value = '';
    }
}

// 学校管理分页控制
function changeSchoolsPageSize() {
    const select = document.getElementById('schoolsPageSize');
    pageSize.schools = parseInt(select.value);
    currentPage.schools = 0;
    loadSchoolsData(0);
}

function jumpToSchoolsPage() {
    const input = document.getElementById('schoolsPageJump');
    const pageNum = parseInt(input.value);
    if (pageNum && pageNum > 0) {
        currentPage.schools = pageNum - 1;
        loadSchoolsData(currentPage.schools);
        input.value = '';
    }
}

// 录取分数管理分页控制
function changeScoresPageSize() {
    const select = document.getElementById('scoresPageSize');
    pageSize.scores = parseInt(select.value);
    currentPage.scores = 0;
    loadScoresData(0);
}

function jumpToScoresPage() {
    const input = document.getElementById('scoresPageJump');
    const pageNum = parseInt(input.value);
    if (pageNum && pageNum > 0) {
        currentPage.scores = pageNum - 1;
        loadScoresData(currentPage.scores);
        input.value = '';
    }
}

// 操作日志分页控制
function changeLogsPageSize() {
    const select = document.getElementById('logsPageSize');
    pageSize.logs = parseInt(select.value);
    currentPage.logs = 0;
    loadLogsData(0);
}

function jumpToLogsPage() {
    const input = document.getElementById('logsPageJump');
    const pageNum = parseInt(input.value);
    if (pageNum && pageNum > 0) {
        currentPage.logs = pageNum - 1;
        loadLogsData(currentPage.logs);
        input.value = '';
    }
}

// 过滤操作日志
function filterLogs() {
    loadLogsData(0);
}

// 响应式处理
window.addEventListener('resize', function() {
    if (window.innerWidth > 768) {
        const sidebar = document.getElementById('sidebar');
        sidebar.classList.remove('show');
    }
});
