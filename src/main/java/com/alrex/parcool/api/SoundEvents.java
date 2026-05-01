package com.alrex.parcool.api;

import com.alrex.parcool.ParCool;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class SoundEvents {
    public static final Holder<SoundEvent> VAULT = register("vault");
    public static final Holder<SoundEvent> VERTICAL_WALL_RUN = register("v_wall_run");
    public static final Holder<SoundEvent> HORIZONTAL_WALL_RUN = register("h_wall_run");
    public static final Holder<SoundEvent> BREAKFALL_JUST = register("breakfall.just");
    public static final Holder<SoundEvent> BREAKFALL_ROLL = register("breakfall.roll");
    public static final Holder<SoundEvent> BREAKFALL_TAP = register("breakfall.tap");
    public static final Holder<SoundEvent> CHARGE_JUMP = register("charge_jump");
    public static final Holder<SoundEvent> CATLEAP = register("catleap");
    public static final Holder<SoundEvent> WALL_JUMP = register("wall_jump");
    public static final Holder<SoundEvent> CLING_TO_CLIFF_GRAB = register("cling_to_cliff.grab");
    public static final Holder<SoundEvent> CLING_TO_CLIFF_JUMP = register("cling_to_cliff.jump");
    public static final Holder<SoundEvent> HANG_DOWN_GRAB = register("hang_down.grab");
    public static final Holder<SoundEvent> HANG_DOWN_JUMP = register("hang_down.jump");
    public static final Holder<SoundEvent> SLIDE = register("slide");
    public static final Holder<SoundEvent> DODGE = register("dodge");
    public static final Holder<SoundEvent> ENABLE_PARCOOL = register("enable_parcool");
    public static final Holder<SoundEvent> DISABLE_PARCOOL = register("disable_parcool");
    public static final Holder<SoundEvent> ZIPLINE_SET = register("zipline.set");
    public static final Holder<SoundEvent> ZIPLINE_REMOVE = register("zipline.remove");

    private static Holder<SoundEvent> register(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(ParCool.MOD_ID, name);
        return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void registerAll() {
        // Trigger static initialization
    }
}
