package me.hapyl.fight.npc;

import me.hapyl.eterna.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerNPC extends PersistentNPC {

    private final Player player;

    public PlayerNPC(@Nonnull Player player, @Nonnull Location location, @Nullable String name) {
        super(location, name);

        this.player = player;
    }

    public PlayerNPC(@Nonnull Player player, double x, double y, double z, @Nullable String name) {
        this(player, BukkitUtils.defLocation(x, y, z), name);
    }

    public PlayerNPC(@Nonnull Player player, double x, double y, double z, float yaw, float pitch, @Nullable String name) {
        this(player, BukkitUtils.defLocation(x, y, z, yaw, pitch), name);
    }

    @Override
    public final void showAll() {
        create();
    }

    public final void create() {
        super.show(player);
    }
}
