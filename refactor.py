import re

with open("src/main/java/com/alrex/parcool/common/action/ActionProcessor.java", "r") as f:
    content = f.read()

# Imports
content = re.sub(r'import net\.neoforged\..+;\n', '', content)
content = re.sub(r'import com\.alrex\.parcool\.api\.unstable\.action\.ParCoolActionEvent;\n', '', content)
content = content.replace('import net.minecraft.client.player.AbstractClientPlayer;', 'import net.minecraft.client.player.AbstractClientPlayer;\nimport net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;\nimport net.minecraft.client.Camera;')

# Annotations
content = content.replace('@SubscribeEvent\n\tpublic void onTick(PlayerTickEvent.Post event) {', 'public static void onTick(Player player) {')
content = content.replace('@SubscribeEvent\n\t\tpublic void onRenderTick(RenderFrameEvent.Pre event) {', 'public static void onRenderTick(float tickDelta) {')
content = content.replace('@SubscribeEvent\n\t\tpublic void onViewRender(ViewportEvent.ComputeCameraAngles event) {', 'public static void onViewRender(Camera camera) {')

# Body modifications
content = content.replace('var player = event.getEntity();', '')
content = content.replace('onTick$doPreprocess(event);', 'onTick$doPreprocess(player);')
content = content.replace('ClientActionProcessor.onTick$doPreprocessInClient(event, parkourability);', 'ClientActionProcessor.onTick$doPreprocessInClient(player, parkourability);')
content = content.replace('onTick$doPreprocessInServer(event);', 'onTick$doPreprocessInServer(player);')
content = content.replace('ClientActionProcessor.onTick$doPostProcessInClient(event, parkourability);', 'ClientActionProcessor.onTick$doPostProcessInClient(player, parkourability);')

content = content.replace('private void onTick$doPreprocess(PlayerTickEvent event)', 'private static void onTick$doPreprocess(Player player)')
content = content.replace('private void onTick$doPreprocessInServer(PlayerTickEvent event)', 'private static void onTick$doPreprocessInServer(Player player)')

content = content.replace('private void processAction', 'private static void processAction')

content = content.replace('NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Tick.Pre(player, action));', '')
content = content.replace('NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Tick.Post(player, action));', '')
content = content.replace('!NeoForge.EVENT_BUS.post(new ParCoolActionEvent.TryToContinueEvent(player, action)).isCanceled()', 'true')
content = content.replace('!NeoForge.EVENT_BUS.post(new ParCoolActionEvent.TryToContinue(player, action)).isCanceled()', 'true')
content = content.replace('NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Finish.Pre(player, action));', '')
content = content.replace('NeoForge.EVENT_BUS.post(new ParCoolActionEvent.StopEvent(player, action));', '')
content = content.replace('NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Finish.Post(player, action));', '')

content = content.replace('!NeoForge.EVENT_BUS.post(new ParCoolActionEvent.TryToStartEvent(player, action)).isCanceled()', 'true')
content = content.replace('!NeoForge.EVENT_BUS.post(new ParCoolActionEvent.TryToStart(player, action)).isCanceled()', 'true')
content = content.replace('NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Pre(player, action));', '')
content = content.replace('NeoForge.EVENT_BUS.post(new ParCoolActionEvent.StartEvent(player, action));', '')
content = content.replace('NeoForge.EVENT_BUS.post(new ParCoolActionEvent.Start.Post(player, action));', '')

content = content.replace('ClientPacketDistributor.sendToServer', 'ClientPlayNetworking.send')

content = content.replace('private static void onTick$doPreprocessInClient(PlayerTickEvent event, Parkourability parkourability) {', 'private static void onTick$doPreprocessInClient(Player player, Parkourability parkourability) {')
content = content.replace('if (!(event.getEntity() instanceof AbstractClientPlayer clientPlayer)) return;', 'if (!(player instanceof AbstractClientPlayer clientPlayer)) return;')
content = content.replace('private static void onTick$doPostProcessInClient(PlayerTickEvent event, Parkourability parkourability) {', 'private static void onTick$doPostProcessInClient(Player playerObj, Parkourability parkourability) {')
content = content.replace('if (!(event.getEntity() instanceof LocalPlayer player)) return;', 'if (!(playerObj instanceof LocalPlayer player)) return;')

content = content.replace('action.onRenderTick(event, player, parkourability);', 'action.onRenderTick(tickDelta, player, parkourability);')
content = content.replace('animation.onRenderTick(event, player, parkourability);', 'animation.onRenderTick(tickDelta, player, parkourability);')
content = content.replace('animation.cameraSetup(event, player, parkourability);', 'animation.cameraSetup(camera, player, parkourability);')


with open("src/main/java/com/alrex/parcool/common/action/ActionProcessor.java", "w") as f:
    f.write(content)

