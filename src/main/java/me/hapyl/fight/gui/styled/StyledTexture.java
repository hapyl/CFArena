package me.hapyl.fight.gui.styled;

import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public final class StyledTexture extends StyledItem {

    public static final StyledTexture ARROW_LEFT = new StyledTexture(
            "b76230a0ac52af11e4bc84009c6890a4029472f3947b4f465b5b5722881aacc7"
    );

    public static final StyledTexture ARROW_RIGHT = new StyledTexture(
            "dbf8b6277cd36266283cb5a9e6943953c783e6ff7d6a2d59d15ad0697e91d43c"
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

    public StyledTexture(String texture) {
        this(texture, "", "");
    }

    private StyledTexture(Material material, String name, String description) {
        super(material, name, description);
    }

    private StyledTexture(String texture, String name, String description) {
        super(texture, name, description);
    }

    @Nonnull
    public ItemBuilder toBuilder() {
        return ItemBuilder.playerHeadUrl(texture);
    }

}
