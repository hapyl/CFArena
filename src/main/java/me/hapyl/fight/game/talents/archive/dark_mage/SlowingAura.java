package me.hapyl.fight.game.talents.archive.dark_mage;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.archive.dark_mage.SpellButton;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.Draw;
import me.hapyl.spigotutils.module.math.geometry.Quality;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

import static org.bukkit.Sound.BLOCK_HONEY_BLOCK_SLIDE;

public class SlowingAura extends DarkMageTalent {

    @DisplayField(suffix = "blocks") private final short maxDistance = 20;
    @DisplayField private final double radius = 4.0d;
    @DisplayField private final double cdIncrease = 0.5d;

    private final int taskPeriod = 5;

    public SlowingAura() {
        super("Slowing Aura", """
                Creates a &fslowness pool&7 at your &etarget&7 block that slows enemies.
                                
                &8;;The aura does not slow its creator.
                """);

        setType(Type.IMPAIR);
        setItem(Material.BONE_MEAL);
        setDurationSec(4);
        setCooldownSec(10);
    }

    @Nonnull
    @Override
    public String getAssistDescription() {
        return "The aura will also increase %s and periodically &4interrupt&7 actions.".formatted(AttributeType.COOLDOWN_MODIFIER);
    }

    @Nonnull
    @Override
    public SpellButton first() {
        return SpellButton.RIGHT;
    }

    @Nonnull
    @Override
    public SpellButton second() {
        return SpellButton.LEFT;
    }

    @Override
    public Response executeSpell(@Nonnull GamePlayer player) {
        final Block targetBlock = player.getTargetBlockExact(maxDistance);

        if (targetBlock == null) {
            return Response.error("No valid block in sight!");
        }

        final Location location = targetBlock.getRelative(BlockFace.UP).getLocation();

        new TimedGameTask(this) {
            @Override
            public void run(int tick) {
                // Affect
                Collect.nearbyPlayers(location, radius).forEach(entity -> {
                    if (entity.equals(player)) {
                        return; // Don't slow Dark Mage
                    }

                    entity.addPotionEffect(PotionEffectType.SLOW, 10, 3);

                    // Witherborn assist
                    if (hasWither(player)) {
                        final EntityAttributes attributes = entity.getAttributes();
                        attributes.increaseTemporary(Temper.WITHERBORN, AttributeType.COOLDOWN_MODIFIER, cdIncrease, 40);

                        // Interrupt
                        if (modulo(20)) {
                            entity.interrupt();
                        }
                    }
                });

                // Fx
                player.playWorldSound(location, BLOCK_HONEY_BLOCK_SLIDE, 0.0f);

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
                                    2, 0.1d, 0.1d, 0.1d, null
                            );
                        }
                    }
                });
            }

        }.setIncrement(taskPeriod).runTaskTimer(0, taskPeriod);


        return Response.OK;
    }
}
