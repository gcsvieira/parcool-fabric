package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.data.CameraAccess;
import net.minecraft.client.Camera;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin implements CameraAccess {
    @Shadow
    private float xRot;
    @Shadow
    private float yRot;

    @Shadow
    @Final
    private Quaternionf rotation;

    @Unique
    private float parCool$roll = 0;

    @Inject(method = "setup", at = @At("RETURN"))
    private void onSetup(CallbackInfo ci) {
        if (parCool$roll != 0) {
            rotation.rotateZ((float) Math.toRadians(parCool$roll));
        }
    }

    @Override
    public void parcool$setRoll(float roll) {
        this.parCool$roll = roll;
    }

    @Override
    public float parcool$getRoll() {
        return this.parCool$roll;
    }

    @Override
    public void parcool$setPitch(float pitch) {
        this.xRot = pitch;
    }

    @Override
    public void parcool$setYaw(float yaw) {
        this.yRot = yaw;
    }

    @Override
    public float parcool$getXRot() {
        return this.xRot;
    }

    @Override
    public float parcool$getYRot() {
        return this.yRot;
    }
}
