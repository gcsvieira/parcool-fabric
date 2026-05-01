---
type: lesson
tags: [mappings, mojang, identifier, friction]
links: ["[[MOC-lessons]]"]
---

# Lesson: Mojang Mappings Evolution (1.21+)

## Context
Migrating utilities from NeoForge 1.21.11 to Fabric 1.21.11 using `loom.officialMojangMappings()`.

## The Problem

### 1. ResourceLocation vs. Identifier
In Minecraft 1.21+, Mojang renamed the internal class `ResourceLocation` to `Identifier`. 
- **Gotcha:** Depending on your mappings (Yarn vs. Mojang vs. Intermediary), you might see different names.
- **Symptom:** `error: cannot find symbol: class ResourceLocation` or `error: incompatible types: Identifier cannot be converted to String`.
- **Fix:** In the current 1.21.11 environment, use `net.minecraft.resources.Identifier` and `Identifier.fromNamespaceAndPath()`. 

### 2. TagKey Stability
Using helper methods like `BlockTags.create()` can be brittle during version transitions if the method signatures change or require specific types (String vs. Identifier).
- **Refinement:** Use `TagKey.create(Registries.BLOCK, identifier)` for a more robust and platform-agnostic way to define tags.

### 3. Missing NeoForge Extensions (getFriction)
NeoForge adds many convenience methods to Vanilla classes like `BlockState`.
- **Observation:** `BlockState.getFriction(Level, BlockPos, Entity)` does not exist in Vanilla/Fabric.
- **Workaround:** Fall back to `state.getBlock().getFriction()`. 
- **Risk:** This loses dynamic friction logic (e.g., from other mods or specific block state properties). This is a known limitation of a pure logic port without a compatibility layer.

## Conclusion
Expect Vanilla class extensions to be missing. When "surgical" logic migration fails due to missing methods, check if they are Forge/NeoForge patches and replace with Vanilla equivalents immediately to maintain build status.
