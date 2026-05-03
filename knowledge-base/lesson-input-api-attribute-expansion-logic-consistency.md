---
id: lesson-input-api-attribute-expansion-logic-consistency
type: lesson
tags: ["#porting", "#movement", "#fabric"]
links: ["[[MOC-lessons]]", "[[task-phase-4-core-action-loop]]"]
---

# 🎓 Lesson: Session 7 - Movement Action Completion

## Context
Finalizing the port of all 26 parkour movement actions from NeoForge to Fabric 1.21.1.

## Technical Findings
1. **Input API Divergence**: Fabric (1.21.1) uses `player.input.keyPresses.forward()` instead of the legacy `cp.input.pressingForward`. Standardizing these in `KeyBindings` utilities saved significant refactoring time.
2. **Vanilla Attribute Expansion**: Minecraft 1.21 added several generic attributes (like `GRAVITY`, `STEP_HEIGHT`, `SWIM_SPEED`). Using these directly instead of platform-specific counterparts (like `NeoForgeMod.SWIM_SPEED`) ensures better compatibility.
3. **Block Destruction Effects**: `net.minecraft.world.level.block.Block.getRawIdFromState` was renamed/replaced by `Block.getId(state)` in some 1.21 versions.
4. **Action Signature Consistency**: Overriding methods like `wantsToShowStatusBar` must exactly match the base `Action` signature (using `Player` instead of `LocalPlayer`) to avoid compilation failures, even if the logic is client-side only.

## Mistakes & Fixes
- **Missing Config Keys**: Several config options (e.g., `HideInBlockSneakNeeded`) were missing from the initial `ParCoolConfig` port. Added them as they were discovered during action porting.
- **Import Omissions**: Mass porting leads to missing imports (e.g., `KeyBindings` in `ChargeJump`). Running `compileJava` frequently catches these early.

## Strategic Takeaway
- **Logic vs. Rendering**: Keeping the animation system stubbed allowed for 100% logic parity without being blocked by rendering pipeline changes. This "logic-first" approach is highly effective for mass porting.
