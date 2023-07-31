package me.hapyl.fight.game.heroes.archive.km;

import me.hapyl.fight.game.heroes.DisabledHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroEquipment;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.weapons.PackedParticle;
import me.hapyl.fight.game.weapons.RangeWeapon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class KillingMachine extends Hero implements DisabledHero {

    private final int weaponCd = 35;

    public KillingMachine() {
        super("War Machine");

        setRole(Role.RANGE);

        setDescription("A machine of war that was left for scrap, until now...");
        setItem("ec2f3d5d62fd9be6d654d314c123390abfa3698d3d87c1516a453a7ee4fcbf");

        final HeroEquipment equipment = this.getEquipment();
        equipment.setChestplate(Material.CHAINMAIL_CHESTPLATE);
        equipment.setLeggings(Material.CHAINMAIL_LEGGINGS);
        equipment.setBoots(Material.CHAINMAIL_BOOTS);

        this.setWeapon(new RangeWeapon(Material.IRON_HORSE_ARMOR, "km_weapon") {
            @Override
            public void onHit(Player player, LivingEntity entity) {

            }

            @Override
            public void onMove(Player player, Location location) {

            }

            @Override
            public void onShoot(Player player) {
                startCooldown(player, isUsingUltimate(player) ? (weaponCd / 2) : weaponCd);
            }

        }.setSound(Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1.4f)
                .setParticleTick(new PackedParticle(Particle.END_ROD))
                .setParticleHit(new PackedParticle(Particle.END_ROD, 1, 0, 0, 0, 0.1f))
                .setDamage(5.0d)
                .setName("Rifle"));

        this.setUltimate(new UltimateTalent(
                "Overload",
                "Overload yourself for {duration}. While overloaded, your fire-rate is increased by &b100% &7and all opponents are highlighted.",
                60
        ).setDurationSec(12).setItem(Material.LIGHTNING_ROD).setSound(Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.0f));

    }

    @Override
    public void useUltimate(Player player) {
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
