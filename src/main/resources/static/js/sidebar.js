// 侧边栏组件
class Sidebar {
    constructor() {
        this.isOpen = false;
        this.currentPage = this.getCurrentPage();
        this.init();
    }

    init() {
        this.createSidebar();
        this.bindEvents();
        this.updateActiveMenu();
        this.checkAdminPermission();
    }

    getCurrentPage() {
        const path = window.location.pathname;
        const page = path.split('/').pop() || 'main.html';
        return page.replace('.html', '');
    }

    createSidebar() {
        // 创建侧边栏HTML
        const sidebarHTML = `
            <!-- 侧边栏切换按钮 -->
            <button class="sidebar-toggle" id="sidebarToggle">
                <i class="fas fa-bars"></i>
            </button>

            <!-- 侧边栏遮罩 -->
            <div class="sidebar-overlay" id="sidebarOverlay"></div>

            <!-- 侧边栏 -->
            <div class="sidebar" id="sidebar">
                <div class="sidebar-header">
                    <div class="sidebar-brand">
                        <i class="fas fa-graduation-cap"></i>
                        <span>高考助手</span>
                    </div>
                    <div class="sidebar-user">
                        <i class="fas fa-user-circle"></i>
                        <span id="sidebarUsername">用户</span>
                    </div>
                    <button class="sidebar-close" id="sidebarClose">
                        <i class="fas fa-times"></i>
                    </button>
                </div>

                <div class="sidebar-menu">
                    <div class="menu-section">
                        <div class="menu-title">主要功能</div>
                        <a href="main.html" class="menu-item" data-page="main">
                            <i class="fas fa-home"></i>
                            <span class="menu-text">首页</span>
                        </a>
                        <a href="recommendation-new.html" class="menu-item" data-page="recommendation-new">
                            <i class="fas fa-chart-line"></i>
                            <span class="menu-text">院校预测</span>
                            <span class="menu-badge">热门</span>
                        </a>
                    </div>

                    <div class="menu-section">
                        <div class="menu-title">性格测试</div>
                        <a href="mbti-test.html" class="menu-item" data-page="mbti-test">
                            <i class="fas fa-brain"></i>
                            <span class="menu-text">MBTI测试</span>
                        </a>
                        <a href="holland-test.html" class="menu-item" data-page="holland-test">
                            <i class="fas fa-compass"></i>
                            <span class="menu-text">霍兰德测试</span>
                        </a>
                    </div>

                    <div class="menu-section">
                        <div class="menu-title">更多功能</div>
                        <a href="ai-chat-test.html" class="menu-item" data-page="ai-chat-test">
                            <i class="fas fa-robot"></i>
                            <span class="menu-text">AI助手</span>
                            <span class="menu-badge">热门</span>
                        </a>
                        <a href="#" class="menu-item disabled" data-page="school-pk">
                            <i class="fas fa-balance-scale"></i>
                            <span class="menu-text">院校PK</span>
                            <span class="menu-badge coming-soon">敬请期待</span>
                        </a>
                        <a href="#" class="menu-item disabled" data-page="more">
                            <i class="fas fa-ellipsis-h"></i>
                            <span class="menu-text">更多功能</span>
                            <span class="menu-badge coming-soon">敬请期待</span>
                        </a>
                    </div>

                    <!-- 管理员专用区域 -->
                    <div class="menu-section admin-section" id="adminSection" style="display: none;">
                        <div class="menu-title admin-title">
                            <i class="fas fa-shield-alt"></i>
                            管理员专区
                        </div>
                        <a href="admin.html" class="menu-item admin-item" data-page="admin">
                            <i class="fas fa-cog"></i>
                            <span class="menu-text">管理后台</span>
                            <span class="menu-badge admin">管理员</span>
                        </a>
                    </div>
                </div>

                <div class="sidebar-footer">
                    <a href="#" class="menu-item" onclick="logout()">
                        <i class="fas fa-sign-out-alt"></i>
                        <span class="menu-text">退出登录</span>
                    </a>
                </div>
            </div>



            <!-- 快速操作按钮 -->
            <div class="quick-actions">
                <button class="quick-action-btn" onclick="window.location.href='main.html'">
                    <i class="fas fa-home"></i>
                    <span class="tooltip">返回首页</span>
                </button>
            </div>
        `;

        // 将侧边栏添加到页面
        document.body.insertAdjacentHTML('beforeend', sidebarHTML);

        // 设置用户名
        const username = localStorage.getItem('username') || '用户';
        document.getElementById('sidebarUsername').textContent = username;
    }

    bindEvents() {
        // 侧边栏切换按钮
        document.getElementById('sidebarToggle').addEventListener('click', () => {
            this.toggle();
        });

        // 侧边栏关闭按钮
        document.getElementById('sidebarClose').addEventListener('click', () => {
            this.close();
        });

        // 遮罩点击关闭
        document.getElementById('sidebarOverlay').addEventListener('click', () => {
            this.close();
        });

        // 键盘快捷键
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && this.isOpen) {
                this.close();
            }
            if (e.ctrlKey && e.key === 'b') {
                e.preventDefault();
                this.toggle();
            }
        });

        // 菜单项点击事件
        document.querySelectorAll('.menu-item:not(.disabled)').forEach(item => {
            item.addEventListener('click', (e) => {
                if (!item.classList.contains('disabled')) {
                    this.close();
                }
            });
        });

        // 禁用的菜单项点击提示（排除AI助手）
        document.querySelectorAll('.menu-item.disabled:not([data-page="ai-chat-test"])').forEach(item => {
            item.addEventListener('click', (e) => {
                e.preventDefault();
                this.showComingSoonMessage();
            });
        });
    }

    toggle() {
        if (this.isOpen) {
            this.close();
        } else {
            this.open();
        }
    }

    open() {
        this.isOpen = true;
        document.getElementById('sidebar').classList.add('active');
        document.getElementById('sidebarOverlay').classList.add('active');
        document.getElementById('sidebarToggle').classList.add('active');
        document.body.style.overflow = 'hidden';
    }

    close() {
        this.isOpen = false;
        document.getElementById('sidebar').classList.remove('active');
        document.getElementById('sidebarOverlay').classList.remove('active');
        document.getElementById('sidebarToggle').classList.remove('active');
        document.body.style.overflow = '';
    }

    updateActiveMenu() {
        // 移除所有活动状态
        document.querySelectorAll('.menu-item').forEach(item => {
            item.classList.remove('active');
        });

        // 设置当前页面的菜单项为活动状态
        const currentMenuItem = document.querySelector(`[data-page="${this.currentPage}"]`);
        if (currentMenuItem) {
            currentMenuItem.classList.add('active');
        }
    }

    // 检查管理员权限
    checkAdminPermission() {
        const username = localStorage.getItem('username');

        // 只有PLeiA用户才显示管理员专区
        if (username === 'PLeiA') {
            const adminSection = document.getElementById('adminSection');
            if (adminSection) {
                adminSection.style.display = 'block';
            }
        }
    }



    showComingSoonMessage() {
        // 创建提示消息
        const message = document.createElement('div');
        message.className = 'coming-soon-message';
        message.innerHTML = `
            <div class="message-content">
                <i class="fas fa-clock"></i>
                <span>该功能正在开发中，敬请期待！</span>
            </div>
        `;
        
        // 添加样式
        message.style.cssText = `
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: rgba(0, 0, 0, 0.8);
            color: white;
            padding: 20px 30px;
            border-radius: 10px;
            z-index: 3000;
            animation: fadeInOut 2s ease;
        `;

        // 添加动画样式
        const style = document.createElement('style');
        style.textContent = `
            @keyframes fadeInOut {
                0% { opacity: 0; transform: translate(-50%, -50%) scale(0.8); }
                20% { opacity: 1; transform: translate(-50%, -50%) scale(1); }
                80% { opacity: 1; transform: translate(-50%, -50%) scale(1); }
                100% { opacity: 0; transform: translate(-50%, -50%) scale(0.8); }
            }
            .message-content {
                display: flex;
                align-items: center;
                gap: 10px;
                font-size: 1rem;
            }
        `;
        document.head.appendChild(style);

        // 显示消息
        document.body.appendChild(message);

        // 2秒后移除消息
        setTimeout(() => {
            if (message.parentNode) {
                message.parentNode.removeChild(message);
            }
            if (style.parentNode) {
                style.parentNode.removeChild(style);
            }
        }, 2000);
    }
}

// 全局退出登录函数
function logout() {
    if (confirm('确定要退出登录吗？')) {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        localStorage.removeItem('userId');
        window.location.href = '/index.html';
    }
}

// 页面加载完成后初始化侧边栏
document.addEventListener('DOMContentLoaded', function() {
    // 检查是否在主页面，如果不是则创建侧边栏
    const currentPage = window.location.pathname.split('/').pop();
    if (currentPage !== 'index.html' && currentPage !== 'login.html') {
        new Sidebar();
    }
});

// 导出Sidebar类供其他脚本使用
window.Sidebar = Sidebar;

// 全局函数：添加管理员侧边栏选项
window.addAdminSidebarOption = function() {
    const adminSection = document.getElementById('adminSection');
    if (adminSection) {
        adminSection.style.display = 'block';
    }
};
