package com.alrex.parcool.common.attachment.client;

import com.alrex.parcool.common.attachment.common.Parkourability;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

public class Animation {
    private static final java.util.WeakHashMap<Player, Animation> INSTANCES = new java.util.WeakHashMap<>();

    public static Animation get(Player player) {
        return INSTANCES.computeIfAbsent(player, p -> new Animation());
    }
    public void tick(AbstractClientPlayer player, Parkourability parkourability) {}
    public void onRenderTick(Float tickDelta, Player player, Parkourability parkourability) {}
    public void cameraSetup(net.minecraft.client.Camera camera, LocalPlayer player, Parkourability parkourability) {}
}
