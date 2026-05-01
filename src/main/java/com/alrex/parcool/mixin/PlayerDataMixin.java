package com.alrex.parcool.mixin;

import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.data.ParkourabilityAccess;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerDataMixin implements ParkourabilityAccess {
    @Unique
    private Parkourability parcool$parkourability;
    @Unique
    private ReadonlyStamina parcool$stamina;

    @Override
    public Parkourability parcool$getParkourability() {
        if (parcool$parkourability == null) {
            parcool$parkourability = new Parkourability();
        }
        return parcool$parkourability;
    }

    @Override
    public ReadonlyStamina parcool$getStamina() {
        if (parcool$stamina == null) {
            parcool$stamina = ReadonlyStamina.createDefault();
        }
        return parcool$stamina;
    }

    @Override
    public void parcool$setStamina(ReadonlyStamina stamina) {
        this.parcool$stamina = stamina;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void parcool$saveData(ValueOutput output, CallbackInfo ci) {
        output.store("ParCoolStamina", ReadonlyStamina.CODEC.codec(), parcool$getStamina());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void parcool$loadData(ValueInput input, CallbackInfo ci) {
        input.read("ParCoolStamina", ReadonlyStamina.CODEC.codec()).ifPresent(s -> parcool$stamina = s);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void parcool$onTick(CallbackInfo ci) {
        com.alrex.parcool.common.action.ActionProcessor.onTick((Player)(Object)this);
    }
}
