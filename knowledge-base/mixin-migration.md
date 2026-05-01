---
id: mixin-migration
type: knowledge
tags: ["#porting", "#mixin"]
links: ["[[MOC-porting]]"]
---

# Mixin Migration Analysis


## Great News: Mixins Are Cross-Compatible

Both NeoForge and Fabric use **SpongePowered Mixin** (same library, same annotations, same injection points). The mixin code itself needs **minimal changes**.

## All 10 Mixins and Their Status

### Common Mixins (3) — Shared between client and server

| Mixin | Target | Injections | Changes Needed |
|---|---|---|---|
| `EntityMixin` | `Entity.class` | 4 `@Inject` + 2 `@Shadow` | **One change:** extends `AttachmentHolder` (NeoForge class) → change to extend `Entity` directly or use the correct Fabric superclass |
| `LivingEntityMixin` | `LivingEntity.class` | 3 `@Inject` + 1 `@Unique` helper | **One change:** references `NeoForgeServerConfig.INSTANCE.fullBoundingBoxLadders` and `NeoForge.EVENT_BUS.post()` — need Fabric replacements |
| `PlayerMixin` | `Player.class` | 2 `@Inject` | **No changes needed** — pure vanilla API |

### Client Mixins (7) — Client-side only

| Mixin | Target | Injections | Changes Needed |
|---|---|---|---|
| `LocalPlayerMixin` | `LocalPlayer.class` | 2 `@Inject` | **No changes needed** — pure vanilla API |
| `PlayerModelMixin` | `PlayerModel.class` | 2 `@Inject` (HEAD + TAIL) | **No changes needed** — pure vanilla API + internal refs |
| `AvatarRendererMixin` | `AvatarRenderer.class` | 2 `@Inject` (HEAD + RETURN) | **Verify target exists** — `AvatarRenderer` may have a different name in Fabric mappings |
| `LivingRendererMixin` | `LivingEntityRenderer.class` | 1 `@Inject` | **No changes needed** |
| `ClientWorldMixin` | `ClientLevel.class` | 1 `@Inject` | **No changes needed** |
| `OptionsMixin` | `Options.class` | 1 `@Inject` | **No changes needed** |
| `ClientPacketListenerMixin` | `ClientPacketListener.class` | 2 `@Inject` | **No changes needed** (methods are empty/no-ops) |

## Specific Issues to Address

### 1. EntityMixin extends AttachmentHolder

```java
// Current (NeoForge)
@Mixin(Entity.class)
public abstract class EntityMixin extends AttachmentHolder {
```

`AttachmentHolder` is a NeoForge class that `Entity` extends in NeoForge's patched Minecraft. In Fabric/vanilla, `Entity` does NOT extend `AttachmentHolder`.

**Fix:** Simply don't extend anything, or extend the actual vanilla superclass:
```java
// Fabric
@Mixin(Entity.class)
public abstract class EntityMixin {
```

### 2. LivingEntityMixin uses NeoForge APIs

Two NeoForge-specific calls in `LivingEntityMixin`:

```java
// 1. NeoForge server config reference
if (!NeoForgeServerConfig.INSTANCE.fullBoundingBoxLadders.get()) {
```
**Fix:** Remove this check or add our own config option. The `fullBoundingBoxLadders` is a NeoForge-specific feature. Default to `false` (vanilla behavior).

```java
// 2. Event bus calls for ClimbPoles
NeoForge.EVENT_BUS.post(new ParCoolActionEvent.TryToStartEvent(...))
NeoForge.EVENT_BUS.post(new ParCoolActionEvent.TryToStart(...))
```
**Fix:** Replace with Fabric callback invocations (see [04-event-system.md](./04-event-system.md)).

### 3. AvatarRenderer class name

`AvatarRenderer` is a relatively new class (1.21.x). Verify:
- Does it exist under the same name in Fabric's Mojmap mappings?
- The mixin targets `AvatarRenderer.class` which renders player-like entities

**Action:** Check Fabric Loom's mapping output. If the class name differs, update the `@Mixin` target.

## Mixin Config File

Current `parcool.mixins.json`:
```json
{
  "required": true,
  "package": "com.alrex.parcool.mixin",
  "compatibilityLevel": "JAVA_21",
  "client": [
    "client.AvatarRendererMixin",
    "client.ClientPacketListenerMixin",
    "client.ClientWorldMixin",
    "client.LivingRendererMixin",
    "client.LocalPlayerMixin",
    "client.OptionsMixin",
    "client.PlayerModelMixin"
  ],
  "mixins": [
    "common.EntityMixin",
    "common.LivingEntityMixin",
    "common.PlayerMixin"
  ]
}
```

**Changes for Fabric:**
- Add `"injectors": { "defaultRequire": 1 }` (already present)
- Reference this file in `fabric.mod.json` under `"mixins"` array
- Format is identical — just the reference location changes

## Access Transformer → Access Widener

Current `accesstransformer.cfg`:
```
public net.minecraft.client.renderer.RenderStateShard f_110147_ # NO_TEXTURE
public net.minecraft.client.renderer.RenderStateShard f_110158_ # CULL
public net.minecraft.client.renderer.RenderStateShard f_110110_ # NO_CULL
public net.minecraft.client.renderer.RenderStateShard f_110152_ # LIGHTMAP
public net.minecraft.client.renderer.RenderStateShard f_173075_ # RENDERTYPE_LEASH_SHADER
public net.minecraft.world.entity.Entity onGround
public net.minecraft.world.damagesource.DamageSources *()
public net.minecraft.world.entity.player.Player canPlayerFitWithinBlocksAndEntitiesWhen(...)
public net.minecraft.world.entity.LivingEntity swimAmount
public net.minecraft.world.entity.LivingEntity swimAmountO
```

**Fabric Access Widener** (`parcool.accesswidener`):
```
accessWidener v2 named

# RenderStateShard fields — need to map to Mojmap/named field names
accessible field net/minecraft/client/renderer/RenderStateShard NO_TEXTURE ...
accessible field net/minecraft/client/renderer/RenderStateShard CULL ...
# (translate SRG names f_XXXXX_ to named mappings)

accessible field net/minecraft/world/entity/Entity onGround Z
accessible method net/minecraft/world/damagesource/DamageSources * ()...
accessible method net/minecraft/world/entity/player/Player canPlayerFitWithinBlocksAndEntitiesWhen (Lnet/minecraft/world/entity/Pose;)Z
accessible field net/minecraft/world/entity/LivingEntity swimAmount F
accessible field net/minecraft/world/entity/LivingEntity swimAmountO F
```

> **Important:** The SRG names (`f_110147_`) must be translated to their Mojang/named equivalents. The comments in the AT file tell us what they are. Use Fabric's mapping tools or search the Minecraft source.
