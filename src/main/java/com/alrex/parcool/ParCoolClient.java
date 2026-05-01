package com.alrex.parcool;

import net.fabricmc.api.ClientModInitializer;

import com.alrex.parcool.client.renderer.entity.ZiplineRopeRenderer;
import com.alrex.parcool.common.block.Blocks;
import com.alrex.parcool.common.entity.EntityTypes;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

public class ParCoolClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Entity Renderers
        EntityRenderers.register(EntityTypes.ZIPLINE_ROPE, ZiplineRopeRenderer::new);

        // Block Render Layers
        BlockRenderLayerMap.putBlock(Blocks.WOODEN_ZIPLINE_HOOK, ChunkSectionLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(Blocks.IRON_ZIPLINE_HOOK, ChunkSectionLayer.CUTOUT);

        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(com.alrex.parcool.common.network.payload.ActionStatePayload.TYPE, com.alrex.parcool.common.network.payload.ActionStatePayload::handleClient);
        
        com.alrex.parcool.client.input.KeyBindings.register();

        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                com.alrex.parcool.client.input.KeyRecorder.onClientTick();
            }
            com.alrex.parcool.common.action.ActionProcessor.ClientActionProcessor.onRenderTick(1.0f);
        });
    }
}
