# OpenMan Android — Autonomous AI Agent Workspace

> Native Android Kotlin replica of **OpenMan / Manus** (`https://github.com/nickvasilescu/openman`) featuring CodeStation's Obsidian Dark Glass design system and direct **FreeLLMAPI** local/cloud gateway integration.

![Platform](https://img.shields.io/badge/Platform-Android_14_%28API_34%29-00D2FF?style=flat-square)
![Language](https://img.shields.io/badge/Kotlin-2.0.20-438DFF?style=flat-square)
![Compose](https://img.shields.io/badge/Jetpack_Compose-2024.09.00-10B981?style=flat-square)
![Design](https://img.shields.io/badge/Design-Liquid_Glass_Obsidian-0B0D12?style=flat-square)

---

## 🚀 Overview

OpenMan Android brings the autonomous agent workspace to your mobile device:
- **Modes**:
  - **Agent**: Autonomous goal breakdown, browser navigation, shell command execution, web search, and artifact synthesis.
  - **Chat**: Interactive conversational assistance.
  - **Plan**: Structured roadmaps and execution blueprints.
- **Live Agent Workspace**:
  - **Work Log**: Real-time thinking traces, collapsible step details, tool indicators, and output buffers.
  - **Simulated Computer View**: Live browser viewport with URL bar and terminal console.
  - **Artifacts Manager**: Exportable Markdown reports, HTML websites, and code snippets.
- **FreeLLMAPI Multi-Provider Engine**:
  - Built-in connection to FreeLLMAPI (`http://10.0.2.2:3001` or `http://127.0.0.1:3001`).
  - Pre-configured unified token `freellmapi-0dafad9d80b0deb3a0e9799aa1ef4e5a2f3215243e7487d0`.
  - Supports `auto`, `fusion`, `gemini-3.8-flash`, `glm-5.3-flash`, `claude-opus-4-5`, and `claude-sonnet-4-5`.
- **Obsidian Liquid Glass Design**:
  - OLED Midnight `#0B0D12` background.
  - 2-layer glass material (specular outer highlight + stabilized inner plate).
  - Neon cyan `#00D2FF` and electric blue `#438DFF` accents.

---

## 🛠️ Build & Installation

```bash
# Clone the repository
git clone https://github.com/Anisghd-lab/openman-android.git
cd openman-android

# Build the debug APK
./gradlew assembleDebug

# Install via ADB
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 📄 License

MIT License. Copyright (c) 2026 GHD Interactive Studio & OpenMan contributors.
