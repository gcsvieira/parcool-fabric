---
id: registry-migration
type: knowledge
tags: ["#porting", "#registry"]
links: ["[[MOC-porting]]"]
---

# Registry Migration: DeferredRegister → Fabric Registry


## The Pattern in NeoForge

Every registerable thing in ParCool follows this pattern:

```java
// NeoForge way
public class Items {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, ParCool.MOD_ID);
    public static final DeferredHolder<Item, Item> PARCOOL_GUIDE = ITEMS.register("parcool_guide",
        (name) -> new Item(new Item.Properties().stacksTo(1).setId(ResourceKey.create(Registries.ITEM, name))));

    public static void registerAll(IEventBus modBus) {
        ITEMS.register(modBus);  // ← ties to NeoForge event bus
    }
}
```

## The Fabric Equivalent

```java
// Fabric way
public class Items {
    public static final Item PARCOOL_GUIDE = Registry.register(
        Registries.ITEM,
        Identifier.of(ParCool.MOD_ID, "parcool_guide"),
        new Item(new Item.Settings().maxCount(1))
    );

    public static void registerAll() {
        // Force class loading triggers the static fields above
        // Or explicitly call Registry.register() here
    }
}
```

Key differences:
- **No `DeferredRegister`** — Fabric registers immediately via `Registry.register()`
- **No event bus binding** — just call during `ModInitializer.onInitialize()`
- **`Item.Properties` → `Item.Settings`** (Fabric uses Mojmap names, may vary by mapping)
- **No `DeferredHolder`** — you hold the object directly
- **No `ResourceKey.create()` in properties** — Fabric handles this differently

## All Registries That Need Migration

| Registry | NeoForge Class | Items | Fabric Approach |
|---|---|---|---|
| Items | `Items.java` | 4 items | `Registry.register(Registries.ITEM, ...)` |
| Blocks | `Blocks.java` | 2 blocks | `Registry.register(Registries.BLOCK, ...)` |
| Block Entities | `TileEntities.java` | 1 type | `Registry.register(Registries.BLOCK_ENTITY_TYPE, ...)` |
| Entity Types | `EntityTypes.java` | 1 type | `Registry.register(Registries.ENTITY_TYPE, ...)` |
| Mob Effects | `Effects.java` | 1 effect | `Registry.register(Registries.MOB_EFFECT, ...)` |
| Potions | `Potions.java` | 1 potion | `Registry.register(Registries.POTION, ...)` |
| Attributes | `Attributes.java` | 2 attrs | `Registry.register(Registries.ATTRIBUTE, ...)` + `FabricDefaultAttributeRegistry` |
| Sound Events | `SoundEvents.java` | ~8 sounds | `Registry.register(Registries.SOUND_EVENT, ...)` |
| Creative Tabs | `CreativeTabs.java` | 1 tab | `FabricItemGroup` or `ItemGroupEvents` |
| Data Components | `DataComponents.java` | ~3 types | `Registry.register(Registries.DATA_COMPONENT_TYPE, ...)` |
| Command Arg Types | `ParCoolArgumentTypeInfos.java` | 4 types | `ArgumentTypeRegistry.registerArgumentType(...)` |

## Attribute Registration Specifics

NeoForge uses an event to add attributes to entities:

```java
// NeoForge
@SubscribeEvent
public static void onAddAttributes(EntityAttributeModificationEvent event) {
    event.add(EntityType.PLAYER, Attributes.MAX_STAMINA);
}
```

Fabric equivalent:

```java
// Fabric
FabricDefaultAttributeRegistry.register(EntityType.PLAYER, 
    DefaultAttributeContainer.builder()
        .add(Attributes.MAX_STAMINA)
        .add(Attributes.STAMINA_RECOVERY)
);
```

> **Note:** For modifying *existing* entity attributes (adding to Player), you may need to use `EntityAttributeModifiedCallback` from Fabric API or a mixin on `Player.createAttributes()`.

## Brewing Recipe Registration

NeoForge uses `RegisterBrewingRecipesEvent`. Fabric uses `FabricBrewingRecipeRegistry.registerPotionRecipe()` during initialization.

## Creative Tab Registration

NeoForge uses `DeferredRegister` for `CreativeModeTab`. Fabric uses `FabricItemGroup.builder()` or `ItemGroupEvents.modifyEntriesEvent()`.
