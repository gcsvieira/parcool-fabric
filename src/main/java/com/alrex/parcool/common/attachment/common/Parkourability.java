package com.alrex.parcool.common.attachment.common;

import com.alrex.parcool.common.info.ActionInfo;
import net.minecraft.world.entity.player.Player;

public class Parkourability {
    private final ActionInfo actionInfo = new ActionInfo();

    public static Parkourability get(Player player) {
        return new Parkourability(); // Temporary stub
    }

    public ActionInfo getActionInfo() {
        return actionInfo;
    }
}
