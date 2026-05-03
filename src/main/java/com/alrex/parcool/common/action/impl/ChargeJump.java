package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.nio.ByteBuffer;

public class ChargeJump extends Action {
    public static final int JUMP_ANIMATION_TICK = 10;
    public static final int JUMP_MAX_CHARGE_TICK = 18;
    private int chargeTick = 0;
    private int lastChargeTick = 0;
    private int notChargeTick = 0;
    private int coolTimeTick = 0;
    private boolean started = false;

    @Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        boolean start = started;
        started = false;
        return start;
    }

    @Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        return getDoingTick() < JUMP_ANIMATION_TICK;
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnStart;
    }

    @Override
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        coolTimeTick = 30;
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
            player.playSound(SoundEvents.CHARGE_JUMP.value(), 1, 1);
        
        com.alrex.parcool.common.attachment.client.Animation animation = com.alrex.parcool.common.attachment.client.Animation.get(player);
        if (animation != null) animation.setAnimator(new com.alrex.parcool.client.animation.impl.ChargeJumpAnimator());
    }

    @Override
    public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
            player.playSound(SoundEvents.CHARGE_JUMP.value(), 1, 1);
        
        com.alrex.parcool.common.attachment.client.Animation animation = com.alrex.parcool.common.attachment.client.Animation.get(player);
        if (animation != null) animation.setAnimator(new com.alrex.parcool.client.animation.impl.ChargeJumpAnimator());
    }

    @Override
    public void onClientTick(Player player, Parkourability parkourability) {
        if (player instanceof LocalPlayer cp) {
            if (cp.onGround()
                    && coolTimeTick <= 0
                    && parkourability.getActionInfo().can(ChargeJump.class)
                    && !cp.isVisuallyCrawling()
                    && !cp.isSprinting()
                    && !cp.isInWater()
                    && !KeyBindings.isKeyForwardDown()
                    && !KeyBindings.isKeyBackDown()
                    && !KeyBindings.isKeyRightDown()
                    && !KeyBindings.isKeyLeftDown()
                    && !parkourability.get(Crawl.class).isDoing()
            ) {
                if (cp.isShiftKeyDown() && KeyRecorder.keySneak.getPreviousTickNotKeyDown() > 5) {
                    chargeTick++;
                    if (chargeTick > JUMP_MAX_CHARGE_TICK) chargeTick = JUMP_MAX_CHARGE_TICK;
                    lastChargeTick = chargeTick;
                    notChargeTick = 0;
                } else {
                    chargeTick--;
                    if (chargeTick < 0) chargeTick = 0;
                    notChargeTick++;
                }
                if (isCharging()) {
                    Vec3 targetAngle = VectorUtil.fromYawDegree(player.yHeadRot);
                    Vec3 currentAngle = VectorUtil.fromYawDegree(player.yBodyRot);
                    double differenceAngle = Math.atan(
                            (currentAngle.x() * targetAngle.z() - targetAngle.x() * currentAngle.z())
                                    / (targetAngle.x() * currentAngle.x() + targetAngle.z() * currentAngle.z())
                    );
                    player.setYBodyRot((float) VectorUtil.toYawDegree(currentAngle.yRot((float) (-differenceAngle / 2))));
                    
                    com.alrex.parcool.common.attachment.client.Animation animation = com.alrex.parcool.common.attachment.client.Animation.get(player);
                    if (animation != null && !(animation.getAnimator() instanceof com.alrex.parcool.client.animation.impl.JumpChargingAnimator)) {
                        animation.setAnimator(new com.alrex.parcool.client.animation.impl.JumpChargingAnimator());
                    }
                }
            } else {
                if (coolTimeTick > 0) coolTimeTick--;
                chargeTick = 0;
                notChargeTick++;
            }
        }
    }

    @Override
    public void onJump(Player player, Parkourability parkourability) {
        double power = chargeTick / (double) JUMP_MAX_CHARGE_TICK;
        if (power >= 0.5) {
            started = true;
        } else {
            power = lastChargeTick / (double) JUMP_MAX_CHARGE_TICK;
            if (power > 0.5 && notChargeTick < 5) {
                started = true;
            }
        }
        if (started) {
            player.setDeltaMovement(player.getDeltaMovement().add(0, 0.160 * power, 0));
        }
    }

    @Override
    public void onLand(Player player, Parkourability parkourability, float fallDistance) {
        if (player.isLocalPlayer() && player instanceof LocalPlayer cp) {
            if (
                    parkourability.getActionInfo().can(ChargeJump.class)
                            && coolTimeTick <= 0
                            && !KeyBindings.isKeyForwardDown()
                            && !KeyBindings.isKeyBackDown()
                            && !KeyBindings.isKeyRightDown()
                            && !KeyBindings.isKeyLeftDown()
                            && (parkourability.get(FastRun.class).getNotDashTick(parkourability.getAdditionalProperties()) < 15)
            ) {
                chargeTick = JUMP_MAX_CHARGE_TICK + 5;
                lastChargeTick = chargeTick;
                notChargeTick = 0;
            }
        }
    }

    @Override
    public boolean wantsToShowStatusBar(Player player, Parkourability parkourability) {
        return isCharging();
    }

    @Override
    public float getStatusValue(Player player, Parkourability parkourability) {
        return ((float) getChargingTick()) / JUMP_MAX_CHARGE_TICK;
    }

    @Override
    public void saveSynchronizedState(ByteBuffer buffer) {
        buffer.putInt(chargeTick);
    }

    @Override
    public void restoreSynchronizedState(ByteBuffer buffer) {
        chargeTick = buffer.getInt();
    }

    public boolean isCharging() {
        return chargeTick > 0 && !isDoing();
    }

    public int getChargingTick() {
        return chargeTick;
    }

    public int getNotChargingTick() {
        return notChargeTick;
    }
}
