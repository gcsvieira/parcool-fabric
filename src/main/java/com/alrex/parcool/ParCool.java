package com.alrex.parcool;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParCool implements ModInitializer {
    public static final String MOD_ID = "parcool";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("ParCool! is initializing on Fabric.");
    }
}
