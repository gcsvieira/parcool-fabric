package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.data.ParkourabilityAccess;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.WorldUtil;
import com.alrex.parcool.client.input.KeyRecorder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.nio.ByteBuffer;

public class Dive extends Action {
    private boolean justJumped = false;
    private double initialYVelocityOfLastJump = 0.42;
    private double playerYSpeedOld = 0;
    private double playerYSpeed = 0;
    private int fallingTick = 0;

    public double getPlayerYSpeed(float partialTick) {
        return Mth.lerp(partialTick, playerYSpeedOld, playerYSpeed);
    }

    @Override
    public void onWorkingTickInLocalClient(Player player, Parkourability parkourability) {
        playerYSpeedOld = playerYSpeed;
        playerYSpeed = player.getDeltaMovement().y();
    }

    @Override
    public void onClientTick(Player player, Parkourability parkourability) {
        if (isDoing() && (playerYSpeed < 0 || fallingTick > 0)) {
            fallingTick++;
        } else {
            fallingTick = 0;
        }
    }

    @Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        if (player.getVehicle() != null) return false;
        boolean startInAir = player.getDeltaMovement().y() < 0
                && parkourability.getAdditionalProperties().getNotLandingTick() > 10
                && parkourability.getAdditionalProperties().getNotInWaterTick() > 30
                && KeyRecorder.keyJumpState.getTickKeyDown() > 10
                && !parkourability.get(CatLeap.class).isDoing()
                && !parkourability.get(RideZipline.class).isDoing()
                && WorldUtil.existsSpaceBelow(player);
        
        if (!(startInAir || (justJumped && WorldUtil.existsDivableSpace(player) && parkourability.get(FastRun.class).canActWithRunning(player)))) {
            justJumped = false;
            return false;
        }

        startInfo.putDouble(initialYVelocityOfLastJump);
        BufferUtil.wrap(startInfo).putBoolean(startInAir);

        justJumped = false;
        return parkourability.getActionInfo().can(Dive.class)
                && !parkourability.get(Crawl.class).isDoing()
                && !player.isVisuallyCrawling();
    }

    @Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        return !(player.isFallFlying()
                || player.getAbilities().flying
                || player.isInWater()
                || player.isInLava()
                || player.isSwimming()
                || player.onGround()
                || ((ParkourabilityAccess) player).parcool$getStamina().isExhausted()
                || parkourability.get(RideZipline.class).isDoing()
        );
    }

    @Override
    public void onJump(Player player, Parkourability parkourability) {
        initialYVelocityOfLastJump = player.getDeltaMovement().y();
        justJumped = true;
    }

    @Override
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        double initialYSpeed = startData.getDouble();
        playerYSpeedOld = playerYSpeed = initialYSpeed;
        
        com.alrex.parcool.common.attachment.client.Animation animation = com.alrex.parcool.common.attachment.client.Animation.get(player);
        if (animation != null) {
            animation.setAnimator(new com.alrex.parcool.client.animation.impl.DiveAnimationHostAnimator(initialYSpeed, BufferUtil.getBoolean(startData)));
        }
    }

    @Override
    public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        double initialYSpeed = startData.getDouble();
        playerYSpeedOld = playerYSpeed = initialYVelocityOfLastJump = initialYSpeed;
        
        com.alrex.parcool.common.attachment.client.Animation animation = com.alrex.parcool.common.attachment.client.Animation.get(player);
        if (animation != null) {
            animation.setAnimator(new com.alrex.parcool.client.animation.impl.DiveAnimationHostAnimator(initialYSpeed, BufferUtil.getBoolean(startData)));
        }
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.None;
    }

    @Override
    public void onStopInLocalClient(Player player) {
        if (player.isInWater()) {
            com.alrex.parcool.common.attachment.client.Animation animation = com.alrex.parcool.common.attachment.client.Animation.get(player);
            Parkourability parkourability = Parkourability.get(player);
            if (animation != null
                    && parkourability != null
                    && parkourability.getAdditionalProperties().getNotLandingTick() >= 5
                    && player.getDeltaMovement().y() < 0
            ) {
                animation.setAnimator(new com.alrex.parcool.client.animation.impl.DiveIntoWaterAnimator(parkourability.get(SkyDive.class).isDoing()));
            }
        }
    }

    @Override
    public void onStopInOtherClient(Player player) {
        if (player.isInWater()) {
            com.alrex.parcool.common.attachment.client.Animation animation = com.alrex.parcool.common.attachment.client.Animation.get(player);
            Parkourability parkourability = Parkourability.get(player);
            if (animation != null
                    && parkourability != null
                    && parkourability.getAdditionalProperties().getNotLandingTick() >= 5
                    && player.getDeltaMovement().y() < 0
            ) {
                animation.setAnimator(new com.alrex.parcool.client.animation.impl.DiveIntoWaterAnimator(parkourability.get(SkyDive.class).isDoing()));
            }
        }
    }

    @Override
    public void saveSynchronizedState(ByteBuffer buffer) {
        buffer.putDouble(playerYSpeed)
                .putDouble(playerYSpeedOld);
    }

    @Override
    public void restoreSynchronizedState(ByteBuffer buffer) {
        playerYSpeed = buffer.getDouble();
        playerYSpeedOld = buffer.getDouble();
    }
}
