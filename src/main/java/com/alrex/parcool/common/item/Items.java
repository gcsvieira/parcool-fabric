package com.alrex.parcool.common.item;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.block.Blocks;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class Items {
    public static final Item PARCOOL_GUIDE = register("parcool_guide", new Item(new Item.Properties().stacksTo(1).setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "parcool_guide")))));
    public static final Item WOODEN_ZIPLINE_HOOK = register("wooden_zipline_hook", new BlockItem(Blocks.WOODEN_ZIPLINE_HOOK, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "wooden_zipline_hook")))));
    public static final Item IRON_ZIPLINE_HOOK = register("iron_zipline_hook", new BlockItem(Blocks.IRON_ZIPLINE_HOOK, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "iron_zipline_hook")))));
    public static final Item ZIPLINE_ROPE = register("zipline_rope", new ZiplineRopeItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "zipline_rope")))));

    private static Item register(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(ParCool.MOD_ID, name), item);
    }

    public static void registerAll() {
        // Trigger static initialization
    }
}
