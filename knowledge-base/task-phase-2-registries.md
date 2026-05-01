---
id: task-phase-2-registries
type: task
tags: ["#phase-2", "#registries"]
links: ["[[MOC-tasks]]"]
---

# 📦 Task: Phase 2 - Registries

**Goal:** Port all custom content (blocks, items, entities, effects) to Fabric Registry.

## Goals
- [x] Port `Attributes`, `Effects`, and `SoundEvents` → verify: [Attributes.java, Effects.java, SoundEvents.java]
- [x] Port `Blocks` and `TileEntities` → verify: [Blocks.java, TileEntities.java]
- [x] Port `Items` and `DataComponents` → verify: [Items.java, DataComponents.java]
- [x] Implement `FabricItemGroup` (Creative Tabs) → verify: [CreativeTabs.java]
- [x] Port `EntityTypes` and `Potions` → verify: [EntityTypes.java, Potions.java]
- [x] Copy all assets (`assets/parcool/`) and data (`data/parcool/`) → verify: [src/main/resources/]
- [x] Port Server Commands → verify: [CommandRegistry.java, ZiplineCommand.java]

## Verification
- [x] Build Success with 1.21.11 Signatures
- [x] Items visible in Creative Inventory (Requires Runtime)
- [x] Blocks can be placed and interacted with (Requires Runtime)
- [x] Commands are registered and suggestible (Requires Runtime)

## Notes
- Encountered "Future API" signatures in 1.21.11 (ValueInput/Output, Mandatory Block Codecs).
- Refactored registries to use `BuiltInRegistries`.
- `ControlLimitationCommand` deferred until Phase 3 (Limitation system dependency).
