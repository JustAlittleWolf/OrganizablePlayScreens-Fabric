package com.kevinthegreat.organizableplayscreens.mixin.accessor;

import com.mojang.blaze3d.platform.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.nio.channels.WritableByteChannel;

@Mixin(NativeImage.class)
public interface NativeImageInvoker {
    @SuppressWarnings("UnusedReturnValue")
    @Invoker
    boolean invokeWriteToChannel(WritableByteChannel output);
}
