package me.hapyl.fight.game.entity;

import me.hapyl.eterna.module.annotate.Super;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;

public class GamePlayerCooldownManager {
    private final GamePlayer player;

    GamePlayerCooldownManager(GamePlayer player) {
        this.player = player;
    }

    public void setCooldown(@Nonnull ItemStack itemStack, int cd) {
        setCooldown0(itemStack, cd, false);
    }

    public void setCooldown(@Nonnull Key key, int cd) {
        setCooldown(ItemBuilder.createDummyCooldownItem(key), cd);
    }

    public void setCooldown(@Nonnull Keyed keyed, int cd) {
        setCooldown(keyed.getKey(), cd);
    }

    public int getCooldown(@Nonnull ItemStack itemStack) {
        return this.player.getEntity().getCooldown(itemStack);
    }

    public int getCooldown(@Nonnull Key key) {
        return getCooldown(ItemBuilder.createDummyCooldownItem(key));
    }

    public int getCooldown(@Nonnull Keyed keyed) {
        return getCooldown(keyed.getKey());
    }

    public boolean hasCooldown(@Nonnull ItemStack itemStack) {
        return this.player.getEntity().hasCooldown(itemStack);
    }

    public boolean hasCooldown(@Nonnull Key key) {
        return hasCooldown(ItemBuilder.createDummyCooldownItem(key));
    }

    public boolean hasCooldown(@Nonnull Keyed keyed) {
        return hasCooldown(keyed.getKey());
    }

    @Nonnull
    public String getCooldownFormatted(@Nonnull ItemStack itemStack) {
        return CFUtils.formatTick(getCooldown(itemStack));
    }

    @Nonnull
    public String getCooldownFormatted(@Nonnull Key key) {
        return getCooldownFormatted(ItemBuilder.createDummyCooldownItem(key));
    }

    @Nonnull
    public String getCooldownFormatted(@Nonnull Keyed keyed) {
        return getCooldownFormatted(keyed.getKey());
    }

    /**
     * @apiNote Prefer cooldown modifier, only use this for icd n shit
     */
    @ApiStatus.Internal
    public void setCooldownIgnoreCooldownModifier(@Nonnull ItemStack itemStack, int cd) {
        setCooldown0(itemStack, cd, true);
    }

    /**
     * @apiNote Prefer cooldown modifier, only use this for icd n shit
     */
    @ApiStatus.Internal
    public void setCooldownIgnoreCooldownModifier(@Nonnull Key key, int cd) {
        setCooldownIgnoreCooldownModifier(ItemBuilder.createDummyCooldownItem(key), cd);
    }

    /**
     * @apiNote Prefer cooldown modifier, only use this for icd n shit
     */
    @ApiStatus.Internal
    public void setCooldownIgnoreCooldownModifier(@Nonnull Keyed keyed, int cd) {
        setCooldownIgnoreCooldownModifier(keyed.getKey(), cd);
    }

    // this is the actual super method, don't make it public
    @Super
    private void setCooldown0(@Nonnull ItemStack itemStack, int cooldown, boolean ignoreCooldownModifier) {
        final Player bukkitEntity = this.player.getEntity();
        final double cooldownModifier = this.player.getAttributes().get(AttributeType.COOLDOWN_MODIFIER);

        bukkitEntity.setCooldown(itemStack, (int) Math.max(0, (ignoreCooldownModifier ? cooldown : cooldown * cooldownModifier)));
    }

}
