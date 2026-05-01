package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.data.ForcedPoseAccess;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;

import java.nio.ByteBuffer;

public class Crawl extends Action {
    @Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        return KeyRecorder.keyCrawlState.isPressed() && player.onGround();
    }

    @Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        return KeyBindings.getKeyCrawl().isDown() || !((ForcedPoseAccess) player).parcool$canEnterPose(Pose.STANDING);
    }

    @Override
    public void onWorkingTick(Player player, Parkourability parkourability) {
        ((ForcedPoseAccess) player).parcool$setForcedPose(Pose.SWIMMING);
    }

    @Override
    public void onStop(Player player) {
        ((ForcedPoseAccess) player).parcool$setForcedPose(null);
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.None;
    }
}
