// 主界面JavaScript
class MainPage {
    constructor() {
        this.init();
    }

    init() {
        this.checkAuth();
        this.checkAdminPermission();
        this.initAnimations();
        this.initScrollEffects();
    }

    // 检查用户认证状态
    checkAuth() {
        const token = localStorage.getItem('token');
        const username = localStorage.getItem('username');

        if (!token || !username) {
            // 未登录，重定向到登录页
            window.location.href = '/index.html';
            return;
        }

        // 显示用户名
        document.getElementById('username').textContent = username;
    }

    // 检查管理员权限
    checkAdminPermission() {
        const username = localStorage.getItem('username');

        // 只有PLeiA用户才显示管理后台选项
        if (username === 'PLeiA') {
            this.showAdminOptions();
        }
    }

    // 显示管理员选项
    showAdminOptions() {
        // 在主界面添加管理后台卡片
        this.addAdminCard();

        // 在侧边栏添加管理后台选项
        this.addAdminSidebarOption();
    }

    // 添加管理后台功能卡片
    addAdminCard() {
        const featuresGrid = document.querySelector('.features-grid');
        if (featuresGrid) {
            const adminCard = document.createElement('div');
            adminCard.className = 'feature-card available admin-card';
            adminCard.onclick = () => this.navigateToAdmin();
            adminCard.innerHTML = `
                <div class="card-header">
                    <div class="card-icon admin-icon">
                        <i class="fas fa-cog"></i>
                    </div>
                    <div class="card-title">
                        <h3>管理后台</h3>
                    </div>
                </div>
                <div class="card-body">
                    <p>系统管理和数据维护</p>
                </div>
                <div class="card-footer">
                    <div class="card-status admin">进入管理</div>
                </div>
                <div class="card-hover-effect"></div>
            `;
            featuresGrid.appendChild(adminCard);
        }
    }

    // 添加侧边栏管理后台选项
    addAdminSidebarOption() {
        // 这个方法将在sidebar.js中实现
        if (window.addAdminSidebarOption) {
            window.addAdminSidebarOption();
        }
    }

    // 跳转到管理后台
    navigateToAdmin() {
        this.navigateTo('admin.html');
    }

    // 初始化动画
    initAnimations() {
        // 页面加载动画
        gsap.from('.welcome-title', {
            duration: 1,
            y: 50,
            opacity: 0,
            ease: 'power3.out'
        });

        gsap.from('.welcome-subtitle', {
            duration: 1,
            y: 30,
            opacity: 0,
            delay: 0.3,
            ease: 'power3.out'
        });

        // 移除统计数据动画

        // 功能卡片动画已删除 - 确保对齐
        gsap.set('.feature-card', { opacity: 1, y: 0 }); // 直接设置为最终状态

        // 步骤动画
        gsap.from('.step-item', {
            duration: 0.8,
            scale: 0.8,
            opacity: 0,
            stagger: 0.2,
            ease: 'back.out(1.7)',
            scrollTrigger: {
                trigger: '.steps-container',
                start: 'top 80%'
            }
        });
    }

    // 移除数字计数器功能

    // 初始化滚动效果
    initScrollEffects() {
        // 导航栏滚动效果
        let lastScrollTop = 0;
        const navbar = document.querySelector('.navbar');

        window.addEventListener('scroll', () => {
            const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
            
            if (scrollTop > lastScrollTop && scrollTop > 100) {
                // 向下滚动，隐藏导航栏
                navbar.style.transform = 'translateY(-100%)';
            } else {
                // 向上滚动，显示导航栏
                navbar.style.transform = 'translateY(0)';
            }
            
            lastScrollTop = scrollTop;
        });

        // 视差滚动效果
        window.addEventListener('scroll', () => {
            const scrolled = window.pageYOffset;
            const parallax = document.querySelector('.welcome-section');
            const speed = scrolled * 0.5;
            
            if (parallax) {
                parallax.style.transform = `translateY(${speed}px)`;
            }
        });
    }

    // 导航到指定页面
    navigateTo(url) {
        // 添加页面切换动画
        gsap.to('body', {
            duration: 0.3,
            opacity: 0,
            ease: 'power2.inOut',
            onComplete: () => {
                window.location.href = url;
            }
        });
    }

    // 退出登录
    logout() {
        // 显示确认对话框
        if (confirm('确定要退出登录吗？')) {
            // 清除本地存储
            localStorage.removeItem('token');
            localStorage.removeItem('username');
            localStorage.removeItem('userId');
            
            // 添加退出动画
            gsap.to('body', {
                duration: 0.5,
                scale: 0.9,
                opacity: 0,
                ease: 'power2.inOut',
                onComplete: () => {
                    window.location.href = '/index.html';
                }
            });
        }
    }
}

// 全局函数
function navigateTo(url) {
    mainPage.navigateTo(url);
}

function logout() {
    mainPage.logout();
}

// 页面加载完成后初始化
let mainPage;
document.addEventListener('DOMContentLoaded', function() {
    mainPage = new MainPage();
});

// 页面可见性变化时的处理
document.addEventListener('visibilitychange', function() {
    if (document.visibilityState === 'visible') {
        // 页面重新可见时，重新检查认证状态
        mainPage.checkAuth();
    }
});

// 添加键盘快捷键支持
document.addEventListener('keydown', function(e) {
    // Ctrl + 1-6 快速导航
    if (e.ctrlKey) {
        switch(e.key) {
            case '1':
                e.preventDefault();
                navigateTo('recommendation-new.html');
                break;
            case '2':
                e.preventDefault();
                navigateTo('mbti-test.html');
                break;
            case '3':
                e.preventDefault();
                navigateTo('holland-test.html');
                break;
            case 'q':
                e.preventDefault();
                logout();
                break;
        }
    }
});

// 添加触摸手势支持（移动端）
let touchStartX = 0;
let touchStartY = 0;

document.addEventListener('touchstart', function(e) {
    touchStartX = e.touches[0].clientX;
    touchStartY = e.touches[0].clientY;
});

document.addEventListener('touchend', function(e) {
    const touchEndX = e.changedTouches[0].clientX;
    const touchEndY = e.changedTouches[0].clientY;
    
    const deltaX = touchEndX - touchStartX;
    const deltaY = touchEndY - touchStartY;
    
    // 检测左右滑动手势
    if (Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > 100) {
        if (deltaX > 0) {
            // 右滑 - 可以添加特定功能
            console.log('右滑手势');
        } else {
            // 左滑 - 可以添加特定功能
            console.log('左滑手势');
        }
    }
});

// 添加页面性能监控
window.addEventListener('load', function() {
    // 计算页面加载时间
    const loadTime = performance.timing.loadEventEnd - performance.timing.navigationStart;
    console.log(`页面加载时间: ${loadTime}ms`);
    
    // 如果加载时间过长，可以显示提示
    if (loadTime > 3000) {
        console.warn('页面加载时间较长，建议优化');
    }
});

// 添加错误处理
window.addEventListener('error', function(e) {
    console.error('页面错误:', e.error);
    // 可以在这里添加错误上报逻辑
});

// 添加网络状态监控
window.addEventListener('online', function() {
    console.log('网络连接已恢复');
    // 可以在这里添加重新连接的逻辑
});

window.addEventListener('offline', function() {
    console.log('网络连接已断开');
    // 可以在这里显示离线提示
});
