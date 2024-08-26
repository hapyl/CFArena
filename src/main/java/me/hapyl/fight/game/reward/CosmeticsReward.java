package me.hapyl.fight.game.reward;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CosmeticEntry;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Cosmetics;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class CosmeticsReward extends SimpleReward {

    private final List<Cosmetics> cosmetics;

    protected CosmeticsReward(@Nonnull String name, @Nonnull Cosmetics[] cosmetics) {
        super(name);

        this.cosmetics = Arrays.asList(cosmetics);
    }

    @Nonnull
    @Override
    public RewardDescription getDescription(@Nonnull Player player) {
        final RewardDescription display = new RewardDescription();

        for (Cosmetics enumCosmetic : cosmetics) {
            final Cosmetic cosmetic = enumCosmetic.getCosmetic();

            display.add(cosmetic.getFormatted());
        }

        return display;
    }

    @Override
    public void grant(@Nonnull Player player) {
        final CosmeticEntry entry = PlayerDatabase.getDatabase(player).cosmeticEntry;

        for (Cosmetics cosmetic : cosmetics) {
            entry.addOwned(cosmetic);
        }
    }

    @Override
    public void revoke(@Nonnull Player player) {
        final CosmeticEntry entry = PlayerDatabase.getDatabase(player).cosmeticEntry;

        for (Cosmetics cosmetic : cosmetics) {
            entry.removeOwned(cosmetic);
        }
    }
}
