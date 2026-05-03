package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.animation.PlayerRenderStateAccess;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class PlayerRenderStateMixin implements PlayerRenderStateAccess {
    @Unique
    public Player parCool$player;
    @Unique
    public float parCool$partialTick;

    @Override
    public Player parCool$getPlayer() {
        return parCool$player;
    }

    @Override
    public void parCool$setPlayer(Player player) {
        this.parCool$player = player;
    }

    @Override
    public float parCool$getPartialTick() {
        return parCool$partialTick;
    }

    @Override
    public void parCool$setPartialTick(float partialTick) {
        this.parCool$partialTick = partialTick;
    }
}
