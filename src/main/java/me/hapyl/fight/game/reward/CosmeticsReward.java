package me.hapyl.fight.game.reward;

import me.hapyl.fight.CF;
import me.hapyl.fight.database.entry.CosmeticEntry;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class CosmeticsReward extends SimpleReward {

    private final List<Cosmetic> cosmetics;

    protected CosmeticsReward(@Nonnull String name, @Nonnull Cosmetic[] cosmetics) {
        super(name);

        this.cosmetics = Arrays.asList(cosmetics);
    }

    @Nonnull
    @Override
    public RewardDescription getDescription(@Nonnull Player player) {
        final RewardDescription display = new RewardDescription();

        for (Cosmetic cosmetic : cosmetics) {
            display.add(cosmetic.getFormatted());
        }

        return display;
    }

    @Override
    public void grant(@Nonnull Player player) {
        final CosmeticEntry entry = CF.getDatabase(player).cosmeticEntry;

        for (Cosmetic cosmetic : cosmetics) {
            entry.addOwned(cosmetic);
        }
    }

    @Override
    public void revoke(@Nonnull Player player) {
        final CosmeticEntry entry = CF.getDatabase(player).cosmeticEntry;

        for (Cosmetic cosmetic : cosmetics) {
            entry.removeOwned(cosmetic);
        }
    }
}
