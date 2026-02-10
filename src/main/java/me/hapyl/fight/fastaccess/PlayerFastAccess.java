package me.hapyl.fight.fastaccess;

import me.hapyl.fight.database.entry.FastAccessEntry;
import me.hapyl.fight.database.rank.PlayerRank;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nullable;

public class PlayerFastAccess {

    private final PlayerProfile profile;
    private final FastAccessEntry entry;
    private final FastAccess[] fastAccess;

    public PlayerFastAccess(PlayerProfile profile) {
        this.profile = profile;
        this.entry = profile.getDatabase().fastAccessEntry;
        this.fastAccess = entry.getArray();
    }

    public void update() {
        final Player player = profile.getPlayer();
        final PlayerInventory inventory = player.getInventory();

        for (int i = 0; i < fastAccess.length; i++) {
            inventory.setItem(9 + i, getItem(i));
        }
    }

    @Nullable
    public FastAccess getFastAccess(int index) {
        return index >= 0 && index < fastAccess.length ? fastAccess[index] : null;
    }

    public void setFastAccess(int index, @Nullable FastAccess access) {
        if (index < 0 || index >= fastAccess.length) {
            return;
        }

        fastAccess[index] = access;
        entry.saveArray(fastAccess);

        update();
    }

    private ItemStack getItem(int index) {
        final FastAccess fastAccess = this.fastAccess[index];

        if (fastAccess != null) {
            return fastAccess.createAsButton(profile.getPlayer());
        }

        final ItemBuilder builder = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE)
                .setName("Quick Access " + (index + 1))
                .addLore()
                .addSmartLore("Quick access buttons can be used to quickly select a class, map or other actions.")
                .addLore();

        final PlayerRank rankToAccess = FastAccess.slowRankMap.get(index);
        final PlayerRank playerRank = profile.getRank();

        if (playerRank.isOrHigher(rankToAccess)) {
            builder.addLore("&8Not configured!");
            builder.addLore();
            builder.addLore(Color.BUTTON + "Click to configure!");
        }
        else {
            builder.addLore(Color.ERROR + "Cannot configure!");
            builder.addLore(Color.ERROR + "This slot requires " + rankToAccess.getPrefix() + Color.ERROR + "!");
        }

        return builder.asIcon();
    }

}
