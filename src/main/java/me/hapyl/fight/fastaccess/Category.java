package me.hapyl.fight.fastaccess;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.color.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public enum Category implements Described {

    SELECT_HERO(
            Material.PLAYER_HEAD, """
            Changes the current hero of yours.
            """
    ) {
        @Override
        public ItemBuilder getItem(@Nonnull Player player) {
            return super.getItem(player)
                        .setHeadTextureUrl(CF.getProfile(player).getHero().getTextureUrl());
        }
    },
    SELECT_MAP(
            Material.FILLED_MAP, """
            Changes the currently selected map.
            
            &cRequires %s&c or higher.
            """.formatted(PlayerRank.GAME_MANAGER.getPrefix())
    ),
    JOIN_TEAM(
            Material.WHITE_BANNER, """
            Changes the current team of yours.
            """
    ),
    TOGGLE_SETTING(
            Material.COMPARATOR, """
            Toggles a specific setting.
            """
    ),
    SELECT_GADGET(
            Material.LEVER, """
            Select a gadget to play around with.
            """
    );

    private final Material material;
    private final String name;
    private final String description;

    Category(@Nonnull Material material, @Nonnull String description) {
        this.name = Chat.capitalize(this);
        this.description = description;
        this.material = material;
    }


    public ItemBuilder getItem(@Nonnull Player player) {
        return new ItemBuilder(material)
                .setName(name)
                .addLore("&8Category")
                .addLore()
                .addTextBlockLore(description)
                .addLore()
                .addLore(Color.BUTTON + "Click to select!");
    }

    @Nonnull
    @Override
    public String getName() {
        return this.name;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return name;
    }
}
