---
id: networking
type: knowledge
tags: ["#porting", "#networking"]
links: ["[[MOC-porting]]"]
---

# Networking Migration


## Current Architecture

ParCool has **8 network payloads** that synchronize action states, stamina, and limitations between client and server.

All payloads implement `CustomPacketPayload` (vanilla Minecraft interface) with `StreamCodec` for serialization.

## Payload Inventory

| Payload | Direction | Purpose |
|---|---|---|
| `ActionStatePayload` | Bidirectional | Sync action start/stop/state changes |
| `ActionStateBroadcastPayload` | Server→Client | Broadcast other players' action states |
| `StaminaPayload` | Bidirectional | Sync stamina values |
| `StaminaBroadcastPayload` | Server→Client | Broadcast other players' stamina |
| `StaminaProcessOnServerPayload` | Bidirectional | Server-side stamina processing |
| `LimitationPayload` | Bidirectional | Sync server limitations to client |
| `ClientInformationPayload` | Bidirectional | Sync client config to server |
| `StartBreakfallEventPayload` | Bidirectional | Trigger breakfall animation |

## NeoForge Registration Pattern

```java
// Registration (in NetworkRegistries.java)
@SubscribeEvent
public static void onRegisterPayload(RegisterPayloadHandlersEvent event) {
    var r = event.registrar(PROTOCOL_VERSION);
    r.playBidirectional(
        ActionStatePayload.TYPE,        // CustomPacketPayload.Type<T>
        ActionStatePayload.CODEC,       // StreamCodec<ByteBuf, T>
        ActionStatePayload::handleServer
    );
}

// Sending (client→server)
ClientPacketDistributor.sendToServer(new ActionStatePayload(...));

// Sending (server→client)
PacketDistributor.sendToPlayer(serverPlayer, new ActionStateBroadcastPayload(...));

// Handling
public static void handleServer(ActionStatePayload payload, IPayloadContext context) {
    context.enqueueWork(() -> { ... });
}
```

## Fabric Equivalent

```java
// Registration (in ModInitializer)
PayloadTypeRegistry.playC2S().register(ActionStatePayload.TYPE, ActionStatePayload.CODEC);
PayloadTypeRegistry.playS2C().register(ActionStateBroadcastPayload.TYPE, ActionStateBroadcastPayload.CODEC);
// For bidirectional: register in BOTH
PayloadTypeRegistry.playC2S().register(ActionStatePayload.TYPE, ActionStatePayload.CODEC);
PayloadTypeRegistry.playS2C().register(ActionStatePayload.TYPE, ActionStatePayload.CODEC);

// Server-side handler
ServerPlayNetworking.registerGlobalReceiver(ActionStatePayload.TYPE, (payload, context) -> {
    context.player().server.execute(() -> {
        // handle on main thread
    });
});

// Client-side handler (in ClientModInitializer)
ClientPlayNetworking.registerGlobalReceiver(ActionStatePayload.TYPE, (payload, context) -> {
    context.client().execute(() -> {
        // handle on render thread
    });
});

// Sending client→server
ClientPlayNetworking.send(new ActionStatePayload(...));

// Sending server→client
ServerPlayNetworking.send(serverPlayer, new ActionStateBroadcastPayload(...));

// Sending to all tracking players (broadcast)
PlayerLookup.tracking(serverPlayer).forEach(p ->
    ServerPlayNetworking.send(p, new ActionStateBroadcastPayload(...))
);
```

## Key Differences

| Aspect | NeoForge | Fabric |
|---|---|---|
| Registration | Event-based (`RegisterPayloadHandlersEvent`) | Direct call to `PayloadTypeRegistry` |
| Bidirectional | One `playBidirectional()` call | Two calls (C2S + S2C) |
| Client→Server send | `ClientPacketDistributor.sendToServer()` | `ClientPlayNetworking.send()` |
| Server→Client send | `PacketDistributor.sendToPlayer()` | `ServerPlayNetworking.send()` |
| Broadcast | `PacketDistributor.sendToPlayersTrackingEntityAndSelf()` | `PlayerLookup.tracking()` loop |
| Thread safety | `context.enqueueWork()` | `context.player().server.execute()` |
| Protocol version | String in `registrar()` | Not required (use payload type ID) |

## What Can Stay The Same

The payload **record classes themselves** can stay almost identical:
- `CustomPacketPayload` is a vanilla Minecraft interface
- `StreamCodec` is vanilla Minecraft
- The `encode`/`decode` logic using `ByteBuf` is vanilla Netty
- The `TYPE` (`CustomPacketPayload.Type<T>`) is vanilla

Only the `handleClient`/`handleServer` method signatures change slightly (different context type).

## Migration Checklist

- [ ] Create `PayloadTypeRegistry` calls for all 8 payloads in initializer
- [ ] Register server receivers with `ServerPlayNetworking.registerGlobalReceiver()`
- [ ] Register client receivers with `ClientPlayNetworking.registerGlobalReceiver()`
- [ ] Replace `ClientPacketDistributor.sendToServer()` → `ClientPlayNetworking.send()`
- [ ] Replace `PacketDistributor.sendToPlayer()` → `ServerPlayNetworking.send()`
- [ ] Replace broadcast pattern in `ActionSynchronizationBroadcaster` and `StaminaSynchronizationBroadcaster`
- [ ] Update `IPayloadContext` usage to Fabric's context pattern
