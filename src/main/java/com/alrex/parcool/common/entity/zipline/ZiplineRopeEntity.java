package com.alrex.parcool.common.entity.zipline;

import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import com.alrex.parcool.common.block.zipline.ZiplineInfo;
import com.alrex.parcool.common.entity.EntityTypes;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.common.zipline.ZiplineType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;



public class ZiplineRopeEntity extends Entity {
    private static final EntityDataAccessor<BlockPos> DATA_START_POS = SynchedEntityData.defineId(ZiplineRopeEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<BlockPos> DATA_END_POS = SynchedEntityData.defineId(ZiplineRopeEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(ZiplineRopeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ZIP_TYPE = SynchedEntityData.defineId(ZiplineRopeEntity.class, EntityDataSerializers.INT);

    private BlockPos zipline_start;
    private BlockPos zipline_end;
    private ZiplineType zip_type;
    private Zipline zipline;

    public ZiplineRopeEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public ZiplineRopeEntity(Level world, BlockPos start, BlockPos end, ZiplineInfo info) {
        super(EntityTypes.ZIPLINE_ROPE, world);
        setStartPos(start);
        setEndPos(end);
        setColor(info.getColor());
        setZiplineType(info.getType());
        setPos((end.getX() + start.getX()) / 2.0 + 0.5, Math.min(end.getY(), start.getY()), (end.getZ() + start.getZ()) / 2.0 + 0.5);
        noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_START_POS, BlockPos.ZERO);
        builder.define(DATA_END_POS, BlockPos.ZERO);
        builder.define(DATA_COLOR, ZiplineRopeItem.DEFAULT_COLOR);
        builder.define(DATA_ZIP_TYPE, ZiplineType.STANDARD.ordinal());
    }

    public Zipline getZipline() {
        BlockPos start = getStartPos();
        BlockPos end = getEndPos();
        ZiplineType type = getZiplineType();
        if (zipline == null || !start.equals(zipline_start) || !end.equals(zipline_end) || !type.equals(zip_type)) {
            zipline_start = start;
            zipline_end = end;
            zip_type = type;
            Vec3 startVec;
            Vec3 endVec;
            BlockEntity startEntity = level().getBlockEntity(start);
            BlockEntity endEntity = level().getBlockEntity(end);
            boolean delayInit = false;
            if (startEntity instanceof ZiplineHookTileEntity) {
                startVec = ((ZiplineHookTileEntity) startEntity).getActualZiplinePoint(end);
            } else {
                startVec = new Vec3(start.getX() + 0.5, start.getY() + 0.5, start.getZ() + 0.5);
                delayInit = true;
            }
            if (endEntity instanceof ZiplineHookTileEntity) {
                endVec = ((ZiplineHookTileEntity) endEntity).getActualZiplinePoint(start);
            } else {
                endVec = new Vec3(end.getX() + 0.5, end.getY() + 0.5, end.getZ() + 0.5);
                delayInit = true;
            }
            if (delayInit) {
                return type.getZipline(startVec, endVec);
            } else {
                zipline = type.getZipline(startVec, endVec);
            }
        }
        return zipline;
    }

    public BlockPos getStartPos() { return getEntityData().get(DATA_START_POS); }
    public BlockPos getEndPos() { return getEntityData().get(DATA_END_POS); }
    private void setStartPos(BlockPos pos) { getEntityData().set(DATA_START_POS, pos); }
    private void setEndPos(BlockPos pos) { getEntityData().set(DATA_END_POS, pos); }
    public int getColor() { return getEntityData().get(DATA_COLOR); }
    private void setColor(int color) { getEntityData().set(DATA_COLOR, color); }
    public ZiplineType getZiplineType() {
        return ZiplineType.values()[getEntityData().get(DATA_ZIP_TYPE) % ZiplineType.values().length];
    }
    private void setZiplineType(ZiplineType type) { getEntityData().set(DATA_ZIP_TYPE, type.ordinal()); }

    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput input) {
        setStartPos(new BlockPos(input.getIntOr("Tile1_X", 0), input.getIntOr("Tile1_Y", 0), input.getIntOr("Tile1_Z", 0)));
        setEndPos(new BlockPos(input.getIntOr("Tile2_X", 0), input.getIntOr("Tile2_Y", 0), input.getIntOr("Tile2_Z", 0)));
        input.getInt("Color").ifPresent(this::setColor);
        input.getInt("ZipType").ifPresent(v -> setZiplineType(ZiplineType.values()[v % ZiplineType.values().length]));
    }

    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput output) {
        BlockPos start = getStartPos();
        BlockPos end = getEndPos();
        output.putInt("Tile1_X", start.getX());
        output.putInt("Tile1_Y", start.getY());
        output.putInt("Tile1_Z", start.getZ());
        output.putInt("Tile2_X", end.getX());
        output.putInt("Tile2_Y", end.getY());
        output.putInt("Tile2_Z", end.getZ());
        output.putInt("Color", getColor());
        output.putInt("ZipType", getZiplineType().ordinal());
    }

    @Override
    public void move(MoverType moverType, Vec3 vec3) {}

    @Override
    public boolean shouldRender(double x, double y, double z) {
        BlockPos start = getStartPos();
        BlockPos end = getEndPos();
        if (start.equals(BlockPos.ZERO) && end.equals(BlockPos.ZERO)) return false;
        
        double distanceSqr = this.position().distanceToSqr(x, y, z);
        return distanceSqr < Zipline.MAXIMUM_HORIZONTAL_DISTANCE * Zipline.MAXIMUM_HORIZONTAL_DISTANCE;
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float v) {
        return false;
    }
}
