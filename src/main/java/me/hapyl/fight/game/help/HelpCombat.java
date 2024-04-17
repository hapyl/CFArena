package me.hapyl.fight.game.help;

import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class HelpCombat extends HelpGUI {

    public HelpCombat(Player player) {
        super(player, "Combat");
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        setItem(
                20,
                new ItemBuilder(Material.STONE_SWORD)
                        .setName("Melee Changes")
                        .addTextBlockLore("""
                                &6No Vanilla Critical
                                There are no vanilla critical hits, no need to jump.
                                &8;;We use attributes for crit.
                                                                
                                &6Static Damage
                                It doesn't matter if you're fighting with a Stick or a Netherite Sword, the item is only for the aesthetics.
                                &8;;The damage is internal for each weapon.
                                """)
                        .asIcon()
        );

        setItem(
                22,
                new ItemBuilder(Material.BOW)
                        .setName("Range Changes")
                        .addTextBlockLore("""
                                &6Reduced Knockback
                                All range weapon, be it Bows, or Raycast weapons have reduced knockback.
                                                                
                                &6Shot Cooldown
                                Each ranged weapon has a cooldown of its own.
                                &8;;The cooldown is affected by an attribute!
                                """)
                        .asIcon()
        );

        setItem(24, new ItemBuilder(Material.APPLE)
                .setName("Health Management")
                .addTextBlockLore("""
                        &6Natural Regeneration
                        Health does not regenerate naturally, keep your eyes on Health Packs around the map and cherish your healers!
                        """)
                .asIcon());
    }

    @Nonnull
    @Override
    public Material getBorder() {
        return Material.RED_STAINED_GLASS_PANE;
    }
}
