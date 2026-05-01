package com.alrex.parcool.common.attachment.client;

import com.alrex.parcool.api.Effects;


import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import com.alrex.parcool.common.stamina.StaminaType;
import com.alrex.parcool.common.stamina.handlers.InfiniteStaminaHandler;
import net.minecraft.client.player.LocalPlayer;

import org.jetbrains.annotations.Nullable;

//@OnlyIn(Dist.CLIENT)
public class LocalStamina {
    @Nullable
    private StaminaType currentType = null;
    @Nullable
    private IParCoolStaminaHandler handler = null;

    private static final java.util.WeakHashMap<LocalPlayer, LocalStamina> INSTANCES = new java.util.WeakHashMap<>();

    public static LocalStamina get(LocalPlayer player) {
        return INSTANCES.computeIfAbsent(player, p -> new LocalStamina());
    }

    public boolean isAvailable() {
        return handler != null && currentType != null;
    }

    public boolean isInfinite(LocalPlayer player) {
        return player.isCreative() || player.isSpectator() || handler instanceof InfiniteStaminaHandler;
    }

    public void changeType(LocalPlayer player, StaminaType type) {
        currentType = type;
        handler = type.newHandler(player);
        ((com.alrex.parcool.common.data.ParkourabilityAccess) player).parcool$setStamina(handler.initializeStamina(player, ((com.alrex.parcool.common.data.ParkourabilityAccess) player).parcool$getStamina()));
    }

    @Nullable
    public IParCoolStaminaHandler getHandler() {
        return handler;
    }

    public boolean isExhausted(LocalPlayer player) {
        return ((com.alrex.parcool.common.data.ParkourabilityAccess) player).parcool$getStamina().isExhausted();
    }

    public int getValue(LocalPlayer player) {
        return ((com.alrex.parcool.common.data.ParkourabilityAccess) player).parcool$getStamina().value();
    }

    public int getMax(LocalPlayer player) {
        return ((com.alrex.parcool.common.data.ParkourabilityAccess) player).parcool$getStamina().max();
    }

    public void consume(LocalPlayer player, int value) {
        if (player.isCreative() || player.isSpectator()) return;
        if (handler == null) return;
        if (isInfinite(player)) return;
        if (player.hasEffect(Effects.INEXHAUSTIBLE)) return;
        ((com.alrex.parcool.common.data.ParkourabilityAccess) player).parcool$setStamina(handler.consume(player, ((com.alrex.parcool.common.data.ParkourabilityAccess) player).parcool$getStamina(), value)
        );
    }

    public void recover(LocalPlayer player, int value) {
        if (player.isCreative() || player.isSpectator()) return;
        if (handler == null) return;
        ((com.alrex.parcool.common.data.ParkourabilityAccess) player).parcool$setStamina(handler.recover(player, ((com.alrex.parcool.common.data.ParkourabilityAccess) player).parcool$getStamina(), value)
        );
    }

    public void onTick(LocalPlayer player) {
        if (handler == null) return;
        ((com.alrex.parcool.common.data.ParkourabilityAccess) player).parcool$setStamina(handler.onTick(player, ((com.alrex.parcool.common.data.ParkourabilityAccess) player).parcool$getStamina())
        );
    }

    public boolean shouldShowHUD(LocalPlayer player) {
        if (handler == null) return false;
        return handler.shouldShowHUD(player);
    }

    public boolean imposeExhaustionPenalty(LocalPlayer player) {
        if (handler == null) return false;
        var current = ((com.alrex.parcool.common.data.ParkourabilityAccess) player).parcool$getStamina();
        return current.isExhausted() && handler.shouldImposeExhaustionPenalty(player, current);
    }

    private ReadonlyStamina oldStamina = ReadonlyStamina.createDefault();
    public void sync(LocalPlayer player) {
        ReadonlyStamina stamina = ((com.alrex.parcool.common.data.ParkourabilityAccess) player).parcool$getStamina();
        if (!stamina.equals(oldStamina)) {
            stamina.sync(player);
        }
        oldStamina = stamina;
    }

    public boolean isUsingExternalStamina() {
        return handler != null && handler.isExternalStamina();
    }
}
