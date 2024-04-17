package me.hapyl.fight.gui;

import me.hapyl.fight.database.collection.HeroStatsCollection;
import me.hapyl.fight.game.heroes.GlobalHeroStats;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.techie.Talent;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.util.Numeric;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.SlotPattern;
import me.hapyl.spigotutils.module.inventory.gui.SmartComponent;
import me.hapyl.spigotutils.module.math.Numbers;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public class HeroStatisticGUI extends StyledGUI {

    private final Heroes heroes;
    private final int index;
    private final HeroStatsCollection stats;
    private final GlobalHeroStats globalStats;

    public HeroStatisticGUI(Player player, Heroes heroes, int index) {
        super(player, heroes.getName() + " Statistics", Size.FIVE);
        this.heroes = heroes;
        this.index = index;
        this.stats = heroes.getStats();
        this.globalStats = Heroes.getGlobalStats();

        openInventory();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Hero Preview - " + heroes.getName(), fn -> new HeroPreviewGUI(player, heroes, index));
    }

    @Override
    public void onUpdate() {
        final Hero hero = heroes.getHero();

        setHeader(
                new ItemBuilder(hero.getItem()).setName(heroes.getName())
                        .addLore()
                        .addLore("Archetype: " + hero.getArchetype())
                        .addLore()
                        .addTextBlockLore(hero.getDescription(), "&8&o", ItemBuilder.DEFAULT_SMART_SPLIT_CHAR_LIMIT)
                        .addLore()
                        .addSmartLore("This hero is ranked &b#%s&7.".formatted(hero.getRank()))
                        .addSmartLore(
                                "Heroes are ranked by the average of the stats. This is not indicative of the hero's strength.",
                                "&8&o"
                        )
                        .asIcon()
        );

        final SmartComponent component = newSmartComponent();

        // Play time
        setItem(12, create(
                Material.CLOCK,
                "Total Play Time",
                "has been played a total of {} times.",
                StatType.PLAYED
        ));

        // Wins
        setItem(14, create(
                Material.FIREWORK_ROCKET,
                "Total Wins",
                "has won {} times.",
                StatType.WINS
        ));

        // Deaths
        setItem(20, create(
                Material.SKELETON_SKULL,
                "Total Deaths",
                "has died {} times.",
                StatType.DEATHS
        ));

        // Ultimates
        setItem(21, create(
                Material.NETHER_STAR,
                "Ultimate Used",
                "has used ultimates {} times.",
                StatType.ULTIMATE_USED
        ));

        // Damage dealt
        setItem(23, create(
                Material.IRON_SWORD,
                "Damage Dealt",
                "has dealt a total of {} damage.",
                StatType.DAMAGE_DEALT
        ));

        // Damage taken
        setItem(24, create(
                Material.IRON_CHESTPLATE,
                "Damage Taken",
                "has taken a total of {} damage.",
                StatType.DAMAGE_TAKEN
        ));

        // Ability Stats
        for (Talent talent : hero.getTalents()) {
            if (talent == null || talent instanceof PassiveTalent) {
                continue;
            }

            final Talents enumTalent = talent.getHandle();
            final long abilityUsage = stats.getAbilityUsage(enumTalent);

            component.add(
                    new ItemBuilder(talent.getItem())
                            .setName(talent.getName())
                            .removeLore()
                            .addLore("&8Talent Usage")
                            .addLore()
                            .addSmartLore("%s has used &b%s&7 &f&l%s&7 times.".formatted(hero.getName(), talent.getName(), abilityUsage))
                            .setAmount((int) Numbers.clamp(abilityUsage, 1, 64))
                            .asIcon()
            );
        }

        component.apply(this, SlotPattern.FANCY, 3);
    }

    private ItemBuilder create0(Material material, String name, String lore, StatType statType) {
        if (!lore.contains("{}")) {
            throw new IllegalArgumentException("Lore must contain '{}'!");
        }

        final Numeric value = stats.getNumeric(statType);
        final ItemBuilder builder = new ItemBuilder(material);

        builder.setAmount(Numbers.clamp(value.intValue(), 1, 64));
        builder.setName(name);
        builder.addSmartLore(heroes.getName() + " " + lore.replace("{}", "&f&l" + value + "&7"));
        builder.addLore();
        builder.addSmartLore(
                "This hero is #%s in %s.".formatted(globalStats.getRating(heroes, statType), Chat.capitalize(statType)),
                "&8&o"
        );

        return builder;
    }

    private ItemStack create(Material material, String name, String lore, StatType statType) {
        return create0(material, name, lore, statType).asIcon();
    }

    private ItemStack create(String texture, String name, String lore, StatType statType) {
        return create0(Material.PLAYER_HEAD, name, lore, statType).setHeadTextureUrl(texture).asIcon();
    }

}
