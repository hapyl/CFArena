package me.hapyl.fight.game.talents.archive.dark_mage;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.dark_mage.DarkMageSpell;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Draw;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

import static org.bukkit.Sound.BLOCK_HONEY_BLOCK_SLIDE;

public class SlowingAura extends DarkMageTalent {

    @DisplayField(suffix = "blocks") private final short maxDistance = 20;
    @DisplayField private final double radius = 4.0d;

    private final int taskPeriod = 5;

    public SlowingAura() {
        super("Slowing Aura", """
                Creates a slowness pool at your target block that slows anyone in range.
                                
                &e;;The aura does not slow its creator.
                """, Material.BONE_MEAL);

        setDurationSec(4);
        setCooldown(200);
    }

    @Nonnull
    @Override
    public String getAssistDescription() {
        return "The aura will also increase talent cooldowns and periodically interrupt actions.";
    }

    @Nonnull
    @Override
    public DarkMageSpell.SpellButton first() {
        return DarkMageSpell.SpellButton.RIGHT;
    }

    @Nonnull
    @Override
    public DarkMageSpell.SpellButton second() {
        return DarkMageSpell.SpellButton.LEFT;
    }

    @Override
    public Response executeSpell(Player player) {
        final Block targetBlock = player.getTargetBlockExact(maxDistance);

        if (targetBlock == null) {
            return Response.error("No valid block in sight!");
        }

        final Location location = targetBlock.getRelative(BlockFace.UP).getLocation();

        new GameTask() {
            private int tick = getDuration();

            @Override
            public void run() {
                if (tick <= 0) {
                    cancel();
                    return;
                }

                tick -= taskPeriod;

                Geometry.drawCircle(location, radius, Quality.HIGH, new Draw(Particle.SPELL) {
                    @Override
                    public void draw(Location location) {
                        final World world = location.getWorld();
                        if (world != null) {
                            world.spawnParticle(
                                    this.getParticle(),
                                    location.getX(),
                                    location.getY(),
                                    location.getZ(),
                                    1, 0, 0, 0, null
                            );
                        }
                    }
                });

                PlayerLib.playSound(location, BLOCK_HONEY_BLOCK_SLIDE, 0.0f);
                Collect.nearbyPlayers(location, radius).forEach(entity -> {
                    if (entity.is(player)) {
                        return; // Don't slow Dark Mage
                    }

                    entity.addPotionEffect(PotionEffectType.SLOW, 10, 3);

                    // Witherborn assist
                    if (!Heroes.DARK_MAGE.getHero().isUsingUltimate(player)) {
                        return;
                    }

                    // Add stun effect
                    entity.addEffect(GameEffectType.SLOWING_AURA, 40, true);

                    // Interrupt
                    if (tick % 20 == 0) {
                        entity.interrupt();
                    }
                });

            }
        }.runTaskTimer(0, taskPeriod);


        return Response.OK;
    }
}
