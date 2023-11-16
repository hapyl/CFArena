package me.hapyl.fight.npc;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.Event;
import me.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Function;

public class PersistentNPC extends HumanNPC {

    private final String name;

    public PersistentNPC(@Nonnull Location location, @Nullable String name) {
        super(location, name == null ? null : Color.BUTTON.bold() + "CLICK", "");
        this.name = name;

        init();
    }

    public PersistentNPC(double x, double y, double z, @Nullable String name) {
        this(x, y, z, 0.0f, 0.0f, name);
    }

    public PersistentNPC(double x, double y, double z, float yaw, float pitch, @Nullable String name) {
        this(BukkitUtils.defLocation(x, y, z, yaw, pitch), name);
    }

    protected PersistentNPC(Location location, String npcName, String skinOwner, Function<UUID, String> hexNameFn) {
        super(location, npcName, skinOwner, hexNameFn);
        this.name = npcName;
    }

    @Event
    public void onSpawn(Player player) {
    }

    @Event
    public void onPrepare() {
    }

    @Override
    public final void show(@Nonnull Player... players) {
        for (Player player : players) {
            if (!shouldCreate(player)) {
                return;
            }

            stopTalking();
            super.show(player);
            onSpawn(player);
        }
    }

    @Override
    public void showAll() {
        Bukkit.getOnlinePlayers().forEach(this::show);
    }

    public boolean shouldCreate(Player player) {
        return true;
    }

    protected void init() {
        final String displayName = Color.SUCCESS + name;

        if (name != null) {
            addTextAboveHead(displayName);
        }

        setLookAtCloseDist(10);
        setChatPrefix(displayName);

        onPrepare();
    }
}
