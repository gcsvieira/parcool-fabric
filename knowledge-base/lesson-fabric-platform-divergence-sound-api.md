---
id: lesson-fabric-platform-divergence-sound-api
type: leaf
tags: ["#porting", "#mass-actions", "#compilation"]
links: ["[[MOC-lessons]]", "[[task-phase-4-core-action-loop]]"]
---

# 🏃 Session 6: Mass Action Porting

## Technical Gotchas

### 1. Platform-Specific Method Divergence
- **Forge/NeoForge**: `Player.setForcedPose(Pose)` and `getForcedPose()` are injected by the loader to handle complex state.
- **Fabric/Vanilla**: These do NOT exist. Use `player.setPose(Pose)` and `player.getPose()`. Note that Vanilla tick logic may overwrite `setPose` unless managed via Mixin or constant setting in `onWorkingTick`.

### 2. Identifier API (Minecraft 1.21.x)
- `Identifier.of(namespace, path)` is the modern standard but may fail depending on the specific Loom/Fabric version. 
- `Identifier.fromNamespaceAndPath(namespace, path)` remains the most stable fallback across 1.21 mappings.

### 3. Namespace Collisions in Config
- Avoid naming fields exactly like their Enum types (e.g., `public FastRunControl FastRunControl`).
- The Java compiler may struggle to differentiate between the static Enum class and the instance field, leading to "static context" errors.
- **Solution**: Use camelCase for fields (e.g., `fastRunControl`).

### 4. Player State Accessibility
- Fields like `swimAmount` and `swimAmountO` are often `protected` in `Player`.
- Direct access in action classes will fail without an Access Widener or Mixin. 
- **Action**: Stubs used for now; full port requires entry in `parcool.accesswidener`.

### 5. Sound API
- When using `Registry.registerForHolder`, the resulting object is a `Holder<T>`.
- Use `HOLDER.value()` to pass to `player.playSound()` instead of `.get()` (which is NeoForge specific).

## Action Migration Checklist
1. Logic port (Copy from NeoForge).
2. Replace `SoundEvents.XXX.get()` with `SoundEvents.XXX.value()`.
3. Rename Config field access to camelCase.
4. Replace `setForcedPose` with `setPose`.
5. Verify `WorldUtil` method compatibility.
