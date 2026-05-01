package com.alrex.parcool.common.potion;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.Effects;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;

public class Potions {
    public static final Potion POOR_ENERGY_DRINK = register("poor_energy_drink",
            new Potion("poor_energy_drink",
                    new MobEffectInstance(Effects.INEXHAUSTIBLE, 2400),
                    new MobEffectInstance(MobEffects.HUNGER, 100),
                    new MobEffectInstance(MobEffects.POISON, 100)
            )
    );

    public static final Potion ENERGY_DRINK = register("energy_drink",
            new Potion("energy_drink",
                    new MobEffectInstance(Effects.INEXHAUSTIBLE, 9600)
            )
    );

    private static Potion register(String name, Potion potion) {
        return Registry.register(BuiltInRegistries.POTION, Identifier.fromNamespaceAndPath(ParCool.MOD_ID, name), potion);
    }

    public static void registerAll() {
        // Trigger static initialization
    }
}
