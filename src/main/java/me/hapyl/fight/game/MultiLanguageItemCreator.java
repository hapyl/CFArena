package me.hapyl.fight.game;

import com.google.common.collect.Maps;
import me.hapyl.fight.translate.Language;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Map;

public abstract class MultiLanguageItemCreator {

    protected final Map<Language, ItemStack> itemStackMap;

    public MultiLanguageItemCreator() {
        this.itemStackMap = Maps.newHashMap();
    }

    @Nonnull
    public abstract ItemStack createItem(@Nonnull Language language);

    public void appendLore(@Nonnull ItemBuilder builder, @Nonnull Language language) {
    }

    @Nonnull
    public final ItemStack getItem(@Nonnull Language language) {
        ItemStack item = itemStackMap.get(language);

        if (item == null) {
            item = createItem(language);
            itemStackMap.put(language, item);
        }

        return item;
    }
}
