package me.hapyl.fight.game.weapons.ability;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.game.talents.Cooldown;
import me.hapyl.fight.game.talents.Timed;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Described;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

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
    private Material cooldownMaterial;

    public Ability(@Nonnull String name, @Nonnull String description) {
        this.name = name;
        this.description = description;
        this.cooldownMap = Maps.newHashMap();
        this.cooldownMaterial = null;
    }

    public Ability(@Nonnull String name, @Nonnull String description, @Nullable Object... format) {
        this(name, description.formatted(format));
    }

    public void setCooldownMaterial(@Nullable Material cooldownMaterial) {
        this.cooldownMaterial = cooldownMaterial;
    }

    @Nullable
    public abstract Response execute(@Nonnull GamePlayer player, @Nonnull ItemStack item);

    public final void execute0(GamePlayer player, ItemStack item) {
        if (hasCooldown(player)) {
            if (player.isSettingEnabled(Settings.SHOW_COOLDOWN_MESSAGE)) {
                sendError(player, "&cThis ability is on cooldown for %s!", getCooldownTimeLeftFormatted(player));
            }
            return;
        }

        final Response response = execute(player, item);
        if (response != null && response.isError()) {
            sendError(player, "Unable to use this! " + response.getReason());
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
        return CFUtils.decimalFormatTick(getCooldownTimeLeft(player));
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
        if (cooldownMaterial != null) {
            player.setCooldown(cooldownMaterial, cooldown);
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

    private void sendError(GamePlayer player, String error, Object... format) {
        player.sendMessage(ChatColor.RED + error, format);
        player.playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 0.0f);
    }

}
