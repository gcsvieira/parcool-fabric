---
id: config-system
type: knowledge
tags: ["#porting", "#config"]
links: ["[[MOC-porting]]"]
---

# Config System Migration


## The Problem

`ParCoolConfig.java` is **985 lines** and is the single most NeoForge-coupled file in the entire codebase. It uses `ModConfigSpec` (NeoForge's built-in TOML config system) which has **no Fabric equivalent**.

## What The Config Contains

### Client Config (~50 values)
- **Booleans** (22): Animation toggles, HUD settings, control preferences
- **Integers** (11): HUD offsets, tick durations, cooldowns
- **Doubles** (7): Speed modifiers, damage reduction rates
- **Enums** (10): HUD type, stamina type, vault mode, control types, color theme
- **Per-action booleans** (22): Enable/disable each parkour action
- **Per-animator booleans** (27): Enable/disable each animation
- **Per-action stamina costs** (22): Stamina consumption per action

### Server Config (~20 values)
- **Booleans** (3): Allow infinite stamina, allow disable wall jump cooldown, dodge invulnerability
- **Integers** (9): Max limits for client config values (server can cap client settings)
- **Doubles** (6): Max limits for speed modifiers, damage reduction

## Config Architecture Pattern

The config uses a clever `Item<T>` interface pattern:

```java
public interface Item<T> {
    T get();
    void set(T value);
    String getPath();
    ModConfigSpec.ConfigValue<T> getInternalInstance();  // ← NeoForge specific
    void register(ModConfigSpec.Builder builder);         // ← NeoForge specific
    void writeToBuffer(ByteBuf buffer);                   // ← For network sync
    T readFromBuffer(ByteBuf buffer);                     // ← For network sync
}
```

The `writeToBuffer`/`readFromBuffer` methods are used for **network synchronization** — the server sends its config limits to the client. This is independent of the config system and should be preserved.

## Fabric Config Options

### Option 1: Cloth Config (Recommended)
- **Dependency:** `cloth-config` library
- **Pros:** Mature, well-documented, provides GUI screen generation
- **Cons:** External dependency

### Option 2: MidnightLib
- **Pros:** Lightweight, simple API
- **Cons:** Less flexible for complex nested configs

### Option 3: Custom JSON Config (No Dependencies)
- **Pros:** Zero dependencies, full control
- **Cons:** Must build GUI manually, more code

### Recommendation: Option 3 (Custom JSON)

Given the complexity of ParCool's config (per-action settings, server-client sync, enum selections), a **custom JSON config** gives the most control without depending on third-party APIs that may break.

## Migration Strategy

### Step 1: Create a simple config holder

```java
public class ParCoolConfig {
    private static final Path CLIENT_PATH = FabricLoader.getInstance()
        .getConfigDir().resolve("parcool-client.json");
    private static final Path SERVER_PATH = FabricLoader.getInstance()
        .getConfigDir().resolve("parcool-server.json");

    // Keep the same Item<T> interface but back with JSON instead of TOML
    public static class Client {
        private static Client instance = new Client();
        // All the same fields, but stored as plain values
        // loaded/saved via Gson
    }
}
```

### Step 2: Preserve the buffer serialization

The `writeToBuffer`/`readFromBuffer` pattern on each config item is used for networking. Keep this interface identical — it doesn't depend on NeoForge.

### Step 3: GUI Screen

The existing `ParCoolSettingScreen` and related screens (`SettingBooleanConfigScreen`, `SettingEnumConfigScreen`, `SettingActionLimitationScreen`) already extend vanilla Minecraft's `Screen` class and do custom rendering. They reference `ModConfigSpec.BooleanValue` etc. but can be refactored to use the `Item<T>` interface instead.

## Key Points

1. The **config GUI already exists** and is mostly vanilla Minecraft rendering code — it just needs to read/write from your JSON config instead of `ModConfigSpec`
2. The **network sync** of server config to client is independent of the config storage — keep the `ByteBuf` serialization
3. The **validation** (min/max ranges) currently done by `ModConfigSpec.IntValue.defineInRange()` needs to be replicated manually
4. Server config should be stored **per-world** in the world save directory, not in global config
