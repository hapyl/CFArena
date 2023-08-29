package me.hapyl.fight.game.heroes.archive.bloodfield;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.TalentHandle;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.Ticking;
import me.hapyl.fight.game.heroes.archive.bloodfield.impel.ImpelInstance;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.archive.bloodfiend.BloodfiendPassive;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.entity.EntityUtils;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class BloodfiendData implements Ticking, TalentHandle<BloodfiendPassive> {

    private final Player player;
    private final Map<GamePlayer, Integer> succulence;

    private ImpelInstance impelInstance;
    private int flightTime;
    private boolean flying;
    private Bat flightBat;
    private GameTask cooldownTask;

    public BloodfiendData(Player player) {
        this.player = player;
        this.succulence = Maps.newConcurrentMap();
        this.flightTime = 0;
        this.flying = false;
    }

    @Nullable
    public ImpelInstance getImpelInstance() {
        return impelInstance;
    }

    @Nonnull
    public ImpelInstance newImpelInstance(Bloodfiend instance, Player player) {
        if (impelInstance != null) {
            impelInstance.stop();
        }

        impelInstance = new ImpelInstance(instance, player, Sets.newHashSet(succulence.keySet()));
        return impelInstance;
    }

    public void reset() {
        if (cooldownTask != null) {
            cooldownTask.cancel();
        }

        if (impelInstance != null) {
            impelInstance.stop();
            impelInstance = null;
        }

        succulence.forEach((player, tick) -> {
            stopSucculence(player);
        });

        succulence.clear();
    }

    public void addSucculence(GamePlayer player) {
        final BloodfiendPassive succulence = getTalent();
        final Integer oldValue = this.succulence.put(player, succulence.biteDuration);

        // Don't do anything if not first hit
        if (oldValue != null) {
            return;
        }

        final double health = player.getHealth();

        player.getAttributes().subtract(AttributeType.MAX_HEALTH, succulence.healthDeduction);

        if (health > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }

        // Fx
        final WorldBorder worldBorder = Bukkit.createWorldBorder();
        worldBorder.setCenter(player.getLocation());
        worldBorder.setSize(1000);
        worldBorder.setWarningDistance(2000);

        player.getPlayer().setWorldBorder(worldBorder);
        player.sendMessage("&6&l🦇 &e%s has bitten you! &c-%s ❤", this.player.getName(), succulence.healthDeduction);
        player.playSound(Sound.ENTITY_BAT_DEATH, 0.75f);
        player.playSound(Sound.ENTITY_ZOMBIE_HURT, 0.75f);
    }

    public void stopSucculence(GamePlayer player) {
        final BloodfiendPassive succulence = getTalent();
        this.succulence.remove(player);

        player.getAttributes().add(AttributeType.MAX_HEALTH, succulence.healthDeduction);
        player.updateHealth();

        // Fx
        player.getPlayer().setWorldBorder(null);
        player.sendMessage("&6&l🦇 &e&oMuch better! &a+%s ❤", succulence.healthDeduction);
        player.playSound(Sound.ENTITY_HORSE_SADDLE, 0.75f);
        player.playSound(Sound.ENTITY_WARDEN_HEARTBEAT, 0.0f);
    }

    @Override
    public void tick() {
        succulence.forEach((player, tick) -> {
            int tickMinusOne = tick - 1;

            if (tickMinusOne <= 0 || player.isDeadOrRespawning()) {
                stopSucculence(player);
            }
            else {
                succulence.put(player, tickMinusOne);
            }
        });

        if (flying) {
            if (flightTime-- <= 0) {
                stopFlying();
                return;
            }

            if (flightBat != null) {
                flightBat.teleport(player.getLocation());
            }

            final int distanceToGround = getDistanceToGround();

            if (distanceToGround >= getTalent().maxFlightHeight) {
                Chat.sendMessage(player, "&2&l\uD83D\uDD4A &aDon't fly too high!");
                stopFlying();
            }

            // Fx
            Chat.sendTitle(player, "", "&2\uD83D\uDD4A &l" + Utils.decimalFormat(flightTime), 0, 5, 0);
        }
    }

    public void startFlying() {
        final Location location = player.getLocation();

        player.setAllowFlight(true);
        player.setFlying(true);
        player.setFlySpeed(0.1f);

        flying = true;
        flightTime = getTalent().flightDuration;

        // Fx
        PlayerLib.playSound(location, Sound.ENTITY_BAT_TAKEOFF, 0.0f);
        PlayerLib.playSound(location, Sound.ENTITY_BAT_TAKEOFF, 0.75f);
        PlayerLib.playSound(location, Sound.ENTITY_BAT_TAKEOFF, 1.75f);

        if (flightBat != null) {
            flightBat.remove();
        }

        flightBat = Entities.BAT.spawn(location, self -> {
            self.setAwake(true);
            self.setInvulnerable(true);
            self.setCustomName(player.getName());
            self.setCustomNameVisible(true);
            EntityUtils.setCollision(self, EntityUtils.Collision.DENY, player);
        });

        CF.getOrCreatePlayer(player).addEffect(GameEffectType.INVISIBILITY, 1000);
    }

    public void stopFlying() {
        flying = false;
        flightTime = 0;
        player.setAllowFlight(false);
        player.setFlying(false);

        cooldownFlight(false);

        flightBat.remove();
        final GamePlayer gamePlayer = CF.getOrCreatePlayer(player);

        gamePlayer.removeEffect(GameEffectType.INVISIBILITY);
        gamePlayer.addEffect(GameEffectType.FALL_DAMAGE_RESISTANCE, 100);

        // Fx
        final Location location = player.getLocation();

        PlayerLib.playSound(location, Sound.ENTITY_BAT_TAKEOFF, 0.0f);
        PlayerLib.playSound(location, Sound.ENTITY_BAT_DEATH, 0.0f);
    }

    public void cooldownFlight(boolean respawn) {
        final BloodfiendPassive talent = getTalent();
        final int cooldown = respawn ? talent.flightCooldown / 2 : talent.flightCooldown;

        player.setCooldown(talent.getMaterial(), cooldown);

        if (cooldownTask != null) {
            cooldownTask.cancel();
        }

        cooldownTask = GameTask.runLater(() -> {
            player.setAllowFlight(true);
            PlayerLib.playSound(player, Sound.ENTITY_BAT_TAKEOFF, 1.25f);
            PlayerLib.playSound(player, Sound.ENTITY_BAT_DEATH, 1.25f);
            Chat.sendMessage(player, "&2&l\uD83D\uDD4A &aSpectral Form is ready!");
        }, cooldown);
    }

    public int getFlightCooldown() {
        final Material material = Talents.SUCCULENCE.getTalent().getMaterial();
        return player.getCooldown(material);
    }

    public int getDistanceToGround() {
        final Location location = player.getLocation();
        final BloodfiendPassive talent = getTalent();

        int distance = 0;
        Block block = location.getBlock();

        while (!block.getType().isSolid() && !(distance > talent.maxFlightHeight)) {
            block = block.getRelative(BlockFace.DOWN);
            distance++;
        }

        return distance;
    }

    @Nonnull
    public Set<GamePlayer> getSucculencePlayers() {
        return succulence.keySet();
    }

    public int getSucculencePlayersCount() {
        return succulence.size();
    }

    @Nonnull
    @Override
    public BloodfiendPassive getTalent() {
        return Talents.SUCCULENCE.getTalent(BloodfiendPassive.class);
    }

    public boolean hasFlightCooldown() {
        return getFlightCooldown() > 0;
    }

    public boolean isFlying() {
        return flying;
    }

    public void clearSucculence() {
        succulence.clear();
    }
}
