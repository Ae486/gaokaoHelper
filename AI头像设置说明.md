# AI助手头像设置说明

## 📁 文件位置
请将您提供的AI助手头像图片保存到以下位置：
```
src/main/resources/static/images/ai-avatar.png
```

## 🖼️ 图片要求
- **格式**: PNG格式（推荐，支持透明背景）
- **尺寸**: 建议 100x100 像素或更高分辨率的正方形图片
- **背景**: 透明背景最佳，会自动裁剪为圆形
- **文件名**: 必须命名为 `ai-avatar.png`

## 📂 目录结构
确保目录结构如下：
```
src/
└── main/
    └── resources/
        └── static/
            ├── images/
            │   └── ai-avatar.png  ← 您的头像图片
            ├── css/
            ├── js/
            └── *.html
```

## 🎨 样式效果
头像将会：
- ✅ 自动裁剪为圆形
- ✅ 添加蓝色边框 (#69cee0)
- ✅ 居中显示并覆盖整个头像区域
- ✅ 如果图片加载失败，显示备用的渐变背景和"AI"文字

## 🔧 如果图片不显示
1. 检查文件路径是否正确
2. 确认文件名为 `ai-avatar.png`
3. 检查图片格式是否为PNG
4. 清除浏览器缓存后重新加载页面

## 🎯 备用方案
如果您希望使用其他格式的图片，可以修改 `ai-chat-test.html` 中的CSS：
```css
.ai-avatar {
    background: url('images/your-image-name.jpg') center/cover;
    /* 其他样式保持不变 */
}
```

## 📱 测试
保存图片后，访问AI对话页面，您应该能看到：
- 圆形的AI助手头像
- 思考时的动画效果
- 逐行浮现的AI回复
