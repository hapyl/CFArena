package me.hapyl.fight.game.cosmetic.archive;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.WinCosmetic;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.reflect.npc.Human;
import me.hapyl.eterna.module.reflect.npc.NPCPose;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Random;
import java.util.Set;

public class TwerkWinEffect extends WinCosmetic {

    private final int MAX_DANCERS = 25;
    private final Set<Human> dancers;

    private final NPCPose pose = NPCPose.STANDING;

    public TwerkWinEffect() {
        super(
                "Twerk It!",
                """
                        Shake it!
                        """,
                Rarity.LEGENDARY
        );

        dancers = Sets.newHashSet();

        setIcon(Material.LEATHER_LEGGINGS);
        setMaxTimes(10);
        setStep(10);
    }

    @Override
    public void onStart(Display display) {
        final Location location = display.getLocation();
        final Player player = display.getPlayer();

        if (player == null) {
            return;
        }

        for (int i = 0; i < MAX_DANCERS; i++) {
            final Human npc = Human.create(getRandomNearbyLocation(location), "", player.getName());
            npc.showAll();

            dancers.add(npc);

            // 50/50 chance to twerk init
            npc.setPose(new Random().nextBoolean() ? NPCPose.CROUCHING : pose);
        }
    }

    @Override
    public void onStop(Display display) {
        dancers.forEach(Human::remove);
        dancers.clear();
    }

    @Override
    public void tickTask(Display display, int tick) {
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
