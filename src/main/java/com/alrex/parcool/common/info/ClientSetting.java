package com.alrex.parcool.common.info;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.Actions;
import com.alrex.parcool.common.stamina.StaminaType;
import com.alrex.parcool.config.ParCoolConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Arrays;
import java.util.EnumMap;

public abstract class ClientSetting {
    public static final StreamCodec<ByteBuf, ClientSetting> STREAM_CODEC = StreamCodec.of(
            (buffer, value) -> value.writeTo(buffer),
            ClientSetting::readFrom
    );

    private static class Default extends ClientSetting {
        @Override
        public boolean getPossibilityOf(Class<? extends Action> action) {
            return true;
        }

        @Override
        public int getStaminaConsumptionOf(Class<? extends Action> action) {
            return 0;
        }

        @Override
        public StaminaType getRequestedStamina() {
            return StaminaType.NONE;
        }

        @Override
        public Boolean get(ParCoolConfig.Client.Booleans item) {
            // Stubbed for now
            return true;
        }

        @Override
        public Integer get(ParCoolConfig.Client.Integers item) {
            return 0;
        }

        @Override
        public Double get(ParCoolConfig.Client.Doubles item) {
            return 0.0;
        }
    }

    private static class Remote extends ClientSetting {
        private final boolean[] actionPossibilities = new boolean[Actions.LIST.size()];
        private final int[] staminaConsumptions = new int[Actions.LIST.size()];
        private final EnumMap<ParCoolConfig.Client.Booleans, Boolean> booleans = new EnumMap<>(ParCoolConfig.Client.Booleans.class);
        private final EnumMap<ParCoolConfig.Client.Integers, Integer> integers = new EnumMap<>(ParCoolConfig.Client.Integers.class);
        private final EnumMap<ParCoolConfig.Client.Doubles, Double> doubles = new EnumMap<>(ParCoolConfig.Client.Doubles.class);
        private StaminaType requestedStamina = StaminaType.PARCOOL;

        public Remote() {
            Arrays.fill(actionPossibilities, true);
            Arrays.fill(staminaConsumptions, 0);
        }

        @Override
        public boolean getPossibilityOf(Class<? extends Action> action) {
            return actionPossibilities[Actions.getIndexOf(action)];
        }

        @Override
        public int getStaminaConsumptionOf(Class<? extends Action> action) {
            return staminaConsumptions[Actions.getIndexOf(action)];
        }

        @Override
        public StaminaType getRequestedStamina() {
            return requestedStamina;
        }

        @Override
        public Boolean get(ParCoolConfig.Client.Booleans item) {
            return booleans.getOrDefault(item, true);
        }

        @Override
        public Integer get(ParCoolConfig.Client.Integers item) {
            return integers.getOrDefault(item, 0);
        }

        @Override
        public Double get(ParCoolConfig.Client.Doubles item) {
            return doubles.getOrDefault(item, 0.0);
        }

    }

    public static final ClientSetting UNSYNCED_INSTANCE = new Default();

    public abstract boolean getPossibilityOf(Class<? extends Action> action);

    public abstract int getStaminaConsumptionOf(Class<? extends Action> action);

    public abstract StaminaType getRequestedStamina();

    public abstract Boolean get(ParCoolConfig.Client.Booleans item);

    public abstract Integer get(ParCoolConfig.Client.Integers item);

    public abstract Double get(ParCoolConfig.Client.Doubles item);

    //@OnlyIn(Dist.CLIENT)
    public static ClientSetting readFromLocalConfig() {
        // Stubbed until config system is migrated
        return UNSYNCED_INSTANCE;
    }

    public void writeTo(ByteBuf buffer) {
        for (Class<? extends Action> action : Actions.LIST) {
            buffer.writeByte((byte) (getPossibilityOf(action) ? 1 : 0));
            buffer.writeInt(getStaminaConsumptionOf(action));
        }
        for (ParCoolConfig.Client.Booleans item : ParCoolConfig.Client.Booleans.values()) {
            buffer.writeByte((byte) (get(item) ? 1 : 0));
        }
        for (ParCoolConfig.Client.Integers item : ParCoolConfig.Client.Integers.values()) {
            buffer.writeInt(get(item));
        }
        for (ParCoolConfig.Client.Doubles item : ParCoolConfig.Client.Doubles.values()) {
            buffer.writeDouble(get(item));
        }
        buffer.writeByte(getRequestedStamina().ordinal());
    }

    public static ClientSetting readFrom(ByteBuf buffer) {
        Remote instance = new Remote();
        for (int i = 0; i < instance.actionPossibilities.length; i++) {
            instance.actionPossibilities[i] = buffer.readByte() != 0;
            instance.staminaConsumptions[i] = buffer.readInt();
        }
        for (ParCoolConfig.Client.Booleans item : ParCoolConfig.Client.Booleans.values()) {
            instance.booleans.put(item, buffer.readByte() != 0);
        }
        for (ParCoolConfig.Client.Integers item : ParCoolConfig.Client.Integers.values()) {
            instance.integers.put(item, buffer.readInt());
        }
        for (ParCoolConfig.Client.Doubles item : ParCoolConfig.Client.Doubles.values()) {
            instance.doubles.put(item, buffer.readDouble());
        }
        instance.requestedStamina = StaminaType.values()[buffer.readByte()];
        return instance;
    }

}
