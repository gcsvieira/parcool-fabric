import re

with open("neoforge-src/main/java/com/alrex/parcool/client/input/KeyBindings.java", "r") as f:
    content = f.read()

# remove neoforge imports
content = re.sub(r'import net\.neoforged\..+;\n', '', content)
content = content.replace("KeyMapping.Category KEY_CATEGORY_PARCOOL = new KeyMapping.Category(Identifier.fromNamespaceAndPath(ParCool.MOD_ID, \"base\"));", "String KEY_CATEGORY_PARCOOL = \"category.parcool.base\";")
content = content.replace("new KeyMapping(\"key.parcool.Enable\", KeyConflictContext.UNIVERSAL, KeyModifier.CONTROL, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, KEY_CATEGORY_PARCOOL)", "new KeyMapping(\"key.parcool.Enable\", GLFW.GLFW_KEY_P, KEY_CATEGORY_PARCOOL)")
content = content.replace("new KeyMapping(\"key.parcool.openSetting\", KeyConflictContext.UNIVERSAL, KeyModifier.ALT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_P, KEY_CATEGORY_PARCOOL)", "new KeyMapping(\"key.parcool.openSetting\", GLFW.GLFW_KEY_P, KEY_CATEGORY_PARCOOL)")
content = content.replace("new KeyMapping(\"key.parcool.GrabWall\", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, KEY_CATEGORY_PARCOOL)", "new KeyMapping(\"key.parcool.GrabWall\", com.mojang.blaze3d.platform.InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_RIGHT, KEY_CATEGORY_PARCOOL)")

content = re.sub(r'new KeyMapping\(([^,]+),\s*InputConstants\.Type\.MOUSE,\s*([^,]+),\s*KEY_CATEGORY_PARCOOL\)', r'new KeyMapping(\1, com.mojang.blaze3d.platform.InputConstants.Type.MOUSE, \2, KEY_CATEGORY_PARCOOL)', content)

content = re.sub(r'@SubscribeEvent\s+public static void register\([^)]+\)\s*\{', 'public static void register() {', content)
content = re.sub(r'event\.register\(([^)]+)\);', r'net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(\1);', content)

# ensure directory exists
import os
os.makedirs("src/main/java/com/alrex/parcool/client/input", exist_ok=True)

with open("src/main/java/com/alrex/parcool/client/input/KeyBindings.java", "w") as f:
    f.write(content)

