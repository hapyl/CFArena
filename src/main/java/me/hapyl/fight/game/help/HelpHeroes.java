package me.hapyl.fight.game.help;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.SlotPattern;
import me.hapyl.spigotutils.module.inventory.gui.SmartComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class HelpHeroes extends HelpGUI {

    private final int SPLIT_WIDTH = 40;

    public HelpHeroes(Player player) {
        super(player, "Heroes");
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        final SmartComponent component = newSmartComponent();

        component.add(
                new ItemBuilder(Material.STICK)
                        .setName("Weapon")
                        .addTextBlockLore("""
                                &6Weapon
                                A hero always possesses a weapon.
                                &8;;Some use sticks for some reason though.
                                                                
                                &6Abilities
                                Some weapons might have unique abilities, make sure to check them!
                                """)
                        .asIcon()
        );

        component.add(
                new ItemBuilder(Material.GOLDEN_CARROT)
                        .setName("Talents")
                        .addTextBlockLore("""
                                &6Active Talents
                                Heroes posses talents that can be activated via a press of a button.
                                                                
                                &6Passive
                                Passive talents are always active! They help you no matter what.
                                """)
                        .asIcon()
        );

        component.add(
                new ItemBuilder(Material.NETHER_STAR)
                        .setName("Ultimate")
                        .addTextBlockLore("""
                                Ultimates are powerful talents that can turn the tide of the battle.
                                                                
                                But to do so, you must first accumulate &b&l※ Energy&7, which is gained by using talents, picking up &bEnergy Packs&7 or simply waiting.
                                &8;;The default button to unleash the ultimate is F.
                                """)
                        .asIcon()
        );

        component.add(
                new ItemBuilder(Material.POWERED_RAIL)
                        .setName("Attributes")
                        .addTextBlockLore("""
                                There are many attributes, such as:
                                - %s.
                                - %s.
                                - %s.
                                - %s, etc.
                                                                
                                Each attribute controls a certain aspect of the hero's playstyle.
                                                                
                                They may be changed in game, therefore buffing or debuffing players.
                                """.formatted(
                                AttributeType.MAX_HEALTH,
                                AttributeType.DEFENSE,
                                AttributeType.SPEED,
                                AttributeType.CRIT_CHANCE
                        ))
                        .asIcon()
        );

        component.apply(this, SlotPattern.DEFAULT, 2);
    }

    @Nonnull
    @Override
    public Material getBorder() {
        return null;
    }


}
