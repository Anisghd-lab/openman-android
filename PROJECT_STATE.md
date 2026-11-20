# 📋 OpenMan Android — Project State Ledger

## 1. Goals & Vision
- Replicate the autonomous AI agent workspace **OpenMan / Manus** (`https://github.com/nickvasilescu/openman`) as a native Android application in Kotlin with Jetpack Compose.
- Strictly adhere to CodeStation's signature **Obsidian Dark Mode** (`#0B0D12` OLED black, translucent glass cards, specular highlights, stabilized inner plates, neon cyan `#00D2FF` and electric blue `#438DFF` accents) powered by the `liquid-glass-design` system.
- Direct integration of **FreeLLMAPI** (`http://10.0.2.2:3001` / `http://127.0.0.1:3001`) with token `freellmapi-0dafad9d80b0deb3a0e9799aa1ef4e5a2f3215243e7487d0` and model router (`auto`, `fusion`, `gemini-3.8-flash`, `glm-5.3-flash`, `claude-opus-4-5`).
- Multi-mode agent orchestration:
  * **Agent Mode**: Autonomous planning, browser automation, terminal execution, search queries, file/artifact compilation.
  * **Chat Mode**: Direct interactive conversations.
  * **Plan Mode**: High-level execution roadmaps.
- Dual-layer Liquid Glass architecture:
  * Outer shell with specular border highlights and gradient depth.
  * Inner stabilized scrim protecting text readability across all views.
- Strict compliance with `github-apk-release`: dedicated GitHub repository (`Anisghd-lab/openman-android`), compilation of release APK, SHA-256 verification, and publication via GitHub Releases.

## 2. Current Development State
- **Project Structure**: Android Gradle project configured with Gradle 8.9, AGP 8.5.2, Kotlin 2.0.20, Compose Compiler 2.0, Android SDK 34.
- **Data & API Layer**:
  - `FreeLlmApiService`: Retrofit 2 + OkHttp 4 client targeting FreeLLMAPI `/v1/chat/completions` and `/v1/models`.
  - `AgentEngine`: Autonomous orchestrator with prompt parsing, step transitions, simulated tool outputs, and artifact extraction.
  - `TaskRepository`: Persistent task state, step history, chat messages, and agent skills.
  - `SettingsRepository`: Dynamic endpoint configuration, API keys, and model selection.
- **UI & Design System**:
  - `LiquidGlassComponents`: 2-layer glass cards, tactile glass buttons, glowing pulse indicators, monospace badges.
  - `HomeScreen`: Workspace dashboard with prompt composer, mode selector capsule, suggested tasks, and task history.
  - `TaskDetailScreen`: Full Manus execution view with Work Log timeline, Simulated Computer/Browser viewport, Terminal console, and Artifacts viewer.
  - `SkillsScreen`: Autonomous skills manager (Browser, Interpreter, Research, Compiler, MCP).
  - `SettingsScreen`: FreeLLMAPI configuration, connection test ping, and model selector.
- **Compilation & Verification**:
  - Successfully built with `./gradlew assembleDebug` (38 tasks executed / up-to-date).
  - APK location: `app/build/outputs/apk/debug/app-debug.apk` (18 MB).
  - SHA-256 Checksum: `baa208d9c53518e5c4f7d0b1935dc6832be695f96e70121b058e60a3dba70918`.

## 3. Known Gaps & Technical Debts
- Hardware acceleration for WebRTC video stream if connecting to live cloud desktop (VNC/Kasm). Currently simulated via live rendered DOM viewport and terminal buffer.
- Streaming SSE token deltas: Currently handles complete tool cycles and rapid updates; SSE streaming converter can be added for per-token rendering.

## 4. Remaining Backlog
- [x] Build and compile debug APK via `./gradlew assembleDebug`.
- [x] Verify SHA-256 checksum of generated APK.
- [ ] Initialize Git repository and push to GitHub `Anisghd-lab/openman-android`.
- [ ] Create GitHub Release `v1.0.0` with APK asset attached.
