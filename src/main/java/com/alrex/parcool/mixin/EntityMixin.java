package com.alrex.parcool.mixin;

import com.alrex.parcool.common.action.impl.HideInBlock;
import com.alrex.parcool.common.data.ForcedPoseAccess;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements ForcedPoseAccess {

    @Unique
    private Pose parcool$forcedPose;

    @Shadow
    public abstract void refreshDimensions();

    @Override
    public void parcool$setForcedPose(Pose pose) {
        if (this.parcool$forcedPose != pose) {
            this.parcool$forcedPose = pose;
            this.refreshDimensions();
        }
    }

    @Override
    public boolean parcool$canEnterPose(Pose pose) {
        if (pose == Pose.STANDING) {
            AABB bb = this.getBoundingBox();
            // Check if standing height (approx 1.8) fits
            AABB standingBox = new AABB(bb.minX + 1.0E-7, bb.minY + 1.0E-7, bb.minZ + 1.0E-7, bb.maxX - 1.0E-7, bb.minY + 1.8, bb.maxZ - 1.0E-7);
            return ((Entity)(Object)this).level().noCollision((Entity)(Object)this, standingBox);
        }
        return true;
    }

    @Inject(method = "getPose", at = @At("HEAD"), cancellable = true)
    private void parcool$getPose(CallbackInfoReturnable<Pose> cir) {
        if (parcool$forcedPose != null) {
            cir.setReturnValue(parcool$forcedPose);
        }
    }

    @Shadow
    public abstract void setBoundingBox(AABB bb);

    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    public abstract void setPos(double x, double y, double z);

    @Shadow
    public boolean noPhysics;

    // onGetEyeHeight removed due to potential compatibility issues in 1.21.2+

    // onIsInWall removed due to potential compatibility issues

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void onMove(MoverType type, Vec3 pos, CallbackInfo ci) {
        if (!((Object) this instanceof Player player)) return;
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        var enforcedPos = parkourability.getBehaviorEnforcer().getEnforcedPosition();
        if (enforcedPos != null) {
            ci.cancel();
            var dMove = enforcedPos.subtract(player.position());
            noPhysics = true;
            setBoundingBox(getBoundingBox().move(dMove));
            setPos(player.getX() + dMove.x, player.getY() + dMove.y, player.getZ() + dMove.z);
        }
    }

    // onOnGround removed due to potential compatibility issues
}
