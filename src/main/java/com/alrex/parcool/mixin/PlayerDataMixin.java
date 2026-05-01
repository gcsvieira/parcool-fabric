package com.alrex.parcool.mixin;

import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.data.ParkourabilityAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerDataMixin implements ParkourabilityAccess {
    @Unique
    private final Parkourability parcool$parkourability = new Parkourability();
    @Unique
    private ReadonlyStamina parcool$stamina = ReadonlyStamina.createDefault();

    @Override
    public Parkourability parcool$getParkourability() {
        return parcool$parkourability;
    }

    @Override
    public ReadonlyStamina parcool$getStamina() {
        return parcool$stamina;
    }

    @Override
    public void parcool$setStamina(ReadonlyStamina stamina) {
        this.parcool$stamina = stamina;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void parcool$saveData(ValueOutput output, CallbackInfo ci) {
        output.store("ParCoolStamina", ReadonlyStamina.CODEC.codec(), parcool$stamina);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void parcool$loadData(ValueInput input, CallbackInfo ci) {
        input.read("ParCoolStamina", ReadonlyStamina.CODEC.codec()).ifPresent(s -> parcool$stamina = s);
    }
}
