package com.kevinthegreat.organizableplayscreens.gui.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class OrganizablePlayScreensOptionsScreen extends Screen {
    private final Screen parent;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

    public OrganizablePlayScreensOptionsScreen(Screen parent) {
        super(Component.translatable("organizableplayscreens:options.title"));
        this.parent = parent;
    }

    public Screen getParent() {
        return parent;
    }

    @Override
    protected void init() {
        GridLayout gridWidget = new GridLayout().spacing(4);
        GridLayout.RowHelper adder = gridWidget.createRowHelper(2);
        adder.addChild(Button.builder(Component.translatable("organizableplayscreens:options.buttonDrag"), _ -> minecraft.gui.setScreen(new OrganizablePlayScreensButtonDragScreen(this))).width(98).build());
        adder.addChild(Button.builder(Component.translatable("organizableplayscreens:options.buttonOptions"), _ -> minecraft.gui.setScreen(new OrganizablePlayScreensButtonOptionsScreen(this))).width(98).build());
        layout.addToContents(gridWidget);

        layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, _ -> onClose()).width(200).build());

        layout.visitWidgets(this::addRenderableWidget);
        layout.arrangeElements();
    }

    @Override
    public void onClose() {
        minecraft.gui.setScreen(parent);
    }
}
