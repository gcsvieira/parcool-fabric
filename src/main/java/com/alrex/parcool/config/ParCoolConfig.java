package com.alrex.parcool.config;

import com.alrex.parcool.common.action.Action;

public class ParCoolConfig {
    public enum AdvantageousDirection {
        Lower, Higher
    }
    public static class Client {
        public enum Booleans {
            ParCoolIsActive,
            EnableStaminaExhaustionPenalty,
            ShowAutoResynchronizationNotification
        }
        public enum Integers {}
        public enum Doubles {}
    }
    public static class Server {
        public enum Booleans {
            AllowInfiniteStamina
        }
        public enum Integers {
            MaxStaminaRecovery,
            MaxStaminaLimit
        }
        public enum Doubles {}
    }
}
