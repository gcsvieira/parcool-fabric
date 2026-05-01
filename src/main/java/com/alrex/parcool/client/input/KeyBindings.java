package com.alrex.parcool.client.input;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.utilities.VectorUtil;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

//@OnlyIn(Dist.CLIENT)
import net.minecraft.resources.Identifier;

public class KeyBindings {
	public static final KeyMapping.Category KEY_CATEGORY_PARCOOL = new KeyMapping.Category(Identifier.fromNamespaceAndPath(ParCool.MOD_ID, "base"));
    private static final Minecraft mc = Minecraft.getInstance();
    private static final KeyMapping keyBindEnable = new KeyMapping("key.parcool.Enable", GLFW.GLFW_KEY_P, KEY_CATEGORY_PARCOOL);
    private static final KeyMapping keyBindCrawl = new KeyMapping("key.parcool.Crawl", GLFW.GLFW_KEY_C, KEY_CATEGORY_PARCOOL);
    private static final KeyMapping keyBindGrabWall = new KeyMapping("key.parcool.ClingToCliff", com.mojang.blaze3d.platform.InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, KEY_CATEGORY_PARCOOL);
    private static final KeyMapping keyBindBreakfall = new KeyMapping("key.parcool.Breakfall", GLFW.GLFW_KEY_R, KEY_CATEGORY_PARCOOL);
    private static final KeyMapping keyBindFastRunning = new KeyMapping("key.parcool.FastRun", GLFW.GLFW_KEY_LEFT_CONTROL, KEY_CATEGORY_PARCOOL);
    private static final KeyMapping keyBindFlipping = new KeyMapping("key.parcool.Flipping", GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY_PARCOOL);
    private static final KeyMapping keyBindVault = new KeyMapping("key.parcool.Vault", com.mojang.blaze3d.platform.InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, KEY_CATEGORY_PARCOOL);
    private static final KeyMapping keyBindDodge = new KeyMapping("key.parcool.Dodge", GLFW.GLFW_KEY_R, KEY_CATEGORY_PARCOOL);
    private static final KeyMapping keyBindRideZipline = new KeyMapping("key.parcool.RideZipline", com.mojang.blaze3d.platform.InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, KEY_CATEGORY_PARCOOL);
    private static final KeyMapping keyBindWallJump = new KeyMapping("key.parcool.WallJump", GLFW.GLFW_KEY_SPACE, KEY_CATEGORY_PARCOOL);
    private static final KeyMapping keyBindHangDown = new KeyMapping("key.parcool.HangDown", com.mojang.blaze3d.platform.InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, KEY_CATEGORY_PARCOOL);
    private static final KeyMapping keyBindWallSlide = new KeyMapping("key.parcool.WallSlide", com.mojang.blaze3d.platform.InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, KEY_CATEGORY_PARCOOL);
    private static final KeyMapping keyBindHideInBlock = new KeyMapping("key.parcool.HideInBlock", GLFW.GLFW_KEY_C, KEY_CATEGORY_PARCOOL);
    private static final KeyMapping keyBindHorizontalWallRun = new KeyMapping("key.parcool.HorizontalWallRun", GLFW.GLFW_KEY_R, KEY_CATEGORY_PARCOOL);
    private static final KeyMapping keyBindQuickTurn = new KeyMapping("key.parcool.QuickTurn", GLFW.GLFW_KEY_UNKNOWN, KEY_CATEGORY_PARCOOL);
    private static final KeyMapping keyBindOpenSettings = new KeyMapping("key.parcool.openSetting", GLFW.GLFW_KEY_P, KEY_CATEGORY_PARCOOL);
	private static final Vec3 forwardVector = new Vec3(0, 0, 1);

	public static KeyMapping getKeySprint() {
		return mc.options.keySprint;
	}

	public static Boolean isKeyJumpDown() {
		return mc.player != null
				&& mc.player.input != null
				&& mc.player.input.keyPresses.jump();
	}

	public static KeyMapping getKeySneak() {
		return mc.options.keyShift;
	}

	public static Vec3 getCurrentMoveVector() {
		var player = Minecraft.getInstance().player;
		if (player == null) return Vec3.ZERO;
		if (player.input == null) return Vec3.ZERO;
		var vector = player.input.getMoveVector();
		if (VectorUtil.isZero(vector)) return Vec3.ZERO;
		double length = vector.length();
		return new Vec3(vector.x / length, 0, vector.y / length);
	}

	public static Vec3 getForwardVector() {
		return forwardVector;
	}

	public static Boolean isAnyMovingKeyDown() {
		return mc.player != null
				&& mc.player.input != null
				&& (mc.player.input.keyPresses.forward()
				|| mc.player.input.keyPresses.right()
				|| mc.player.input.keyPresses.backward()
				|| mc.player.input.keyPresses.left()
				|| mc.player.input.getMoveVector().x != 0
				|| mc.player.input.getMoveVector().y != 0);
	}

	public static Boolean isLeftAndRightDown() {
		return mc.player != null && mc.player.input != null && mc.player.input.keyPresses.left() && mc.player.input.keyPresses.right();
	}

    public static Boolean isKeyForwardDown() {
		return mc.player != null && mc.player.input != null && mc.player.input.keyPresses.forward();
	}

    public static Boolean isKeyLeftDown() {
		return mc.player != null &&mc.player.input != null && mc.player.input.keyPresses.left();
	}

    public static Boolean isKeyRightDown() {
		return mc.player != null &&mc.player.input != null && mc.player.input.keyPresses.right();
	}

    public static Boolean isKeyBackDown() {
		return mc.player != null && mc.player.input != null && mc.player.input.keyPresses.backward();
	}

    public static KeyMapping getKeyBindEnable() {
        return keyBindEnable;
    }

	public static KeyMapping getKeyCrawl() {
		return keyBindCrawl;
	}

	public static KeyMapping getKeyQuickTurn() {
		return keyBindQuickTurn;
	}

	public static KeyMapping getKeyGrabWall() {
		return keyBindGrabWall;
	}

	public static KeyMapping getKeyVault() {
		return keyBindVault;
	}

	public static KeyMapping getKeyActivateParCool() {
		return keyBindOpenSettings;
	}

	public static KeyMapping getKeyBreakfall() {
		return keyBindBreakfall;
	}

	public static KeyMapping getKeyFastRunning() {
		return keyBindFastRunning;
	}

	public static KeyMapping getKeyDodge() {
		return keyBindDodge;
	}

    public static KeyMapping getKeyRideZipline() {
        return keyBindRideZipline;
    }

	public static KeyMapping getKeyWallSlide() {
		return keyBindWallSlide;
	}

	public static KeyMapping getKeyHangDown() {
		return keyBindHangDown;
	}

    public static KeyMapping getKeyHideInBlock() {
        return keyBindHideInBlock;
    }

	public static KeyMapping getKeyHorizontalWallRun() {
		return keyBindHorizontalWallRun;
	}

	public static KeyMapping getKeyWallJump() {
		return keyBindWallJump;
	}

	public static KeyMapping getKeyFlipping() {
		return keyBindFlipping;
	}

	public static void register() {
        net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(keyBindEnable);
		net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(keyBindCrawl);
		net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(keyBindGrabWall);
		net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(keyBindBreakfall);
		net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(keyBindFastRunning);
		net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(keyBindDodge);
        net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(keyBindRideZipline);
		net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(keyBindWallSlide);
		net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(keyBindWallJump);
		net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(keyBindVault);
		net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(keyBindHorizontalWallRun);
        net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(keyBindHideInBlock);
		net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(keyBindOpenSettings);
		net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(keyBindQuickTurn);
		net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(keyBindFlipping);
		net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(keyBindHangDown);
	}
}
