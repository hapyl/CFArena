package me.hapyl.fight.game.heroes.bloodfield;

import me.hapyl.fight.game.TalentReference;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.Outline;
import me.hapyl.fight.game.talents.bloodfiend.BloodfiendPassive;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;

import javax.annotation.Nonnull;

public class BiteData implements TalentReference<BloodfiendPassive> {

    private final BloodfiendPassive reference;
    private final GamePlayer player;
    private final LivingGameEntity entity;
    private long lastBite;

    protected int tick;

    public BiteData(BloodfiendPassive reference, GamePlayer player, LivingGameEntity entity) {
        this.reference = reference;
        this.player = player;
        this.entity = entity;
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

        final double health = entity.getHealth();

        entity.getAttributes().subtract(AttributeType.MAX_HEALTH, reference.healthDeduction);

        if (health > entity.getMaxHealth()) {
            entity.setHealth(entity.getMaxHealth());
        }

        // Fx (Only show for players)
        if (!(entity instanceof GamePlayer entityPlayer)) {
            return;
        }

        entityPlayer.setOutline(Outline.RED);
        entityPlayer.sendMessage("&6&l🦇 &e%s has bitten you! &c-%s ❤", this.player.getName(), reference.healthDeduction);
        entityPlayer.playSound(Sound.ENTITY_BAT_DEATH, 0.75f);
        entityPlayer.playSound(Sound.ENTITY_ZOMBIE_HURT, 0.75f);
    }

    public void remove() {
        entity.getAttributes().add(AttributeType.MAX_HEALTH, reference.healthDeduction);

        if (!(entity instanceof GamePlayer entityPlayer)) {
            return;
        }

        // Fx
        entityPlayer.setOutline(Outline.CLEAR);
        entityPlayer.sendMessage("&6&l🦇 &e&oMuch better! &a+%s ❤", reference.healthDeduction);
        entityPlayer.playSound(Sound.ENTITY_HORSE_SADDLE, 0.75f);
        entityPlayer.playSound(Sound.ENTITY_WARDEN_HEARTBEAT, 0.0f);
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
    public LivingGameEntity getEntity() {
        return entity;
    }

}
