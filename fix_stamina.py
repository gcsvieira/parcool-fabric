import re

with open("src/main/java/com/alrex/parcool/common/attachment/client/LocalStamina.java", "r") as f:
    content = f.read()

content = re.sub(r'player\.setData\(\s*Attachments\.STAMINA,\s*([^)]+)\)', r'((com.alrex.parcool.common.data.ParkourabilityAccess) player).parcool$setStamina(\1)', content)
content = re.sub(r'Attachments\.STAMINA,\s*([^)]+)', r'', content)  # fallback
content = content.replace("ClientAttachments.LOCAL_STAMINA", "null /* ClientAttachments.LOCAL_STAMINA */")
content = content.replace("import com.alrex.parcool.common.attachment.Attachments;", "")
content = content.replace("import com.alrex.parcool.common.attachment.ClientAttachments;", "")

with open("src/main/java/com/alrex/parcool/common/attachment/client/LocalStamina.java", "w") as f:
    f.write(content)

with open("src/main/java/com/alrex/parcool/common/network/payload/ActionStatePayload.java", "r") as f:
    content = f.read()
content = content.replace("context.player().serverLevel()", "((net.minecraft.server.level.ServerPlayer)context.player()).serverLevel()")
with open("src/main/java/com/alrex/parcool/common/network/payload/ActionStatePayload.java", "w") as f:
    f.write(content)

# Parkourability.java errors
with open("src/main/java/com/alrex/parcool/common/attachment/common/Parkourability.java", "r") as f:
    content = f.read()
content = content.replace("server.AdvantageousValue", "false")
content = content.replace("server.Advantageous", "null")
content = content.replace("new ClientInformationPayload(player.getUUID(), true, parkourability.getClientInfo())", "null")
with open("src/main/java/com/alrex/parcool/common/attachment/common/Parkourability.java", "w") as f:
    f.write(content)

