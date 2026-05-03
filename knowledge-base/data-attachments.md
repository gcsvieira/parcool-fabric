---
id: data-attachments
type: knowledge
tags: ["#porting", "#data"]
links: ["[[MOC-porting]]", "[[lesson-data-attachment-stability-hitbox-sync]]"]
---

# Data Attachments Migration


## What Are Data Attachments?

NeoForge's **Data Attachments** (replacing the old Capabilities system) let you attach arbitrary data to game objects (entities, chunks, levels). ParCool uses them to attach **Parkourability** (the action system) and **Stamina** data to players.

## ParCool's Attachments

### 1. Stamina (common — both sides)
```java
// Registered attachment
public static final Supplier<AttachmentType<ReadonlyStamina>> STAMINA = ATTACHMENT_TYPES.register(
    "stamina",
    () -> AttachmentType.builder(ReadonlyStamina::createDefault)
        .serialize(ReadonlyStamina.CODEC)  // ← persisted to save data
        .build()
);
```

Usage: `player.getData(Attachments.STAMINA)` — returns `ReadonlyStamina`

### 2. Parkourability (common — both sides)
```java
public static final Supplier<AttachmentType<Parkourability>> PARKOURABILITY = ATTACHMENT_TYPES.register(
    "parkourability",
    () -> AttachmentType.builder(Parkourability::new).build()  // ← NOT persisted
);
```

Usage: `Parkourability.get(player)` internally calls `player.getData(Attachments.PARKOURABILITY)`

### 3. Client-Only Attachments
- `Animation` — per-player animation state (client-side only)
- `LocalStamina` — client-side stamina with prediction

These are registered via `ClientAttachments.java` on the mod event bus.

## The Problem

Fabric has **no built-in attachment/capability system**. Options:

### Option A: Cardinal Components API (Recommended)
A popular Fabric library that provides entity components (similar to NeoForge attachments).

```java
// Define a component
public interface ParkourabilityComponent extends Component {
    Parkourability get();
}

// Register
public static final ComponentKey<ParkourabilityComponent> PARKOURABILITY =
    ComponentRegistry.getOrCreate(Identifier.of("parcool", "parkourability"), ParkourabilityComponent.class);

// Use
Parkourability p = PARKOURABILITY.get(player).get();
```

**Pros:** Clean API, supports serialization, widely used
**Cons:** External dependency

### Option B: Manual HashMap (No Dependencies)
```java
public class PlayerDataManager {
    private static final Map<UUID, Parkourability> parkourabilities = new HashMap<>();
    private static final Map<UUID, ReadonlyStamina> staminas = new HashMap<>();

    public static Parkourability getParkourability(Player player) {
        return parkourabilities.computeIfAbsent(player.getUUID(), k -> new Parkourability());
    }

    // Clean up on disconnect
    public static void remove(UUID uuid) {
        parkourabilities.remove(uuid);
        staminas.remove(uuid);
    }
}
```

**Pros:** Zero dependencies, simple
**Cons:** Must manually handle lifecycle (create on join, destroy on leave, persist stamina)

### Option C: Mixin to inject fields
```java
public interface ParkourabilityAccess {
    Parkourability parcool$getParkourability();
}

@Mixin(Player.class)
public class PlayerDataMixin implements ParkourabilityAccess {
    @Unique
    private Parkourability parcool$parkourability = new Parkourability();

    @Override
    public Parkourability parcool$getParkourability() {
        return parcool$parkourability;
    }
}
```

**Pros:** Zero dependencies, direct field access (fast), automatically tied to entity lifecycle
**Cons:** Serialization must be handled manually via save/load mixins

## Recommendation: Option C (Mixin-injected fields)

Why:
1. **Zero external dependencies** — keeps the mod self-contained
2. **Best performance** — direct field access, no map lookups
3. **Automatic lifecycle** — data lives and dies with the entity
4. **ParCool already uses mixins heavily** — consistent with the codebase
5. For serialization (stamina persistence), add `@Inject` into `Player.saveAdditionalData` / `readAdditionalData`

## Migration Steps

1. Create interface `ParkourabilityAccess` with getter
2. Create `PlayerDataMixin` implementing the interface on `Player.class`
3. For `ReadonlyStamina` (needs persistence): inject into save/load methods
4. For client-side data (`Animation`, `LocalStamina`): mixin on `AbstractClientPlayer` or use a simple `WeakHashMap<Player, Animation>`
5. Replace all `player.getData(Attachments.STAMINA)` calls with `((ParkourabilityAccess)player).parcool$getParkourability()` or a static helper

## Usage Pattern After Migration

```java
// Before (NeoForge)
Parkourability p = Parkourability.get(player);
// internally: player.getData(Attachments.PARKOURABILITY)

// After (Fabric)
Parkourability p = Parkourability.get(player);
// internally: ((ParkourabilityAccess) player).parcool$getParkourability()
```

The public API surface (`Parkourability.get(player)`) stays the same — only the internal implementation changes.
