package com.alrex.parcool;

import com.alrex.parcool.common.registry.ModRegistries;
import com.alrex.parcool.server.command.CommandRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParCool implements ModInitializer {
    public static final String MOD_ID = "parcool";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("ParCool! is initializing on Fabric.");
        ModRegistries.registerAll();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            CommandRegistry.register(dispatcher);
        });

        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.playC2S().register(com.alrex.parcool.common.network.payload.ActionStatePayload.TYPE, com.alrex.parcool.common.network.payload.ActionStatePayload.CODEC);
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.playS2C().register(com.alrex.parcool.common.network.payload.ActionStatePayload.TYPE, com.alrex.parcool.common.network.payload.ActionStatePayload.CODEC);
        net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.registerGlobalReceiver(com.alrex.parcool.common.network.payload.ActionStatePayload.TYPE, com.alrex.parcool.common.network.payload.ActionStatePayload::handleServer);
    }
}
