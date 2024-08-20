package me.hapyl.fight.game.talents.shadow_assassin;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.shadow_assassin.ShadowAssassinData;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.eterna.module.math.Tick;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DarkCover extends ShadowAssassinTalent {

    @DisplayField private final int darkCoverDuration = Tick.fromSecond(6);
    @DisplayField private final short energyRegen = 10;
    @DisplayField private final double explosionRadius = 3.5d;
    @DisplayField private final double explosionDamage = 10.0d;
    @DisplayField(percentage = true) private final double attackIncrease = 0.25d;
    @DisplayField private final int attackIncreaseDuration = Tick.fromSecond(3);
    @DisplayField private final double furyDamageMultiplier = 2.0d;

    private final PlayerMap<GameTask> darkCoverTask = PlayerMap.newMap();

    public DarkCover(@Nonnull DatabaseKey key) {
        super(key, "Dark Cover");

        setType(TalentType.DAMAGE);
        setItem(Material.NETHERITE_BOOTS);

        // Make sure setTalents is last
        setTalents(new Stealth(), new Fury(25));
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        final GameTask task = darkCoverTask.remove(player);

        if (task != null) {
            task.cancel();
        }
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        darkCoverTask.values().forEach(GameTask::cancel);
        darkCoverTask.clear();
    }

    public void onDamage(@Nonnull GamePlayer player, @Nullable LivingGameEntity entity, double damage) {
        if (entity == null) {
            return;
        }

        final GameTask task = darkCoverTask.remove(player);

        if (task == null) {
            return;
        }

        task.cancel();
        executeAoEDamage(entity.getLocation().add(0.0d, 1.0d, 0.0d), player, explosionDamage, true);
        setDarkCover(player, false);
    }

    public void executeAoEDamage(@Nonnull Location location, @Nonnull GamePlayer player, double damage, boolean regenerateEnergy) {
        final ShadowAssassinData data = getData(player);

        Collect.nearbyEntities(location, explosionRadius).forEach(entity -> {
            if (entity.equals(player)) {
                return;
            }

            entity.damage(damage, player, EnumDamageCause.DARK_ENERGY);
            if (regenerateEnergy) {
                data.addEnergy(energyRegen);
            }
        });

        // Fx
        final World world = player.getWorld();

        world.spawn(location, Firework.class, self -> {
            final FireworkMeta meta = self.getFireworkMeta();
            final FireworkEffect.Builder builder = FireworkEffect.builder()
                    .with(FireworkEffect.Type.BALL)
                    .withColor(Color.PURPLE_SHADOW.getBukkitColor())
                    .withFlicker();

            meta.addEffect(builder.build());

            self.setFireworkMeta(meta);
            self.detonate();
        });

        player.spawnWorldParticle(location, Particle.WITCH, 10, 0.5d, 0.5d, 0.5d, 1);
        player.spawnWorldParticle(location, Particle.POOF, 1, 0d, 0d, 0d, 1);

        player.playWorldSound(location, Sound.ENTITY_ENDER_DRAGON_HURT, 0.0f);
        player.playWorldSound(location, Sound.ENTITY_ENDERMAN_HURT, 0.75f);
        player.playWorldSound(location, Sound.ENTITY_SHULKER_DEATH, 0.0f);
    }

    public void setDarkCover(GamePlayer player, boolean flag) {
        if (flag) {
            player.addEffect(Effects.INVISIBILITY, 999999, true);
            playDarkCoverFx(player, true);
        }

        else {
            darkCoverTask.remove(player);

            player.removeEffect(Effects.INVISIBILITY);
            playDarkCoverFx(player, false);
        }
    }

    public void playDarkCoverFx(GamePlayer player, boolean flag) {
        final Location location = player.getEyeLocation();

        if (flag) {
            player.spawnWorldParticle(location, Particle.CRIT, 20, 0, 0.2, 0, 1.0f);
            player.spawnWorldParticle(location, Particle.ENCHANTED_HIT, 20, 0, 0.2, 0, 0.5f);
            player.spawnWorldParticle(location, Particle.WARPED_SPORE, 10, 0, 0.5, 0, 0);
            player.playWorldSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.75f);

            player.sendTitle("&8&l\uD83E\uDEA3", "&7In Dark Cover", 0, 200000, 0);
        }
        else {
            player.spawnWorldParticle(location, Particle.ENCHANT, 10, 0, 0, 0, 2);
            player.playWorldSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.25f);

            player.clearTitle();
        }
    }

    public boolean isInDarkCover(@Nonnull GamePlayer player) {
        return darkCoverTask.containsKey(player);
    }

    private class Stealth extends StealthTalent {

        public Stealth() {
            super(DarkCover.this);

            setDescription("""
                    Cloak yourself in darkness and become &ainvisible&7 and &ainvulnerable&7 for a maximum of &b{darkCoverDuration}&7.
                    
                    Dealing damage clears this effect and deals &cAoE damage&7.
                    
                    Also regenerate %s{energyRegen} %s&7 per enemy hit.
                    """.formatted(Named.SHADOW_ENERGY.getColor(), Named.SHADOW_ENERGY)
            );

            setCooldownSec(12);
        }

        @Override
        public Response execute(@Nonnull GamePlayer player) {
            setDarkCover(player, true);

            final GameTask oldTask = darkCoverTask.remove(player);

            if (oldTask != null) {
                oldTask.cancel();
            }

            darkCoverTask.put(player, GameTask.runLater(() -> setDarkCover(player, false), darkCoverDuration));

            return Response.OK;
        }
    }

    private class Fury extends FuryTalent {

        public Fury(int furyCost) {
            super(DarkCover.this, furyCost);

            setDescription("""
                    Instantly deal &cAoE damage&7 in front of you and gain %s boost.
                    
                    The damage dealt is &ax{furyDamageMultiplier}&7 of that in &9Stealth&7 mode.
                    """.formatted(AttributeType.ATTACK)
            );
            setCooldownSec(18);
        }

        @Override
        public Response execute(@Nonnull GamePlayer player) {
            executeAoEDamage(player.getLocationInFront(2.5d).add(0.0d, 1.0d, 0.0), player, explosionDamage * furyDamageMultiplier, false);

            player.getAttributes().increaseTemporary(Temper.DARK_COVER, AttributeType.ATTACK, attackIncrease, attackIncreaseDuration);

            return Response.OK;
        }
    }


}
