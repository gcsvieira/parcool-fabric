---
id: task-phase-7-networking
type: task
tags: ["#phase-7", "#networking"]
links: ["[[MOC-tasks]]"]
---

# 🌐 Task: Phase 7 - Networking

**Goal:** Implement Fabric Networking for multiplayer synchronization.

## Goals
- [ ] Port all 8 payload classes to `CustomPacketPayload`
- [ ] Register payloads with `PayloadTypeRegistry`
- [ ] Implement `ActionSynchronizationBroadcaster`
- [ ] Implement `StaminaSynchronizationBroadcaster`
- [ ] Port Player Join/Clone/Visibility handlers

## Verification
- [ ] Actions performed by one player are visible to others
- [ ] Stamina stays in sync between client and server
- [ ] No networking-related crashes in multiplayer
