package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.animation.PlayerRenderStateAccess;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.common.attachment.client.Animation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, AvatarRenderState, PlayerModel> {

    @Unique
    private PlayerModelRotator parCool$rotator = null;

    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    protected void onExtractRenderStateTail(net.minecraft.world.entity.Avatar player, AvatarRenderState state, float partialTick, CallbackInfo ci) {
        var stateAccess = (PlayerRenderStateAccess) state;
        stateAccess.parCool$setPlayer((net.minecraft.world.entity.player.Player) player);
        stateAccess.parCool$setPartialTick(partialTick);
    }

    @Inject(method = "setupRotations(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V", at = @At("RETURN"))
    protected void onSetupRotationsTail(AvatarRenderState state, PoseStack poseStack, float rotationYaw, float scale, CallbackInfo ci) {
        var stateAccess = (PlayerRenderStateAccess) state;
        AbstractClientPlayer player = (AbstractClientPlayer) stateAccess.parCool$getPlayer();
        if (player == null) return;

        Animation animation = Animation.get(player);
        if (parCool$rotator != null) {
            animation.rotatePost(player, parCool$rotator);
            parCool$rotator = null;
        }
    }

    @Inject(method = "setupRotations(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V", at = @At("HEAD"), cancellable = true)
    protected void onSetupRotationsHead(AvatarRenderState state, PoseStack poseStack, float rotationYaw, float scale, CallbackInfo ci) {
        var stateAccess = (PlayerRenderStateAccess) state;
        AbstractClientPlayer player = (AbstractClientPlayer) stateAccess.parCool$getPlayer();
        if (player == null) return;

        Animation animation = Animation.get(player);
        parCool$rotator = new PlayerModelRotator(poseStack, player, stateAccess.parCool$getPartialTick(), rotationYaw);
        if (animation.rotatePre(player, parCool$rotator)) {
            parCool$rotator = null;
            ci.cancel();
        }
    }
}
