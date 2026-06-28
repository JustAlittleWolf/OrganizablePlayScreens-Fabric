package com.kevinthegreat.organizableplayscreens.mixin.accessor;

import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FaviconTexture.class)
public interface FaviconTextureAccessor {
    @Accessor
    @Nullable DynamicTexture getTexture();

    @Invoker("<init>")
    static FaviconTexture create(TextureManager textureManager, Identifier identifier) {
        throw new IllegalStateException("Mixin invoker failed to apply");
    }
}
