package com.alrex.parcool.common.block.zipline;

import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.NotNull;

public class WoodenZiplineHookBlock extends ZiplineHookBlock {
    public static final MapCodec<WoodenZiplineHookBlock> CODEC = simpleCodec(WoodenZiplineHookBlock::new);

    public WoodenZiplineHookBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends WoodenZiplineHookBlock> codec() {
        return CODEC;
    }
}
