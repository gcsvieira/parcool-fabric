package com.alrex.parcool.common.item;

import com.alrex.parcool.ParCool;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class CreativeTabs {
    public static final CreativeModeTab MAIN = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "main"),
            FabricItemGroup.builder()
                    .title(Component.translatable("itemGroup.ParCool"))
                    .icon(() -> new ItemStack(Items.PARCOOL_GUIDE))
                    .displayItems((params, output) -> {
                        output.accept(Items.PARCOOL_GUIDE);
                        output.accept(Items.WOODEN_ZIPLINE_HOOK);
                        output.accept(Items.IRON_ZIPLINE_HOOK);
                        output.accept(Items.ZIPLINE_ROPE);
                    })
                    .build()
    );

    public static void registerAll() {
        // Trigger static initialization
    }
}
