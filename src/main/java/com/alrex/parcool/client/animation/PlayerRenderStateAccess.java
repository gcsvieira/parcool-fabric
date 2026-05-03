package com.alrex.parcool.client.animation;

import net.minecraft.world.entity.player.Player;

public interface PlayerRenderStateAccess {
    Player parCool$getPlayer();
    void parCool$setPlayer(Player player);
    float parCool$getPartialTick();
    void parCool$setPartialTick(float partialTick);
}
