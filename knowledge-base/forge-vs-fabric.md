---
id: forge-vs-fabric
type: knowledge
tags: ["#porting", "#comparison"]
links: ["[[MOC-porting]]"]
---

# Forge vs Fabric: The Key Differences You Need to Know


## Mental Model

Think of it this way:
- **Forge/NeoForge** = opinionated framework. Provides APIs for everything — events, config, registries, networking, capabilities/attachments. You code *within* their framework.
- **Fabric** = minimal toolkit. Provides the bare minimum (Mixin injection, registry access, basic events). You build more yourself, or pull in **Fabric API** modules.

## The Two Loaders Side-by-Side

| Concept | NeoForge (current) | Fabric |
|---|---|---|
| **Mod entry point** | `@Mod` annotation on a class | `ModInitializer` / `ClientModInitializer` interfaces declared in `fabric.mod.json` |
| **Event system** | `IEventBus` + `@SubscribeEvent` annotations, `NeoForge.EVENT_BUS.post()` | **Fabric API Callbacks** — functional interfaces you register, e.g. `ServerTickEvents.END_SERVER_TICK.register(...)` |
| **Registry** | `DeferredRegister` + `DeferredHolder`, registered to mod event bus | `Registry.register()` directly, or helper methods. No deferred pattern needed. |
| **Networking** | `CustomPacketPayload` + `RegisterPayloadHandlersEvent` + `StreamCodec` | `PayloadTypeRegistry` + `ServerPlayNetworking` / `ClientPlayNetworking` |
| **Config** | `ModConfigSpec` (TOML-based, built-in) | **No built-in config.** Use a library like `cloth-config`, `MidnightLib`, or roll your own JSON/TOML. |
| **Data Attachments** | `AttachmentType` registered in `NeoForgeRegistries.ATTACHMENT_TYPES` | **No equivalent.** Use custom `HashMap<UUID, Data>`, or Cardinal Components API (third-party) |
| **Access Transformers** | `accesstransformer.cfg` (make private fields public) | **Access Wideners** (`.accesswidener` file) — same concept, different syntax |
| **Mod metadata** | `neoforge.mods.toml` | `fabric.mod.json` |
| **Mappings** | Mojang + Parchment | Mojang (via Loom) or Intermediary. Parchment works too. |
| **Mixin** | SpongePowered Mixin (built-in) | SpongePowered Mixin (built-in) — **identical library** |
| **Side annotations** | `@OnlyIn(Dist.CLIENT)` / `Dist.CLIENT` checks | `Environment(EnvType.CLIENT)` / `FabricLoader.getInstance().getEnvironmentType()` |
| **Build system** | Gradle + NeoForge MDG plugin | Gradle + **Fabric Loom** plugin |
| **Key bindings** | `RegisterKeyMappingsEvent` + `KeyConflictContext` + `KeyModifier` | `KeyBindingHelper.registerKeyBinding()` — simpler, no conflict context system |
| **GUI/HUD overlay** | `RegisterGuiLayersEvent` + `GuiLayer` | `HudRenderCallback.EVENT.register(...)` |
| **Entity renderers** | `EntityRenderersEvent.RegisterRenderers` | `EntityRendererRegistry.register()` |
| **Commands** | `RegisterCommandsEvent` | `CommandRegistrationCallback.EVENT.register(...)` |
| **Player events** | `PlayerTickEvent`, `PlayerEvent.PlayerLoggedOutEvent`, etc. | `ServerTickEvents`, `ServerPlayConnectionEvents.DISCONNECT`, etc. |

## What's The Same (Good News)

1. **Mixin is identical.** All 10 mixins use `org.spongepowered.asm.mixin` — this is the same library on both loaders. Mixin JSON config format is the same. The actual mixin code (`@Inject`, `@Shadow`, `@Mixin`, `CallbackInfo`) **does not change**.

2. **Minecraft's own code is the same.** All `net.minecraft.*` imports work on both loaders (same deobfuscated game code). `Player`, `Level`, `BlockState`, `Vec3`, `Minecraft.getInstance()` — all identical.

3. **Resource packs / Data packs.** The `assets/parcool/` and `data/parcool/` directories (models, textures, lang, recipes, tags, loot tables) are **vanilla Minecraft** format. They work on both loaders with zero changes.

4. **Game logic.** The `Action`, `Animator`, `Parkourability` systems — all pure Java game logic with Minecraft API calls — port with minimal changes.

## What's Different (The Work)

| Layer | Effort | Files Affected |
|---|---|---|
| Build system & metadata | Medium | `build.gradle`, `gradle.properties`, `fabric.mod.json` (new) |
| Mod entry point | Low | `ParCool.java` (1 file, rewrite) |
| Registry system | Medium | ~12 files (Items, Blocks, Effects, Attributes, etc.) |
| Event system | **High** | ~15 handler files + ActionProcessor |
| Networking | **High** | ~10 files (8 payloads + 2 broadcasters + registries) |
| Config system | **High** | `ParCoolConfig.java` (985 lines, full rewrite) |
| Data attachments | Medium | 4 files (Attachments, Parkourability, Stamina, Animation) |
| Access transforms | Low | 1 file → convert to `.accesswidener` |
| Key bindings | Low | 1 file |
| Rendering/HUD | Low-Medium | ~5 files |
| External mod compat | Low | 3 files (can defer) |

## What You Do NOT Need to Touch

- All 22 Action implementations (`Vault.java`, `WallJump.java`, etc.) — pure game logic
- All 27 Animator implementations — pure rendering math
- All utility classes (`WorldUtil`, `VectorUtil`, `MathUtil`, etc.)
- All resource files (textures, models, sounds, lang)
- The zipline system (blocks, entities, curves) — only registry wrappers change
