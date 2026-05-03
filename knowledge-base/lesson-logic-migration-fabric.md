---
id: lesson-logic-migration-fabric
type: lesson
tags: ["#fabric", "#logic", "#migration"]
links: ["[[MOC-lessons]]", "[[task-phase-1-copy-logic]]"]
---

# 🎓 Lesson: Architecting Logic Migration for Fabric

## Context
During Phase 1 of the ParCool port, we migrated over 40 files including core `Action` logic, `ActionProcessor`, `Stamina` handlers, and `Zipline` math. This phase focused on "clean-room" porting while maintaining compilation.

## ⚠️ The "Split Environment" Trap
**Problem:** Fabric Loom's `splitEnvironmentSourceSets()` strictly separates `main` (common) from `client`.
**Discovery:** Many classes in the NeoForge `common` package referenced `LocalPlayer` or `RenderFrameEvent`. These caused immediate compilation failures in the Fabric `main` source set.
**Solution:**
- Swapped `LocalPlayer` with `Player` in common logic signatures.
- Stubbed client-only event parameters as `Object` or commented out methods that are purely visual.
- **Rule of Thumb:** If it's in `src/main`, it must be environment-agnostic.

## 🛠️ The Skeleton-Stub Strategy
**Pattern:** Don't migrate everything at once. 
1. Identify the core "Spine" (e.g., `Action.java`).
2. Create empty stubs for all dependencies (`Parkourability`, `ReadonlyStamina`).
3. Fill the stubs with minimal methods needed for the Spine to compile.
4. **Benefit:** This prevents "dependency hell" and allows incremental validation via `./gradlew compileJava`.

## 🧩 Ambiguous Constructor Gotcha
**Scenario:** Migrating Enums with multiple constructors taking `null`.
```java
// FAILS in some environments due to ambiguity
PARCOOL(null) 

// FIXED
PARCOOL((Function<Player, IParCoolStaminaHandler>) null)
```
**Lesson:** Always be explicit with `null` when constructors are overloaded during migration.

## 🏷️ Annotation Alignment
**Mismatch:** NeoForge often uses `javax.annotation.Nullable`.
**Standard:** Fabric/Vanilla (1.21+) tends toward `org.jetbrains.annotations.Nullable`. 
**Action:** Mass-replace `javax.annotation` with `org.jetbrains.annotations` to align with the modern toolchain and avoid missing dependency errors.

## ✅ Verification Milestone
Even with 90% of the implementation logic stubbed, hitting the **Main Menu** in `runClient` is a critical test. It proves that:
1. `fabric.mod.json` is correct.
2. The `onInitialize` sequence is unbroken.
3. No critical Mixins (even if not yet added) or Registry issues are blocking startup.
