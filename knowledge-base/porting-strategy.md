---
id: porting-strategy
type: knowledge
tags: ["#porting", "#strategy"]
links: ["[[MOC-porting]]"]
---

# Porting Strategy: Phased Execution Plan


## Philosophy

Port in **vertical slices** — each phase produces a testable, runnable mod. Never go more than a few hours without being able to launch the game.

---

## Phase 0: Project Scaffold (Day 1)
**Goal:** Empty Fabric mod that loads in Minecraft

- [ ] Create new Fabric project directory
- [ ] Configure `build.gradle`, `gradle.properties`, `settings.gradle`
- [ ] Create `fabric.mod.json`
- [ ] Create `ParCool.java` implementing `ModInitializer` (empty `onInitialize()`)
- [ ] Create `ParCoolClient.java` implementing `ClientModInitializer`
- [ ] Copy `parcool.mixins.json` (remove all entries temporarily)
- [ ] Create `parcool.accesswidener` (empty)
- [ ] Run `./gradlew runClient` — verify game launches with mod in list

**Verify:** Mod appears in Fabric mod menu. No crashes.

---

## Phase 1: Copy Pure Logic (Day 1-2)
**Goal:** All platform-independent code compiles

- [ ] Copy `utilities/` package (all 7 files) — zero changes needed
- [ ] Copy `common/action/Action.java`, `Actions.java`, `InstantAction.java`, `StaminaConsumeTiming.java`, `AdditionalProperties.java`, `BehaviorEnforcer.java`
- [ ] Copy all `common/action/impl/` (22 files) — stub out NeoForge event references
- [ ] Copy `common/info/` (3 files)
- [ ] Copy `common/stamina/` (4 files)
- [ ] Copy `common/zipline/` (4 files)
- [ ] Copy `common/tags/BlockTags.java`
- [ ] Copy `common/damage/DamageSources.java`

**Verify:** Project compiles (with some stubs). Game still launches.

---

## Phase 2: Registries (Day 2-3)
**Goal:** Custom blocks, items, entities, effects exist in-game

- [ ] Port `api/Attributes.java` → direct `Registry.register()` calls
- [ ] Port `api/Effects.java` → direct registration
- [ ] Port `api/SoundEvents.java` → direct registration
- [ ] Port `common/block/Blocks.java` + block classes
- [ ] Port `common/block/TileEntities.java`
- [ ] Port `common/item/Items.java` + item classes
- [ ] Port `common/item/DataComponents.java`
- [ ] Port `common/item/CreativeTabs.java` → `FabricItemGroup`
- [ ] Port `common/entity/EntityTypes.java`
- [ ] Port `common/potion/Potions.java`
- [ ] Port `common/potion/ParCoolBrewingRecipe.java`
- [ ] Copy ALL resource files (`assets/parcool/`, `data/parcool/`) — unchanged
- [ ] Register attributes on player entity
- [ ] Port `server/command/` (4 arg types + 2 commands + registry)

**Verify:** Items appear in creative tab. Blocks can be placed. Commands work.

---

## Phase 3: Data Attachments (Day 3-4)
**Goal:** Parkourability and Stamina data attached to players

- [ ] Create `ParkourabilityAccess` interface
- [ ] Create `PlayerDataMixin` injecting fields into `Player`
- [ ] Port `Parkourability.java` — replace `getData()` with mixin access
- [ ] Port `ReadonlyStamina.java` — add save/load via mixin
- [ ] Create `Parkourability.get(player)` static helper using mixin cast
- [ ] Wire up data creation on player join / cleanup on leave

**Verify:** `Parkourability.get(player)` returns valid data. No NPEs.

---

## Phase 4: Core Action Loop (Day 4-6)
**Goal:** Parkour actions trigger in singleplayer

- [ ] Port `ActionProcessor.java` — replace `@SubscribeEvent` with Fabric tick callbacks
- [ ] Port `EventBusForgeRegistry.java` — convert to direct callback registrations
- [ ] Port key bindings (`KeyBindings.java`) → `KeyBindingHelper`
- [ ] Port `KeyRecorder.java` → `ClientTickEvents`
- [ ] Port `InputHandler.java` → mixin or callback
- [ ] Port `common/handlers/PlayerJumpHandler.java`
- [ ] Port `common/handlers/PlayerDamageHandler.java`
- [ ] Wire up `LivingEntityMixin`, `EntityMixin`, `PlayerMixin` (re-enable in mixins.json)
- [ ] Port client-side stamina (`LocalStamina.java`, `ClientAttachments.java`)
- [ ] Create custom Fabric event callbacks for ParCool API events (or stub them)

**Verify:** Press movement keys. Actions trigger. Player does wall jumps, vaults, slides. Stamina depletes.

---

## Phase 5: Animations (Day 6-7)
**Goal:** Other players see your animations, camera effects work

- [ ] Copy all `client/animation/` code (27 animators + infrastructure)
- [ ] Port `Animation.java` (client attachment → WeakHashMap or mixin)
- [ ] Re-enable `PlayerModelMixin` and `AvatarRendererMixin`
- [ ] Port camera manipulation (add Camera mixin for ViewportEvent replacement)
- [ ] Port `RenderBehaviorEnforcer.java`
- [ ] Re-enable `OptionsMixin`

**Verify:** Third-person view shows animations. Camera rolls during dodge. First-person view effects work.

---

## Phase 6: HUD and GUI (Day 7-8)
**Goal:** Stamina HUD renders, settings screens work

- [ ] Port `HUDRegistry.java` → `HudRenderCallback`
- [ ] Port `HUDManager.java` — minimal changes
- [ ] Copy HUD implementations (StaminaHUD, LightStaminaHUD)
- [ ] Port settings screens — replace `ModConfigSpec` references with config Item<T>
- [ ] Port `OpenSettingsParCoolHandler.java`
- [ ] Port `EnableOrDisableParCoolHandler.java`

**Verify:** Stamina bar visible. Settings screen opens with Alt+P. Changes persist.

---

## Phase 7: Networking (Day 8-10)
**Goal:** Full multiplayer support

- [ ] Port all 8 payload classes (minimal changes to records)
- [ ] Create networking registration using `PayloadTypeRegistry` + Fabric networking
- [ ] Port `ActionSynchronizationBroadcaster.java`
- [ ] Port `StaminaSynchronizationBroadcaster.java`
- [ ] Port `LoginLogoutHandler.java` → `ServerPlayConnectionEvents`
- [ ] Port `PlayerJoinHandler.java`
- [ ] Port `PlayerCloneHandler.java` → `ServerPlayerEvents.COPY_FROM`
- [ ] Port `PlayerVisibilityHandler.java`

**Verify:** Start server + client. Two players see each other's animations. Stamina syncs.

---

## Phase 8: Config (Day 10-12)
**Goal:** Full config system with persistence

- [ ] Create JSON config loader/saver
- [ ] Port all `ParCoolConfig.Client` values
- [ ] Port all `ParCoolConfig.Server` values
- [ ] Wire config values to existing references throughout codebase
- [ ] Port server→client config sync via networking
- [ ] Update settings GUI to use new config backend

**Verify:** Config file generates. Values persist across restarts. Server limits apply.

---

## Phase 9: Polish (Day 12-14)
**Goal:** Feature parity with NeoForge version

- [ ] Port Access Widener (translate SRG → named fields)
- [ ] Port external mod compat (`AdditionalMods`, ShoulderSurfing, BetterThirdPerson)
- [ ] Port `server/limitation/` system
- [ ] Add Patchouli guide integration (if Patchouli for Fabric exists)
- [ ] Test all 22 actions individually
- [ ] Test all edge cases (water, creative mode, spectator, death/respawn)
- [ ] Test all config options
- [ ] Build release jar

**Verify:** Full feature parity. All actions work. Multiplayer works. Config works.

---

## Total Estimated Effort

| Aspect | Files | Effort |
|---|---|---|
| Zero-change files | ~120 | Copy only |
| Light refactor files | ~40 | 1-5 line changes |
| Medium refactor files | ~25 | Significant edits |
| Heavy rewrite files | ~13 | Near-total rewrite |
| **Total** | **198** | **~10-14 working days** |
