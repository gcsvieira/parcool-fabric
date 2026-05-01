package com.alrex.parcool.common.entity.zipline;

import com.alrex.parcool.common.zipline.Zipline;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class ZiplineRopeEntity extends Entity {
    public ZiplineRopeEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public BlockPos getStartPos() { return BlockPos.ZERO; }
    public BlockPos getEndPos() { return BlockPos.ZERO; }
    public abstract Zipline getZipline();
}
