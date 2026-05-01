package com.alrex.parcool.config;

public class ParCoolConfig {
    public enum AdvantageousDirection {
        Lower, Higher
    }

    public static class Client {
        public enum Booleans {
            ParCoolIsActive(true),
            EnableStaminaExhaustionPenalty(true),
            ShowAutoResynchronizationNotification(false),
            Enable3DRenderingForZipline(true);

            private final boolean defaultValue;

            Booleans(boolean defaultValue) {
                this.defaultValue = defaultValue;
            }

            public boolean get() {
                // Simplified for porting
                return defaultValue;
            }
        }

        public enum Integers {}
        public enum Doubles {}
    }

    public static class Server {
        public enum Booleans {
            AllowInfiniteStamina(true);

            private final boolean defaultValue;

            Booleans(boolean defaultValue) {
                this.defaultValue = defaultValue;
            }

            public boolean get() {
                return defaultValue;
            }
        }

        public enum Integers {
            MaxStaminaRecovery(100),
            MaxStaminaLimit(1000);

            private final int defaultValue;

            Integers(int defaultValue) {
                this.defaultValue = defaultValue;
            }

            public int get() {
                return defaultValue;
            }
        }

        public enum Doubles {}
    }
}
