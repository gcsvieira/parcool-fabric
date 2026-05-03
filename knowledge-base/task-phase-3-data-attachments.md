---
id: task-phase-3-data-attachments
type: task
tags: ["#phase-3", "#data"]
links: ["[[MOC-tasks]]", "[[lesson-data-attachment-stability-hitbox-sync]]"]
---

# 💾 Task: Phase 3 - Data Attachments

**Goal:** Implement a Fabric-equivalent to NeoForge Data Attachments using Mixins.

## Goals
- [x] Create `ParkourabilityAccess` interface → verify: [ParkourabilityAccess.java]
- [x] Create `PlayerDataMixin` for persistent field injection into `Player` → verify: [PlayerDataMixin.java]
- [x] Refactor `Parkourability.java` to use Mixin access → verify: [Parkourability.java]
- [x] Wire up data persistence (NBT save/load) via Mixin hooks → verify: [ValueInput/Output in PlayerDataMixin]

## Verification
- [x] `Parkourability.get(player)` returns a valid instance
- [x] Data persists across player sessions (re-log)
