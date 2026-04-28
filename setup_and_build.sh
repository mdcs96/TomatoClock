#!/bin/bash

echo "🍅 番茄钟 APK 编译脚本"
echo "======================="
echo ""

# 检查是否有 Homebrew
if ! command -v brew &> /dev/null; then
    echo "❌ 未安装 Homebrew，请先安装: https://brew.sh"
    exit 1
fi

echo "✅ 检测到 Homebrew"

# 安装 Gradle（如果未安装）
if ! command -v gradle &> /dev/null; then
    echo "📦 正在安装 Gradle..."
    brew install gradle
else
    echo "✅ Gradle 已安装"
fi

# 检查 Android SDK
if [ ! -d "$HOME/Library/Android/sdk" ]; then
    echo ""
    echo "⚠️  未检测到 Android SDK"
    echo ""
    echo "请选择安装方式:"
    echo "1) 安装 Android Studio (推荐，包含完整工具)"
    echo "2) 仅安装命令行工具 (最小化安装)"
    echo "3) 跳过，稍后手动安装"
    echo ""
    read -p "请输入选项 (1-3): " choice

    case $choice in
        1)
            echo "正在安装 Android Studio..."
            brew install --cask android-studio
            echo ""
            echo "⚠️  请启动 Android Studio 完成初始设置，然后重新运行此脚本"
            exit 0
            ;;
        2)
            echo "正在安装 Android 命令行工具..."
            brew install --cask android-commandlinetools

            export ANDROID_HOME=$HOME/Library/Android/sdk
            export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin

            echo "正在安装必要的 SDK 组件..."
            sdkmanager --install "platform-tools" "platforms;android-34" "build-tools;34.0.0"
            ;;
        3)
            echo "跳过 SDK 安装"
            echo ""
            echo "请手动安装 Android SDK，然后设置 ANDROID_HOME 环境变量"
            exit 0
            ;;
        *)
            echo "无效选项"
            exit 1
            ;;
    esac
fi

echo "✅ Android SDK 已就绪"

# 设置环境变量
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools

# 创建 local.properties
echo "sdk.dir=$ANDROID_HOME" > local.properties

echo ""
echo "🔨 开始编译 APK..."
echo ""

# 使用 Gradle 编译
gradle wrapper
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ 编译成功！"
    echo ""
    echo "APK 位置: app/build/outputs/apk/debug/app-debug.apk"
    echo ""

    # 复制 APK 到桌面
    if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
        cp app/build/outputs/apk/debug/app-debug.apk ~/Desktop/TomatoClock.apk
        echo "✅ APK 已复制到桌面: ~/Desktop/TomatoClock.apk"
        echo ""
        echo "📱 安装到手机:"
        echo "   1. 将 TomatoClock.apk 传输到手机"
        echo "   2. 在手机上打开文件管理器"
        echo "   3. 点击 APK 文件安装"
    fi
else
    echo ""
    echo "❌ 编译失败，请检查错误信息"
    exit 1
fi
