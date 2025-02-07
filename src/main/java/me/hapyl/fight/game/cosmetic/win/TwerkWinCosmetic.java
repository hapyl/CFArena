package me.hapyl.fight.game.cosmetic.win;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.reflect.npc.Human;
import me.hapyl.eterna.module.reflect.npc.NPCPose;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.Set;

public class TwerkWinCosmetic extends WinCosmetic {

    private final int maxDancers = 25;
    private final Set<Human> dancers;

    private final NPCPose pose = NPCPose.STANDING;

    public TwerkWinCosmetic(@Nonnull Key key) {
        super(key, "Twerk It!");

        setDescription("""
                Shake it!
                """
        );

        setRarity(Rarity.LEGENDARY);
        setIcon(Material.LEATHER_LEGGINGS);

        dancers = Sets.newHashSet();

        setMaxTimes(10);
        setStep(10);
    }

    @Override
    public void onStart(@Nonnull Display display) {
        final Location location = display.getLocation();
        final Player player = display.getPlayer();

        if (player == null) {
            return;
        }

        for (int i = 0; i < maxDancers; i++) {
            final Human npc = Human.create(getRandomNearbyLocation(location), "", player.getName());
            npc.showAll();

            dancers.add(npc);

            // 50/50 chance to twerk init
            npc.setPose(new Random().nextBoolean() ? NPCPose.CROUCHING : pose);
        }
    }

    @Override
    public void onStop(@Nonnull Display display) {
        dancers.forEach(Human::remove);
        dancers.clear();
    }

    @Override
    public void tickTask(@Nonnull Display display, int tick) {
        for (Human dancer : dancers) {
            // Switch pose
            dancer.setPose(dancer.getPose() == pose ? NPCPose.CROUCHING : pose);

            // Fx
            PlayerLib.playSound(dancer.getLocation(), Sound.BLOCK_LAVA_POP, new Random().nextFloat(0.0f, 2.0f));
        }
    }

    private Location getRandomNearbyLocation(Location origin) {
        final World world = origin.getWorld();
        if (world == null) {
            return origin;
        }

        final double x = origin.getX() + new Random().nextDouble(-20, 20);
        final double z = origin.getZ() + new Random().nextDouble(-20, 20);

        return new Location(world, x, world.getHighestBlockYAt((int) x, (int) z) + 1, z, new Random().nextFloat(0, 360), 0.0f);
    }
}
