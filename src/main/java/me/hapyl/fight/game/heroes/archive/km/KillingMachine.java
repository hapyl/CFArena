package me.hapyl.fight.game.heroes.archive.km;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.UltimateResponse;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.weapons.PackedParticle;
import me.hapyl.fight.game.weapons.range.RangeWeapon;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class KillingMachine extends Hero implements DisabledHero {

    private final int weaponCd = 35;

    public KillingMachine(@Nonnull Heroes handle) {
        super(handle, "War Machine");

        setDescription("A machine of war that was left for scrap, until now...");
        setItem("ec2f3d5d62fd9be6d654d314c123390abfa3698d3d87c1516a453a7ee4fcbf");

        setRace(Race.CYBERNETIC);

        final Equipment equipment = this.getEquipment();
        equipment.setChestPlate(Material.CHAINMAIL_CHESTPLATE);
        equipment.setLeggings(Material.CHAINMAIL_LEGGINGS);
        equipment.setBoots(Material.CHAINMAIL_BOOTS);

        this.setWeapon(new RangeWeapon(Material.IRON_HORSE_ARMOR, "km_weapon") {

        }.setSound(Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1.4f)
                .setParticleTick(new PackedParticle(Particle.END_ROD))
                .setParticleHit(new PackedParticle(Particle.END_ROD, 1, 0, 0, 0, 0.1f))
                .setDamage(5.0d)
                .setName("Rifle"));

    }

    public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
        // Glow Self
        //final Glowing glowing = new Glowing(player, ChatColor.RED, getUltimateDuration());
        //final List<GamePlayer> alivePlayers = Manager.current().getCurrentGame().getAlivePlayers();
        //
        //alivePlayers.forEach(gamePlayer -> {
        //    final Player alivePlayer = gamePlayer.getPlayer();
        //
        //    // Add player to see our glowing
        //
        //    if (alivePlayer == player) {
        //        return;
        //    }
        //
        //    // FIXME: 020, Mar 20, 2023 -> This blinks for some reason
        //
        //    // Highlight other players unless self
        //    final Glowing glowingOther = new Glowing(alivePlayer, ChatColor.AQUA, getUltimateDuration());
        //    glowingOther.addPlayer(player);
        //    glowingOther.glow();
        //});
        //
        //glowing.glow();

        return UltimateResponse.OK;
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.LASER_EYE.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return null;
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }
}
