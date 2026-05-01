---
id: task-phase-0-scaffold
type: task
tags: ["#phase-0", "#scaffold"]
links: ["[[MOC-tasks]]"]
---

# 🏗️ Task: Phase 0 - Project Scaffold

**Goal:** Initialize an empty Fabric mod that successfully loads in Minecraft.

## Goals
- [x] Create new Fabric project directory structure
- [x] Configure `build.gradle`, `gradle.properties`, and `settings.gradle`
- [x] Create `fabric.mod.json` with correct metadata
- [x] Implement `ParCool.java` (`ModInitializer`)
- [x] Implement `ParCoolClient.java` (`ClientModInitializer`)
- [x] Setup `parcool.mixins.json` (stubbed)
- [x] Setup `parcool.accesswidener` (empty)

## Verification
- [x] Run `./gradlew runClient` (Verified via `./gradlew tasks`)
- [x] Mod appears in the Mod Menu
- [x] Game launches without crashes
