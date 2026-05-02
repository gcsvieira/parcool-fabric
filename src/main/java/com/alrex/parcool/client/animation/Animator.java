package com.alrex.parcool.client.animation;

import com.alrex.parcool.common.attachment.common.Parkourability;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Camera;

public abstract class Animator {
    public interface PlayerModelTransformer {}
    public interface PlayerModelRotator {}

    private int tick = 0;

    public void tick(Player player) {
        tick++;
    }

    protected int getTick() {
        return tick;
    }

    public abstract boolean shouldRemoved(Player player, Parkourability parkourability);

    public boolean animatePre(
            Player player,
            Parkourability parkourability,
            PlayerModelTransformer transformer
    ) {
        return false;
    }

    public void animatePost(
            Player player,
            Parkourability parkourability,
            PlayerModelTransformer transformer
    ) {
    }

    public boolean rotatePre(
            Player player,
            Parkourability parkourability,
            PlayerModelRotator rotator
    ) {
        return false;
    }

    public void rotatePost(
            Player player,
            Parkourability parkourability,
            PlayerModelRotator rotator
    ) {
    }

    public void onCameraSetUp(
            Camera camera,
            Player clientPlayer,
            Parkourability parkourability
    ) {
    }

    public void onRenderTick(
            float tickDelta,
            Player player,
            Parkourability parkourability
    ) {
    }
}
