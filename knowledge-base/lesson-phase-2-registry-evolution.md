---
id: lesson-phase-2-registry-evolution
type: lesson
tags: ["#phase-2", "#registries", "#1.21.11"]
links: ["[[MOC-lessons]]", "[[task-phase-2-registries]]", "[[registry-migration]]"]
---

# 🎓 Lesson: Registry & API Evolution in 1.21.11

**Context:** During Phase 2, we encountered significant API differences in the `1.21.11` environment compared to standard `1.21.1`.

## 🧠 Key Discoveries

### 1. The "Future" API
Despite the `.11` suffix, the environment uses signatures aligned with Minecraft `1.21.2` and `1.21.4`.
- **Serialization**: `BlockEntity` and `Entity` save/load methods now use `ValueInput` and `ValueOutput` (Fabric's view of the new Mojang serialization API) instead of raw `CompoundTag`.
- **Block Codecs**: All blocks MUST implement `codec()` returning a `MapCodec`.
- **Push Reactions**: `getPistonPushReaction` is no longer a method override in `Block`; it must be set via `BlockBehaviour.Properties.pushReaction(PushReaction)`.

### 2. Registry Access
In Fabric, use `BuiltInRegistries` directly for `Registry.register`.
- **Pattern**: `Registry.register(BuiltInRegistries.ITEM, id, item)`
- **Holders**: Use `Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, id, object)` to get a `Holder<T>` reference, which is now the standard for sounds and attributes.

### 3. Tooltip API Changes
The `appendHoverText` signature has evolved to use a `Consumer<Component>` and `TooltipDisplay`.
- **New Signature**: `void appendHoverText(ItemStack, TooltipContext, TooltipDisplay, Consumer<Component>, TooltipFlag)`

## 🛠️ Solutions Implemented
- **Adapting to ValueInput**: Used `input.getIntOr("key", default)` and `output.putInt("key", value)` to satisfy the new serialization interfaces.
- **Mandatory Codecs**: Added `public static final MapCodec<T> CODEC = simpleCodec(T::new)` to block implementations.
- **Annotation Swap**: Replaced `javax.annotation` with `org.jetbrains.annotations` (provided by Fabric/JetBrains) to resolve missing dependency errors.

## ⚠️ Gotchas
- **Mandatory setId**: In `1.21.11`, `BlockBehaviour.Properties` and `Item.Properties` **MUST** have `.setId(ResourceKey)` called during construction if they use certain logic (like `effectiveDrops`). Failure to do so results in a `java.lang.NullPointerException: Block id not set` during registry initialization.
- **DyedItemColor**: In `1.21.11`, the constructor only takes `(int rgb)`, whereas in later snapshots it takes `(int rgb, boolean showInTooltip)`.
- **BlockEntityType Builder**: Use `FabricBlockEntityTypeBuilder` for maximum compatibility in Fabric, especially when multiple blocks share a single TileEntity.
