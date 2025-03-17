package me.hapyl.fight.terminology;

import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledTexture;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public class TermGUI extends StyledGUI {

    private final Term term;
    @Nullable private final String query;

    public TermGUI(Player player, Term term) {
        this(player, term, null);
    }

    public TermGUI(Player player, Term term, @Nullable String query) {
        super(player, "Term: '%s'".formatted(term.getName()), Size.FOUR);

        this.term = term;
        this.query = query;

        openInventory();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of((query != null ? "Term Search: '%s'".formatted(query) : "Term Search"), player -> new TermCollectionGUI(player, query));
    }

    @Override
    public void onUpdate() {
        setHeader(StyledTexture.ICON_TERMS.asIcon());

        setItem(22, new ItemBuilder(Material.CREEPER_BANNER_PATTERN)
                .setName(term.getName())
                .addLore("")
                .addTextBlockLore(term.getDescription())
                .asIcon());
    }
}
