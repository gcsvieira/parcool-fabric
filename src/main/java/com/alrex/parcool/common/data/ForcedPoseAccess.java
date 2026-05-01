package com.alrex.parcool.common.data;

import net.minecraft.world.entity.Pose;

public interface ForcedPoseAccess {
    void parcool$setForcedPose(Pose pose);
    boolean parcool$canEnterPose(Pose pose);
}
