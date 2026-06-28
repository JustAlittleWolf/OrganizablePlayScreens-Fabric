package com.kevinthegreat.organizableplayscreens.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

import java.util.Optional;

@FunctionalInterface
public interface EntryWithIcon {
    /**
     * Gets a custom icon which will be prioritized over the default icon.
     */
    default Optional<Identifier> getCustomIcon() {
        return Optional.empty();
    }

    void renderIcon(GuiGraphicsExtractor context, int x, int y, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta, String name, int depth);
}
