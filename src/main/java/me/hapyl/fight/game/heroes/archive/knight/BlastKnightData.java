package me.hapyl.fight.game.heroes.archive.knight;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.PlayerData;
import me.hapyl.fight.util.MaterialCooldown;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class BlastKnightData extends PlayerData implements MaterialCooldown {

    public static final int MAX_CHARGE = 15;

    private int shieldCharge;

    public BlastKnightData(GamePlayer player) {
        super(player);

        this.shieldCharge = 0;
    }

    @Override
    public void remove() {

    }

    public int getShieldCharge() {
        return shieldCharge;
    }

    public boolean isShieldOnCooldown() {
        return player.hasCooldown(Material.SHIELD);
    }

    public void incrementShieldCharge() {
        // Notify full charge
        if (shieldCharge + 1 == MAX_CHARGE) {
            player.sendTitle("🛡", "&2&lShield fully charged!", 5, 20, 5);
            player.playSound(Sound.ITEM_SHIELD_BLOCK, 0.0f);
        }

        shieldCharge = Math.min(shieldCharge + 1, MAX_CHARGE);
        startCooldown(player);
    }

    @Nonnull
    @Override
    public Material getCooldownMaterial() {
        return Material.SHIELD;
    }

    @Override
    public int getCooldown() {
        return 20;
    }

    public void resetShieldCharge() {
        shieldCharge = 0;
    }
}
