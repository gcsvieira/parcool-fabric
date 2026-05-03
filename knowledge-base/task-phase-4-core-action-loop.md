---
id: task-phase-4-core-action-loop
type: task
tags: ["#phase-4", "#loop"]
links: ["[[MOC-tasks]]", "[[lesson-fabric-platform-divergence-sound-api]]", "[[lesson-input-api-attribute-expansion-logic-consistency]]"]
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
- [x] Mass porting of remaining movement actions
    - [x] Slide, Vault, WallJump, Roll
    - [x] Dodge, FastRun, ClingToCliff, ClimbUp
    - [x] VerticalWallRun, HorizontalWallRun, WallSlide, ClimbPoles
    - [x] JumpFromBar, HangDown, CatLeap, ChargeJump
    - [x] Flipping, HideInBlock, Dive, SkyDive
    - [x] QuickTurn, Tap, RideZipline, BreakfallReady, FastSwim

## Verification
- [x] Movement keys trigger parkour actions (Slide, Vault, etc.)
- [x] Stamina depletes during actions (Verified for FastRun/Slide/etc.)
- [x] Actions feel responsive and correct
- [x] Compilation successful across all ported actions

## 🧪 Action Test List (Phase 4 Final)
- [x] **Slide**: Sprint + Sneak on ground.
- [x] **Vault**: Sprint/Run toward 1-block obstacles.
- [x] **WallJump**: Jump toward wall, press Jump again.
- [ ] **Roll**: Fall from height + hold Breakfall key.
    - Most likely won't work without the animation logic.
- [x] **Dodge**: Double-tap move key or press Dodge key.
- [x] **FastRun**: Hold FastRun key while sprinting.
- [x] **ClingToCliff**: Jump toward ledge + hold Grab.
- [x] **ClimbUp**: Move forward while Clinging.
- [ ] **VerticalWallRun**: Sprint toward wall + Jump.
    - Didn't seem to work.
- [x] **HorizontalWallRun**: Sprint along wall + hold WallRun.
- [x] **WallSlide**: Slide down wall while touching it.
- [x] **ClimbPoles**: (Stub - verify no crash on contact).
    - It actually works? When I jump to a fence, it climbs. However, I can't climb the last fence to get on top of it (most likely because of the 1.5 block height)
- [x] **JumpFromBar**: Press Jump while Hanging.
- [ ] **HangDown**: Sneak over edge with Bar/Fence.
    - Didn't seem to work.
- [x] **CatLeap**: While fast running, quickly press Sneak.
- [ ] **ChargeJump**: Hold Sneak on ground + Jump.
    - Didn't seem to work. I tried jumping right after crouching and nothing happened. Maybe its because the stamina logic is missing.
- [ ] **Flipping**: Sprint + Jump + Flip key (or config trigger).
    - Most likely won't work without the animation logic.
- [ ] **Dive**: Jump + Dive key in air.
    - Most likely won't work without the animation logic.
- [ ] **SkyDive**: Dive + Jump for glide.
    - Most likely won't work without the animation logic.
- [x] **QuickTurn**: Press QuickTurn key to 180.
- [ ] **Tap**: Soft landing (auto or key).
    - Just like Roll, most likely won't work without the animation, sound and particles logic.
- [ ] **RideZipline**: Hold Zipline key near rope in air.
    - Zipline rope entity isn't implemented yet. Game crashes when I add it. Won't work right now.
- [x] **Crawl**: Press Crawl key (verify hitbox).
- [ ] **HideInBlock**: Press Hide key near valid hole.
    - Block particles play out, player speed goes down, but player doesn't actually hide nor crawl.
- [ ] **BreakfallReady**: Check stamina/logic before impact.
    - Can't test without stamina system.
- [ ] **FastSwim**: Sprint/FastRun in water.
    - Didn't seem to work. Most likely because of the stamina system.
