package com.alrex.parcool.common.block.zipline;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class IronZiplineHookBlock extends ZiplineHookBlock {
    public static final MapCodec<IronZiplineHookBlock> CODEC = simpleCodec(IronZiplineHookBlock::new);

    public IronZiplineHookBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends IronZiplineHookBlock> codec() {
        return CODEC;
    }

    @Override
    public Vec3 getActualZiplinePoint(BlockPos pos, BlockState state) {
        return new Vec3(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
    }
}
