package me.hapyl.fight.game.heroes.miku;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.game.weapons.ability.DummyAbility;
import org.bukkit.Material;

public class MikuHatsuneWeapon extends Weapon {
    protected MikuHatsuneWeapon() {
        super(Material.LILY_PAD, Key.ofString("leek"));
        
        setName("Healthy Leek");
        setDamage(5.0);
        
        setAbility(
                AbilityType.ATTACK, new DummyAbility(
                        "Eat Your Vegetables", """
                                               Smack a teammate to force them to eat their vegetables, healing them and granting a buff.
                                               """
                )
        );
    }
    
    
}
