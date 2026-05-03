package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.nio.ByteBuffer;

public class Dodge extends Action {
    public static final int MAX_TICK = 11;
    private static final BehaviorEnforcer.ID ID_JUMP_CANCEL = BehaviorEnforcer.newID();
    private static final BehaviorEnforcer.ID ID_DESCEND_EDGE = BehaviorEnforcer.newID();

    private static int getMaxCoolTime(ActionInfo info) {
        return Math.max(
                ParCoolConfig.Client.Integers.DodgeCoolTime.get(),
                ParCoolConfig.Server.Integers.DodgeCoolTime.get()
        );
    }

    private static int getMaxSuccessiveDodge(ActionInfo info) {
        return Math.min(
                ParCoolConfig.Client.Integers.MaxSuccessiveDodgeCount.get(),
                ParCoolConfig.Server.Integers.MaxSuccessiveDodgeCount.get()
        );
    }

    private static int getSuccessiveCoolTime(ActionInfo info) {
        return Math.max(
                ParCoolConfig.Client.Integers.SuccessiveDodgeCoolTime.get(),
                ParCoolConfig.Server.Integers.SuccessiveDodgeCoolTime.get()
        );
    }

    public enum DodgeDirection {
        Front, Back, Left, Right;
    }

    private int coolTime = 0;
    private int successivelyCount = 0;
    private int successivelyCoolTick = 0;

    @Override
    public void onClientTick(Player player, Parkourability parkourability) {
        if (coolTime > 0) coolTime--;
        if (successivelyCoolTick > 0) {
            successivelyCoolTick--;
        } else {
            successivelyCount = 0;
        }
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnStart;
    }

    public double getSpeedModifier(ActionInfo info) {
        return Math.min(
                ParCoolConfig.Client.Doubles.DodgeSpeedModifier.get(),
                ParCoolConfig.Server.Doubles.MaxDodgeSpeedModifier.get()
        );
    }

    @Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        boolean enabledDoubleTap = ParCoolConfig.Client.Booleans.EnableDoubleTappingForDodge.get();
        DodgeDirection direction = null;
        var dodgeVec = KeyRecorder.getLastMoveVector();
        if (enabledDoubleTap) {
            if (KeyRecorder.keyBack.isDoubleTapped()) direction = DodgeDirection.Back;
            if (KeyRecorder.keyLeft.isDoubleTapped()) direction = DodgeDirection.Left;
            if (KeyRecorder.keyRight.isDoubleTapped()) direction = DodgeDirection.Right;
        }
        if (direction == null && KeyRecorder.keyDodge.isPressed()) {
            if (KeyBindings.isKeyBackDown()) direction = DodgeDirection.Back;
            if (KeyBindings.isKeyForwardDown()) direction = DodgeDirection.Front;
            if (KeyBindings.isKeyLeftDown()) direction = DodgeDirection.Left;
            if (KeyBindings.isKeyRightDown()) direction = DodgeDirection.Right;
            if (direction != null) dodgeVec = KeyBindings.getCurrentMoveVector();
        }
        if (direction == null || dodgeVec == null) return false;
        
        startInfo.putInt(direction.ordinal());
        startInfo.putDouble(dodgeVec.x);
        startInfo.putDouble(dodgeVec.z);
        return ((parkourability.getAdditionalProperties().getLandingTick() > 5 || parkourability.getAdditionalProperties().getPreviousNotLandingTick() < 2)
                && player.onGround()
                && !isInSuccessiveCoolDown(parkourability.getActionInfo())
                && coolTime <= 0
                && !player.isInWater()
                && !player.isShiftKeyDown()
                && !parkourability.get(Crawl.class).isDoing()
                && !parkourability.get(Roll.class).isDoing()
                && !parkourability.get(Tap.class).isDoing()
        );
    }

    @Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        return !(parkourability.get(Roll.class).isDoing()
                || parkourability.get(ClingToCliff.class).isDoing()
                || getDoingTick() >= MAX_TICK
                || player.isInWater()
                || player.isFallFlying()
                || player.getAbilities().flying
        );
    }

    @Override
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        DodgeDirection direction = DodgeDirection.values()[startData.getInt()];
        var dodgeVec = new Vec3(startData.getDouble(), 0, startData.getDouble());
        coolTime = getMaxCoolTime(parkourability.getActionInfo());
        if (successivelyCount < getMaxSuccessiveDodge(parkourability.getActionInfo())) {
            successivelyCount++;
        }
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get()) {
            player.playSound(SoundEvents.DODGE.value(), 1f, 1f);
        }
        successivelyCoolTick = getSuccessiveCoolTime(parkourability.getActionInfo());

        if (!player.onGround()) return;
        var cameraEntity = Minecraft.getInstance().getCameraEntity();
        var cameraYRot = cameraEntity != null ? cameraEntity.getYRot() : 0;
        dodgeVec = VectorUtil.rotateYDegrees(dodgeVec, cameraYRot);
        dodgeVec = dodgeVec.scale(0.9 * getSpeedModifier(parkourability.getActionInfo()));
        player.setDeltaMovement(dodgeVec);

        com.alrex.parcool.common.attachment.client.Animation animation = com.alrex.parcool.common.attachment.client.Animation.get(player);
        if (animation != null) animation.setAnimator(new com.alrex.parcool.client.animation.impl.DodgeAnimator(direction));

        parkourability.getBehaviorEnforcer().addMarkerCancellingJump(ID_JUMP_CANCEL, this::isDoing);
        if (!ParCoolConfig.Client.Booleans.CanGetOffStepsWhileDodge.get()) {
            parkourability.getBehaviorEnforcer().addMarkerCancellingDescendFromEdge(ID_DESCEND_EDGE, this::isDoing);
        }
    }

    @Override
    public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        DodgeDirection direction = DodgeDirection.values()[startData.getInt()];
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get())
            player.playSound(SoundEvents.DODGE.value(), 1f, 1f);

        com.alrex.parcool.common.attachment.client.Animation animation = com.alrex.parcool.common.attachment.client.Animation.get(player);
        if (animation != null) animation.setAnimator(new com.alrex.parcool.client.animation.impl.DodgeAnimator(direction));
    }

    public boolean isInSuccessiveCoolDown(ActionInfo info) {
        return successivelyCount >= getMaxSuccessiveDodge(info);
    }

    @Override
    public boolean wantsToShowStatusBar(Player player, Parkourability parkourability) {
        return coolTime > 0 || isInSuccessiveCoolDown(parkourability.getActionInfo());
    }

    @Override
    public float getStatusValue(Player player, Parkourability parkourability) {
        ActionInfo info = parkourability.getActionInfo();
        int maxCoolTime = getMaxCoolTime(info);
        int successiveMaxCoolTime = getSuccessiveCoolTime(info);
        return Math.max(
                (float) coolTime / maxCoolTime,
                isInSuccessiveCoolDown(info) ? (float) (successivelyCoolTick) / (successiveMaxCoolTime) : 0
        );
    }
}
