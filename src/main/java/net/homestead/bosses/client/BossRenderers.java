package net.homestead.bosses.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.homestead.bosses.HomesteadBosses;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = HomesteadBosses.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BossRenderers {
    public static void register(FMLClientSetupEvent event) { }

    @SubscribeEvent public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // modelScale maps the player model's ~1.8-block native height onto each boss's actual
        // hitbox height (sized() in HomesteadBosses) so the model you see matches the hitbox
        // you fight, instead of a normal-sized player floating inside an oversized box.
        event.registerEntityRenderer(HomesteadBosses.PUMPKIN_KING.get(), ctx -> new BossRenderer<>(ctx, "pumpkin_king", 1.7f, 0.9f));
        event.registerEntityRenderer(HomesteadBosses.BAKERS_BANE.get(), ctx -> new BossRenderer<>(ctx, "bakers_bane", 1.55f, 0.85f));
        event.registerEntityRenderer(HomesteadBosses.WHEAT_HARVESTER.get(), ctx -> new BossRenderer<>(ctx, "wheat_harvester", 1.9f, 1.0f));
        event.registerEntityRenderer(HomesteadBosses.PUMPKIN_MINION.get(), ctx -> new BossRenderer<>(ctx, "pumpkin_minion", 0.8f, 0.4f));
    }

    private static class BossRenderer<T extends net.minecraft.world.entity.Mob> extends HumanoidMobRenderer<T, HumanoidModel<T>> {
        private final ResourceLocation texture;
        private final float modelScale;

        BossRenderer(EntityRendererProvider.Context ctx, String name, float modelScale, float shadowRadius) {
            super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER)), shadowRadius);
            this.texture = new ResourceLocation(HomesteadBosses.MODID, "textures/entity/" + name + ".png");
            this.modelScale = modelScale;
        }

        @Override public ResourceLocation getTextureLocation(T entity) { return texture; }

        @Override protected void scale(T entity, PoseStack poseStack, float partialTickTime) {
            poseStack.scale(modelScale, modelScale, modelScale);
        }
    }
}
