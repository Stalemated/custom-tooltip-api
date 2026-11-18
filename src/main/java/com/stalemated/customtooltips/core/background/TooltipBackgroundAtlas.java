package com.stalemated.customtooltips.core.background;

import com.stalemated.customtooltips.CustomTooltips;
import com.stalemated.lib.util.ResourceIds;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public final class TooltipBackgroundAtlas {
    public static final ResourceLocation LOCATION = ResourceIds.of(CustomTooltips.ASSETS_NAMESPACE, "textures/atlas/tooltip_backgrounds.png");
    private static final ResourceLocation DEFINITION = ResourceIds.of(CustomTooltips.ASSETS_NAMESPACE, "tooltip_backgrounds");

    private static Atlas atlas;

    private TooltipBackgroundAtlas() {
    }

    public static CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier barrier, ResourceManager manager, ProfilerFiller preparationProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        if (atlas == null) atlas = new Atlas(Minecraft.getInstance().getTextureManager());
        return atlas.reload(barrier, manager, preparationProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
    }

    static TextureAtlasSprite sprite(ResourceLocation spriteId) {
        return atlas != null ? atlas.sprite(spriteId) : null;
    }

    private static final class Atlas extends TextureAtlasHolder {
        private Atlas(TextureManager textureManager) {
            super(textureManager, LOCATION, DEFINITION);
        }

        private TextureAtlasSprite sprite(ResourceLocation spriteId) {
            return getSprite(spriteId);
        }
    }
}
