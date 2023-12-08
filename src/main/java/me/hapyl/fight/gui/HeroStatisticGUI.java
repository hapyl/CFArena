package me.hapyl.fight.gui;

import me.hapyl.fight.database.collection.HeroStatsCollection;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.stats.StatType;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.gui.styled.StyledTexture;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import me.hapyl.spigotutils.module.inventory.gui.SlotPattern;
import me.hapyl.spigotutils.module.inventory.gui.SmartComponent;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HeroStatisticGUI extends PlayerGUI {

    private final SlotPattern GENERAL_SLOTS = new SlotPattern(new byte[][] { { 0, 1, 1, 1, 1, 1, 1, 1, 0 } });
    private final SlotPattern ABILITY_SLOTS = SlotPattern.FANCY;

    private final Heroes heroes;
    private final int index;

    public HeroStatisticGUI(Player player, Heroes heroes, int index) {
        super(player, heroes.getName() + " Statistics", 4);
        this.heroes = heroes;
        this.index = index;

        update();
    }

    private void update() {
        final HeroStatsCollection stats = heroes.getStats();
        final String name = heroes.getName();
        final SmartComponent component = newSmartComponent();
        final SmartComponent abilityComponent = newSmartComponent();

        final int played = (int) stats.getStat(StatType.PLAYED);
        final double wins = (int) stats.getStat(StatType.WINS);
        final int kills = (int) stats.getStat(StatType.KILLS);
        final int deaths = (int) stats.getStat(StatType.DEATHS);
        final int ultimate = (int) stats.getStat(StatType.ULTIMATE_USED);
        final double damageDealt = stats.getStat(StatType.DAMAGE_DEALT);
        final double damageTaken = stats.getStat(StatType.DAMAGE_TAKEN);

        component.add(create(Material.PLAYER_HEAD, "Total Play Time", name, "has been played", "times", played));
        component.add(create(Material.DIAMOND_BLOCK, "Total Wins", name, "has won", "games", wins));
        component.add(create(Material.DIAMOND_SWORD, "Total Kills", name, "has killed", "players", kills));
        component.add(create(Material.SKELETON_SKULL, "Total Deaths", name, "has died", "times", deaths));
        component.add(create(Material.NETHER_STAR, "Total Ultimate Used", name, "has used", "ultimates", ultimate));
        component.add(create(Material.IRON_SWORD, "Total Damage Dealt", name, "has dealt", "damage", damageDealt));
        component.add(create(Material.IRON_CHESTPLATE, "Total Damage Taken", name, "has taken", "damage", damageTaken));

        // Ability Stats
        for (Talent talent : heroes.getHero().getTalents()) {
            final Talents enumTalent = Talents.fromTalent(talent);
            if (talent instanceof PassiveTalent || talent == null || enumTalent == null) {
                continue;
            }

            final long abilityUsage = stats.getAbilityUsage(enumTalent);

            abilityComponent.add(new ItemBuilder(talent.getItem())
                    .setName(talent.getName())
                    .setLore("&b%s&7 has been used &b&l%s&7 times.".formatted(
                            talent.getName(),
                            abilityUsage
                    ))
                    .setAmount((int) abilityUsage)
                    .asIcon()
            );
        }

        component.apply(this, GENERAL_SLOTS, 1);
        abilityComponent.apply(this, ABILITY_SLOTS, 2);

        // Add arrow to go back to the hero preview GUI
        setItem(
                31,
                StyledTexture.ARROW_LEFT.asIcon("Go Back"),
                pl -> new HeroPreviewGUI(getPlayer(), heroes, index).openInventory()
        );

        openInventory();
    }

    private ItemStack create(Material material, String name, String heroName, String lore, String suffix, Number amount) {
        return new ItemBuilder(material).setName(name)
                .setLore("&b%s &7%s &b&l%s &7%s.".formatted(heroName, lore, BukkitUtils.decimalFormat(amount.doubleValue()), suffix))
                .setAmount(amount.intValue())
                .asIcon();
    }

}
