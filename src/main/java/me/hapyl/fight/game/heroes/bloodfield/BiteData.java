package me.hapyl.fight.game.heroes.bloodfield;

import me.hapyl.fight.game.TalentReference;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.bloodfiend.BloodfiendPassive;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;

import javax.annotation.Nonnull;

public class BiteData implements TalentReference<BloodfiendPassive> {

    private final BloodfiendPassive reference;
    private final GamePlayer vampire;
    private final GamePlayer player;
    private long lastBite;

    protected int tick;

    public BiteData(BloodfiendPassive reference, GamePlayer vampire, GamePlayer player) {
        this.reference = reference;
        this.vampire = vampire;
        this.player = player;
        this.tick = 0;
        this.lastBite = 0L;
    }

    public void bite(int duration) {
        final boolean firstBite = tick == 0;

        this.tick = duration;
        this.lastBite = System.currentTimeMillis();

        // Don't do anything if not first hit
        if (!firstBite) {
            return;
        }

        final double health = player.getHealth();

        player.getAttributes().subtract(AttributeType.MAX_HEALTH, reference.healthDeduction);

        if (health > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }

        // Fx
        final WorldBorder worldBorder = Bukkit.createWorldBorder();
        worldBorder.setCenter(player.getLocation());
        worldBorder.setSize(1000);
        worldBorder.setWarningDistance(2000);

        player.getPlayer().setWorldBorder(worldBorder);
        player.sendMessage("&6&l🦇 &e%s has bitten you! &c-%s ❤", this.vampire.getName(), reference.healthDeduction);
        player.playSound(Sound.ENTITY_BAT_DEATH, 0.75f);
        player.playSound(Sound.ENTITY_ZOMBIE_HURT, 0.75f);
    }

    public void remove() {
        player.getAttributes().add(AttributeType.MAX_HEALTH, reference.healthDeduction);
        player.updateHealth();

        // Fx
        player.getPlayer().setWorldBorder(null);
        player.sendMessage("&6&l🦇 &e&oMuch better! &a+%s ❤", reference.healthDeduction);
        player.playSound(Sound.ENTITY_HORSE_SADDLE, 0.75f);
        player.playSound(Sound.ENTITY_WARDEN_HEARTBEAT, 0.0f);
    }

    @Nonnull
    @Override
    public BloodfiendPassive getTalent() {
        return reference;
    }

    public long getLastBite() {
        return lastBite;
    }

    @Nonnull
    public GamePlayer getPlayer() {
        return player;
    }
}
