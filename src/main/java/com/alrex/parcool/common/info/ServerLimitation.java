package com.alrex.parcool.common.info;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.Actions;
import com.alrex.parcool.common.stamina.StaminaType;
import com.alrex.parcool.config.ParCoolConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;
import java.util.EnumMap;

public abstract class ServerLimitation {
    private static class Default extends ServerLimitation {
        @Override
        public boolean isPermitted(Class<? extends Action> action) {
            return true;
        }

        @Override
        public int getStaminaConsumptionOf(Class<? extends Action> action) {
            return 0;
        }

        @Override
        public Boolean get(ParCoolConfig.Server.Booleans item) {
            return false;
        }

        @Override
        public Integer get(ParCoolConfig.Server.Integers item) {
            return 0;
        }

        @Override
        public Double get(ParCoolConfig.Server.Doubles item) {
            return 0.0;
        }

        @Override
        public StaminaType getForcedStamina() {
            return StaminaType.NONE;
        }

        @Override
        public boolean isSynced() {
            return false;
        }
    }

    private static class Remote extends ServerLimitation {
        private final boolean[] actionPossibilities = new boolean[Actions.LIST.size()];
        private final int[] leastStaminaConsumptions = new int[Actions.LIST.size()];
        private final EnumMap<ParCoolConfig.Server.Booleans, Boolean> booleans = new EnumMap<>(ParCoolConfig.Server.Booleans.class);
        private final EnumMap<ParCoolConfig.Server.Integers, Integer> integers = new EnumMap<>(ParCoolConfig.Server.Integers.class);
        private final EnumMap<ParCoolConfig.Server.Doubles, Double> doubles = new EnumMap<>(ParCoolConfig.Server.Doubles.class);
        private StaminaType forcedStamina = StaminaType.NONE;

        public Remote() {
            Arrays.fill(actionPossibilities, true);
            Arrays.fill(leastStaminaConsumptions, 0);
        }

        @Override
        public boolean isPermitted(Class<? extends Action> action) {
            return actionPossibilities[Actions.getIndexOf(action)];
        }

        @Override
        public int getStaminaConsumptionOf(Class<? extends Action> action) {
            return leastStaminaConsumptions[Actions.getIndexOf(action)];
        }

        @Override
        public Boolean get(ParCoolConfig.Server.Booleans item) {
            return booleans.getOrDefault(item, true);
        }

        @Override
        public Integer get(ParCoolConfig.Server.Integers item) {
            return integers.getOrDefault(item, 0);
        }

        @Override
        public Double get(ParCoolConfig.Server.Doubles item) {
            return doubles.getOrDefault(item, 0.0);
        }

        @Override
        public StaminaType getForcedStamina() {
            return forcedStamina;
        }

        @Override
        public boolean isSynced() {
            return true;
        }
    }

    public static final ServerLimitation UNSYNCED_INSTANCE = new Default();

    public abstract boolean isPermitted(Class<? extends Action> action);

    public abstract int getStaminaConsumptionOf(Class<? extends Action> action);

    public abstract Boolean get(ParCoolConfig.Server.Booleans item);

    public abstract Integer get(ParCoolConfig.Server.Integers item);

    public abstract Double get(ParCoolConfig.Server.Doubles item);

    public abstract StaminaType getForcedStamina();

    public abstract boolean isSynced();

    public static ServerLimitation get(ServerPlayer player) {
        // Stubbed until Limitations system is migrated
        return UNSYNCED_INSTANCE;
    }

    public void writeTo(ByteBuf buffer) {
        for (Class<? extends Action> action : Actions.LIST) {
            buffer.writeByte((byte) (isPermitted(action) ? 1 : 0));
            buffer.writeInt(getStaminaConsumptionOf(action));
        }
        for (ParCoolConfig.Server.Booleans item : ParCoolConfig.Server.Booleans.values()) {
            buffer.writeByte((byte) (get(item) ? 1 : 0));
        }
        for (ParCoolConfig.Server.Integers item : ParCoolConfig.Server.Integers.values()) {
            buffer.writeInt(get(item));
        }
        for (ParCoolConfig.Server.Doubles item : ParCoolConfig.Server.Doubles.values()) {
            buffer.writeDouble(get(item));
        }
        buffer.writeByte(getForcedStamina().ordinal());
    }

    public static ServerLimitation readFrom(ByteBuf buffer) {
        Remote instance = new Remote();
        for (int i = 0; i < instance.actionPossibilities.length; i++) {
            instance.actionPossibilities[i] = buffer.readByte() != 0;
            instance.leastStaminaConsumptions[i] = buffer.readInt();
        }
        for (ParCoolConfig.Server.Booleans item : ParCoolConfig.Server.Booleans.values()) {
            instance.booleans.put(item, buffer.readByte() != 0);
        }
        for (ParCoolConfig.Server.Integers item : ParCoolConfig.Server.Integers.values()) {
            instance.integers.put(item, buffer.readInt());
        }
        for (ParCoolConfig.Server.Doubles item : ParCoolConfig.Server.Doubles.values()) {
            instance.doubles.put(item, buffer.readDouble());
        }
        instance.forcedStamina = StaminaType.values()[buffer.readByte()];
        return instance;
    }
}
