package me.hapyl.fight.game.setting;

import me.hapyl.fight.CF;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.display.Display;
import me.hapyl.fight.display.Displayed;
import me.hapyl.fight.enumclass.EnumItem;
import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.registry.PatternId;
import me.hapyl.fight.util.PlayerItemCreator;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.util.Enums;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

public class Setting<E extends Enum<E> & EnumSetting> extends PatternId implements Displayed, EnumItem, PlayerItemCreator {

    private static final Pattern PATTERN = Pattern.compile("^[A-Z_]+$");

    private final Display display;
    private final Category category;
    private final Class<E> clazz;
    private final E defaultValue;
    private final int ordinal;

    public Setting(String id, Display display, Category category, Class<E> clazz) {
        this(id, display, category, clazz, clazz.getEnumConstants()[0]);
    }

    public Setting(String id, Display display, Category category, Class<E> clazz, E defaultValue) {
        super(PATTERN, id);
        this.display = display;
        this.category = category;
        this.clazz = clazz;
        this.defaultValue = defaultValue;

        // register
        this.ordinal = Settings.register(this);
    }

    @Nonnull
    public Class<E> getClazz() {
        return clazz;
    }

    @Nonnull
    @Override
    public ItemBuilder create(@Nonnull Player player) {
        final boolean isEnabled = true;

        final ItemBuilder builder = new ItemBuilder(getMaterial())
                .setName((isEnabled ? Color.SUCCESS : Color.ERROR) + getName())
                .addLore("&8%s Setting", Chat.capitalize(getCategory()))
                .addLore()
                .addTextBlockLore(getDescription())
                .addLore();

        if (isEnabled) {
            builder.addLore("&a&lCURRENTLY ENABLED!");
            builder.addLore(Color.BUTTON + "Click to disable!");
        }
        else {
            builder.addLore("&c&lCURRENTLY DISABLED!");
            builder.addLore(Color.BUTTON + "Click to enable!");
        }

        return builder.predicate(isEnabled, ItemBuilder::glow);
    }

    @Nonnull
    public E getValue(@Nonnull Player player) {
        final PlayerDatabase database = CF.getDatabase(player);
        final E value = Enums.byName(clazz, database.getSettings().getValue(this));

        if (value != null) {
            return value;
        }

        return defaultValue;
    }

    public void setValue(@Nonnull Player player, @Nonnull E value) {
        final PlayerDatabase database = CF.getDatabase(player);

        database.getSettings().setValue(this, value);
    }

    public boolean isValue(@Nonnull Player player, @Nonnull E enumBool) {
        return getValue(player) == enumBool;
    }

    @Deprecated
    public boolean isEnabled(Player player) {
        if (!(defaultValue instanceof EnumBool)) {
            throw new UnsupportedOperationException("not supported");
        }

        return getValue(player) == EnumBool.ENABLED;
    }

    @Deprecated
    public boolean isDisabled(Player player) {
        if (!(defaultValue instanceof EnumBool)) {
            throw new UnsupportedOperationException("deprecated");
        }

        return getValue(player) == EnumBool.DISABLED;
    }

    @Nonnull
    @Override
    public String name() {
        return getId();
    }

    @Override
    public int ordinal() {
        return ordinal;
    }

    @Nonnull
    @Override
    public Display getDisplay() {
        return display;
    }

    @Nonnull
    public Category getCategory() {
        return category;
    }

    @Nonnull
    public E getDefaultValue() {
        return defaultValue;
    }

    @Event
    public void onValueSet(@Nonnull Player player, @Nonnull E newValue) {
    }

}
