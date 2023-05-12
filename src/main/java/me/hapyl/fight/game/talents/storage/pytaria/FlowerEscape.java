package me.hapyl.fight.game.talents.storage.pytaria;

import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.storage.Pytaria;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.Utils;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Draw;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class FlowerEscape extends Talent {

    @DisplayField(suffix = "blocks") private final double flowerRadius = 2.5d;
    @DisplayField private final double flowerDamage = 5.0d;
    @DisplayField private final int pulsePeriod = 20;

    public FlowerEscape() {
        super("Flower Escape", """
                Throw a deadly flower at your current location and dash backwards.
                                
                The flower will continuously pulse and deal damage to nearby players.
                                        
                After the duration is over, it will explode dealing double the damage.
                """, Type.COMBAT);

        setItem(Material.RED_TULIP);
        setDuration(120);
        setCd(getDuration() * 2);
    }

    @Override
    public Response execute(Player player) {
        final Location location = player.getLocation();
        final Vector vector = player.getLocation().getDirection().normalize().multiply(-1.5d);
        player.setVelocity(vector.setY(0.5d));

        final ArmorStand entity = Entities.ARMOR_STAND.spawn(location, me -> {
            me.setMarker(true);
            me.setInvisible(true);

            Nulls.runIfNotNull(me.getEquipment(), equipment -> equipment.setHelmet(new ItemStack(getMaterial())));
        });

        final double finalDamage = Heroes.PYTARIA.getHero(Pytaria.class).calculateDamage(player, flowerDamage);

        new GameTask() {
            private int tick = getDuration();

            @Override
            public void run() {
                if (GamePlayer.getPlayer(player).isDead()) {
                    entity.remove();
                    cancel();
                    return;
                }

                if (tick-- <= 0) {
                    entity.remove();
                    PlayerLib.playSound(location, Sound.ITEM_TOTEM_USE, 2.0f);
                    PlayerLib.spawnParticle(location, Particle.SPELL_MOB, 15, 1, 0.5, 1, 0);
                    Utils.getPlayersInRange(location, flowerRadius).forEach(victim -> {
                        GamePlayer.damageEntity(victim, finalDamage * 2.0d, player, EnumDamageCause.FLOWER);
                    });

                    cancel();
                    return;
                }

                // Animation
                final int tickModPulse = tick % pulsePeriod;
                if (tickModPulse > 0 && tickModPulse < 10) {
                    entity.teleport(location.add(0, 0.1d, 0));
                }
                else {
                    entity.teleport(location.subtract(0, 0.1d, 0));
                }

                entity.setHeadPose(entity.getHeadPose().add(0.0d, 0.1d, 0.0d));

                // pulse
                if (tickModPulse == 0) {
                    final Location fixedLocation = entity.getEyeLocation().add(0.0d, 1.5d, 0.0d);

                    Geometry.drawCircle(fixedLocation, flowerRadius, Quality.HIGH, new Draw(Particle.TOTEM) {
                        @Override
                        public void draw(Location location) {
                            if (location.getWorld() == null) {
                                return;
                            }
                            location.getWorld().spawnParticle(getParticle(), location, 1, 0, 0, 0, 0.2);
                        }
                    });

                    Utils.getPlayersInRange(fixedLocation, flowerRadius).forEach(target -> {
                        GamePlayer.damageEntity(target, finalDamage, player, EnumDamageCause.FLOWER);
                    });

                    final float pitch = Math.min(0.5f + ((0.1f * (((float) getDuration() - tick) / 20))), 2.0f);

                    PlayerLib.playSound(fixedLocation, Sound.ENTITY_ENDER_DRAGON_FLAP, 1.75f);
                    PlayerLib.playSound(fixedLocation, Sound.BLOCK_NOTE_BLOCK_COW_BELL, pitch);
                }

            }
        }.runTaskTimer(0, 1);
        return Response.OK;
    }
}
