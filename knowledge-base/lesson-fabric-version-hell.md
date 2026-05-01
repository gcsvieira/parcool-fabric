---
type: lesson
tags: [fabric, gradle, versioning, minecraft-1-21-11]
links: ["[[MOC-lessons]]"]
---

# Lesson: Fabric Version Alignment (1.21.11)

## Context
During the bootstrap of the ParCool Fabric port, we encountered significant build failures related to "unpick version" and "Javadoc intermediary namespace" conflicts.

## The Problem
1. **Misreading Metadata:** The target version `1.21.11` was initially misread as `1.21.1`.
2. **Toolchain Mismatch:** `1.21.11` is a transitional "obfuscated" version that requires specific toolchain alignments:
    - **Loom:** `1.16-SNAPSHOT`.
    - **Plugin ID:** `net.fabricmc.fabric-loom-remap` (specifically for obfuscated versions).
    - **Gradle:** `8.14`.

## The "Version Tetris" Trap
Guessing version combinations based on "standard" patterns (e.g., assuming 1.21.1 patterns work for 1.21.11) leads to:
- `UnsupportedOperationException: Unsupported unpick version`
- `IllegalStateException: Javadoc must have an intermediary source namespace`

## The Fix (Ground Truth)
When in doubt, bypass searches and manual guesses. Go directly to the **[Official Fabric Template Generator](https://fabricmc.net/develop/template/)**.

### Verified Stack for 1.21.11:
- **Minecraft:** `1.21.11`
- **Yarn Mappings:** `1.21.11+build.5`
- **Loom Plugin:** `1.16-SNAPSHOT`
- **Plugin ID:** `net.fabricmc.fabric-loom-remap`
- **Loader:** `0.19.2`
- **Fabric API:** `0.141.3+1.21.11`
- **Gradle:** `8.14`

## Conclusion
Always pull versioning from the live generator for non-standard Minecraft subversions. Do not assume minor versions share the same toolchain requirements.
