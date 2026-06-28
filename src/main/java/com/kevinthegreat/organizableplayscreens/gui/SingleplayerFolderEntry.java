package com.kevinthegreat.organizableplayscreens.gui;

import com.kevinthegreat.organizableplayscreens.OrganizablePlayScreens;
import com.kevinthegreat.organizableplayscreens.api.EntryType;
import com.kevinthegreat.organizableplayscreens.mixin.accessor.SelectWorldScreenAccessor;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SingleplayerFolderEntry extends AbstractSingleplayerEntry implements AbstractFolderEntry<WorldSelectionList, WorldSelectionList.Entry> {
    /**
     * All non-world entries in this folder.
     */
    @NotNull
    private final List<AbstractSingleplayerEntry> nonWorldEntries;
    /**
     * All world entries in this folder.
     */
    @NotNull
    private final List<WorldSelectionList.WorldListEntry> worldEntries;
    /**
     * This button moves the selected entry into this folder.
     */
    private final Button buttonMoveInto;

    public SingleplayerFolderEntry(@NotNull SelectWorldScreen screen, @Nullable SingleplayerFolderEntry parent) {
        this(screen, parent, I18n.get("organizableplayscreens:entry.new", EntryType.FOLDER.text().getString()), null);
    }

    public SingleplayerFolderEntry(@NotNull SelectWorldScreen screen, @Nullable SingleplayerFolderEntry parent, @NotNull String name, @Nullable NativeImage customIconImage) {
        this(screen, parent, name, customIconImage, new ArrayList<>(), new ArrayList<>());
    }

    public SingleplayerFolderEntry(@NotNull SelectWorldScreen screen, @Nullable SingleplayerFolderEntry parent, @NotNull String name, @Nullable NativeImage customIconImage, @NotNull List<AbstractSingleplayerEntry> nonWorldEntries, @NotNull List<WorldSelectionList.WorldListEntry> worldEntries) {
        super(screen, parent, EntryType.FOLDER, name, customIconImage);
        this.nonWorldEntries = nonWorldEntries;
        this.worldEntries = worldEntries;
        buttonMoveInto = Button.builder(Component.nullToEmpty("+"), _ -> {
            WorldSelectionList levelList = ((SelectWorldScreenAccessor) screen).getList();
            WorldSelectionList.Entry entry = levelList.getSelected();
            if (entry instanceof WorldSelectionList.WorldListEntry worldEntry) {
                levelList.organizableplayscreens_getWorlds().put(worldEntry, this);
                worldEntries.add(worldEntry);
                OrganizablePlayScreens.sortWorldEntries(worldEntries);
                levelList.organizableplayscreens_getCurrentWorldEntries().remove(worldEntry);
            } else if (entry instanceof AbstractSingleplayerEntry nonWorldEntry) {
                nonWorldEntry.parent = this;
                nonWorldEntries.add(nonWorldEntry);
                levelList.organizableplayscreens_getCurrentNonWorldEntries().remove(nonWorldEntry);
            }
            levelList.setSelected(null);
            levelList.organizableplayscreens_updateAndSave();
        }).width(20).tooltip(OrganizablePlayScreens.MOVE_ENTRY_INTO_TOOLTIP).build();
    }

    public @NotNull List<AbstractSingleplayerEntry> getNonWorldEntries() {
        return nonWorldEntries;
    }

    public @NotNull List<WorldSelectionList.WorldListEntry> getWorldEntries() {
        return worldEntries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WorldSelectionList.Entry> getEntries() {
        ArrayList<WorldSelectionList.Entry> entries = new ArrayList<>();
        entries.addAll(getNonWorldEntries());
        entries.addAll(getWorldEntries());
        return entries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Button getButtonMoveInto() {
        return buttonMoveInto;
    }

    @Override
    public void entrySelectionConfirmed(WorldSelectionList levelList) {
        super.entrySelectionConfirmed(levelList);
        levelList.organizableplayscreens_setCurrentFolder(this);
    }

    /**
     * Handles mouse clicks for this folder.
     * <p>
     * Calls mouse click on {@link #buttonMoveInto} and {@link AbstractSingleplayerEntry#mouseClicked(MouseButtonEvent, boolean)}
     */
    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        if (buttonMoveInto.mouseClicked(click, doubled)) {
            return true;
        }
        return super.mouseClicked(click, doubled);
    }
}
