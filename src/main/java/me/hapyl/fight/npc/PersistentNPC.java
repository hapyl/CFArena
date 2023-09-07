package me.hapyl.fight.npc;

import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.entity.Event;
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

    public PersistentNPC(@Nonnull Location location, @Nullable String name) {
        super(location, name == null ? null : Color.BUTTON.bold() + "CLICK", "");

        final String displayName = Color.SUCCESS + name;

        if (name != null) {
            addTextAboveHead(displayName);
        }

        setLookAtCloseDist(10);
        setChatPrefix(displayName);

        onPrepare();
    }

    public PersistentNPC(double x, double y, double z, @Nullable String name) {
        this(x, y, z, 0.0f, 0.0f, name);
    }

    public PersistentNPC(double x, double y, double z, float yaw, float pitch, @Nullable String name) {
        this(BukkitUtils.defLocation(x, y, z, yaw, pitch), name);
    }

    @Deprecated
    private PersistentNPC(@Nonnull Location location, @Nullable String npcName, @Nullable String skinOwner) {
        super(location, npcName, skinOwner);
    }

    @Deprecated
    private PersistentNPC(Location location, String npcName, String skinOwner, Function<UUID, String> hexNameFn) {
        super(location, npcName, skinOwner, hexNameFn);
    }

    public void create(Player player) {
        if (!shouldCreate(player)) {
            return;
        }

        super.show(player);
        onSpawn(player);
    }

    @Event
    public void onSpawn(Player player) {
    }

    @Event
    public void onPrepare() {
    }

    @Override
    @Deprecated(forRemoval = true)
    public final void show(@Nonnull Player... players) {
        throw new IllegalStateException("use PersistentNPC#create()");
    }

    @Override
    public void showAll() {
        Bukkit.getOnlinePlayers().forEach(this::create);
    }

    public boolean shouldCreate(Player player) {
        return true;
    }
}
