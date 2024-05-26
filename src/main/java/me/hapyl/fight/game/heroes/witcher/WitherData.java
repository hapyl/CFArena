package me.hapyl.fight.game.heroes.witcher;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.heroes.dark_mage.AnimatedWither;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.EntityUtils;
import me.hapyl.spigotutils.module.locaiton.LocationHelper;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Wither;

import javax.annotation.Nonnull;

public class WitherData extends PlayerData {

    public final AnimatedWither animatedWither;

    public WitherData(GamePlayer player, int duration) {
        super(player);

        this.animatedWither = new AnimatedWither(getWitherLocation(player), 400) {

            @Override
            public void onInit(@Nonnull Wither wither) {
                wither.setCustomName(Chat.format("&c%s's %s".formatted(player.getName(), witherName())));
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

                player.spawnWorldParticle(location, Particle.LARGE_SMOKE, 5, 0.25d, 0.25d, 0.25d, 0.025f);
                wither.remove();
            }

            @Override
            public void onTick(int tick) {
                final Location startLocation = wither.getLocation();
                final Location location = getWitherLocation(player);

                wither.teleport(LocationHelper.clerp(startLocation, location, 0.5d));
            }

            private String witherName() {
                return Heroes.DARK_MAGE.getHero().getUltimate().getName();
            }
        }.startAnimation(400, 800, (float) 400 / duration);

    }

    @Override
    public void remove() {
        animatedWither.stopAnimation();
        animatedWither.remove();
    }

    private Location getWitherLocation(GamePlayer player) {
        final Location location = player.getEyeLocation();
        return LocationHelper.getToTheRight(location, 1.5d);
    }

}
