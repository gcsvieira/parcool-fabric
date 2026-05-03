package com.alrex.parcool.config;

public class ParCoolConfig {
    public enum AdvantageousDirection {
        Lower, Higher
    }

    public static class Client {
        private static final Client INSTANCE = new Client();
        public static Client getInstance() { return INSTANCE; }

        public enum ClingToCliffControl {
            PressKey, Toggle;
            public ClingToCliffControl get() { return this; }
        }
        public ClingToCliffControl clingToCliffControl = ClingToCliffControl.PressKey;

        public enum WallJumpControl {
            PressKey, ReleaseKey;
            public WallJumpControl get() { return this; }
        }
        public WallJumpControl wallJumpControl = WallJumpControl.PressKey;

        public enum FastRunControl {
            PressKey, Toggle, Auto;
            public FastRunControl get() { return this; }
        }
        public FastRunControl fastRunControl = FastRunControl.PressKey;

        public enum HorizontalWallRunControl {
            PressKey, Auto;
            public HorizontalWallRunControl get() { return this; }
        }
        public HorizontalWallRunControl hWallRunControl = HorizontalWallRunControl.PressKey;

        public enum FlippingControl {
            TapMovementAndJump, DoubleJump;
            public FlippingControl get() { return this; }
        }
        public FlippingControl flipControl = FlippingControl.TapMovementAndJump;

        public enum TypeSelectionMode {
            SpeedVault, KongVault, Dynamic;
            public TypeSelectionMode get() { return this; }
        }
        public TypeSelectionMode vaultAnimationMode = TypeSelectionMode.Dynamic;

        public enum Booleans {
            ParCoolIsActive(true),
            EnableStaminaExhaustionPenalty(true),
            ShowAutoResynchronizationNotification(false),
            Enable3DRenderingForZipline(true),
            EnableWallJumpCooldown(true),
            EnableActionSounds(true),
            EnableActionParticles(true),
            VaultKeyPressedNeeded(false),
            EnableVaultInAir(true),
            EnableActionPhysics(true),
            EnableRollWhenCreative(true),
            EnableDoubleTappingForDodge(true),
            CanGetOffStepsWhileDodge(false),
            SubstituteSprintForFastRun(false),
            EnableJustTimeEffectOfBreakfall(true),
            EnableActionParticlesOfJustTimeBreakfall(true),
            HideInBlockSneakNeeded(false),
            EnableCameraAnimationOfRolling(true),
            EnableCameraAnimationOfHangDown(true),
            EnableCameraAnimationOfHWallRun(true),
            EnableCameraAnimationOfVault(true),
            EnableLeanAnimationOfFastRun(true),
            EnableCameraAnimationOfBackWallJump(true),
            EnableCameraAnimationOfDodge(true),
            EnableCameraAnimationOfFlipping(true),
            EnableFallingAnimation(true),
            EnableAnimation(true),
            EnableFPVAnimation(true);

            private final boolean defaultValue;

            Booleans(boolean defaultValue) {
                this.defaultValue = defaultValue;
            }

            public boolean get() {
                return defaultValue;
            }
        }

        public enum Integers {
            AcceptableAngleOfWallJump(45),
            SlidingContinuableTick(15),
            WallRunContinuableTick(25),
            DodgeCoolTime(10),
            MaxSuccessiveDodgeCount(3),
            SuccessiveDodgeCoolTime(40),
            CoyoteTime(3),
            JustTimeBreakfallTick(5);

            private final int defaultValue;

            Integers(int defaultValue) {
                this.defaultValue = defaultValue;
            }

            public int get() {
                return defaultValue;
            }
        }
        public enum Doubles {
            DodgeSpeedModifier(1.0),
            FastRunSpeedModifier(10.0),
            FastSwimSpeedModifier(10.0),
            SkyDiveSpeedDecreaseRate(0.98),
            LowestFallDistanceForBreakfall(3.0),
            DamageCompleteRemovableHeightBreakfall(6.0),
            DamageReductionRateBreakfall(0.5);

            private final double defaultValue;
            Doubles(double defaultValue) { this.defaultValue = defaultValue; }
            public double get() { return defaultValue; }
        }
    }

    public static class Server {
        public enum Booleans {
            AllowInfiniteStamina(false),
            AllowDisableWallJumpCooldown(false),
            DodgeProvideInvulnerableFrame(true);

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
            MaxStaminaLimit(1000),
            MaxSlidingContinuableTick(20),
            MaxWallRunContinuableTick(40),
            DodgeCoolTime(10),
            MaxSuccessiveDodgeCount(3),
            SuccessiveDodgeCoolTime(40),
            MaxCoyoteTime(20),
            MaxJustTimeBreakfallTick(20);

            private final int defaultValue;

            Integers(int defaultValue) {
                this.defaultValue = defaultValue;
            }

            public int get() {
                return defaultValue;
            }
        }
        public enum Doubles {
            MaxDodgeSpeedModifier(1.5),
            MaxFastRunSpeedModifier(20.0),
            MaxFastSwimSpeedModifier(20.0),
            MinSkyDiveSpeedDecreaseRate(0.5),
            MinLowestFallDistanceForBreakfall(2.0),
            MaxDamageCompleteRemovableHeightBreakfall(10.0),
            MaxDamageReductionRateBreakfall(0.9);

            private final double defaultValue;
            Doubles(double defaultValue) { this.defaultValue = defaultValue; }
            public double get() { return defaultValue; }
        }
    }
}
