---
id: task-phase-5-animations
type: task
tags: ["#phase-5", "#animations"]
links: ["[[MOC-tasks]]"]
---

# 🎬 Task: Phase 5 - Animations

**Goal:** Restore player animations and camera effects.

## Goals
- [ ] Port all 27 animator implementations
- [ ] Enable `PlayerModelMixin` and `AvatarRendererMixin`
- [ ] Port Camera manipulation Mixins
- [ ] Port `RenderBehaviorEnforcer`

## Verification
- [x] Launch game without `InvalidInjectionException` or `MixinTransformerError`
- [x] Animations are visible in third-person
    - Some animations dont happen due to the core action being flawed, for example: Climbing fences has no animations, Vertical wall run doesn't happen (therefore couldnt test animation), Sky dive and water dive don't work either. Sneak charge jump plays the animation, but the jump doesn't get higher.
- [x] Camera effects (rolls, tilts) trigger correctly via `GameRendererMixin`
    - [x] Fixed: Camera roll now explicitly resets to 0 in `Animation.java` when no animator is active.
- [x] Arms/Legs bobbing is correctly animated using `state.ageInTicks`
- [x] No `ClassCastException` in `PlayerRendererMixin.extractRenderState` when casting `Avatar` to `Player`
- [x] Partial tick interpolation is smooth in animators (verify bridge access)
- [x] Shadow rendering remains correct (verify `LivingEntityRenderer` parent call stability)

## Post-Stabilization Test Checklist
- [ ] **Camera Roll Reset**: Wallrun along a wall, then stop. Camera must return to 0 tilt instantly.
- [ ] **Soft Fall (Breakfall)**: Fall from > 3 blocks and hold `R`. Character should roll/tap on landing.
- [ ] **Sneak Charge Jump**: Hold `Sneak` + `Jump` until bar is full, then release. Jump should be significantly higher.
- [ ] **Vertical Wall Run**: Sprint at a wall, jump, then press jump again while looking up (4-12 ticks after first jump).
- [ ] **Dive (Sky/Water)**: Hold `Jump` in mid-air to dive.
- [ ] **Fast Swim**: Sprint in water. Check if speed is noticeably faster (modifier applied to attributes).

Conclusion: Core logic for events (Jump/Land) and camera cleanup has been implemented. Verification needed in-game.