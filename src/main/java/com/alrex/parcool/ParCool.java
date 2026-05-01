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
    }
}
