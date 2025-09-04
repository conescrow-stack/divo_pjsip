#!/bin/bash

# PJSIP Android Build Script for GitHub Codespaces
# This script builds PJSIP for Android using NDK r25c

set -e

echo "🚀 Starting PJSIP Android Build Process..."

# Update system packages
echo "📦 Updating system packages..."
sudo apt-get update
sudo apt-get install -y build-essential wget unzip

# Download Android NDK r25c
echo "📱 Downloading Android NDK r25c..."
NDK_VERSION="r25c"
NDK_DIR="/opt/android-ndk-${NDK_VERSION}"

if [ ! -d "$NDK_DIR" ]; then
    wget -O android-ndk.zip "https://dl.google.com/android/repository/android-ndk-${NDK_VERSION}-linux-x86_64.zip"
    unzip android-ndk.zip
    sudo mv "android-ndk-${NDK_VERSION}" /opt/
    rm android-ndk.zip
fi

# Set environment variables
export ANDROID_NDK_ROOT="$NDK_DIR"
export PATH="$NDK_DIR/toolchains/llvm/prebuilt/linux-x86_64/bin:$PATH"
export TARGET_ABI="arm64-v8a"

echo "🔧 Environment variables set:"
echo "   ANDROID_NDK_ROOT: $ANDROID_NDK_ROOT"
echo "   TARGET_ABI: $TARGET_ABI"

# Verify NDK installation
echo "✅ Verifying NDK installation..."
ls -la "$NDK_DIR/toolchains/llvm/prebuilt/linux-x86_64/bin" | head -5

# Navigate to PJSIP source
cd pjproject-2.15.1

# Create config_site.h if it doesn't exist
echo "📝 Creating config_site.h..."
mkdir -p pjlib/include/pj
cat > pjlib/include/pj/config_site.h << 'EOF'
/* Activate Android specific settings in the 'config_site_sample.h' */
#define PJ_CONFIG_ANDROID 1
#include <pj/config_site_sample.h>

#define PJMEDIA_HAS_VIDEO 1
EOF

# Configure PJSIP for Android
echo "⚙️ Configuring PJSIP for Android..."
./configure-android --use-ndk-cflags

# Build PJSIP
echo "🔨 Building PJSIP..."
make dep && make clean && make

echo "✅ PJSIP build completed successfully!"

# Build PJSUA2 Java interface
echo "☕ Building PJSUA2 Java interface..."
cd pjsip-apps/src/swig
make

echo "✅ PJSUA2 Java interface built successfully!"

# Create output directory structure
echo "📁 Creating output directory structure..."
mkdir -p ../../../../output/libs/arm64-v8a
mkdir -p ../../../../output/java

# Copy built libraries
echo "📋 Copying built libraries..."
cp -v ../../swig/java/android/pjsua2/src/main/jniLibs/arm64-v8a/*.so ../../../../output/libs/arm64-v8a/
cp -rv ../../swig/java/android/pjsua2/src/main/java/org/pjsip/pjsua2 ../../../../output/java/

echo "🎉 Build process completed successfully!"
echo "📦 Output files are in the 'output' directory:"
echo "   - Native libraries: output/libs/arm64-v8a/"
echo "   - Java bindings: output/java/org/pjsip/pjsua2/"
