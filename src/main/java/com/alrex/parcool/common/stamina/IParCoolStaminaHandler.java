package com.alrex.parcool.common.stamina;

import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import net.minecraft.world.entity.player.Player;

public interface IParCoolStaminaHandler {
    public ReadonlyStamina initializeStamina(Player player, ReadonlyStamina current);

    public ReadonlyStamina consume(Player player, ReadonlyStamina current, int value);

    public ReadonlyStamina recover(Player player, ReadonlyStamina current, int value);

    public default ReadonlyStamina onTick(Player player, ReadonlyStamina current) {
        return current;
    }

    public default boolean shouldShowHUD(Player player) {
        return false;
    }

    public default boolean shouldImposeExhaustionPenalty(Player player, ReadonlyStamina current) {
        return true;
    }

    public default void processOnServer(Player player, int value) {
    }

    public default boolean isExternalStamina() {
        return false;
    }
}
