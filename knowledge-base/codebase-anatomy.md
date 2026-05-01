---
id: codebase-anatomy
type: knowledge
tags: ["#architecture", "#codebase"]
links: ["[[MOC-architecture]]"]
---

# Codebase Anatomy


## Package Structure

```
com.alrex.parcool/
├── ParCool.java                    ← Mod entry point (@Mod annotation)
├── api/                            ← Public API for other mods
│   ├── Attributes.java             ← Custom player attributes (MAX_STAMINA, STAMINA_RECOVERY)
│   ├── Effects.java                ← Mob effects (Inexhaustible)
│   ├── SoundEvents.java            ← Custom sounds
│   ├── Stamina.java                ← Stamina API
│   ├── client/gui/                 ← HUD events API
│   └── unstable/
│       ├── action/ParCoolActionEvent.java  ← Event hierarchy for action lifecycle
│       ├── animation/              ← Animation extension API
│       └── Limitation.java         ← Limitation query API
│
├── client/                         ← CLIENT-SIDE ONLY code
│   ├── animation/                  ← 27 animator implementations
│   │   ├── Animator.java           ← Base animator class
│   │   ├── AnimatorList.java       ← Registry of all animators
│   │   ├── PlayerModelTransformer.java  ← Model manipulation utilities
│   │   ├── PlayerModelRotator.java      ← Rotation utilities
│   │   └── impl/                   ← All concrete animators
│   ├── gui/                        ← Settings screens (4 files)
│   ├── hud/                        ← Stamina HUD system
│   │   ├── HUDManager.java         ← HUD lifecycle
│   │   ├── HUDRegistry.java        ← Registers HUD as GUI layer
│   │   └── impl/                   ← HUD renderers
│   ├── input/
│   │   ├── KeyBindings.java        ← 16 key bindings
│   │   └── KeyRecorder.java        ← Input state tracking
│   ├── renderer/                   ← Entity renderers, RenderTypes
│   └── RenderBehaviorEnforcer.java ← Camera type overriding
│
├── common/                         ← SHARED (client + server) code
│   ├── action/                     ← THE CORE — parkour action system
│   │   ├── Action.java             ← Base class for all actions
│   │   ├── Actions.java            ← Registry of all action classes
│   │   ├── ActionProcessor.java    ← Tick loop — processes all actions per player
│   │   ├── BehaviorEnforcer.java   ← Prevents vanilla behaviors during actions
│   │   ├── InstantAction.java      ← One-shot actions
│   │   └── impl/                   ← 22 action implementations
│   ├── attachment/                 ← NeoForge Data Attachments
│   │   ├── Attachments.java        ← Registration (STAMINA, PARKOURABILITY)
│   │   ├── ClientAttachments.java  ← Client-only attachment registration
│   │   ├── client/
│   │   │   ├── Animation.java      ← Per-player animation state
│   │   │   └── LocalStamina.java   ← Client-side stamina tracking
│   │   └── common/
│   │       ├── Parkourability.java  ← THE MAIN ATTACHMENT — holds all action instances
│   │       └── ReadonlyStamina.java ← Serializable stamina data
│   ├── block/                      ← Zipline hook blocks + tile entities
│   ├── damage/                     ← Custom damage sources
│   ├── entity/                     ← Zipline rope entity
│   ├── handlers/                   ← NeoForge event handlers (12 files)
│   ├── info/                       ← Action/Client/Server info containers
│   ├── item/                       ← Items + DataComponents
│   ├── network/                    ← Networking (payloads + broadcasters)
│   ├── potion/                     ← Brewing recipes + effects
│   ├── registries/                 ← EventBusForgeRegistry (handler registration hub)
│   ├── stamina/                    ← Stamina handler interfaces + implementations
│   ├── tags/                       ← Custom block tags
│   └── zipline/                    ← Zipline math (curves, types)
│
├── config/
│   └── ParCoolConfig.java          ← 985-line config (Client + Server sections)
│
├── extern/                         ← External mod compatibility
│   ├── AdditionalMods.java         ← Mod presence detection
│   ├── betterthirdperson/          ← BetterThirdPerson integration
│   └── shouldersurfing/            ← ShoulderSurfing integration
│
├── mixin/                          ← All Sponge Mixins
│   ├── client/ (7 mixins)          ← Client-side injections
│   └── common/ (3 mixins)          ← Shared injections
│
├── server/
│   ├── command/                    ← /parcool commands
│   └── limitation/                 ← Server-side action limitations
│
└── utilities/                      ← Math, vectors, easing, world queries
```

## The Core Loop: How ParCool Actually Works

```
Player joins → Parkourability attachment created → contains all 22 Action instances
    ↓
Every tick (ActionProcessor.onTick):
    For each Action:
        1. Can it start? (check keys, world state, stamina)
        2. If started → sync to server via ActionStatePayload
        3. If running → execute onWorkingTick (modify player movement/velocity)
        4. If should stop → finish() + sync
    ↓
    Post-process: update stamina, sync stamina
    ↓
Every render frame (ClientActionProcessor.onRenderTick):
    For each player in world:
        Animation.onRenderTick → interpolate animations
    ↓
Player model setup (PlayerModelMixin):
    Animation.animatePre/animatePost → transform limb positions
    ↓
Player rotation (AvatarRendererMixin):
    Animation.rotatePre/rotatePost → apply body rotation
```

## Files That DO NOT Use Any NeoForge API (Pure Game Logic)

These files can be copied as-is to Fabric:

- All 22 `common/action/impl/*.java` — only use `net.minecraft.*` + internal APIs
- All 27 `client/animation/impl/*.java` — pure math/rendering
- All `utilities/*.java` — pure math
- `common/action/Action.java` — base class (uses `RenderFrameEvent` in 1 method signature)
- `common/action/BehaviorEnforcer.java`
- `common/zipline/*.java` — pure math
- `common/info/*.java` — data containers
- `common/stamina/*.java` — stamina logic (interfaces + handlers)

## Files That HEAVILY Use NeoForge API (Need Full Rewrite)

- `ParCool.java` — entry point
- `config/ParCoolConfig.java` — 985 lines of `ModConfigSpec`
- `common/registries/EventBusForgeRegistry.java` — all event wiring
- `common/network/NetworkRegistries.java` — payload registration
- `common/attachment/Attachments.java` — `AttachmentType` registry
- `common/handlers/*.java` — 12 files using `@SubscribeEvent`
- `client/hud/HUDRegistry.java` — GUI layer registration
- `client/input/KeyBindings.java` — key registration via event
