# Lesson: Mixin Field Access and Parameter Type Changes

## Context
During Phase 5 animation stabilization, we needed to intercept landing events to trigger "Soft Fall" (Breakfall) and "Charge Jump" landings.

## The Problem
1. **Shadow Failures**: Attempting to `@Shadow` the `fallDistance` field in `Entity` or `LivingEntity` failed with `InvalidMixinException` because no refmap was loaded, making the field "invisible" to Mixin at runtime.
2. **Type Mismatch**: `fallDistance` was assumed to be `float` (legacy Minecraft), but in 1.21.x it was changed to `double`. This caused `InvalidInjectionException` when trying to inject into methods using it.

## The Solution
1. **Use Parameter Injections**: Instead of shadowing the `fallDistance` field, we injected into `LivingEntity.causeFallDamage(double, float, DamageSource)`. This method receives the fall distance as a parameter, bypassing the need to access the field directly.
2. **Signature Verification**: Runtime errors revealed that `fallDistance` is now a `double`. Always trust the "Expected" part of the `InvalidInjectionException` error message.
3. **Access Widener**: When field access is truly necessary across many classes, use `parcool.accesswidener` to make the field `accessible`. This is cleaner than multiple Mixin shadows.

## Patterns for Future
- If a `@Shadow` fails with "not located in target class", check if the field visibility is the issue or if the name/type has changed.
- Prefer injecting into methods that already take the desired field as a parameter.
- Use `CallbackInfoReturnable` when injecting into methods with return types (like `causeFallDamage` which returns `boolean`).
