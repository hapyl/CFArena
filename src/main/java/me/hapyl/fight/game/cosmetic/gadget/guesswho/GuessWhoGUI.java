package me.hapyl.fight.game.cosmetic.gadget.guesswho;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.annotate.OverridingMethodsMustImplementEvents;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroProfile;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.util.ItemStacks;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

public abstract class GuessWhoGUI extends StyledGUI {
    
    protected static final int[] heroSlots = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };
    
    protected final GuessWhoPlayer player;
    
    public GuessWhoGUI(GuessWhoPlayer player, Supplier<String> name) {
        super(player.getPlayer(), name.get(), Size.FIVE);
        
        this.player = player;
    }
    
    public GuessWhoGUI(GuessWhoPlayer player, String subMenu) {
        this(player, () -> menuArrowSplit("Guess Who", subMenu));
    }
    
    @Override
    @OverridingMethodsMustImplementEvents
    public void onUpdate() {
        super.onUpdate();
        
        fillRow(0, ItemStacks.BLACK_BAR);
        fillRow(5, ItemStacks.BLACK_BAR);
        
        final GuessWhoActivity game = player.getGame();
        final List<Hero> board = game.board();
        
        for (int i = 0; i < board.size(); i++) {
            final Hero hero = board.get(i);
            final int slot = heroSlots[i];
            
            setItem(slot, getItem(hero));
            setAction(slot, player -> onClick(hero), ClickType.LEFT);
        }
    }
    
    @Override
    public boolean isSetCloseButton() {
        return false;
    }
    
    @Nonnull
    public ItemStack getItem(@Nonnull Hero hero) {
        final ItemBuilder builder = createItem(hero);
        
        if (player.hasRuledOut(hero)) {
            return new ItemBuilder(Material.GRAY_DYE)
                    .setName(hero.getName())
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
        
        // Profile
        builder.addLore(Color.DEFAULT.bold() + "ᴘʀᴏꜰɪʟᴇ");
        builder.addLore(" &7Archetypes: ");
        
        profile.iterateArchetypes((i, archetype) -> {
            builder.addLore("   &7%s%s".formatted(archetype.toString(), i == 0 ? " &a&l⭐" : ""));
        });
        
        builder.addLore(" &7Affiliation: " + profile.getAffiliation());
        builder.addLore(" &7Gender: " + profile.getGender());
        builder.addLore(" &7Race: " + profile.getRace());
        builder.addLore(" &7Player Rating: " + hero.getAverageRating());
        
        // Talents
        final List<Talent> talents = hero.getActiveTalents();
        
        builder.addLore();
        builder.addLore(Color.DEFAULT.bold() + "ᴛᴀʟᴇɴᴛꜱ &8(%s active)".formatted(hero.getActiveTalentsCount()));
        
        // Display in this order:
        //  Active
        //  Ultimate
        //  Passive
        // Passive last because it doesn't have stats, so it doesn't look weird in the GUI
        
        int index = 0;
        for (Talent talent : talents) {
            if (talent == null) {
                continue;
            }
            
            if (index++ != 0) {
                builder.addLore("");
            }
            
            builder.addLore(formatTalent(index, talent));
            builder.addLore(formatTalentDetails(talent));
        }
        
        // Add ultimate
        final UltimateTalent ultimate = hero.getUltimate();
        
        builder.addLore();
        builder.addLore(formatTalent(-1, ultimate));
        builder.addLore(formatTalentDetails(ultimate));
        
        // Add passive last
        final Talent passive = hero.getPassiveTalent();
        
        if (passive != null) {
            builder.addLore();
            builder.addLore(formatTalent(-2, passive));
        }
        
        return builder;
    }
    
    public abstract void onClick(@Nonnull Hero hero);
    
    private static String formatTalent(int index, Talent talent) {
        final String stringIndex = switch (index) {
            case -1 -> "ULT";
            case -2 -> "PAS";
            default -> "%03d".formatted(index);
        };
        
        return " &8%s &2%s &8(%s)".formatted(stringIndex, talent.getName(), talent.getTypeAsString());
    }
    
    private static String formatTalentDetails(Talent talent) {
        final String suffix;
        final double suffixValue;
        
        if (talent instanceof UltimateTalent ultimateTalent) {
            suffix = "ᴄᴏsᴛ";
            suffixValue = ultimateTalent.cost();
        }
        else {
            suffix = "ʀᴇɢᴇɴ";
            suffixValue = talent.getPoint();
        }
        
        return "  &f &f⌚ ᴄᴅ %s &7∣ &b※ %s %.0f".formatted(talent.getCooldownFormatted(), suffix, suffixValue);
    }
    
}
