---
id: lesson-mixin-descriptor-mismatch-rendering-pipeline
type: lesson
tags: [mixin, rendering, 1.21.11, mojmap, avatarrenderstate, descriptor-mismatch]
links: ["[[MOC-lessons]]", "[[MOC-porting]]"]
---

# Lesson: Mixin Descriptor Mismatches in the 1.21.11 Rendering Pipeline

## Context

Migrating ParCool's animation system to the Minecraft 1.21.11 (1.21.2+) rendering architecture, which introduces `AvatarRenderState` and significant refactors to `GameRenderer` and `LivingEntityRenderer`.

## 1. The "LVT Resolution" Trap: Interface Bridges vs. Mixin Casts

### The Problem
When trying to share data between `PlayerRenderStateMixin` and `PlayerRendererMixin`, we initially used a direct cast:
```java
// ❌ This causes MixinTransformerError (Invalid LVT) during runtime transformation
Player player = ((PlayerRenderStateMixin) (Object) state).parCool$player;
```
Even if it compiles, the Mixin applicator often fails to resolve the type at runtime because the Mixin class itself is being transformed.

### The Fix: The Bridge Pattern
Define a clean Java interface for the data you're injecting, and have the Mixin implement it.
```java
// ✅ Safe and stable
public interface PlayerRenderStateAccess {
    Player parCool$getPlayer();
    void parCool$setPlayer(Player player);
}

// In Mixin
@Mixin(AvatarRenderState.class)
public class PlayerRenderStateMixin implements PlayerRenderStateAccess { ... }
```

## 2. Signature Mismatches: `Avatar` vs `Player`

### The Problem
In the 1.21.11 mapping environment, `PlayerRenderer` (remapped as `AvatarRenderer`) uses `Avatar` in its `extractRenderState` method, not `AbstractClientPlayer`.
```java
// ❌ Expected (Lnet/minecraft/world/entity/Avatar;...) but found (Lnet/minecraft/client/player/AbstractClientPlayer;...)
@Inject(method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;...)", ...)
```

### The Fix
Always trust the crash log's "Expected" signature. Use `javap` to verify the exact descriptor in the game JAR if unsure.

## 3. The 1.21.2+ `LivingEntityRenderer` Refactor

### The Problem
`LivingEntityRenderer.setupRotations` signature changed. `ageInTicks` was removed from the parameters and moved into the `RenderState`.
```java
// ❌ 1.20 signature
protected void setupRotations(S state, PoseStack poseStack, float ageInTicks, float rotationYaw, float scale)

// ✅ 1.21.11 signature
protected void setupRotations(S state, PoseStack poseStack, float rotationYaw, float scale)
```

## 4. `GameRenderer` and `DeltaTracker` Evolution

### The Problem
In 1.21.2+, `GameRenderer.render` no longer takes `float tickDelta` and `long nanoTime`. It now takes `DeltaTracker deltaTracker`.
Furthermore, the `Camera.setup` call was moved from `render` to a new method: **`updateCamera(DeltaTracker)`**.

### The Fix
Update the Mixin to target the correct method and descriptor.
```java
@Inject(
    method = "updateCamera(Lnet/minecraft/client/DeltaTracker;)V", 
    at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;ZZF)V", shift = At.Shift.AFTER)
)
```
*Note: `Camera.setup` also changed its first parameter from `BlockGetter` to `Level`.*

## Key Takeaways for Mixin Debugging

1. **`javap` is mandatory**: When a Mixin fails with "Scanned 0 targets", use `javap -c -cp <jar> <class>` to find where the code actually moved.
2. **Descriptor Precision**: Mixin requires exact matches for parameter types (e.g., `Level` vs `BlockGetter`). Even if a class implements an interface, the bytecode uses a specific type.
3. **Bridge Interfaces**: Never cast to a Mixin class. Always use an interface to access mixed-in fields/methods from other classes.
