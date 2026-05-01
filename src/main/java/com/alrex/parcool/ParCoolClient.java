package com.alrex.parcool;

import net.fabricmc.api.ClientModInitializer;

import com.alrex.parcool.client.renderer.entity.ZiplineRopeRenderer;
import com.alrex.parcool.common.block.Blocks;
import com.alrex.parcool.common.entity.EntityTypes;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

public class ParCoolClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Entity Renderers
        EntityRendererRegistry.register(EntityTypes.ZIPLINE_ROPE, ZiplineRopeRenderer::new);

        // Block Render Layers
        BlockRenderLayerMap.putBlock(Blocks.WOODEN_ZIPLINE_HOOK, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.IRON_ZIPLINE_HOOK, ChunkSectionLayer.CUTOUT);
    }
}
