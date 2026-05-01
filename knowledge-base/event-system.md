---
id: event-system
type: knowledge
tags: ["#porting", "#events"]
links: ["[[MOC-porting]]"]
---

# Event System Migration: NeoForge Events → Fabric Callbacks


## How NeoForge Events Work in ParCool

ParCool uses TWO event buses:
1. **Mod Event Bus** (`ModLoadingContext.get().getActiveContainer().getEventBus()`) — for lifecycle events (setup, registration)
2. **Game Event Bus** (`NeoForge.EVENT_BUS`) — for gameplay events (ticks, damage, player actions)

Events are consumed via `@SubscribeEvent` annotation on methods, registered either by class or instance.

## Complete Event Mapping Table

### Game Events (NeoForge.EVENT_BUS → Fabric Callbacks)

| NeoForge Event | Used In | Fabric Equivalent |
|---|---|---|
| `PlayerTickEvent.Post` | `ActionProcessor` | `ServerTickEvents.END_SERVER_TICK` + manual player iteration, OR a mixin on `PlayerEntity.tick()` |
| `PlayerEvent.PlayerLoggedOutEvent` | `LoginLogoutHandler` | `ServerPlayConnectionEvents.DISCONNECT` |
| `EntityJoinLevelEvent` | `PlayerJoinHandler` | `ServerEntityEvents.ENTITY_LOAD` or `EntityJoinWorldCallback` |
| `LivingFallEvent` | `PlayerDamageHandler` | `LivingEntityEvents` (Fabric API) or Mixin on `LivingEntity.handleFallDamage` |
| `LivingIncomingDamageEvent` | `PlayerDamageHandler` | Mixin on `LivingEntity.damage()` or `ServerLivingEntityEvents.ALLOW_DAMAGE` |
| `PlayerEvent.Clone` | `PlayerCloneHandler` | `ServerPlayerEvents.COPY_FROM` |
| `LivingEvent.LivingVisibilityEvent` | `PlayerVisibilityHandler` | Mixin on `LivingEntity.getArmorVisibility()` |
| `LivingEvent.LivingJumpEvent` | `PlayerJumpHandler` | Mixin on `LivingEntity.jump()` (already have `LivingEntityMixin.onJumpFromGround`) |
| `RegisterCommandsEvent` | `ParCool` | `CommandRegistrationCallback.EVENT` |
| `ServerAboutToStartEvent` | `Limitations` | `ServerLifecycleEvents.SERVER_STARTING` |
| `ServerStoppingEvent` | `Limitations` | `ServerLifecycleEvents.SERVER_STOPPING` |
| `ServerTickEvent.Post` | `ActionSyncBroadcaster` | `ServerTickEvents.END_SERVER_TICK` |
| `RegisterBrewingRecipesEvent` | `ParCoolBrewingRecipe` | `FabricBrewingRecipeRegistry` (in initializer) |

### Client Events (NeoForge.EVENT_BUS → Fabric Client Callbacks)

| NeoForge Event | Used In | Fabric Equivalent |
|---|---|---|
| `RenderFrameEvent.Pre` | `ActionProcessor.ClientActionProcessor` | `WorldRenderEvents.START` or mixin |
| `ViewportEvent.ComputeCameraAngles` | `ActionProcessor.ClientActionProcessor` | Mixin on `Camera.update()` or `GameRenderer` |
| `ClientTickEvent.Post` | `KeyRecorder` | `ClientTickEvents.END_CLIENT_TICK` |
| `InputEvent.Key` | `OpenSettingsParCoolHandler` | `ClientTickEvents` + key check, or `KeyBindingHelper` |
| `MovementInputUpdateEvent` | `InputHandler` | Mixin on `KeyboardInput.tick()` |

### Mod Bus Events (→ Fabric Initializer calls)

| NeoForge Event | Used In | Fabric Equivalent |
|---|---|---|
| `FMLCommonSetupEvent` | `ParCool.setup()` | Code in `ModInitializer.onInitialize()` |
| `FMLLoadCompleteEvent` | `ParCool.loaded()` | Code at end of `onInitialize()` |
| `RegisterKeyMappingsEvent` | `KeyBindings` | `KeyBindingHelper.registerKeyBinding()` in `ClientModInitializer` |
| `EntityRenderersEvent.RegisterRenderers` | `Renderers` | `EntityRendererRegistry.register()` in `ClientModInitializer` |
| `RegisterGuiLayersEvent` | `HUDRegistry` | `HudRenderCallback.EVENT.register()` |
| `EntityAttributeModificationEvent` | `AddAttributesHandler` | `FabricDefaultAttributeRegistry` |
| `RegisterPayloadHandlersEvent` | `NetworkRegistries` | `PayloadTypeRegistry` + `ServerPlayNetworking.registerGlobalReceiver()` |

## Custom Events (ParCool's own API)

ParCool defines its own event hierarchy for other mods:

```
ParCoolActionEvent (extends Event)
├── TryToStart (ICancellableEvent)
├── TryToContinue (ICancellableEvent)
├── Start.Pre / Start.Post
├── Finish.Pre / Finish.Post
└── Tick.Pre / Tick.Post
```

These are posted via `NeoForge.EVENT_BUS.post(...)` throughout `ActionProcessor`.

**Fabric porting options:**
1. **Custom callback pattern** — create `ParCoolActionCallback` functional interfaces, use Fabric's `Event` factory
2. **Simple listener list** — `List<Consumer<ActionEvent>>` with register/fire methods
3. **Drop the API initially** — since these events are for *other* mods to consume, they can be implemented later

**Recommendation:** Option 1 (Fabric Event factory) for proper API compatibility:

```java
public final class ParCoolActionCallback {
    public static final Event<TryToStart> TRY_TO_START = EventFactory.createArrayBacked(
        TryToStart.class,
        listeners -> (player, action) -> {
            for (var listener : listeners) {
                if (!listener.onTryToStart(player, action)) return false;
            }
            return true;
        }
    );

    @FunctionalInterface
    public interface TryToStart {
        boolean onTryToStart(Player player, Action action);
    }
}
```

## Handler Files and Their Porting Complexity

| Handler File | Events Used | Porting Approach |
|---|---|---|
| `ActionProcessor.java` | `PlayerTickEvent.Post`, `RenderFrameEvent.Pre`, `ViewportEvent.ComputeCameraAngles` | Register to Fabric tick/render callbacks |
| `LoginLogoutHandler.java` | `PlayerLoggedOutEvent` | `ServerPlayConnectionEvents.DISCONNECT` |
| `PlayerJoinHandler.java` | `EntityJoinLevelEvent`, `ClientTickEvent` | `ServerEntityEvents` + `ClientTickEvents` |
| `PlayerDamageHandler.java` | `LivingFallEvent`, `LivingIncomingDamageEvent` | Mixins or Fabric damage events |
| `PlayerCloneHandler.java` | `PlayerEvent.Clone` | `ServerPlayerEvents.COPY_FROM` |
| `PlayerVisibilityHandler.java` | `LivingVisibilityEvent` | Mixin |
| `PlayerJumpHandler.java` | `LivingJumpEvent` | Already handled by `LivingEntityMixin` |
| `EnableOrDisableParCoolHandler.java` | `InputEvent.Key` | `ClientTickEvents` + key check |
| `OpenSettingsParCoolHandler.java` | `InputEvent.Key` | Same as above |
| `InputHandler.java` | `MovementInputUpdateEvent` | Mixin on input |
| `KeyRecorder.java` | `ClientTickEvent.Post` | `ClientTickEvents.END_CLIENT_TICK` |
| `HUDManager.java` | `RenderFrameEvent` | `HudRenderCallback` |
