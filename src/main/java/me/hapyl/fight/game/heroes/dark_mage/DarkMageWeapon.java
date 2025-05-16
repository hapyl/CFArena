package me.hapyl.fight.game.heroes.dark_mage;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.game.weapons.ability.DummyAbility;
import org.bukkit.Material;

public class DarkMageWeapon extends Weapon {

    public DarkMageWeapon() {
        super(Material.WOODEN_HOE, Key.ofString("dark_mage_weapon"));

        setName("Ancient Wand");
        setDescription("""
                An ancient item capable of casting the darkest of spells...
                """);

        setDamage(7.0d);

        setAbility(AbilityType.RIGHT_CLICK, new SpellMode());
    }

    private static class SpellMode extends DummyAbility {

        public SpellMode() {
            super("Runic Spell", """
                    Enter runic spell mode.
                    
                    While in this mode, combine %s&7 and %s&7 runes using &a&nleft&7 and &c&nright&7 clicks respectively.
                    
                    Successfully combining two runes will &bcast&7 the &acorresponding&7 spell.
                    """.formatted(SpellButton.LEFT, SpellButton.RIGHT)
            );
        }
    }
}
