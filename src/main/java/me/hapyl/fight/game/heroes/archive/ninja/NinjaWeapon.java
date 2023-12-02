package me.hapyl.fight.game.heroes.archive.ninja;

import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.game.weapons.ability.DummyAbility;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.Material;

public class NinjaWeapon extends Weapon {

    public final Weapon noAbilityWeapon;
    @DisplayField public final int stunCd = Tick.fromSecond(10);

    public NinjaWeapon() {
        super(Material.STONE_SWORD);

        setName("斬馬刀");
        setDescription("Light but sharp sword.");
        setDamage(8.0d);

        setAbility(AbilityType.ATTACK, new HitStun());

        this.noAbilityWeapon = createCopy().setDamage(getDamage() / 2.0d);
    }

    private class HitStun extends DummyAbility {

        public HitStun() {
            super("Stun", """
                    Stun the attacked enemy but lose &b50%%&7 of weapon damage.
                    """);

            setCooldown(stunCd);
        }
    }
}
