package com.alrex.parcool.mixin;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.action.impl.ClimbPoles;
import com.alrex.parcool.common.action.impl.ClimbUp;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.jetbrains.annotations.NotNull;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	public LivingEntityMixin(EntityType<?> p_i48580_1_, Level p_i48580_2_) {
		super(p_i48580_1_, p_i48580_2_);
	}

	@Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
	public void onJumpFromGround(CallbackInfo ci) {
		if (!((Object) this instanceof Player player)) return;
		Parkourability parkourability = Parkourability.get(player);
		if (parkourability == null) return;
		if (parkourability.getBehaviorEnforcer().cancelJump()) {
			ci.cancel();
		}
	}

	@Inject(method = "Lnet/minecraft/world/entity/LivingEntity;onClimbable()Z", at = @At("HEAD"), cancellable = true)
	public void onClimbable(CallbackInfoReturnable<Boolean> cir) {
		if (this.isSpectator()) {
			cir.setReturnValue(false);
		} else {
			LivingEntity entity = (LivingEntity) (Object) this;
			if (!(entity instanceof Player player)) {
				return;
			}
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability == null) {
				return;
			}
			if (!parkourability.getActionInfo().can(ClimbPoles.class)) {
				return;
			}
			/*
			if (parkourability.get(ClimbUp.class).isDoing()) {
				return;
			}
			ChargeJump chargeJump = parkourability.get(ChargeJump.class);
			if (chargeJump.isDoing() || chargeJump.isCharging()) {
				return;
			}
			*/
			BlockPos blockpos = this.blockPosition();
			BlockState blockstate = this.getBlockStateOn();
			boolean onLadder = parCool$isLivingOnCustomLadder(blockstate, entity.level(), blockpos, entity);
			if (onLadder) {
				cir.setReturnValue(true);
			}
		}
	}

	@Unique
	public boolean parCool$isLivingOnCustomLadder(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull LivingEntity entity) {
		boolean isSpectator = (entity instanceof Player && entity.isSpectator());
		if (isSpectator) return false;
		
        return parCool$isCustomLadder(state, world, pos, entity);
	}

	@Unique
	private boolean parCool$isCustomLadder(BlockState state, Level world, BlockPos pos, LivingEntity entity) {
		Block block = state.getBlock();
		if (block instanceof CrossCollisionBlock) {
			int zCount = 0;
			int xCount = 0;
			if (state.getValue(CrossCollisionBlock.NORTH)) zCount++;
			if (state.getValue(CrossCollisionBlock.SOUTH)) zCount++;
			if (state.getValue(CrossCollisionBlock.EAST)) xCount++;
			if (state.getValue(CrossCollisionBlock.WEST)) xCount++;
			boolean stacked = world.isLoaded(pos.above()) && world.getBlockState(pos.above()).getBlock() instanceof CrossCollisionBlock;
			if (!stacked && world.isLoaded(pos.below()) && world.getBlockState(pos.below()).getBlock() instanceof CrossCollisionBlock)
				stacked = true;

			return ((zCount + xCount <= 1) || (zCount == 1 && xCount == 1)) && stacked;
		} else if (block instanceof RotatedPillarBlock) {
			boolean stacked = world.isLoaded(pos.above()) && world.getBlockState(pos.above()).getBlock() instanceof RotatedPillarBlock;
			if (!stacked && world.isLoaded(pos.below()) && world.getBlockState(pos.below()).getBlock() instanceof RotatedPillarBlock)
				stacked = true;
			return !state.isCollisionShapeFullBlock(world, pos) && stacked && state.getValue(RotatedPillarBlock.AXIS).isVertical();
		} else if (block instanceof EndRodBlock) {
			Direction direction = state.getValue(DirectionalBlock.FACING);
			return !state.isCollisionShapeFullBlock(world, pos) && (direction == Direction.UP || direction == Direction.DOWN);
		}
		return state.getTags().anyMatch(it -> it.equals(BlockTags.POLE_CLIMBABLE));
	}

	@Inject(method = "setSprinting", at = @At("HEAD"), cancellable = true)
	public void onSetSprinting(boolean sprint, CallbackInfo ci) {
		if (!((Object) this instanceof Player player)) return;
		if (player.isLocalPlayer()) {
			Parkourability parkourability = Parkourability.get(player);
			if (parkourability != null && parkourability.getBehaviorEnforcer().cancelSprint()) {
				ci.cancel();
			}
		}
	}
}
