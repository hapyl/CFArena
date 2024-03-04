package me.hapyl.fight.game.heroes.archive.swooper;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.EntityLocation;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.talents.archive.swooper.SwooperPassive;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.reflect.glow.Glowing;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class SwooperData extends PlayerData {

    private static final Particle.DustTransition nestParticleData = new Particle.DustTransition(
            org.bukkit.Color.fromRGB(46, 59, 79),
            org.bukkit.Color.fromRGB(42, 68, 110), 1
    );

    private static final BlockData nestParticleData2 = Material.GRAY_CONCRETE.createBlockData();

    private final Swooper swooper;
    private final Set<LivingGameEntity> highlightedEntities;
    protected int ultimateShots;
    protected int sneakTicks;
    protected int lastButt;
    @Nullable
    protected EntityLocation nestLocation;
    private boolean stealthMode;

    public SwooperData(Swooper swooper, GamePlayer player) {
        super(player);

        this.swooper = swooper;
        this.highlightedEntities = Sets.newHashSet();
    }

    public int getUltimateShots() {
        return ultimateShots;
    }

    public boolean isStealthMode() {
        return stealthMode;
    }

    public void setStealthMode(boolean stealth) {
        this.stealthMode = stealth;

        // Enter mode
        if (stealth) {
            player.hidePlayer();
            nestLocation = player.getEntityLocation().add(0, 0.15, 0);

            // Fx
            player.playWorldSound(Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 0.75f);
        }
        else {
            player.showPlayer();
            nestLocation = null;

            // Start cooldown
            player.startCooldown(swooper.getPassiveTalent());

            // Fx
            player.playWorldSound(Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 1.0f);
        }

        // Title because humans are stupid
        player.sendTitle(Named.REFRACTION.getCharacterColored(), stealth ? "&aᴀᴄᴛɪᴠᴀᴛᴇᴅ" : "&cᴅᴇᴀᴄᴛɪᴠᴀᴛᴇᴅ", 0, 15, 5);
    }

    public void drawNestParticles() {
        if (nestLocation == null) {
            return;
        }

        final SwooperPassive passiveTalent = swooper.getPassiveTalent();

        // Fx
        final int aliveTicks = player.aliveTicks();
        final double radians = Math.toRadians(aliveTicks);

        final double x = Math.sin(radians * 5) * passiveTalent.maxNestStrayDistance;
        final double y = Math.sin(radians) * 0.1d;
        final double z = Math.cos(radians * 5) * passiveTalent.maxNestStrayDistance;

        nestLocation.modifyAnd(x, y, z, then -> {
            player.spawnWorldParticle(then, Particle.DUST_COLOR_TRANSITION, 1, 0, 0, 0, 0, nestParticleData);
        });

        nestLocation.modifyAnd(z, y, x, then -> {
            player.spawnWorldParticle(then, Particle.DUST_COLOR_TRANSITION, 1, 0, 0, 0, 0, nestParticleData);
        });

        // Ambient
        player.spawnWorldParticle(nestLocation, Particle.FALLING_DUST, 2, 0.8d, 0.4d, 0.8d, 0, nestParticleData2);
    }

    public boolean isTooFarAwayFromNest() {
        final double maxNestStrayDistance = swooper.getPassiveTalent().maxNestStrayDistance;

        return nestLocation != null && player.getLocation().distance(nestLocation) >= maxNestStrayDistance;
    }

    public void addHighlighted(@Nonnull LivingGameEntity entity) {
        highlightedEntities.add(entity);

        Glowing.glowInfinitely(entity.getEntity(), ChatColor.DARK_PURPLE, player.getPlayer());
    }

    public void removeHighlighted(@Nonnull LivingGameEntity entity) {
        if (!highlightedEntities.contains(entity)) {
            return;
        }

        highlightedEntities.remove(entity);

        Glowing.stopGlowing(player.getPlayer(), entity.getEntity());
    }

    public boolean isHighlighted(LivingGameEntity entity) {
        return highlightedEntities.contains(entity);
    }

    @Override
    public void remove() {
        highlightedEntities.forEach(entity -> {
            Glowing.stopGlowing(player.getPlayer(), entity.getEntity());
        });
        highlightedEntities.clear();
    }

    @Nonnull
    public String makeBars() {
        return Named.REFRACTION.getCharacterColored() + " " + makeBar(sneakTicks);
    }

    public boolean isEnemyWithinNest() {
        if (nestLocation == null) {
            return false;
        }

        final double maxNestStrayDistance = swooper.getPassiveTalent().maxNestStrayDistance;

        for (LivingGameEntity entity : Collect.nearbyEntities(nestLocation, maxNestStrayDistance)) {
            if (player.isSelfOrTeammate(entity)) {
                continue;
            }

            return true;
        }

        return false;
    }

    private String makeBar(int i) {
        final int halfStandingTime = swooper.getPassiveTalent().sneakThreshold;
        final int progress = i * 10 / halfStandingTime;

        return (Color.SKY_BLUE + "|".repeat(progress)) + (Color.DARK_GRAY + "|".repeat(10 - progress));
    }
}
