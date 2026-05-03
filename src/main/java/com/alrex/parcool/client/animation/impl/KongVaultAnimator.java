package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.client.data.CameraAccess;
import com.alrex.parcool.common.action.impl.Vault;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.Easing;
import com.alrex.parcool.utilities.EasingFunctions;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class KongVaultAnimator extends Animator {
    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return getTick() >= Vault.MAX_TICK;
    }

    private float getFactor(float phase) {
        if (phase < 0.5) {
            return EasingFunctions.SinInOutBySquare(phase * 2);
        } else {
            return EasingFunctions.SinInOutBySquare(2 - phase * 2);
        }
    }

    @Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        float phase = (getTick() + rotator.getPartialTick()) / Vault.MAX_TICK;
        float factor = getFactor(phase);
        float yFactor = new Easing(phase)
                .squareOut(0, 0.45f, 0, 1)
                .squareIn(0.45f, 1, 1, 0)
                .get();
        rotator
                .startBasedCenter()
                .translateY(-yFactor * player.getBbHeight() / 4.5f)
                .rotatePitchFrontward(factor * 110)
                .end();
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float phase = (getTick() + transformer.getPartialTick()) / Vault.MAX_TICK;
        float legXFactor = new Easing(phase)
                .sinInOut(0, 0.2f, 0, -0.3f)
                .sinInOut(0.2f, 0.4f, -0.3f, 1)
                .sinInOut(0.4f, 0.85f, 1, -0.3f)
                .sinInOut(0.85f, 1, -0.3f, 0)
                .get();
        float animFactor = new Easing(phase)
                .sinInOut(0, 0.25f, 0, 1)
                .linear(0.25f, 0.75f, 1, 1)
                .sinInOut(0.75f, 1, 1, 0)
                .get();
        float headPitchFactor = new Easing(phase)
                .sinInOut(0, 0.10f, 0, -0.2f)
                .sinInOut(0.10f, 0.25f, -0.2f, 1)
                .sinInOut(0.25f, 1, 1f, 0)
                .get();
        float armZFactor = new Easing(phase)
                .sinInOut(0, 0.4f, 0, 1)
                .sinInOut(0.4f, 1, 1, 0)
                .get();
        float armXFactor = new Easing(phase)
                .sinInOut(0, 0.5f, 0, 1)
                .sinInOut(0.5f, 1, 1, 0)
                .get();
        transformer
                .translateLeftArm(-0.6f * armZFactor, 0, 0)
                .translateRightArm(0.6f * armZFactor, 0, 0)
                .rotateRightLeg((float) Math.toRadians(legXFactor * -90), 0, 0, animFactor)
                .rotateLeftLeg((float) Math.toRadians(legXFactor * -90), 0, 0, animFactor)
                .rotateRightArm((float) Math.toRadians(-170 * armXFactor), 0, (float) Math.toRadians(15 * armZFactor), animFactor)
                .rotateLeftArm((float) Math.toRadians(-170 * armXFactor), 0, (float) Math.toRadians(-15 * armZFactor), animFactor)
                .rotateAdditionallyHeadPitch(40 * headPitchFactor)
                .end();
    }

    @Override
    public void onCameraSetUp(float tickDelta, Camera camera, Player clientPlayer, Parkourability parkourability) {
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson() ||
                !ParCoolConfig.Client.Booleans.EnableCameraAnimationOfVault.get()) return;
        float phase = (float) ((getTick() + tickDelta) / Vault.MAX_TICK);
        float factor = getFactor(phase);
        CameraAccess access = (CameraAccess) camera;
        access.parcool$setPitch(access.parcool$getXRot() + 25 * factor);
    }
}
