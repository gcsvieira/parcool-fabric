package com.alrex.parcool.server.command.impl;

import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.data.ParkourabilityAccess;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.permissions.Permissions;

public class DebugCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> getBuilder() {
        return Commands.literal("debug")
                .requires(source -> true)
                .then(Commands.literal("stamina")
                        .then(Commands.literal("get")
                                .executes(context -> {
                                    if (context.getSource().getEntity() instanceof Player player) {
                                        var stamina = ((ParkourabilityAccess) player).parcool$getStamina();
                                        context.getSource().sendSuccess(() -> Component.literal("Stamina: " + stamina.value() + " / " + stamina.max()), false);
                                    }
                                    return 1;
                                })
                        )
                        .then(Commands.literal("set")
                                .then(Commands.argument("value", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            if (context.getSource().getEntity() instanceof Player player) {
                                                int val = IntegerArgumentType.getInteger(context, "value");
                                                var stamina = ((ParkourabilityAccess) player).parcool$getStamina();
                                                var newStamina = new ReadonlyStamina(stamina.isExhausted(), val, stamina.max());
                                                ((ParkourabilityAccess) player).parcool$setStamina(newStamina);
                                                context.getSource().sendSuccess(() -> Component.literal("Set stamina to: " + val), false);
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
                .then(Commands.literal("parkourability")
                        .executes(context -> {
                            if (context.getSource().getEntity() instanceof Player player) {
                                var p = Parkourability.get(player);
                                context.getSource().sendSuccess(() -> Component.literal("Parkourability instance: " + (p != null ? "VALID" : "NULL")), false);
                            }
                            return 1;
                        })
                );
    }
}
