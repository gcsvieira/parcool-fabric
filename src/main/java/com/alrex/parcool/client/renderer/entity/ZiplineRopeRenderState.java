package com.alrex.parcool.client.renderer.entity;

import com.alrex.parcool.common.zipline.Zipline;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.BlockPos;

public class ZiplineRopeRenderState extends EntityRenderState {
    public double x, y, z;
    public BlockPos startPos = BlockPos.ZERO;
    public BlockPos endPos = BlockPos.ZERO;
    public int color;
    public Zipline zipline;
    public int startBlockLightLevel;
    public int endBlockLightLevel;
    public int startSkyBrightness;
    public int endSkyBrightness;
}
