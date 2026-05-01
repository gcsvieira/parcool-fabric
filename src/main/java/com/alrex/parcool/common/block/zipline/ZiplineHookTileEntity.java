package com.alrex.parcool.common.block.zipline;

import com.alrex.parcool.common.block.TileEntities;
import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import com.alrex.parcool.common.item.Items;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class ZiplineHookTileEntity extends BlockEntity {

    private final TreeMap<BlockPos, ZiplineInfo> connections = new TreeMap<>();
    private final TreeMap<BlockPos, ZiplineRopeEntity> connectionEntities = new TreeMap<>();

    public ZiplineHookTileEntity(BlockPos pos, BlockState state) {
        super(TileEntities.ZIPLINE_HOOK, pos, state);
    }

    public Set<BlockPos> getConnectionPoints() {
        return connections.keySet();
    }

    private TreeMap<BlockPos, ZiplineInfo> getConnectionInfo() {
        return connections;
    }

    public List<ItemStack> removeAllConnection() {
        if (level == null) return Collections.emptyList();
        getConnectionPoints().stream()
                .filter(level::isLoaded)
                .map(level::getBlockEntity)
                .map(it -> it instanceof ZiplineHookTileEntity ? (ZiplineHookTileEntity) it : null)
                .filter(Objects::nonNull)
                .forEach(it -> it.onPairHookRegistrationRemoved(this));
        
        List<ItemStack> itemStacks = Collections.emptyList();
        if (!level.isClientSide()) {
            connectionEntities.values().forEach((it) -> it.remove(Entity.RemovalReason.DISCARDED));
            itemStacks = getConnectionInfo().values().stream().map(it -> {
                ItemStack stack = new ItemStack(Items.ZIPLINE_ROPE);
                ZiplineRopeItem.setColor(stack, it.getColor());
                return stack;
            }).collect(Collectors.toList());
        }
        connectionEntities.clear();
        getConnectionInfo().clear();
        setChanged();
        return itemStacks;
    }

    private void onPairHookRegistrationRemoved(ZiplineHookTileEntity removedPair) {
        connections.remove(removedPair.getBlockPos());
        connectionEntities.remove(removedPair.getBlockPos());
        setChanged();
    }

    private void onPairHookUnloaded(ZiplineHookTileEntity removedPair) {
        connectionEntities.remove(removedPair.getBlockPos());
    }

    public void onChunkUnloaded() {
        if (level != null) {
            getConnectionPoints().stream()
                    .filter(level::isLoaded)
                    .map(level::getBlockEntity)
                    .map(it -> it instanceof ZiplineHookTileEntity ? (ZiplineHookTileEntity) it : null)
                    .filter(Objects::nonNull)
                    .forEach(it -> it.onPairHookUnloaded(this));
            if (!level.isClientSide()) {
                connectionEntities.values().forEach((it) -> it.remove(Entity.RemovalReason.DISCARDED));
            }
            connectionEntities.clear();
        }
    }

    public Vec3 getActualZiplinePoint(@Nullable BlockPos connected) {
        if (level == null)
            return new Vec3(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5);
        BlockState state = level.getBlockState(this.getBlockPos());
        Block block = state.getBlock();
        if (block instanceof ZiplineHookBlock) {
            return ((ZiplineHookBlock) block).getActualZiplinePoint(this.getBlockPos(), state);
        }
        return new Vec3(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5);
    }

    public boolean connectTo(ZiplineHookTileEntity target, ZiplineInfo info) {
        if (this == target) return false;

        if (level != null && !level.isClientSide()) {
            if (this.getConnectionPoints().stream().anyMatch(target.getBlockPos()::equals)) {
                return false;
            }
            ZiplineRopeEntity ropeEntity = spawnRope(level, target, info);
            if (ropeEntity != null) {
                this.getConnectionInfo().put(target.getBlockPos(), info);
                this.setChanged();
                target.getConnectionInfo().put(this.getBlockPos(), info);
                target.setChanged();

                return true;
            }
        }
        return false;
    }

    @Nullable
    private ZiplineRopeEntity spawnRope(Level level, ZiplineHookTileEntity target, ZiplineInfo info) {
        if (level.isClientSide()) return null;
        if (target.connectionEntities.containsKey(this.getBlockPos())) return null;

        ZiplineRopeEntity entity = new ZiplineRopeEntity(level, getBlockPos(), target.getBlockPos(), info);
        boolean result = level.addFreshEntity(entity);
        if (result) {
            this.connectionEntities.put(target.getBlockPos(), entity);
            target.connectionEntities.put(this.getBlockPos(), entity);
        }
        return result ? entity : null;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        var connectionsList = output.childrenList("Connection");
        for (Map.Entry<BlockPos, ZiplineInfo> entry : connections.entrySet()) {
            var entryTag = connectionsList.addChild();
            BlockPos rel = entry.getKey().subtract(worldPosition);
            entryTag.putInt("rX", rel.getX());
            entryTag.putInt("rY", rel.getY());
            entryTag.putInt("rZ", rel.getZ());
            entryTag.store("Info", ZiplineInfo.CODEC, entry.getValue());
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.childrenList("Connection").ifPresent(list -> {
            connections.clear();
            for (ValueInput entryTag : list) {
                BlockPos pos;
                int rX = entryTag.getIntOr("rX", Integer.MIN_VALUE);
                if (rX != Integer.MIN_VALUE) {
                    pos = worldPosition.offset(rX, entryTag.getIntOr("rY", 0), entryTag.getIntOr("rZ", 0));
                } else {
                    int x = entryTag.getIntOr("X", Integer.MIN_VALUE);
                    if (x != Integer.MIN_VALUE) {
                        pos = new BlockPos(x, entryTag.getIntOr("Y", 0), entryTag.getIntOr("Z", 0));
                    } else continue;
                }

                entryTag.read("Info", ZiplineInfo.CODEC).ifPresent(info -> connections.put(pos, info));
            }
        });
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ZiplineHookTileEntity self) {
        if (level != null && !level.isClientSide()) {
            self.connectionEntities.values().removeIf(it -> !it.isAlive());
            if (self.connectionEntities.size() < self.connections.size()) {
                List<ZiplineHookTileEntity> tileEntities = self.connections.keySet()
                        .stream()
                        .filter(it -> !self.connectionEntities.containsKey(it))
                        .filter(level::isLoaded)
                        .map(level::getBlockEntity)
                        .map(it -> it instanceof ZiplineHookTileEntity ? (ZiplineHookTileEntity) it : null)
                        .filter(Objects::nonNull)
                        .toList();
                tileEntities.forEach(it -> {
                    if (it.getConnectionPoints().contains(self.getBlockPos())) {
                        self.spawnRope(level, it, self.connections.get(it.getBlockPos()));
                    } else {
                        self.connections.remove(it.getBlockPos());
                        self.setChanged();
                    }
                });
            }
        }
    }
}
