package me.hapyl.fight.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.util.UUID;
import java.util.function.Consumer;

public class ItemStackRandomizedData extends ItemStack {

    ItemStackRandomizedData(@Nonnull Material type, @Nullable String texture) {
        super(type, 1);

        // Set texture if player head
        if (texture != null) {
            meta(SkullMeta.class, meta -> {
                final PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());

                try {
                    final PlayerTextures textures = profile.getTextures();
                    textures.setSkin(new URI("https://textures.minecraft.net/texture/" + texture).toURL());

                    profile.setTextures(textures);
                    meta.setPlayerProfile(profile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // Randomize by setting a unique name
        meta(ItemMeta.class, meta -> {
            meta.displayName(Component.text(UUID.randomUUID().toString()));
        });
    }

    @Nonnull
    public static ItemStack of(@Nonnull Material material) {
        return new ItemStackRandomizedData(material, null);
    }

    @Nonnull
    public static ItemStack of(@Nonnull String headTexture) {
        return new ItemStackRandomizedData(Material.PLAYER_HEAD, headTexture);
    }

    private <T extends ItemMeta> void meta(Class<T> clazz, Consumer<T> consumer) {
        final T meta = clazz.cast(getItemMeta());

        consumer.accept(meta);
        setItemMeta(meta);
    }


}
