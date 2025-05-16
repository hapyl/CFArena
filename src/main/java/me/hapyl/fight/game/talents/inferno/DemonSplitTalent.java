package me.hapyl.fight.game.talents.inferno;

import me.hapyl.eterna.module.math.geometry.Drawable;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.inferno.InfernoData;
import me.hapyl.fight.game.heroes.inferno.InfernoDemon;
import me.hapyl.fight.game.heroes.inferno.InfernoDemonType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Describable;
import org.bukkit.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class DemonSplitTalent extends Talent implements Describable {

    private static final Key COOLDOWN_KEY = Key.ofString("demon_split_cooldown");

    private final InfernoDemonType type;

    public DemonSplitTalent(@Nonnull Key key, @Nonnull InfernoDemonType type, @Nonnull Material material) {
        super(key, "Demonsplit: " + type.getName());

        this.type = type;

        setDescription("""
                Split into a powerful &4demon&7 - %s&7.
                &8&o;;The demon inherits your health.
                
                %s
                %s&4Demonsplit talents share a cooldown!
                """.formatted(type.getName(), describe(), describeReform()));

        setMaterial(material);

        setDurationSec(10);
        setCooldownSec(20);
    }

    @Nonnull
    public InfernoDemonType type() {
        return type;
    }

    @Nonnull
    @Override
    public abstract String describe();

    @Nonnull
    public abstract ReformDescription describeReform();

    @Nonnull
    public abstract DemonInstance newInstance(@Nonnull GamePlayer player);

    @Nonnull
    @Override
    public String getTalentClassType() {
        return "Demon " + super.getTalentClassType();
    }

    @Nonnull
    @Override
    public final Key cooldownKey() {
        return COOLDOWN_KEY;
    }

    @Override
    public final @Nullable Response execute(@Nonnull GamePlayer player) {
        final InfernoData data = player.getPlayerData(HeroRegistry.INFERNO);

        if (data.currentDemon != null) {
            return Response.error("Already in demon form!");
        }

        final World world = player.getWorld();
        final DemonInstance demonInstance = newInstance(player);
        final InfernoDemon demon = type.createDemon(this, player);
        data.currentDemon = demon;

        player.addEffect(EffectType.INVISIBLE, getDuration());

        // Form
        demonInstance.onForm(player, data);

        new TickingGameTask() {
            @Override
            public void onTaskStop() {
                data.remove();
                demonInstance.remove();
            }

            @Override
            public void run(int tick) {
                if (player.isDeadOrRespawning()) {
                    cancel();
                    return;
                }

                // Tick
                demonInstance.onTick(player, data, tick);

                // Sync demon
                demon.entity().teleport(player);

                // Reform
                if (tick > getDuration()) {
                    demonInstance.onReform(player, data);
                    cancel();

                    // Reform fx
                    world.strikeLightningEffect(player.getEyeLocation().add(0d, 0.5d, 0d));

                    player.spawnWorldParticle(Particle.EXPLOSION_EMITTER, 1);

                    player.playWorldSound(Sound.ENTITY_BLAZE_DEATH, 0.75f);
                    player.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 0.75f);

                    drawParticleBox(player, location -> player.spawnWorldParticle(location, Particle.FALLING_LAVA, 1), 2.0d);
                }
            }
        }.runTaskTimer(0, 1);

        // Fx
        world.strikeLightningEffect(player.getEyeLocation().add(0d, 0.5d, 0d));

        player.spawnWorldParticle(Particle.EXPLOSION_EMITTER, 1);

        player.playWorldSound(Sound.ENTITY_MOOSHROOM_CONVERT, 0.75f);
        player.playWorldSound(Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.75f);

        return Response.ok();
    }

    protected void drawParticleBox(GamePlayer player, Drawable drawable, double height) {
        final Location location = player.getLocation();

        final double width = 1.0;
        final double depth = 1.0;
        final double step = 0.2;

        for (double x = -width / 2; x <= width / 2; x += step) {
            drawEdge(location, x, 0, -depth / 2, drawable);
            drawEdge(location, x, 0, depth / 2, drawable);
            drawEdge(location, x, height, -depth / 2, drawable);
            drawEdge(location, x, height, depth / 2, drawable);
        }

        for (double z = -depth / 2; z <= depth / 2; z += step) {
            drawEdge(location, -width / 2, 0, z, drawable);
            drawEdge(location, width / 2, 0, z, drawable);
            drawEdge(location, -width / 2, height, z, drawable);
            drawEdge(location, width / 2, height, z, drawable);
        }

        // Vertical edges (YZ plane)
        for (double y = 0; y <= height; y += step) {
            drawEdge(location, -width / 2, y, -depth / 2, drawable);
            drawEdge(location, width / 2, y, -depth / 2, drawable);
            drawEdge(location, -width / 2, y, depth / 2, drawable);
            drawEdge(location, width / 2, y, depth / 2, drawable);
        }

    }

    private void drawEdge(Location location, double x, double y, double z, Drawable drawable) {
        location.add(x, y, z);
        drawable.draw(location);
        location.subtract(x, y, z);
    }

    public static class ReformDescription {

        private final String name;
        private final String description;

        public ReformDescription(@Nonnull String name, @Nonnull String description) {
            this.name = name;
            this.description = description;
        }

        @Override
        public String toString() {
            return """
                    &6Reform: %s
                    After &b{duration}, transform back to your own self and %s
                    """.formatted(name, description);
        }

    }
}
