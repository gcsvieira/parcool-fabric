package com.alrex.parcool.api;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.potion.effects.InexhaustibleEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;

public class Effects {
    public static final Holder<MobEffect> INEXHAUSTIBLE = register("inexhaustible", new InexhaustibleEffect());

    private static Holder<MobEffect> register(String name, MobEffect effect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(ParCool.MOD_ID, name), effect);
    }

    public static void registerAll() {
        // Trigger static initialization
    }
}
