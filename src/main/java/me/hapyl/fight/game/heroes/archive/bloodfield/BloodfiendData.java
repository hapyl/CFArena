package me.hapyl.fight.game.heroes.archive.bloodfield;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.game.TalentReference;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.effect.archive.BleedEffect;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.archive.bloodfield.impel.ImpelInstance;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.archive.bloodfiend.BloodCup;
import me.hapyl.fight.game.talents.archive.bloodfiend.BloodfiendPassive;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Ticking;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class BloodfiendData implements Ticking, TalentReference<BloodfiendPassive> {

    private final GamePlayer player;
    private final Map<GamePlayer, BiteData> succulence; // FIXME -> Maybe change this to apply to living entities?
    private final BleedEffect bleedEffect;
    private ImpelInstance impelInstance;
    private int flightTime;
    private boolean flying;
    private GameTask cooldownTask;
    private BatCloud batCloud;
    private int blood;

    public BloodfiendData(GamePlayer player) {
        this.player = player;
        this.succulence = Maps.newConcurrentMap();
        this.flightTime = 0;
        this.flying = false;
        this.bleedEffect = (BleedEffect) Effects.BLEED.getEffect();
    }

    @Nullable
    public ImpelInstance getImpelInstance() {
        return impelInstance;
    }

    @Nonnull
    public ImpelInstance newImpelInstance(Bloodfiend instance, GamePlayer player) {
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

        if (batCloud != null) {
            batCloud.remove();
            batCloud = null;
        }

        if (impelInstance != null) {
            impelInstance.stop();
            impelInstance = null;
        }

        succulence.forEach((player, tick) -> {
            stopSucculence(player);
        });

        succulence.clear();
        blood = 0;
    }

    @Nonnull
    public BiteData getBiteData(GamePlayer player) {
        return succulence.computeIfAbsent(player, fn -> new BiteData(getTalent(), this.player, player));
    }

    public void addSucculence(GamePlayer player) {
        final BloodfiendPassive succulence = getTalent();
        final BiteData biteData = getBiteData(player);

        biteData.bite(succulence.biteDuration);
    }

    public void stopSucculence(GamePlayer player) {
        final BloodfiendPassive succulence = getTalent();
        final BiteData biteDara = this.succulence.remove(player);

        if (biteDara != null) {
            biteDara.remove();
        }
    }

    @Override
    public void tick() {
        succulence.forEach((player, data) -> {
            data.tick--;

            if (data.tick <= 0 || player.isDeadOrRespawning()) {
                stopSucculence(player);
            }
            else {
                // Fx
                bleedEffect.spawnParticle(player.getLocation().add(0, 0.5, 0));
                bleedEffect.spawnParticle(this.player.getLocation().add(0, 0.5, 0));
            }
        });

        if (flying) {
            if (flightTime-- <= 0) {
                stopFlying();
                return;
            }

            if (batCloud != null) {
                batCloud.tick();
            }

            final int distanceToGround = getDistanceToGround();

            if (distanceToGround >= getTalent().maxFlightHeight) {
                player.sendMessage("&6&l\uD83D\uDD4A &eThe bats are afraid of height!");
                stopFlying();
                return;
            }

            // Fx
            player.sendSubtitle("&2\uD83D\uDD4A &l" + CFUtils.decimalFormatTick(flightTime), 0, 5, 0);
        }
    }

    public void startFlying() {
        final Location location = player.getLocation();

        player.setAllowFlight(true);
        player.setFlying(true);
        player.setFlySpeed(0.08f);

        flying = true;
        flightTime = getTalent().flightDuration;

        // Fx
        PlayerLib.playSound(location, Sound.ENTITY_BAT_TAKEOFF, 0.0f);
        PlayerLib.playSound(location, Sound.ENTITY_BAT_TAKEOFF, 0.75f);
        PlayerLib.playSound(location, Sound.ENTITY_BAT_TAKEOFF, 1.75f);

        if (batCloud != null) {
            batCloud.remove();
        }

        batCloud = new BatCloud(player.getPlayer());
    }

    public void stopFlying() {
        flying = false;
        flightTime = 0;
        player.setAllowFlight(false);
        player.setFlying(false);

        cooldownFlight(false);

        batCloud.remove();
        batCloud = null;

        player.addEffect(Effects.FALL_DAMAGE_RESISTANCE, 100);

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
            player.playSound(Sound.ENTITY_BAT_TAKEOFF, 1.25f);
            player.playSound(Sound.ENTITY_BAT_HURT, 0.0f);
            player.sendMessage("&2&l\uD83D\uDD4A &aSpectral Form is ready!");
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

    public boolean isBitten(GamePlayer gamePlayer) {
        return succulence.containsKey(gamePlayer);
    }

    public void addBlood() {
        final BloodCup bloodCup = Talents.BLOOD_CUP.getTalent(BloodCup.class);

        if (ThreadRandom.nextDouble() > bloodCup.chance || blood >= bloodCup.maxBlood) {
            return;
        }

        blood++;
        bloodCup.updateTexture(this);
    }

    public void clearBlood() {
        blood = 0;
    }

    public int getBlood() {
        return blood;
    }

    @Nonnull
    public GamePlayer getPlayer() {
        return player;
    }

    @Nullable
    public GamePlayer getMostRecentBitPlayer() {
        GamePlayer player = null;
        long mostRecent = 0;

        for (BiteData data : succulence.values()) {
            final long lastBite = data.getLastBite();

            if (mostRecent == 0 || lastBite > mostRecent) {
                mostRecent = lastBite;
                player = data.getPlayer();
            }
        }

        return player;
    }
}
