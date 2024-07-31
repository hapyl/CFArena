package me.hapyl.fight.terminology;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledItem;
import me.hapyl.fight.gui.styled.StyledPageGUI;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.Response;
import me.hapyl.spigotutils.module.inventory.SignGUI;
import me.hapyl.spigotutils.module.inventory.gui.StrictAction;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TermCollectionGUI extends StyledPageGUI<Term> {

    @Nullable private String query;

    public TermCollectionGUI(Player player, @Nullable String query) {
        super(player, "Terminology", Size.FOUR);

        setQuery(query);
    }

    @Override
    public void onUpdate() {
        setHeader(StyledItem.ICON_TERMS.asIcon());

        // Search
        setItem(
                42,
                new ItemBuilder(Material.OAK_SIGN).setName("Search Terms")
                        .addLore()
                        .addLore("Current Query: " + (query != null ? query : "&8None!"))
                        .addLore()
                        .addLore(Color.BUTTON + "Click to search.")
                        .addLoreIf(Color.BUTTON_DARKER + "Right Click to clear.", query != null)
                        .asIcon(),
                new StrictAction() {
                    @Override
                    public void onLeftClick(@Nonnull Player player) {
                        new SignGUI(player, "Enter Query") {
                            @Override
                            public void onResponse(Response response) {
                                runSync(() -> setQuery(response.getAsString()));
                            }
                        };
                    }

                    @Override
                    public void onRightClick(@Nonnull Player player) {
                        if (query == null) {
                            return;
                        }

                        setQuery(null);
                    }
                }
        );
    }

    @Nonnull
    @Override
    public ItemStack asItem(@Nonnull Player player, Term content, int index, int page) {
        final ItemBuilder builder = new ItemBuilder(Material.CREEPER_BANNER_PATTERN);

        builder.setName(content.getName());
        builder.addLore();
        builder.addTextBlockLore(content.getShortDescription());
        builder.addLore();
        builder.addLore(Color.BUTTON + "Click for more details!");

        return builder.asIcon();
    }

    @Override
    public void onClick(@Nonnull Player player, @Nonnull Term content, int index, int page, @Nonnull ClickType clickType) {
        new TermGUI(player, content, query);
    }

    private void setQuery(@Nullable String query) {
        List<Term> terms;

        if (query == null) {
            terms = Terms.listTerms();
        }
        else {
            terms = Terms.byContext(query);
        }

        this.query = query;

        // Update empty contents items because query changed
        setEmptyContentsItem(new ItemBuilder(Material.RED_DYE).setName("&cNo matching terms!")
                .addLore()
                .addSmartLore("There are not terms matching the query '%s'!".formatted(query))
                .asIcon());

        setContents(terms);
        openInventory(1);
    }
}
