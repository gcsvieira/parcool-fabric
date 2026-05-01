---
id: task-phase-4-core-action-loop
type: task
tags: ["#phase-4", "#loop"]
links: ["[[MOC-tasks]]"]
---

# 🔄 Task: Phase 4 - Core Action Loop

**Goal:** Enable the parkour movement logic in a singleplayer environment.

## Goals
- [x] Port `ActionProcessor` to Fabric tick callbacks
- [x] Register all handlers to Fabric event callbacks
- [x] Port Key Bindings and `KeyRecorder`
- [x] Enable core Mixins (`LivingEntity`, `Entity`, `Player`)
- [x] Port client-side stamina logic
- [x] Implement and stabilize Crawl action
- [ ] Mass port remaining movement actions

## Verification
- [x] Movement keys trigger parkour actions (e.g., Wall Jump)
- [x] Stamina depletes during actions
- [x] Actions feel responsive and correct
