---
id: lesson-data-attachment-stability-hitbox-sync
type: lesson
tags: ["#npe", "#config", "#hitbox"]
links: ["[[MOC-lessons]]", "[[task-phase-3-data-attachments]]", "[[data-attachments]]"]
---

# Session 4 Stability & Infrastructure Lessons

## 1. Lazy Init vs. Direct Field Access (Save NPE)
**Issue:** `PlayerDataMixin` used lazy getters for `Parkourability` and `Stamina` during ticking, but `addAdditionalSaveData` accessed the `parcool$stamina` field directly.
**Result:** NPE when saving world if the player hadn't ticked yet (e.g., during world join/save sequence).
**Fix:** Always use the `parcool$getStamina()` getter to ensure initialization before usage.

## 2. Config/Limitation Stubs Blocking Actions
**Issue:** Default implementations of `ClientSetting` and `ServerLimitation` (used before sync) returned `false` for action availability.
**Result:** Actions like Crawl were registered and ticking but never allowed to start.
**Fix:** Hardcode stubs to return `true` during the migration phase so features can be tested without a full config system.

## 3. Forced Pose Hitbox Sync
**Issue:** Forcing a player pose via `getPose` mixin doesn't automatically update the collision box.
**Result:** Player looks like they are crawling but can't fit through 1-block gaps.
**Fix:** Shadow `Entity.refreshDimensions()` and call it whenever the forced pose changes.

## 4. Shadow Method Fragility (Mappings)
**Issue:** Attempting to `@Shadow` the protected `canEnterPose(Pose)` method in `Entity` failed with `InvalidMixinException`. The method name or signature likely differed in the target environment vs. development mappings.
**Result:** Game crash on startup (Mixin application failure).
**Fix:** Implement the check manually using the `level().noCollision(entity, AABB)` API. This is more robust against mapping changes.

## 5. Headless Environment runClient Failure
**Issue:** Executing `./gradlew runClient` in a headless server environment (like this agent's terminal) will cause a crash when OpenGL tries to initialize.
**Result:** Logs show successful Mixin application but crash at splash screen.
**Fix:** Use `./gradlew compileJava` for verification, or rely on the user for GUI testing. Log analysis can still verify Mixin success before the OpenGL crash.
