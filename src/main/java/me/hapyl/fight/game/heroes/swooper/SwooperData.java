package me.hapyl.fight.game.heroes.swooper;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.EntityLocation;
import me.hapyl.fight.game.entity.EquipmentSlots;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.game.talents.swooper.SwooperPassive;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.Iterators;
import me.hapyl.spigotutils.module.reflect.glow.Glowing;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
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
            nestLocation = player.getEntityLocation().add(0, 0.15, 0);

            // Fx
            player.playWorldSound(Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 0.75f);
        }
        else {
            showPlayer();
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

    // This is a very hacky way of hiding the player,
    // but allowing projectiles to damage him.
    public void hidePlayer() {
        // Add invisibility
        player.addPotionEffect(PotionEffectType.INVISIBILITY, 1, 3);

        // Hide equipment
        makeEquipmentMap(false);

        // Remove arrows
        player.getPlayer().setArrowsInBody(0);
    }

    public void showPlayer() {
        makeEquipmentMap(true);
    }

    private void makeEquipmentMap(boolean real) {
        final Map<EquipmentSlot, ItemStack> map = Maps.newHashMap();

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot == EquipmentSlot.BODY) {
                // FIXME: Calling BODY slot on a player throws an exception because exceptions are fun
                continue;
            }

            map.put(slot, real ? player.getEquipment().getItem(slot) : null);
        }

        Bukkit.getOnlinePlayers().forEach(online -> {
            if (player.is(online) || player.getTeam().isEntry(Entry.of(online))) {
                return;
            }

            online.sendEquipmentChange(player.getPlayer(), map);
        });
    }

    private String makeBar(int i) {
        final int halfStandingTime = swooper.getPassiveTalent().sneakThreshold;
        final int progress = i * 10 / halfStandingTime;

        return (Color.SKY_BLUE + "|".repeat(progress)) + (Color.DARK_GRAY + "|".repeat(10 - progress));
    }
}
