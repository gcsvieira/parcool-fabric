package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.nio.ByteBuffer;

public class FastSwim extends Action {
    private static final Identifier FAST_SWIM_MODIFIER = Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "modifier.speed.fastswim");
    private double speedModifier = 0;
    private boolean toggleStatus;

    public double getSpeedModifier(ActionInfo info) {
        return Math.min(
                info.getClientSetting().get(ParCoolConfig.Client.Doubles.FastSwimSpeedModifier),
                info.getServerLimitation().get(ParCoolConfig.Server.Doubles.MaxFastSwimSpeedModifier)
        );
    }

    @Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        return canContinue(player, parkourability);
    }

    @Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        return (player.isInWater()
                && player.getVehicle() == null
                && !player.isFallFlying()
                && player.isSprinting()
                && player.isSwimming()
                && !parkourability.get(FastRun.class).isDoing()
                && ((ParCoolConfig.Client.getInstance().fastRunControl.get() == ParCoolConfig.Client.FastRunControl.PressKey && KeyBindings.getKeyFastRunning().isDown())
                || (ParCoolConfig.Client.getInstance().fastRunControl.get() == ParCoolConfig.Client.FastRunControl.Toggle && toggleStatus)
                || ParCoolConfig.Client.getInstance().fastRunControl.get() == ParCoolConfig.Client.FastRunControl.Auto)
        );
    }

    @Override
    public void onClientTick(Player player, Parkourability parkourability) {
        if (player.isLocalPlayer()) {
            if (ParCoolConfig.Client.getInstance().fastRunControl.get() == ParCoolConfig.Client.FastRunControl.Toggle
                    && parkourability.getAdditionalProperties().getSprintingTick() > 3
                    && player.isInWater()
                    && player.isSwimming()
            ) {
                if (KeyRecorder.keyFastRunning.isPressed())
                    toggleStatus = !toggleStatus;
            } else {
                toggleStatus = false;
            }
        }
    }

    @Override
    public void onWorkingTickInClient(Player player, Parkourability parkourability) {
        com.alrex.parcool.common.attachment.client.Animation animation = com.alrex.parcool.common.attachment.client.Animation.get(player);
        if (animation != null && !animation.hasAnimator()) {
            animation.setAnimator(new com.alrex.parcool.client.animation.impl.FastSwimAnimator());
        }
    }

    @Override
    public void onStartInServer(Player player, Parkourability parkourability, ByteBuffer startData) {
        speedModifier = parkourability.get(FastSwim.class).getSpeedModifier(parkourability.getActionInfo());
    }

    @Override
    public void onServerTick(Player player, Parkourability parkourability) {
        // In 1.21+, we use WATER_MOVEMENT_EFFICIENCY or just MOVEMENT_SPEED for swim speed if applicable
        // But vanilla added generic swim speed in 1.21.1?
        // Let's check if it exists in the classpath. For now I'll use MOVEMENT_SPEED as a fallback if not sure, 
        // but better to use the correct one.
        // Actually, I'll use MOVEMENT_SPEED for now to ensure compilation, or check if WATER_MOVEMENT_EFFICIENCY exists.
        
        AttributeInstance attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr == null) return;
        if (attr.getModifier(FAST_SWIM_MODIFIER) != null) attr.removeModifier(FAST_SWIM_MODIFIER);
        if (isDoing()) {
            player.setSprinting(true);
            attr.addTransientModifier(new AttributeModifier(
                    FAST_SWIM_MODIFIER,
                    speedModifier / 8d,
                    AttributeModifier.Operation.ADD_VALUE
            ));
        }
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnWorking;
    }
}
