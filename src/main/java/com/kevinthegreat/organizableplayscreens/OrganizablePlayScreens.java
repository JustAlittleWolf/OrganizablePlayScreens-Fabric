package com.kevinthegreat.organizableplayscreens;

import com.kevinthegreat.organizableplayscreens.api.EntryType;
import com.kevinthegreat.organizableplayscreens.mixin.WorldSelectionListMixin;
import com.kevinthegreat.organizableplayscreens.mixin.accessor.FaviconTextureAccessor;
import com.kevinthegreat.organizableplayscreens.option.OrganizablePlayScreensOptions;
import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class OrganizablePlayScreens implements ModInitializer {
    public static final String MOD_ID = "organizableplayscreens";
    public static final String MOD_NAME = "Organizable Play Screens";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Identifier OPTIONS_BUTTON_ENABLED = Identifier.fromNamespaceAndPath(MOD_ID, "options/enabled");
    public static final Identifier OPTIONS_BUTTON_DISABLED = Identifier.fromNamespaceAndPath(MOD_ID, "options/disabled");
    public static final Identifier OPTIONS_BUTTON_FOCUSED = Identifier.fromNamespaceAndPath(MOD_ID, "options/focused");
    public static final Tooltip MOVE_ENTRY_INTO_TOOLTIP = Tooltip.create(Component.translatable(MOD_ID + ":folder.moveInto"));
    public static final Tooltip MOVE_ENTRY_BACK_TOOLTIP = Tooltip.create(Component.translatable(MOD_ID + ":folder.moveBack"));
    private static OrganizablePlayScreens instance;
    public final OrganizablePlayScreensOptions options;

    public OrganizablePlayScreens() {
        instance = this;
        options = new OrganizablePlayScreensOptions();
    }

    public static OrganizablePlayScreens getInstance() {
        return instance;
    }

    @Override
    public void onInitialize() {
        LOGGER.info(MOD_NAME + " initialized.");
    }

    /**
     * Sorts a list of {@link WorldSelectionList.WorldListEntry} with {@link net.minecraft.world.level.storage.LevelSummary#compareTo(net.minecraft.world.level.storage.LevelSummary)}.
     *
     * @param worldEntries The list of {@link WorldSelectionList.WorldListEntry} to sort.
     */
    public static void sortWorldEntries(List<WorldSelectionList.WorldListEntry> worldEntries) {
        worldEntries.sort(Comparator.comparing(worldEntry -> ((WorldSelectionListMixin.WorldEntryAccessor) worldEntry).getSummary()));
    }

    public static void updateEntryNbt(CompoundTag nbtEntry, boolean multiplayer) {
        if (nbtEntry.getString("type").isEmpty()) {
            nbtEntry.putString("type", nbtEntry.getBooleanOr("type", false) ? EntryType.FOLDER.id().toString() : multiplayer ? "minecraft:server" : "minecraft:world");
        }
    }

    public static NativeImage readCustomIcon(CompoundTag nbtEntry) {
        return nbtEntry.getByteArray("customIcon").map(customIconBytes -> {
            try {
                return NativeImage.read(customIconBytes);
            } catch (IOException e) {
                LOGGER.error("Failed to read custom icon for entry {}", nbtEntry, e);
            }
            return null;
        }).orElse(null);
    }

    public static <E extends ObjectSelectionList.Entry<E>> @Nullable FaviconTexture uploadCustomIcon(E entry, @Nullable NativeImage customIconImage) {
        if (customIconImage == null) return null;

        try {
            FaviconTexture texture = FaviconTextureAccessor.create(Minecraft.getInstance().getTextureManager(), Identifier.fromNamespaceAndPath(MOD_ID, "custom_icon/" + entry.hashCode()));
            texture.upload(customIconImage);
            return texture;
        } catch (Exception e) {
            LOGGER.error("Invalid icon for entry {}", entry, e);
        }
        return null;
    }

    public static @Nullable NativeImage copyCustomIcon(@Nullable FaviconTexture oldCustomIconTexture) {
        if (oldCustomIconTexture == null || ((FaviconTextureAccessor) oldCustomIconTexture).getTexture() == null) return null;

        NativeImage oldImage = ((FaviconTextureAccessor) oldCustomIconTexture).getTexture().getPixels();
        NativeImage newImage = new NativeImage(oldImage.format(), oldImage.getWidth(), oldImage.getHeight(), false);
        newImage.copyFrom(oldImage);
        return newImage;
    }
}
