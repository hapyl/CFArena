package me.hapyl.fight.game.heroes.storage.extra;

import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.EntityUtils;
import me.hapyl.spigotutils.module.locaiton.LocationHelper;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class WitherData {

    public static final long ASSIST_DELAY = 1000;
    public static final double ASSIST_DAMAGE_TOTAL = 10.0d;
    public static final int ASSIST_HITS = 3;

    public final Player player;
    public final AnimatedWither animatedWither;
    public final Wither wither;
    private long lastAssist;

    public WitherData(Player player) {
        this.player = player;
        this.animatedWither = new AnimatedWither(getWitherLocation(player), 400) {

            @Override
            public void onInit(@Nonnull Wither wither) {
                wither.setCustomName(Chat.format("&c%s's %s", player.getName(), witherName()));
                wither.setCustomNameVisible(false);
                wither.setInvulnerable(true);
                wither.setSilent(true);

                EntityUtils.setCollision(wither, EntityUtils.Collision.DENY);
            }

            @Override
            public void onStart() {
            }

            @Override
            public void onStop() {
                Chat.sendMessage(player, "&cYour %s is gone!", witherName());
                wither.remove();
            }

            @Override
            public void onTick(int tick) {
                wither.teleport(getWitherLocation(player));

                // Make wither always look at the target
                final LivingEntity target = Utils.getTargetEntity(player, 10.0d, 0.8d, living -> living != player && living != wither);

                if (target == null) {
                    return;
                }

                final Vector dirBetweenLocations = target.getLocation().toVector().subtract(wither.getLocation().toVector());
                final Location location = wither.getLocation();
                location.setDirection(dirBetweenLocations);

                wither.teleport(location);

                wither.setTarget(target);
                wither.setTarget(Wither.Head.LEFT, target);
                wither.setTarget(Wither.Head.RIGHT, target);
            }

            private String witherName() {
                return Heroes.DARK_MAGE.getHero().getUltimate().getName();
            }
        }.startAnimation(400, 800, (float) 400 / Heroes.DARK_MAGE.getHero().getUltimateDuration());

        this.wither = animatedWither.wither;
    }

    public void remove() {
        animatedWither.stopAnimation();
        animatedWither.remove();
    }

    public void assistAttack() {
        if (System.currentTimeMillis() - lastAssist < ASSIST_DELAY) {
            return;
        }

        lastAssist = System.currentTimeMillis();

        final LivingEntity target = wither.getTarget();

        if (target == null) {
            Debug.info("Target is null.");
            return;
        }

        GameTask.runTaskTimerTimes(task -> {
            target.setNoDamageTicks(0); // yes
            GamePlayer.damageEntityTick(target, ASSIST_DAMAGE_TOTAL / ASSIST_HITS, player, EnumDamageCause.WITHERBORN, ASSIST_HITS);

            // Fx
            PlayerLib.playSound(wither.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.25f);
        }, 0, ASSIST_HITS, ASSIST_HITS);
    }


    private Location getWitherLocation(Player player) {
        final Location location = player.getEyeLocation();
        return LocationHelper.getToTheRight(location, 1.5d);
    }
}
