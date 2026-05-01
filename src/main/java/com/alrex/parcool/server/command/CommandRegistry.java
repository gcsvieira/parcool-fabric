package com.alrex.parcool.server.command;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.server.command.impl.ZiplineCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CommandRegistry {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal(ParCool.MOD_ID)
                        .then(ZiplineCommand.getBuilder())
                // ControlLimitationCommand will be added after Limitation system is ported in Phase 3
        );
    }
}
