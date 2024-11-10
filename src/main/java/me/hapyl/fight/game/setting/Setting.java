package me.hapyl.fight.game.setting;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.util.SmallCapsDescriber;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

public interface Setting extends Keyed, Described, SmallCapsDescriber {

    @Nonnull
    Material getMaterial();

    @Nonnull
    Category getCategory();

    boolean getDefaultValue();

    @OverridingMethodsMustInvokeSuper
    default void onEnable(@Nonnull Player player) {
        Chat.sendMessage(player, "&3&l⚙ &a%s is now &nenabled&a!".formatted(getNameSmallCaps()));
    }

    @OverridingMethodsMustInvokeSuper
    default void onDisabled(@Nonnull Player player) {
        Chat.sendMessage(player, "&3&l⚙ &c%s is now &ndisabled&c!".formatted(getNameSmallCaps()));
    }

    @Nonnull
    @Override
    String getName();

    @Nonnull
    @Override
    String getDescription();

    @Nonnull
    @Override
    String getNameSmallCaps();
}
