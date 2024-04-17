package me.hapyl.fight.game.talents.moonwalker;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.moonwalker.MoonZone;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.spigotutils.module.block.display.DisplayEntity;
import me.hapyl.spigotutils.module.locaiton.LocationHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;

public class MoonPillarZone extends MoonZone {

    private final MoonPillarTalent talent;
    private final DisplayEntity entity;

    public MoonPillarZone(MoonPillarTalent talent, GamePlayer player, Location centre, double size, int energy) {
        super(player, centre, size, energy);

        this.talent = talent;

        centre.getBlock().setType(Material.BARRIER, false);
        centre.getBlock().getRelative(BlockFace.UP).setType(Material.BARRIER, false);
        centre.getBlock().getRelative(BlockFace.UP, 2).setType(Material.BARRIER, false);

        this.entity = LocationHelper.modify(centre, -0.5, -3, -0.5, loc -> {
            return talent.displayData.spawnInterpolated(centre);
        });

        // Fx
        new TickingGameTask() {
            @Override
            public void run(int tick) {
                if (tick > 6) {
                    cancel();
                    return;
                }

                entity.teleport(entity.getLocation().add(0, 0.5, 0));
            }
        }.runTaskTimer(0, 1);

        player.playWorldSound(centre, Sound.BLOCK_PISTON_EXTEND, 0.25f);
    }

    @Override
    public void remove() {
        super.remove();

        entity.remove();

        centre.getBlock().setType(Material.AIR, false);
        centre.getBlock().getRelative(BlockFace.UP).setType(Material.AIR, false);
        centre.getBlock().getRelative(BlockFace.UP, 2).setType(Material.AIR, false);

        // FX
        player.playWorldSound(centre, Sound.ENTITY_IRON_GOLEM_DAMAGE, 0.75f);
        player.spawnWorldParticle(centre.add(0, 2, 0), Particle.SPIT, 15, 0, 1, 0, 0.075f);
    }
}
