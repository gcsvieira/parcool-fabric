package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.animation.PlayerRenderStateAccess;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.attachment.client.Animation;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin<S extends AvatarRenderState> extends HumanoidModel<S> {
    @Shadow
    @Final
    private boolean slim;

    @Unique
    private PlayerModelTransformer parCool$transformer = null;

    public PlayerModelMixin(ModelPart modelPart) {
        super(modelPart);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V", at = @At("HEAD"), cancellable = true)
    protected void onSetupAnimHead(S state, CallbackInfo info) {
        Player player = ((PlayerRenderStateAccess) state).parCool$getPlayer();
        if (player == null) return;
        
        PlayerModel model = (PlayerModel) (Object) this;

        parCool$transformer = new PlayerModelTransformer(
                player,
                model,
                slim,
                state,
                state.rightArmPose,
                state.leftArmPose
        );
        parCool$transformer.reset();

        Animation animation = Animation.get(player);
        if (animation == null) return;

        boolean shouldCancel = animation.animatePre(player, parCool$transformer);
        if (shouldCancel) {
            parCool$transformer = null;
            info.cancel();
        }
    }

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V", at = @At("TAIL"))
    protected void onSetupAnimTail(S state, CallbackInfo ci) {
        Player player = ((PlayerRenderStateAccess) state).parCool$getPlayer();
        if (player == null) return;

        Animation animation = Animation.get(player);

        if (parCool$transformer != null) {
            animation.animatePost(player, parCool$transformer);
            parCool$transformer = null;
        }
    }
}
