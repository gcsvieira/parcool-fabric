---
id: task-phase-7-networking
type: task
tags: ["#phase-7", "#networking", "#sync"]
links: ["[[MOC-tasks]]"]
---

# 🌐 Task: Phase 7 - Networking & State Sync

**Goal:** Fix state persistence, stamina synchronization, and entity-based movement.

## Goals
- [ ] **Fix Stamina Persistence**: Ensure `Parkourability` data survives player death/dimension change.
- [ ] **Stabilize Zipline Rope Entity**: Resolve game crash when spawning/interacting with `ZiplineRopeEntity`.
- [ ] **Sync Action State**: Ensure server-side logic (`onWorkingTickInServer`) matches client state for all 26 actions.
- [ ] **Fix Blocked Actions (from Phase 4 test)**:
    - [ ] `ChargeJump`: Enable stamina-based charging.
    - [ ] `FastSwim`: Enable stamina-based speed boost.
    - [ ] `BreakfallReady`: Enable stamina-based landing logic.
    - [ ] `RideZipline`: Enable rope interaction.

## Verification
- [ ] Stamina bar correctly reflects server-side value.
- [ ] Zipline ropes can be placed and ridden without crashes.
- [ ] Actions requiring stamina fail when exhausted.
