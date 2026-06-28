package com.kevinthegreat.organizableplayscreens.gui.screen;

import com.kevinthegreat.organizableplayscreens.OrganizablePlayScreens;
import com.kevinthegreat.organizableplayscreens.option.BothSuppliableIntSliderCallbacks;
import com.kevinthegreat.organizableplayscreens.option.OrganizablePlayScreensOptions;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrganizablePlayScreensButtonDragScreen extends Screen {
    private static final Identifier MENU_LIST_BACKGROUND_TEXTURE = Identifier.withDefaultNamespace("textures/gui/menu_list_background.png");
    private static final Identifier INWORLD_MENU_LIST_BACKGROUND_TEXTURE = Identifier.withDefaultNamespace("textures/gui/inworld_menu_list_background.png");
    private static final int HEADER_HEIGHT = 33;
    private static final int FOOTER_HEIGHT = 60;

    private final Screen parent;
    private final OrganizablePlayScreensOptions options = OrganizablePlayScreens.getInstance().options;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, HEADER_HEIGHT, FOOTER_HEIGHT);
    private final List<Button> draggableButtons = new ArrayList<>();

    private double mouseClickRelativeX;
    private double mouseClickRelativeY;

    public OrganizablePlayScreensButtonDragScreen(Screen parent) {
        super(Component.translatable("organizableplayscreens:options.buttonDrag"));
        this.parent = parent;
    }

    public Screen getParent() {
        return parent;
    }

    @Override
    protected void init() {
        layout.addTitleHeader(title, font);

        draggableButtons.add(addRenderableWidget(Button.builder(Component.nullToEmpty("←"), _ -> {}).bounds(options.backButtonX.get(), options.backButtonY.get(), 20, 20).build()));
        draggableButtons.add(addRenderableWidget(Button.builder(Component.nullToEmpty("←+"), _ -> {}).bounds(options.moveEntryBackButtonX.get(), options.moveEntryBackButtonY.get(), 20, 20).tooltip(OrganizablePlayScreens.MOVE_ENTRY_BACK_TOOLTIP).build()));
        draggableButtons.add(addRenderableWidget(Button.builder(Component.nullToEmpty("+"), _ -> {}).bounds(options.getValue(options.newFolderButtonX), options.newFolderButtonY.get(), 20, 20).build()));

        SpriteIconButton buttonOptions = addRenderableWidget(SpriteIconButton.builder(Component.translatable("organizableplayscreens:options.optionsButton"), _ -> {}, true)
                .size(20, 20)
                .sprite(new WidgetSprites(
                        OrganizablePlayScreens.OPTIONS_BUTTON_ENABLED,
                        OrganizablePlayScreens.OPTIONS_BUTTON_DISABLED,
                        OrganizablePlayScreens.OPTIONS_BUTTON_FOCUSED
                ), 20, 20)
                .build()
        );
        buttonOptions.setPosition(options.getValue(options.optionsButtonX), options.optionsButtonY.get());
        draggableButtons.add(buttonOptions);

        layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, _ -> onClose()).width(200).build());

        layout.visitWidgets(this::addRenderableWidget);
        layout.arrangeElements();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
        context.blit(RenderPipelines.GUI_TEXTURED, minecraft.level == null ? MENU_LIST_BACKGROUND_TEXTURE : INWORLD_MENU_LIST_BACKGROUND_TEXTURE, 0, layout.getHeaderHeight(), width, height - layout.getFooterHeight(), width, height - layout.getHeaderHeight() - layout.getFooterHeight(), 32, 32);
        context.blit(RenderPipelines.GUI_TEXTURED, minecraft.level == null ? Screen.HEADER_SEPARATOR : Screen.INWORLD_HEADER_SEPARATOR, 0, layout.getHeaderHeight() - 2, 0, 0, width, 2, 32, 2);
        context.blit(RenderPipelines.GUI_TEXTURED, minecraft.level == null ? Screen.FOOTER_SEPARATOR : Screen.INWORLD_FOOTER_SEPARATOR, 0, height - layout.getFooterHeight(), 0, 0, width, 2, 32, 2);

        super.extractRenderState(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        Optional<GuiEventListener> element = getChildAt(click.x(), click.y());
        if (element.isPresent() && element.get() instanceof Button button && draggableButtons.contains(button)) {
            mouseClickRelativeX = click.x() - button.getX();
            mouseClickRelativeY = click.y() - button.getY();
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent click, double offsetX, double offsetY) {
        if (getFocused() instanceof Button button && draggableButtons.contains(button)) {
            int x = (int) Math.clamp(click.x() - mouseClickRelativeX, 0, width - button.getWidth());
            int y = (int) Math.clamp(click.y() - mouseClickRelativeY, 0, height - button.getHeight());

            button.setPosition(x, y);
            if (button == draggableButtons.get(0)) {
                setOption(options.backButtonX, x);
                setOption(options.backButtonY, y);
            } else if (button == draggableButtons.get(1)) {
                setOption(options.moveEntryBackButtonX, x);
                setOption(options.moveEntryBackButtonY, y);
            } else if (button == draggableButtons.get(2)) {
                setOption(options.newFolderButtonX, x);
                setOption(options.newFolderButtonY, y);
            } else if (button == draggableButtons.get(3)) {
                setOption(options.optionsButtonX, x);
                setOption(options.optionsButtonY, y);
            }
            return true;
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    private void setOption(OptionInstance<Integer> option, int value) {
        if (option.values() instanceof BothSuppliableIntSliderCallbacks bothSuppliableCallbacks) {
            option.set(bothSuppliableCallbacks.displayValueParser().apply(String.valueOf(value)));
        } else {
            option.set(value);
        }
    }

    @Override
    public void onClose() {
        minecraft.gui.setScreen(parent);
    }

    @Override
    public void removed() {
        options.save();
    }
}
