import re
import os

# fix Animation.java
with open("src/main/java/com/alrex/parcool/common/attachment/client/Animation.java", "r") as f:
    content = f.read()

content = re.sub(r'import net\.neoforged\..+;\n', '', content)
content = content.replace('import com.alrex.parcool.api.unstable.client.ParCoolAnimationEvent;', '')
content = content.replace('public void cameraSetup(ViewportEvent.ComputeCameraAngles event, LocalPlayer player, Parkourability parkourability)', 'public void cameraSetup(net.minecraft.client.Camera camera, LocalPlayer player, Parkourability parkourability)')
content = content.replace('if (event instanceof RenderFrameEvent.Pre){', 'if (true){')
content = content.replace('NeoForge.EVENT_BUS.post(animationEvent);', '')

with open("src/main/java/com/alrex/parcool/common/attachment/client/Animation.java", "w") as f:
    f.write(content)

# fix ActionStatePayload.java
with open("src/main/java/com/alrex/parcool/common/network/payload/ActionStatePayload.java", "r") as f:
    content = f.read()

content = re.sub(r'import net\.neoforged\..+;\n', '', content)
content = content.replace('import javax.annotation.Nonnull;', 'import org.jetbrains.annotations.NotNull;')
content = content.replace('@Nonnull', '@NotNull')
content = content.replace('public void handle(IPayloadContext context)', 'public void handle(net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.Context context)')

content = content.replace('NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Pre(player, action));', '')
content = content.replace('NeoForge.EVENT_BUS.post(new ParCoolActionEvent.StartEvent(player, action));', '')
content = content.replace('NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Post(player, action));', '')
content = content.replace('NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Finish.Pre(player, action));', '')
content = content.replace('NeoForge.EVENT_BUS.post(new ParCoolActionEvent.StopEvent(player, action));', '')
content = content.replace('NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Finish.Post(player, action));', '')

with open("src/main/java/com/alrex/parcool/common/network/payload/ActionStatePayload.java", "w") as f:
    f.write(content)

