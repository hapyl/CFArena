package me.hapyl.fight.game.cosmetic.win;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.npc.Npc;
import me.hapyl.eterna.module.npc.NpcPose;
import me.hapyl.eterna.module.npc.appearance.AppearanceBuilder;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.reflect.Skin;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.task.TickingGameTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.Set;

public class TwerkWinCosmetic extends WinCosmetic {

    private static final int maxDancers = 25;
    
    public TwerkWinCosmetic(@Nonnull Key key) {
        super(key, "Twerk It!");

        setDescription("""
                Shake it!
                """
        );

        setRarity(Rarity.LEGENDARY);
        setIcon(Material.LEATHER_LEGGINGS);

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

        final Set<Npc> dancers = Sets.newHashSet();
        
        for (int i = 0; i < maxDancers; i++) {
            final Npc npc = new Npc(getRandomNearbyLocation(location), Component.empty(), AppearanceBuilder.ofMannequin(Skin.ofPlayer(player)));
            npc.showAll();

            dancers.add(npc);

            // 50/50 chance to twerk init
            npc.setPose(new Random().nextBoolean() ? NpcPose.CROUCHING : NpcPose.STANDING);
        }
        
        new TickingGameTask() {
            private int danceTimes;
            
            @Override
            public void run(int tick) {
                if (danceTimes++ >= 10) {
                    dancers.forEach(Npc::destroy);
                    dancers.clear();
                    
                    cancel();
                    return;
                }
                
                dancers.forEach(npc -> {
                    final NpcPose pose = npc.getPose();
                    
                    npc.setPose(pose == NpcPose.CROUCHING ? NpcPose.STANDING : NpcPose.CROUCHING);
                    PlayerLib.playSound(npc.getLocation(), Sound.BLOCK_LAVA_POP, new Random().nextFloat(0.0f, 2.0f));
                });
            }
        }.runTaskTimer(0, 10);
    }

    @Override
    public void onStop(@Nonnull Display display) {
    }

    @Override
    public void onTick(@Nonnull Display display, int tick) {
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
