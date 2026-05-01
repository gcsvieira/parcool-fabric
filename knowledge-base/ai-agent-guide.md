---
id: ai-agent-guide
type: knowledge
tags: ["#ai", "#instructions"]
links: ["[[MOC-lessons]]"]
---

# AI Agent Guide: What Skills and Knowledge Are Needed


## For the Human Partner (You)

### What You Need to Know

1. **Java 21 features** — records, pattern matching, sealed classes, `var`. The codebase uses all of these.

2. **Gradle basics** — you'll run `./gradlew runClient`, `./gradlew build`. The build file is ~50 lines. No deep Gradle knowledge needed.

3. **How Minecraft mods load** — In Fabric: game starts → Fabric Loader finds your `fabric.mod.json` → loads your `ModInitializer` class → calls `onInitialize()` → your code registers everything. Client-side code loads separately via `ClientModInitializer`.

4. **Client vs Server side** — Minecraft is a client-server architecture even in singleplayer. Some code runs on both sides, some only on client (rendering, input), some only on server (world saves, authority). The `@Environment(EnvType.CLIENT)` annotation marks client-only code. Mixins are split into `client` and `common` sections.

5. **What a Mixin does** — It's bytecode injection. `@Inject(at = @At("HEAD"))` means "run my code at the start of this method." `@Inject(at = @At("RETURN"))` means "run it at the end." `cancellable = true` means "I can prevent the original method from executing." Think of it as aspect-oriented programming for Minecraft.

6. **How to read the logs** — When the game crashes, read `crash-reports/crash-*.txt`. For runtime errors, check `logs/latest.log`. Mixin errors are verbose but usually tell you exactly which injection failed.

### Skills You'll Exercise

- Reading and understanding existing Java code (the NeoForge version)
- Making targeted find-and-replace edits across files
- Running the dev client and testing in-game
- Debugging by reading crash reports
- Comparing API documentation between NeoForge and Fabric

---

## For the AI Agent

### Capabilities Needed

1. **Java source code transformation** — The bulk of the work is replacing `import net.neoforged.*` with equivalent Fabric patterns. The agent needs to understand both APIs.

2. **Pattern recognition** — Most registrations follow the same pattern. Recognize `DeferredRegister.create(Registries.X, MOD_ID)` and convert to `Registry.register(Registries.X, id, object)`.

3. **Build system configuration** — Generate `build.gradle` for Fabric Loom instead of NeoForge MDG.

4. **JSON file generation** — Create `fabric.mod.json`, update `parcool.mixins.json`.

5. **Access Widener translation** — Convert SRG field names (`f_110147_`) to Mojang named equivalents.

6. **Understanding side-effects** — Know that removing `NeoForge.EVENT_BUS.post(...)` calls means API consumers won't get events. Must replace with equivalent Fabric events.

### What the Agent Should NOT Do

1. **Don't rewrite game logic** — The action implementations, animation math, utility functions are correct and battle-tested. Don't "improve" them.

2. **Don't add dependencies unnecessarily** — Prefer solutions using Fabric API + mixins over third-party libraries.

3. **Don't guess at mappings** — If unsure whether a class name is the same between NeoForge and Fabric, look it up. With Mojang mappings on both sides, they should be identical, but some NeoForge patches add methods that don't exist in vanilla.

4. **Don't change the public API** — `Parkourability.get(player)`, action class names, config paths — these should stay the same for anyone who might reference them.

### Key NeoForge-Specific Classes to Watch For

These classes/imports appear frequently and need systematic replacement:

| Import | Frequency | Replacement |
|---|---|---|
| `net.neoforged.bus.api.SubscribeEvent` | ~25 uses | Remove — use direct callback registration |
| `net.neoforged.bus.api.IEventBus` | ~15 uses | Remove — not needed in Fabric |
| `net.neoforged.neoforge.common.NeoForge` | ~20 uses | Remove EVENT_BUS references → Fabric callbacks |
| `net.neoforged.neoforge.registries.DeferredRegister` | ~12 uses | `Registry.register()` |
| `net.neoforged.neoforge.registries.DeferredHolder` | ~10 uses | Direct reference to registered object |
| `net.neoforged.neoforge.common.ModConfigSpec` | ~50 uses | Custom config implementation |
| `net.neoforged.neoforge.attachment.*` | ~5 uses | Mixin-based attachments |
| `net.neoforged.neoforge.event.*` | ~20 uses | Various Fabric callbacks |
| `net.neoforged.neoforge.client.event.*` | ~10 uses | Fabric client callbacks |
| `net.neoforged.neoforge.network.*` | ~8 uses | Fabric Networking API |
| `net.neoforged.fml.*` | ~8 uses | Fabric Loader API |
| `net.neoforged.api.distmarker.Dist` | ~3 uses | `net.fabricmc.api.EnvType` |

### Recommended Agent Workflow

```
1. Start with Phase 0-1 (scaffold + pure logic copy)
   - This is mostly file copying with zero transformation
   - Verify compilation

2. Do registries (Phase 2) one registry at a time
   - Items → test → Blocks → test → Effects → test
   - Each registry follows the same transformation pattern

3. Do attachments (Phase 3) before actions
   - Actions depend on Parkourability.get() working

4. Do the action loop (Phase 4) — the hardest phase
   - Port ActionProcessor tick/render hooks
   - Port all event handlers
   - This is where most NeoForge → Fabric API translation happens

5. Do networking (Phase 7) after actions work locally
   - Verify singleplayer first, then add multiplayer sync

6. Config (Phase 8) last — it's isolated and the biggest rewrite
   - The mod works without config (defaults are hardcoded)
```

---

## Risk Assessment

| Risk | Likelihood | Impact | Mitigation |
|---|---|---|---|
| MC version mismatch (1.21.11 vs Fabric's supported versions) | Medium | High | Check Fabric API version compatibility first. May need to target 1.21.4 instead. |
| AvatarRenderer mixin target doesn't exist | Low | Medium | Verify class existence in Fabric's mapped jar. May need alternative injection point. |
| NeoForge-patched methods used unknowingly | Medium | Medium | Any `net.minecraft` method that doesn't exist in vanilla will fail. Grep for NeoForge-added methods. |
| Config system complexity | Low | High | Start with hardcoded defaults, add config last |
| Fabric API event gaps | Medium | Medium | Some NeoForge events have no Fabric callback equivalent. Use mixins as fallback. |
| Performance regression from HashMap attachments vs native | Low | Low | WeakHashMap or mixin injection performs fine |

## NeoForge-Patched Minecraft Methods to Watch

NeoForge patches vanilla Minecraft to add methods. If ParCool calls any of these, they won't exist in Fabric:

- `Entity.getData()` — NeoForge attachment system
- `LivingEntity.onClimbable()` — may have NeoForge hooks
- `Player.canPlayerFitWithinBlocksAndEntitiesWhen()` — exists in vanilla but access-transformed

**Action:** Before copying any file, check its `net.minecraft` method calls against vanilla Minecraft's API. If a method doesn't exist in vanilla, it's a NeoForge patch and needs a workaround.
