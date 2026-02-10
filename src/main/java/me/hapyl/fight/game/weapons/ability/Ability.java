package me.hapyl.fight.game.weapons.ability;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.eterna.module.util.Described;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.SoundEffect;
import me.hapyl.fight.game.setting.EnumSetting;
import me.hapyl.fight.game.talents.Cooldown;
import me.hapyl.fight.game.talents.Timed;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

public abstract class Ability implements Described, Timed, Cooldown, DisplayFieldProvider {

    private final String name;
    private final Map<UUID, AbilityCooldown> cooldownMap;

    private String description;
    private int cooldown;
    private int duration;
    private Key cooldownKey;

    public Ability(@Nonnull String name, @Nonnull String description) {
        this.name = name;
        this.description = description;
        this.cooldownMap = Maps.newHashMap();
        this.cooldownKey = null;
    }

    public void setCooldownKey(@Nonnull Keyed keyed) {
        this.cooldownKey = keyed.getKey();
    }

    @Nullable
    public abstract Response execute(@Nonnull GamePlayer player);

    public final void execute0(@Nonnull GamePlayer player) {
        if (hasCooldown(player)) {
            if (player.isSettingEnabled(EnumSetting.SHOW_COOLDOWN_MESSAGE)) {
                Response.error(player, "Ability on cooldown for %s!".formatted(getCooldownTimeLeftFormatted(player)));
                player.playSound(SoundEffect.ERROR);
            }

            return;
        }

        final Response response = execute(player);
        
        if (response != null && response.isError()) {
            response.sendError(player);
            return;
        }

        if (cooldown > 0 && (response != null && response.isOk())) {
            startCooldown(player, cooldown);
        }
    }

    public int getCooldownTimeLeft(GamePlayer player) {
        final AbilityCooldown abilityCooldown = cooldownMap.get(player.getUUID());

        if (abilityCooldown == null) {
            return 0;
        }

        return abilityCooldown.getTimeLeft();
    }

    @Nonnull
    public String getCooldownTimeLeftFormatted(GamePlayer player) {
        return CFUtils.formatTick(getCooldownTimeLeft(player));
    }

    public void stopCooldown(GamePlayer player) {
        cooldownMap.remove(player.getUUID());
    }

    public void clearCooldowns() {
        cooldownMap.clear();
    }

    public void startCooldown(GamePlayer player) {
        startCooldown(player, cooldown);
    }

    public void startCooldown(GamePlayer player, int cooldown) {
        cooldownMap.put(player.getUUID(), new AbilityCooldown(System.currentTimeMillis(), cooldown * 50L));

        if (cooldownKey != null) {
            player.cooldownManager.setCooldown(cooldownKey, cooldown);
        }
    }

    public boolean hasCooldown(GamePlayer player) {
        final AbilityCooldown abilityCooldown = cooldownMap.get(player.getUUID());

        if (abilityCooldown == null) {
            return false;
        }

        if (abilityCooldown.isOver()) {
            cooldownMap.remove(player.getUUID());
            return false;
        }

        return true;
    }

    @Override
    public int getCooldown() {
        return cooldown;
    }

    @Override
    public Ability setCooldown(int cooldown) {
        this.cooldown = cooldown;
        return this;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public Ability setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    @Nonnull
    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isTypeApplicable(@Nonnull AbilityType type) {
        return true;
    }

}
