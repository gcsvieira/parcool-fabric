import re

# ActionStatePayload.java
with open("src/main/java/com/alrex/parcool/common/network/payload/ActionStatePayload.java", "r") as f:
    content = f.read()

content = content.replace("public static void handleClient(ActionStatePayload payload, IPayloadContext context)", "public static void handleClient(ActionStatePayload payload, net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context context)")
content = content.replace("public static void handleServer(ActionStatePayload payload, IPayloadContext context)", "public static void handleServer(ActionStatePayload payload, net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.Context context)")
content = content.replace("context.enqueueWork(() -> {", "")
content = content.replace("        });", "")
content = content.replace("ActionSynchronizationBroadcaster.add(payload);", "for (net.minecraft.server.level.ServerPlayer other : context.player().serverLevel().players()) { if (other != context.player()) net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(other, payload); }")

with open("src/main/java/com/alrex/parcool/common/network/payload/ActionStatePayload.java", "w") as f:
    f.write(content)

# LocalStamina.java
with open("src/main/java/com/alrex/parcool/common/attachment/client/LocalStamina.java", "r") as f:
    content = f.read()

content = content.replace("player.getData(Attachments.STAMINA)", "((com.alrex.parcool.common.data.ParkourabilityAccess) player).parcool$getStamina()")
content = content.replace("player.setData(Attachments.STAMINA, ", "((com.alrex.parcool.common.data.ParkourabilityAccess) player).parcool$setStamina(")

with open("src/main/java/com/alrex/parcool/common/attachment/client/LocalStamina.java", "w") as f:
    f.write(content)

# ActionProcessor.java
with open("src/main/java/com/alrex/parcool/common/action/ActionProcessor.java", "r") as f:
    content = f.read()

content = content.replace("player.getData(Attachments.STAMINA)", "((com.alrex.parcool.common.data.ParkourabilityAccess) player).parcool$getStamina()")
content = content.replace("player.setData(Attachments.STAMINA, ", "((com.alrex.parcool.common.data.ParkourabilityAccess) player).parcool$setStamina(")

with open("src/main/java/com/alrex/parcool/common/action/ActionProcessor.java", "w") as f:
    f.write(content)

