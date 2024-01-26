package me.hapyl.fight.game.heroes.archive.witcher;

import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.heroes.archive.dark_mage.AnimatedWither;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.EntityUtils;
import me.hapyl.spigotutils.module.locaiton.LocationHelper;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Wither;

import javax.annotation.Nonnull;

public class WitherData extends PlayerData {

    public static final long ASSIST_DELAY = 2000; // Increase from 1000 -> 2500
    public static final double ASSIST_DAMAGE_TOTAL = 10.0d;
    public static final int ASSIST_HITS = 3;

    public final AnimatedWither animatedWither;
    public final Wither wither;
    private long lastAssist;

    public WitherData(GamePlayer player) {
        super(player);
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
                player.sendMessage(Color.WITHERS + "Your %s is gone!", witherName());

                final Location location = wither.getLocation();

                player.spawnWorldParticle(location, Particle.SMOKE_LARGE, 5, 0.25d, 0.25d, 0.25d, 0.025f);
                wither.remove();
            }

            @Override
            public void onTick(int tick) {
                wither.teleport(getWitherLocation(player));
            }

            private String witherName() {
                return Heroes.DARK_MAGE.getHero().getUltimate().getName();
            }
        }.startAnimation(400, 800, (float) 400 / Heroes.DARK_MAGE.getHero().getUltimateDuration());

        this.wither = animatedWither.wither;
    }

    @Override
    public void remove() {
        animatedWither.stopAnimation();
        animatedWither.remove();
    }

    public void assistAttack(@Nonnull LivingGameEntity entity) {
        if (System.currentTimeMillis() - lastAssist < ASSIST_DELAY) {
            return;
        }

        lastAssist = System.currentTimeMillis();
        final double damage = ASSIST_DAMAGE_TOTAL / ASSIST_HITS;

        GameTask.runTaskTimerTimes(task -> {
            entity.damageTick(damage, player, EnumDamageCause.WITHERBORN, ASSIST_HITS);

            // Fx
            final Location location = wither.getLocation();

            player.playWorldSound(location, Sound.ENTITY_WITHER_SHOOT, 1.25f);
            player.spawnWorldParticle(location, Particle.SWEEP_ATTACK, 1);
        }, 0, ASSIST_HITS, ASSIST_HITS);
    }

    private Location getWitherLocation(GamePlayer player) {
        final Location location = player.getEyeLocation();
        return LocationHelper.getToTheRight(location, 1.5d);
    }
}
