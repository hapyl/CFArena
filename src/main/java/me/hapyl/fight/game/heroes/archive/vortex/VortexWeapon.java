package me.hapyl.fight.game.heroes.archive.vortex;

import me.hapyl.fight.game.HeroReference;
import me.hapyl.fight.game.weapons.Weapon;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class VortexWeapon extends Weapon implements HeroReference<Vortex> {

    private final Vortex hero;

    public VortexWeapon(Vortex vortex) {
        super(Material.STONE_SWORD);

        this.hero = vortex;

        setName("Sword of Thousands Stars");
        setDescription("A sword with an astral link to the stars.");
        setDamage(6.5d);
    }

    @Nonnull
    @Override
    public Vortex getHero() {
        return hero;
    }

}
