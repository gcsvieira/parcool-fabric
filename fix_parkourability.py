import re
import os

with open("neoforge-src/main/java/com/alrex/parcool/common/attachment/common/Parkourability.java", "r") as f:
    content = f.read()

content = re.sub(r'import net\.neoforged\..+;\n', '', content)
content = content.replace('import javax.annotation.Nullable;', 'import org.jetbrains.annotations.Nullable;')
content = content.replace('public static Parkourability get(Player player) {\n\t\treturn player.getData(Attachments.PARKOURABILITY);\n\t}', 'public static Parkourability get(Player player) {\n\t\treturn ((com.alrex.parcool.common.data.ParkourabilityAccess) player).parcool$getParkourability();\n\t}')
content = content.replace('ClientPacketDistributor.sendToServer(new ClientInformationPayload(player.getUUID(), true, parkourability.getClientInfo()));', 'net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(new ClientInformationPayload(player.getUUID(), true, parkourability.getClientInfo()));')

with open("src/main/java/com/alrex/parcool/common/attachment/common/Parkourability.java", "w") as f:
    f.write(content)

# Fix LocalStamina.java
with open("src/main/java/com/alrex/parcool/common/attachment/client/LocalStamina.java", "r") as f:
    local_stamina = f.read()
local_stamina = local_stamina.replace('import javax.annotation.Nullable;', 'import org.jetbrains.annotations.Nullable;')
with open("src/main/java/com/alrex/parcool/common/attachment/client/LocalStamina.java", "w") as f:
    f.write(local_stamina)

