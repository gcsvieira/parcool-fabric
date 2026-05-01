package com.alrex.parcool.common.block.zipline;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.context.BlockPlaceContext;

public class IronZiplineHookBlock extends ZiplineHookBlock {
    public static final MapCodec<IronZiplineHookBlock> CODEC = simpleCodec(IronZiplineHookBlock::new);
    public static final BooleanProperty ORTHOGONAL = BooleanProperty.create("orthogonal");

    public IronZiplineHookBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(ORTHOGONAL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ORTHOGONAL);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(ORTHOGONAL, false); // Simplistic for now
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
