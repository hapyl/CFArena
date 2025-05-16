package me.hapyl.fight.guesswho.gui;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.annotate.OverridingMethodsMustImplementEvents;
import me.hapyl.fight.game.heroes.Affiliation;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroProfile;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.guesswho.GuessWho;
import me.hapyl.fight.guesswho.GuessWhoPlayer;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.util.ItemStacks;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class GuessWhoGUI extends StyledGUI {

    private static final int[] heroSlots =
            {
                    10, 11, 12, 13, 14, 15, 16,
                    19, 20, 21, 22, 23, 24, 25,
                    28, 29, 30, 31, 32, 33, 34,
                    37, 38, 39, 40, 41, 42, 43
            };

    protected final GuessWhoPlayer data;

    public GuessWhoGUI(GuessWhoPlayer data, String subMenu) {
        super(data.getPlayer(), menuArrowSplit("Guess Who", subMenu), Size.FIVE);

        this.data = data;
    }

    @EventLike
    public void onOpen() {
    }

    @Override
    @OverridingMethodsMustImplementEvents
    public void onUpdate() {
        fillRow(0, ItemStacks.BLACK_BAR);
        fillRow(5, ItemStacks.BLACK_BAR);

        final GuessWho game = data.getGame();
        final List<Hero> board = game.getBoard();

        for (int i = 0; i < board.size(); i++) {
            final Hero hero = board.get(i);
            final int slot = heroSlots[i];

            setItem(slot, getItem(hero));
            setClick(slot, player -> onClick(hero), ClickType.LEFT);
        }
    }

    @Override
    public boolean isSetCloseButton() {
        return false;
    }

    @Nonnull
    public ItemStack getItem(@Nonnull Hero heroes) {
        final ItemBuilder builder = createItem(heroes);

        if (data.isRuledOut(heroes)) {
            return new ItemBuilder(Material.GRAY_DYE)
                    .setName(heroes.getName())
                    .addLore()
                    .addLore("&cRuled out!")
                    .asIcon();
        }

        return builder.asIcon();
    }

    @Nonnull
    public ItemBuilder createItem(@Nonnull Hero hero) {
        final ItemBuilder builder = ItemBuilder.playerHeadUrl(hero.getTextureUrl());

        builder.setName(hero.getName());
        builder.addLore();

        final HeroProfile profile = hero.getProfile();
        final Affiliation affiliation = profile.getAffiliation();

        // General
        builder.addLore("Archetypes: ");

        for (Archetype archetype : profile.getArchetypes()) {
            builder.addLore(" %s".formatted(archetype.toString()));
        }

        if (affiliation == Affiliation.NOT_SET) {
            builder.addLore("&mAffiliation");
        }
        else {
            builder.addLore("Affiliation: " + affiliation);
        }

        builder.addLore("Gender: " + profile.getGender());
        builder.addLore("Race: " + profile.getRace());
        builder.addLore("Player Rating: " + hero.getAverageRating());

        // Talents
        final List<Talent> talents = hero.getTalentsSorted();

        builder.addLore();
        builder.addLore("Talents: &8(%s ᴀᴄᴛɪᴠᴇ)".formatted(hero.getActiveTalentsCount()));
        builder.addLore();

        int index = 1;
        for (Talent talent : talents) {
            if (talent == null) {
                continue;
            }

            if (index != 1) {
                builder.addLore("");
            }

            final String typeName = talent.getType().getName();

            if (talent instanceof PassiveTalent) {
                builder.addLore("ᴘᴀssɪᴠᴇ ᴛᴀʟᴇɴᴛ &8(%s)".formatted(typeName));
            }
            else {
                builder.addLore("%s ᴛᴀʟᴇɴᴛ &8(%s)".formatted(intToLiteral(index++), typeName));
                builder.addLore(" &f⌚ ᴄᴅ %s &7∣ &b※ ʀᴇɢᴇɴ %s".formatted(talent.getCooldownFormatted(), talent.getPoint()));
            }
        }

        final UltimateTalent ultimate = hero.getUltimate();

        builder.addLore();
        builder.addLore("ᴜʟᴛɪᴍᴀᴛᴇ &8(%s)".formatted(ultimate.getType().getName()));
        builder.addLore(" &f⌚ ᴄᴅ %s &7∣ &b※ ᴄᴏsᴛ %s".formatted(ultimate.getCooldownFormatted(), ultimate.cost()));

        return builder;
    }

    public abstract void onClick(@Nonnull Hero hero);

    private String intToLiteral(int i) {
        return switch (i) {
            case 1 -> "ғɪʀsᴛ";
            case 2 -> "sᴇᴄᴏɴᴅ";
            case 3 -> "ᴛʜɪʀᴅ";
            case 4 -> "ғᴏᴜʀᴛʜ";
            case 5 -> "ғɪғᴛʜ";
            case 6 -> "sɪxᴛʜ";
            default -> "ᴜɴᴋɴᴏᴡɴ";
        };
    }

}
