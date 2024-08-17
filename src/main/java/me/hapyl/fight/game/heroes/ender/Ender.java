package me.hapyl.fight.game.heroes.ender;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.event.custom.EnderPearlTeleportEvent;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.UltimateResponse;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.ender.EnderPassive;
import me.hapyl.fight.game.talents.ender.TransmissionBeacon;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.TickingGameTask;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class Ender extends Hero implements Listener {

    public Ender(@Nonnull DatabaseKey key) {
        super(key, "Ender");

        setArchetypes(Archetype.DAMAGE, Archetype.MOBILITY, Archetype.MELEE, Archetype.SELF_SUSTAIN, Archetype.SELF_BUFF);
        setGender(Gender.UNKNOWN);

        setItem("aacb357709d8cdf1cd9c9dbe313e7bab3276ae84234982e93e13839ab7cc5d16");
        setMinimumLevel(5);

        setDescription(
                "Weird enderman-like looking warrior with teleportation abilities. Hits you with his arm, but it hurts like a brick."
        );

        final HeroAttributes attributes = getAttributes();
        attributes.setHealth(120);

        final Equipment equipment = this.getEquipment();
        equipment.setChestPlate(85, 0, 102);
        equipment.setLeggings(128, 0, 128);
        equipment.setBoots(136, 0, 204);

        setWeapon(new EnderWeapon());
        setUltimate(new EnderUltimate());
    }

    @EventHandler()
    public void handleTeleportEvent(EnderPearlTeleportEvent ev) {
        final GamePlayer player = ev.getPlayer();

        if (!validatePlayer(player.getPlayer())) {
            return;
        }

        getPassiveTalent().handleTeleport(player);
    }

    @Override
    public void onStart(@Nonnull GameInstance instance) {
        new TickingGameTask() {
            @Override
            public void run(int tick) {
                // Damage players in water
                HeroRegistry.ENDER.getAlivePlayers().forEach(player -> {
                    if (!player.getPlayer().isInWater()) {
                        return;
                    }

                    player.damage(2.0d, EnumDamageCause.WATER);
                    player.playWorldSound(Sound.ENTITY_ENDERMAN_HURT, 1.2f);
                });
            }
        }.runTaskTimer(0, 15);
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.TELEPORT_PEARL.getTalent();
    }

    @Override
    public TransmissionBeacon getSecondTalent() {
        return (TransmissionBeacon) Talents.TRANSMISSION_BEACON.getTalent();
    }

    @Override
    public EnderPassive getPassiveTalent() {
        return (EnderPassive) Talents.ENDER_PASSIVE.getTalent();
    }

    private class EnderUltimate extends UltimateTalent {
        public EnderUltimate() {
            super("Transmission!", 50);

            setDescription("""
                    Instantly teleport to your &b&lTransmission &b&lBeacon &7and collect it for further use.
                    """);

            setType(TalentType.MOVEMENT);
            setItem(Material.SHULKER_SHELL);
            setSound(Sound.ENTITY_GUARDIAN_HURT_LAND, 0.75f);
            setCooldownSec(20);
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            if (!getSecondTalent().hasBeacon(player)) {
                return UltimateResponse.error("The beacon is not placed!");
            }

            getSecondTalent().teleportToBeacon(player);

            return UltimateResponse.OK;
        }
    }
}
