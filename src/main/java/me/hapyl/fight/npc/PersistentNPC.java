package me.hapyl.fight.npc;

import me.hapyl.fight.dialog.Placeholder;
import me.hapyl.fight.game.Event;
import me.hapyl.fight.game.color.Color;
import me.hapyl.eterna.module.annotate.Super;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.reflect.npc.ClickType;
import me.hapyl.eterna.module.reflect.npc.HumanNPC;
import me.hapyl.eterna.module.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Function;

public class PersistentNPC extends HumanNPC {

    private final String name;

    @Nonnull
    private NPCSound sound;

    public PersistentNPC(@Nonnull Location location, @Nullable String name) {
        this(location, name, "", uuid -> ("§8[NPC] " + uuid.toString().replace("-", "")).substring(0, 16));
    }

    public PersistentNPC(double x, double y, double z, @Nullable String name) {
        this(x, y, z, 0.0f, 0.0f, name);
    }

    public PersistentNPC(double x, double y, double z, float yaw, float pitch, @Nullable String name) {
        this(BukkitUtils.defLocation(x, y, z, yaw, pitch), name);
    }

    @Super
    protected PersistentNPC(Location location, String name, String skinOwner, Function<UUID, String> hexNameFn) {
        super(location, name == null ? null : Color.BUTTON.bold() + "CLICK", skinOwner, hexNameFn);

        this.name = name;
        this.sound = new NPCSound();

        init();
    }

    public void sendMessage(@Nonnull Player player, @Nonnull String message, @Nonnull Object... format) {
        Chat.sendMessage(player, "&e[NPC] %s&f: %s".formatted(getName(), Placeholder.formatAll(message.formatted(format), player, this)));
        sound.play(player);
    }

    @Override
    public final void onClick(@Nonnull Player player, @Nonnull ClickType type) {
        onClick(player);
    }

    public PersistentNPC setInteractionDelay(int interactionDelayTick) {
        super.setInteractionDelayTick(interactionDelayTick);
        return this;
    }

    @Event
    public void onClick(@Nonnull Player player) {
    }

    public void setSound(@Nonnull NPCSound sound) {
        this.sound = sound;
    }

    @Deprecated
    @Override
    public void sendNpcMessage(Player player, String msg) {
        sendMessage(player, msg);
    }

    @Event
    public void onSpawn(Player player) {
    }

    @Event
    public void onPrepare() {
    }

    @Override
    public final void show(@Nonnull Player player) {
        if (!shouldCreate(player)) {
            return;
        }

        stopTalking();
        super.show(player);
        onSpawn(player);
    }

    @Override
    public void showAll() {
        Bukkit.getOnlinePlayers().forEach(this::show);
    }

    public boolean shouldCreate(Player player) {
        return true;
    }

    @Event
    public void onCreate(@Nonnull Player player) {
    }

    @Override
    public String getName() {
        return Color.SUCCESS + name;
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
