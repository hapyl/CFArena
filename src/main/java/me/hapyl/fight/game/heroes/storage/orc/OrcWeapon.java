package me.hapyl.fight.game.heroes.storage.orc;

import me.hapyl.fight.game.weapons.Weapon;
import org.bukkit.Material;

public class OrcWeapon extends Weapon {
    public OrcWeapon() {
        super(Material.IRON_AXE);

        setDamage(12.0d);
        setAttackSpeed(-0.5d);
        setId("orc_axe");
        setName("Poleax");
    }
}
