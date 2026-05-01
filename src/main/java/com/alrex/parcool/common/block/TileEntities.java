package com.alrex.parcool.common.block;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TileEntities {
    public static final BlockEntityType<ZiplineHookTileEntity> ZIPLINE_HOOK = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "zipline_hook"),
            FabricBlockEntityTypeBuilder.create(ZiplineHookTileEntity::new, Blocks.WOODEN_ZIPLINE_HOOK, Blocks.IRON_ZIPLINE_HOOK).build()
    );

    public static void registerAll() {
        // Trigger static initialization
    }
}
