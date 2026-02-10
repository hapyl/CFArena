package me.hapyl.fight.game.heroes.km;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroProfile;
import me.hapyl.fight.game.heroes.Race;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class KillingMachine extends Hero implements Disabled {

    private final int weaponCd = 35;

    public KillingMachine(@Nonnull Key key) {
        super(key, "War Machine");

        setDescription("""
                A machine of war that was left for scrap, until it gained conscience of its own...
                """);

        setItem("ec2f3d5d62fd9be6d654d314c123390abfa3698d3d87c1516a453a7ee4fcbf");

        final HeroProfile profile = getProfile();
        profile.setRace(Race.CYBERNETIC);

        final HeroEquipment equipment = this.getEquipment();
        equipment.setChestPlate(Material.CHAINMAIL_CHESTPLATE);
        equipment.setLeggings(Material.CHAINMAIL_LEGGINGS);
        equipment.setBoots(Material.CHAINMAIL_BOOTS);

        //this.setWeapon(new RangeWeapon(Material.IRON_HORSE_ARMOR, "km_weapon") {
        //
        //}.setSound(Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1.4f)
        //        .setParticleTick(new PackedParticle(Particle.END_ROD))
        //        .setParticleHit(new PackedParticle(Particle.END_ROD, 1, 0, 0, 0, 0.1f))
        //        .setDamage(5.0d)
        //        .setName("Rifle"));

        setUltimate(new KillingMachineUltimate());
    }

    @Override
    public Talent getFirstTalent() {
        return TalentRegistry.LASER_EYE;
    }

    @Override
    public Talent getSecondTalent() {
        return null;
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }

    private class KillingMachineUltimate extends UltimateTalent {

        public KillingMachineUltimate() {
            super(KillingMachine.this, "", 50);
        }

        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
            return execute(() -> {
            });
        }
    }
}
