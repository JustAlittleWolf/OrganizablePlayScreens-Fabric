package com.kevinthegreat.organizableplayscreens.mixin;

import com.kevinthegreat.organizableplayscreens.OrganizablePlayScreens;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @WrapOperation(method = "setScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;removed()V"))
    private void organizableplayscreens_passNewScreenToRemoved(Screen screen, Operation<Void> original, @Local(argsOnly = true, name = "screen") Screen newScreen) {
        ScopedValue.where(OrganizablePlayScreens.REMOVED_NEW_SCREEN, newScreen).run(() -> original.call(screen));
    }
}
