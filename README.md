# PJSIP Android Build Repository

This repository contains the PJSIP source code and build scripts to create Android libraries using GitHub Codespaces.

## 🚀 Quick Start

### 1. Open in GitHub Codespaces
- Click the "Code" button on this repository
- Select "Codespaces" tab
- Click "Create codespace on main"

### 2. Run the Build Script
Once the Codespace is open, run:
```bash
chmod +x build-pjsip.sh
./build-pjsip.sh
```

### 3. Download the Results
After successful build, the output will be in:
- `output/libs/arm64-v8a/` - Native libraries (.so files)
- `output/java/org/pjsip/pjsua2/` - Java bindings

## 📱 What You'll Get

- **PJSIP Native Libraries** - Ready for Android integration
- **Java Bindings** - PJSUA2 API for Android apps
- **Multiple Architectures** - arm64-v8a (can be extended)

## 🔧 Build Process

The build script will:
1. Install required packages
2. Download Android NDK r25c
3. Configure PJSIP for Android
4. Build native libraries
5. Generate Java bindings
6. Organize output files

## 📦 Integration

Copy the built files to your Android project:
- `.so` files → `app/src/main/jniLibs/arm64-v8a/`
- Java files → `app/src/main/java/org/pjsip/pjsua2/`

## ⚠️ Requirements

- GitHub Codespaces (free tier: 60 hours/month)
- PJSIP source code (included)
- Android NDK r25c (downloaded automatically)

## 🎯 Expected Output

```
output/
├── libs/
│   └── arm64-v8a/
│       ├── libpjsua2.so
│       ├── libc++_shared.so
│       └── other dependencies...
└── java/
    └── org/pjsip/pjsua2/
        └── *.java files...
```

## 🚨 Troubleshooting

If the build fails:
1. Check the error messages
2. Ensure you have sufficient Codespaces time
3. Try running the script again

## 📞 Support

This build process creates the exact PJSIP libraries needed for your DIVO app integration.
