---
id: rendering-and-gui
type: knowledge
tags: ["#porting", "#rendering"]
links: ["[[MOC-porting]]", "[[lesson-rendering-refactor-chunksectionlayer]]"]
---

# Rendering, HUD, and Client-Side Migration


## HUD System

### Current Architecture
- `HUDRegistry` registers a `GuiLayer` via `RegisterGuiLayersEvent`
- `HUDManager` renders the stamina bar using vanilla `GuiGraphics` API
- Two HUD types: `StaminaHUD` (large) and `LightStaminaHUD` (small)
- Custom sprites loaded from `textures/gui/sprites/hud/`

### Fabric Equivalent

```java
// NeoForge
@SubscribeEvent
public static void register(RegisterGuiLayersEvent event) {
    event.registerAbove(VanillaGuiLayers.EXPERIENCE_BAR, id, HUDManager.getInstance());
}

// Fabric
HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
    HUDManager.getInstance().render(drawContext, renderTickCounter);
});
```

**Changes needed:**
- Replace `RegisterGuiLayersEvent` → `HudRenderCallback.EVENT.register()`
- The actual rendering code (`GuiGraphics.blit()`, sprite rendering) is vanilla Minecraft — no changes
- All HUD texture sprites are vanilla format — no changes

## Entity Renderer Registration

```java
// NeoForge
public static void register(EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(EntityTypes.ZIPLINE_ROPE.get(), ZiplineRopeRenderer::new);
}

// Fabric
EntityRendererRegistry.register(EntityTypes.ZIPLINE_ROPE, ZiplineRopeRenderer::new);
```

One-line change. The `ZiplineRopeRenderer` itself uses vanilla rendering APIs.

## RenderTypes and RenderPipelines

`RenderTypes.java` and `RenderPipelines.java` use vanilla Minecraft rendering classes. They reference fields made accessible via Access Transformers:

```
public net.minecraft.client.renderer.RenderStateShard f_110147_ # NO_TEXTURE
public net.minecraft.client.renderer.RenderStateShard f_110158_ # CULL
```

**Action:** Convert these to Access Wideners (see [05-mixin-migration.md](./05-mixin-migration.md)).

## Key Bindings

### Current (NeoForge)
```java
// Registration via event
@SubscribeEvent
public static void register(RegisterKeyMappingsEvent event) {
    event.register(keyBindCrawl);
    event.register(keyBindVault);
    // ...16 keybindings total
}

// Key definition with conflict context
private static final KeyMapping keyBindEnable = new KeyMapping(
    "key.parcool.Enable",
    KeyConflictContext.UNIVERSAL,    // ← NeoForge-specific
    KeyModifier.CONTROL,             // ← NeoForge-specific
    InputConstants.Type.KEYSYM,
    GLFW.GLFW_KEY_P,
    KEY_CATEGORY_PARCOOL
);
```

### Fabric Equivalent
```java
// Registration in ClientModInitializer
public static void registerAll() {
    KeyBindingHelper.registerKeyBinding(keyBindCrawl);
    KeyBindingHelper.registerKeyBinding(keyBindVault);
    // ...
}

// Key definition (simpler — no conflict context)
private static final KeyBinding keyBindEnable = new KeyBinding(
    "key.parcool.Enable",
    InputUtil.Type.KEYSYM,
    GLFW.GLFW_KEY_P,
    "category.parcool"
);
```

**Key differences:**
- `KeyMapping` → `KeyBinding` (Fabric naming)
- `KeyConflictContext` and `KeyModifier` don't exist in Fabric — the Ctrl+P combo would need to be checked manually in the tick handler
- `KeyMapping.Category` → string-based category

## Camera Manipulation

`ActionProcessor.ClientActionProcessor` hooks `ViewportEvent.ComputeCameraAngles` for camera effects during actions (roll camera on dodge, rotate on wall jump, etc.).

**Fabric:** No direct callback exists. Use a **mixin** on `Camera.update()` or `GameRenderer.renderLevel()`:

```java
@Mixin(Camera.class)
public class CameraMixin {
    @Inject(method = "setup", at = @At("RETURN"))
    private void onSetup(BlockGetter area, Entity entity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        // Apply ParCool camera rotations here
    }
}
```

## Render Tick

`RenderFrameEvent.Pre` is used for interpolating animations between game ticks.

**Fabric:** Use `WorldRenderEvents.START` or a mixin. Alternatively, `ClientTickEvents` + partial tick tracking.

## Settings GUI Screens

The 4 GUI screens (`ParCoolSettingScreen`, `SettingBooleanConfigScreen`, `SettingEnumConfigScreen`, `SettingActionLimitationScreen`) all extend vanilla `Screen` and use vanilla `GuiGraphics`. They reference `ModConfigSpec` values but can be refactored to use the config `Item<T>` interface.

**The screens work with vanilla rendering and need only config-reference updates.**

## RenderBehaviorEnforcer

Forces camera type to first-person during certain actions. Currently done via `OptionsMixin` which intercepts `getCameraType()`. This mixin is pure vanilla — **no changes needed**.
