---
type: lesson
tags: [rendering, mojmap, chunksectionlayer, rendertype, block-layers, 1.21.11]
links: ["[[MOC-lessons]]", "[[rendering-and-gui]]"]
---

# Lesson: RenderType Split — Block Layers vs. Geometry Rendering (1.21.11)

## Context

Porting `ParCoolClient.java` to Fabric 1.21.11 with `loom.officialMojangMappings()` and Fabric API `0.141.3+1.21.11`. The goal was to register zipline hook blocks to the correct render layer using `BlockRenderLayerMap`.

## The Problem

Every attempt to pass a `RenderType` to `BlockRenderLayerMap.putBlock` failed at compile time:

```java
// All of these fail — RenderType has no such static factory methods:
BlockRenderLayerMap.putBlock(block, RenderType.solid());   // ❌ method not found
BlockRenderLayerMap.putBlock(block, RenderType.cutout());  // ❌ method not found
BlockRenderLayerMap.putBlock(block, RenderType.CUTOUT);    // ❌ field not found
```

The `RenderType` class **resolves fine** — it exists at `net.minecraft.client.renderer.rendertype.RenderType`. The issue is that `.solid()`, `.cutout()`, etc. **do not exist as static factory methods on it in 1.21.11**.

## Root Cause: The Rendering Refactor

In Minecraft 1.21.x, Mojang split what was previously a single `RenderType` concern into two separate systems:

| Purpose | Old (pre-1.21) | New (1.21.11 Mojmap) |
|---|---|---|
| **Block layer assignment** (which pass a block renders in) | `RenderType.cutout()` etc. | `ChunkSectionLayer` enum |
| **Custom geometry rendering** (entity/rope/etc.) | `RenderType.create(...)` | `RenderType.create(...)` ✅ same |

`BlockRenderLayerMap` was updated to match. The **actual signature** in Fabric API `0.141.3+1.21.11` is:

```java
// Confirmed from source: FabricMC/fabric @ 0.141.3+1.21.11
public static void putBlock(Block block, ChunkSectionLayer layer)
```

Source: `net.fabricmc.fabric.impl.client.rendering.BlockRenderLayerMapImpl` — uses `Map<Block, ChunkSectionLayer>` internally.

## The Fix

```diff
- import net.minecraft.client.renderer.rendertype.RenderType;
+ import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

- BlockRenderLayerMap.putBlock(Blocks.WOODEN_ZIPLINE_HOOK, RenderType.solid());
- BlockRenderLayerMap.putBlock(Blocks.IRON_ZIPLINE_HOOK, RenderType.solid());
+ BlockRenderLayerMap.putBlock(Blocks.WOODEN_ZIPLINE_HOOK, ChunkSectionLayer.CUTOUT);
+ BlockRenderLayerMap.putBlock(Blocks.IRON_ZIPLINE_HOOK, ChunkSectionLayer.CUTOUT);
```

## ChunkSectionLayer Constants

`ChunkSectionLayer` is an **enum** at `net.minecraft.client.renderer.chunk.ChunkSectionLayer`:

| Constant | Use case |
|---|---|
| `SOLID` | Fully opaque blocks |
| `CUTOUT_MIPPED` | Cutout with mipmaps (e.g. leaves) |
| `CUTOUT` | Sharp transparency (e.g. glass pane, iron bars, hooks) |
| `TRANSLUCENT` | Blended transparency (e.g. stained glass) |
| `TRIPWIRE` | Special case for tripwire |

## What NOT to Confuse

`RenderType` is still the right class for **custom geometry** (entity renderers, ropes, etc.):

```java
// ✅ Correct — ModRenderTypes.java uses RenderType.create() for custom rope geometry
ZIPLINE_2D = RenderType.create("zipline2d", RenderSetup.builder(...).createRenderSetup());
```

`RenderType` for geometry + `ChunkSectionLayer` for block layer assignment. They are separate concerns.

## How to Debug This Class of Error

When a well-known static method is missing from a class that *does* resolve:

1. **Check the Fabric API source at the exact version tag** — not docs, not web results, not other mods. Go to `github.com/FabricMC/fabric` → switch to tag `<fabric_api_version>` from `gradle.properties`.
2. **Read the `*Impl.java`** — the impl class reveals the actual internal type used, which is the ground truth.
3. **Don't trust LLM suggestions for 1.21.x rendering APIs** — they lag behind Mojang's refactors by several versions.

## Conclusion

When `BlockRenderLayerMap.putBlock` fails, **do not** look for a different `RenderType` factory. Look for `ChunkSectionLayer`. The two concepts were silently decoupled in 1.21.x. Always verify Fabric API signatures against the exact version tag on GitHub.
