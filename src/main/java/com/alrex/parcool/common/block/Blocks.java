package com.alrex.parcool.common.block;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.block.zipline.IronZiplineHookBlock;
import com.alrex.parcool.common.block.zipline.WoodenZiplineHookBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class Blocks {
    public static final Block WOODEN_ZIPLINE_HOOK = register("wooden_zipline_hook",
            new WoodenZiplineHookBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(1.0f, 3.0f)
                    .sound(SoundType.WOOD)
                    .pushReaction(PushReaction.DESTROY)
                    .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "wooden_zipline_hook")))
            )
    );

    public static final Block IRON_ZIPLINE_HOOK = register("iron_zipline_hook",
            new IronZiplineHookBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(1.0f, 3.0f)
                    .noCollision()
                    .sound(SoundType.CHAIN)
                    .pushReaction(PushReaction.DESTROY)
                    .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "iron_zipline_hook")))
            )
    );

    private static Block register(String name, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, Identifier.fromNamespaceAndPath(ParCool.MOD_ID, name), block);
    }

    public static void registerAll() {
        // Trigger static initialization
    }
}
