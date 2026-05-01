---
id: task-phase-3-data-attachments
type: task
tags: ["#phase-3", "#data"]
links: ["[[MOC-tasks]]"]
---

# 💾 Task: Phase 3 - Data Attachments

**Goal:** Implement a Fabric-equivalent to NeoForge Data Attachments using Mixins.

## Goals
- [ ] Create `ParkourabilityAccess` interface
- [ ] Create `PlayerDataMixin` for persistent field injection into `Player`
- [ ] Refactor `Parkourability.java` to use Mixin access
- [ ] Wire up data persistence (NBT save/load) via Mixin hooks

## Verification
- [ ] `Parkourability.get(player)` returns a valid instance
- [ ] Data persists across player sessions (re-log)
