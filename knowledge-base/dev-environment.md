---
id: dev-environment
type: knowledge
tags: ["#architecture", "#dev"]
links: ["[[MOC-architecture]]"]
---

# Development Environment Setup


## What You Need Installed

1. **JDK 21** — required by MC 1.21.x
2. **IntelliJ IDEA** (recommended) or VS Code with Java extensions
3. **Gradle** — bundled via wrapper, no manual install needed
4. **Minecraft launcher** — for testing the final jar

## Setting Up a Fabric Mod Project

### Step 1: Use the Fabric Template Generator

Visit https://fabricmc.net/develop/template/ or use the CLI:

```bash
mkdir ParCoolFabric && cd ParCoolFabric

# Option A: Clone the template
git clone https://github.com/FabricMC/fabric-example-mod .

# Option B: Use the online generator at fabricmc.net
# Download and extract into this directory
```

### Step 2: Configure `gradle.properties`

```properties
# Fabric Properties
minecraft_version=1.21.4
yarn_mappings=1.21.4+build.8
loader_version=0.16.14
fabric_version=0.114.0+1.21.4

# Mod Properties
mod_version=3.4.3.3-fabric
maven_group=com.alrex
archives_base_name=parcool
```

> **Note on MC version:** ParCool targets 1.21.11, but Fabric typically targets specific subversions (1.21.4, etc.). Choose the latest 1.21.x that Fabric API supports. Check https://fabricmc.net/develop/

### Step 3: Configure `build.gradle`

```groovy
plugins {
    id 'fabric-loom' version '1.9-SNAPSHOT'
    id 'maven-publish'
}

dependencies {
    minecraft "com.mojang:minecraft:${project.minecraft_version}"
    mappings "net.fabricmc:yarn:${project.yarn_mappings}:v2"
    modImplementation "net.fabricmc:fabric-loader:${project.loader_version}"
    modImplementation "net.fabricmc.fabric-api:fabric-api:${project.fabric_version}"
}
```

### Step 4: Create `fabric.mod.json`

```json
{
  "schemaVersion": 1,
  "id": "parcool",
  "version": "${version}",
  "name": "ParCool!",
  "description": "Parkour actions for Minecraft",
  "authors": ["alRex_U"],
  "license": "LGPL-3.0",
  "icon": "parcool_logo.png",
  "environment": "*",
  "entrypoints": {
    "main": ["com.alrex.parcool.ParCool"],
    "client": ["com.alrex.parcool.ParCoolClient"]
  },
  "mixins": ["parcool.mixins.json"],
  "accessWidener": "parcool.accesswidener",
  "depends": {
    "fabricloader": ">=0.16.0",
    "minecraft": "~1.21.4",
    "java": ">=21",
    "fabric-api": "*"
  }
}
```

### Step 5: Run the Dev Client

```bash
./gradlew runClient        # Launch Minecraft with your mod
./gradlew runServer        # Launch a dedicated server
./gradlew build            # Build the jar for distribution
```

## How to Test Like a Modder

### Development Cycle

```
1. Edit code
2. ./gradlew runClient     (launches MC with hot-reloadable mod)
3. Test in-game:
   - Join a singleplayer world (creative mode)
   - Press assigned keys to trigger parkour actions
   - Watch the console/log for errors
4. Fix → repeat
```

### What to Test For Each Phase

| Phase | Test Criteria |
|---|---|
| Phase 1 (Skeleton) | Game launches without crash, mod appears in mod list |
| Phase 2 (Registries) | Items/blocks appear in creative tab, can be placed/used |
| Phase 3 (Actions) | Press movement keys → actions trigger, player moves |
| Phase 4 (Animations) | Third-person view shows custom animations |
| Phase 5 (HUD) | Stamina bar renders, depletes on actions |
| Phase 6 (Networking) | Multiplayer: other players see your animations |
| Phase 7 (Config) | Config file generates, values persist |

### Debug Tools

- **F3 debug screen** — shows entity data, coordinates
- **`/parcool`** commands — once command system is ported
- **Log file** — `logs/latest.log` in the run directory
- **Mixin debug** — add `-Dmixin.debug=true` to JVM args

### Multiplayer Testing

Run both client and server:
```bash
# Terminal 1
./gradlew runServer

# Terminal 2
./gradlew runClient
# Connect to localhost in-game
```

## Project Structure (Fabric)

```
ParCoolFabric/
├── build.gradle
├── gradle.properties
├── settings.gradle
├── src/main/
│   ├── java/com/alrex/parcool/
│   │   ├── ParCool.java           ← ModInitializer
│   │   ├── ParCoolClient.java     ← ClientModInitializer (NEW)
│   │   └── ... (same package structure)
│   └── resources/
│       ├── fabric.mod.json         ← replaces neoforge.mods.toml
│       ├── parcool.mixins.json     ← same file, same format
│       ├── parcool.accesswidener   ← replaces accesstransformer.cfg
│       ├── parcool_logo.png
│       ├── assets/parcool/         ← IDENTICAL to NeoForge version
│       └── data/parcool/           ← IDENTICAL to NeoForge version
```

## Mapping Differences

NeoForge uses **Mojang mappings + Parchment** (parameter names).
Fabric uses **Intermediary** as the stable middle layer, with **Yarn** or **Mojang mappings** for development.

**Recommendation:** Use **Mojang mappings** in the Fabric project (supported via Loom) to minimize the translation effort from NeoForge. Add to `build.gradle`:

```groovy
mappings loom.officialMojangMappings()
```

This means `Player`, `Level`, `BlockState` etc. keep their exact same names.
