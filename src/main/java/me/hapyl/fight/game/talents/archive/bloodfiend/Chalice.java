package me.hapyl.fight.game.talents.archive.bloodfiend;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.bloodfield.Bloodfiend;
import me.hapyl.fight.game.heroes.archive.bloodfield.BloodfiendData;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Chalice extends TickingGameTask {

    private final BloodChalice reference;
    private final Player player;
    private final ArmorStand[] stand;
    private final double yOffset = 1.5d;
    private final Location initialLocation;

    public Chalice(BloodChalice reference, Player player, Location location) {
        this.reference = reference;
        this.player = player;
        this.stand = new ArmorStand[2];

        this.stand[0] = Entities.ARMOR_STAND.spawn(location, self -> {
            Utils.lockArmorStand(self);

            self.setMaxHealth(reference.chaliceHealth);
            self.setHealth(reference.chaliceHealth);
            self.setHelmet(Talents.BLOOD_CHALICE.getTalent().getItem());
            self.setGravity(false);
            self.setInvulnerable(false);
            self.setInvisible(true);
            self.setSilent(true);
        });

        this.initialLocation = location.add(0.0d, yOffset, 0.0d);
        this.stand[1] = Entities.ARMOR_STAND.spawn(initialLocation, self -> {
            self.setMarker(true);
            self.setSmall(true);
            self.setInvisible(true);
            self.setSilent(true);
            self.setHelmet(ItemBuilder.playerHeadUrl("c0340923a6de4825a176813d133503eff186db0896e32b6704928c2a2bf68422").asIcon());

            Bukkit.getOnlinePlayers().forEach(online -> {
                final Scoreboard scoreboard = online.getScoreboard();
                Team team = scoreboard.getTeam("chalice");

                if (team == null) {
                    team = scoreboard.registerNewTeam("chalice");
                    team.setColor(player == online ? ChatColor.GREEN : ChatColor.RED);
                }

                team.addEntry(self.getUniqueId().toString());
            });

            self.setGlowing(true);
        });

        runTaskTimer(0, 1);
    }

    public int getTimeLeft() {
        return reference.getDuration() - getTick();
    }

    @Override
    public void run(int tick) {
        final int duration = reference.getDuration();
        final int timeLeft = getTimeLeft();
        final Bloodfiend bloodfiend = Heroes.BLOODFIEND.getHero(Bloodfiend.class);
        final BloodfiendData data = bloodfiend.getData(player);

        if (player.getLocation().distance(stand[0].getLocation()) >= reference.maxDistance) {
            remove();

            Chat.sendMessage(player, "&4&l🍷 &cThe Chalice broke because you strayed too far away!");
            PlayerLib.playSound(player, Sound.BLOCK_GLASS_BREAK, 0.0f);
            return;
        }

        if (tick >= duration || stand[0].isDead()) {
            // Instant kill if not removed
            if (tick >= duration) {
                data.getSucculencePlayers().forEach(target -> {
                    target.setLastDamager(CF.getOrCreatePlayer(player));
                    target.setLastDamageCause(EnumDamageCause.CHALICE);
                    target.die(true);

                    target.sendMessage("&4&l🍷 &c%s's Blood Chalice took your life!", player.getName());
                });
            }

            remove();
            return;
        }

        stand[0].setCustomName(Chat.format(
                "&4%s's Chalice &c%s",
                player.getName(),
                Utils.decimalFormat(timeLeft)
        ));
        stand[0].setCustomNameVisible(true);

        final Location location = stand[1].getLocation();

        // Affect
        if (tick % reference.interval == 0) {
            data.getSucculencePlayers().forEach(target -> {
                target.damage(reference.damage, player, EnumDamageCause.CHALICE);
                bloodfiend.drawTentacleParticles(location, target.getLocation());
            });
        }

        // Fx
        if (tick % 10 == 0) {
            data.getSucculencePlayers().forEach(target -> {
                target.playSound(stand[0].getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 0.0f);
            });
        }

        final double radians = Math.toRadians(tick * 2);
        final double y = Math.sin(radians) / 2;

        location.setY(initialLocation.getY() + y);
        location.setYaw(location.getYaw() + 5);

        stand[1].teleport(location);

        final World world = location.getWorld();
        if (world != null) {
            world.spawnParticle(
                    Particle.REDSTONE,
                    location.add(0.0d, 0.76d, 0.0d),
                    1,
                    0.2d,
                    0.2d,
                    0.2d,
                    new Particle.DustOptions(Color.RED, 1)
            );
        }
    }

    public void remove() {
        final Location location = stand[0].getLocation().add(0.0d, 1.5d, 0.0d);

        for (ArmorStand armorStand : stand) {
            armorStand.remove();
        }

        cancel();

        // Fx
        PlayerLib.playSound(location, Sound.BLOCK_GLASS_BREAK, 0.0f);
        PlayerLib.spawnParticle(location, Particle.EXPLOSION_NORMAL, 15, 0.1d, 0.1d, 0.1d, 0.05f);
    }
}
