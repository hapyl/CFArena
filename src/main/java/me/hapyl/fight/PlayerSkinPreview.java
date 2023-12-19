package me.hapyl.fight;

import me.hapyl.fight.game.playerskin.PlayerSkin;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.ux.Message;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class PlayerSkinPreview extends TickingGameTask {

    private final double rotationPerTick = 2;

    private final Player player;
    private final PlayerSkin skin;
    protected HumanNPC npc;
    private double rotation = 0;

    public PlayerSkinPreview(@Nonnull Player player, @Nonnull PlayerSkin skin) {
        this.player = player;
        this.skin = skin;

        final Location location = player.getLocation();
        final Vector direction = location.getDirection().normalize().setY(0.0d);

        location.add(direction.multiply(2.0d));
        location.add(0.0d, 0.25d, 0.0d);

        final Vector directionTowardsPlayer = player.getLocation()
                .toVector()
                .normalize()
                .setY(0)
                .subtract(location.toVector().normalize().setY(0));
        location.setDirection(directionTowardsPlayer);

        if (!location.getBlock().isEmpty()) {
            Message.error(player, "Could not preview skin because there is nowhere to put it! (Move away from blocks)");
            return;
        }

        npc = new HumanNPC(location, null);
        npc.setSkin(skin.getTexture(), skin.getSignature());
        npc.show(player);

        runTaskTimer(1, 1);
    }

    @Override
    public void run(int tick) {
        if (npc == null) {
            cancel();
            return;
        }

        final Location location = npc.getLocation();

        if (rotation > 360) {
            npc.remove();
            cancel();

            // Fx
            PlayerLib.spawnParticle(player, location.add(0, 1, 0), Particle.EXPLOSION_NORMAL, 20, 0.25d, 1d, 0.25d, 0.025f);
            return;
        }

        // Swing
        if (rotation > 0 && rotation % (100 / rotationPerTick) == 0) {
            npc.swingMainHand();
            PlayerLib.playSound(player, location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f);
        }

        location.setYaw((float) (location.getYaw() + rotationPerTick));
        npc.teleport(location);

        rotation += rotationPerTick;
    }
}
