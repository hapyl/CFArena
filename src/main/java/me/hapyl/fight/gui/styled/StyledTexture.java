package me.hapyl.fight.gui.styled;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.game.collectible.relic.Type;

import javax.annotation.Nonnull;

public final class StyledTexture extends StyledItem {

    public static final StyledTexture ARROW_LEFT = new StyledTexture(
            "b76230a0ac52af11e4bc84009c6890a4029472f3947b4f465b5b5722881aacc7"
    );

    public static final StyledTexture ARROW_RIGHT = new StyledTexture(
            "dbf8b6277cd36266283cb5a9e6943953c783e6ff7d6a2d59d15ad0697e91d43c"
    );

    public static final StyledTexture QUESTION = new StyledTexture(
            "46ba63344f49dd1c4f5488e926bf3d9e2b29916a6c50d610bb40a5273dc8c82",
            "???", ""
    );

    public static final StyledTexture ACHIEVEMENT_TIERED_COMPLETE = new StyledTexture(
            "dbfc7fa4e74b6e401ae064148ae5e768b893f1d9297f87475101f7225a2a5a6a"
    );

    public static final StyledTexture ACHIEVEMENT_TIERED_INCOMPLETE = new StyledTexture(
            "d7f5766d2928dc0df1b3404c3bd073c9476d26c80573b0332e7cce73df15482a"
    );

    public static final StyledTexture CRATE_CONVERT = new StyledTexture(
            "d5c6dc2bbf51c36cfc7714585a6a5683ef2b14d47d8ff714654a893f5da622"
    );

    public static final StyledTexture THE_EYE = new StyledTexture(
            "a88b1cd9574672e8e3262f210c0dddbc082ea7569e8e70f0c07b4bee75e32f62",
            "&dThe Eye",
            "&8Overseer."
    );

    public static final StyledTexture RELIC_HUNT = new StyledTexture(
            Type.AMETHYST.getTexture(),
            "&dRelic Hunt", """
            &7&o;;There are relics scattered all over the world.
                        
            &7&o;;Help me see them again, and I shall grant you rewards.
            """
    );

    public static final StyledTexture DAILY = new StyledTexture(
            "86f1c9ecbcd49842dcbe9a3d85abba6479ea46bdd424dbd9d0ef54c28bf502d7",
            "&aDaily Bonds", """
            &7&o;;You and I have bonds.
                        
            &7&o;;Experience them to gain wisdom.
            """
    );

    public static final StyledTexture CHEST = new StyledTexture(
            "47ec41e0df8e170d97f9b9af1d65edad4979c78c89b01b180f389ee08a61af82"
    );

    public static final StyledTexture CHEST_EMERALD = new StyledTexture(
            "4ba55671f97ff3bfc5be335ae92cd9749abd619e7afc2a6673597b80b755c741"
    );

    public static final StyledTexture CHEST_DIAMOND = new StyledTexture(
            "31f7cdfea2d21cd5f6ebbf48481761c6cbdf36d00fe64083686e9aeaa3f1f217"
    );

    public static final StyledTexture TNT = new StyledTexture(
            "94f90c7bd60bfd0dfc31808d0484d8c2db9959f68df91fbf29423a3da62429a6"
    );

    public StyledTexture(String texture) {
        this(texture, null, null);
    }

    private StyledTexture(String texture, String name, String description) {
        super(texture, name, description);
    }

    @Nonnull
    public ItemBuilder toBuilderClean() {
        return ItemBuilder.playerHeadUrl(texture);
    }

    @Nonnull
    public ItemBuilder toBuilder() {
        final ItemBuilder builder = ItemBuilder.playerHeadUrl(texture);

        if (name != null) {
            builder.setName(name);
        }

        if (description != null) {
            builder.addTextBlockLore(description);
        }

        return builder;
    }

}
