package me.hapyl.fight.npc;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.hologram.StringArray;
import me.hapyl.eterna.module.reflect.npc.ClickType;
import me.hapyl.eterna.module.reflect.npc.HumanNPC;
import me.hapyl.eterna.module.reflect.npc.NPCFormat;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.registry.Keyed;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.CF;
import me.hapyl.fight.annotate.AutoRegisteredListener;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.task.DelegateTask;
import me.hapyl.fight.game.task.PersistentTask;
import me.hapyl.fight.util.Delegate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.UUID;
import java.util.function.Function;

@AutoRegisteredListener
public class PersistentNPC extends HumanNPC implements Ticking, Keyed, Delegate {

    private static final NPCFormat FORMAT = new NPCFormat(
            new NPCFormat.TextFormat("&e[NPC] %s{name}: &f{text}".formatted(Color.SUCCESS)),
            new NPCFormat.NameFormat("%s{name}".formatted(Color.SUCCESS))
    );

    @Nonnull protected final Key key;
    @Nonnull protected PersistentNPCSound sound;

    protected int tick;

    public PersistentNPC(@Nonnull Key key, double x, double y, double z, @Nullable String name) {
        this(key, x, y, z, 0.0f, 0.0f, name);
    }

    public PersistentNPC(@Nonnull Key key, double x, double y, double z, float yaw, float pitch, @Nullable String name) {
        this(key, BukkitUtils.defLocation(x, y, z, yaw, pitch), name, uuid -> ("§8" + uuid.toString()).substring(0, 16));
    }

    protected PersistentNPC(@Nonnull Key key, @Nonnull Location location, @Nullable String name, @Nonnull Function<UUID, String> hexName) {
        super(location, name, null, hexName);

        this.key = key;
        this.sound = new PersistentNPCSound();

        setFormat(FORMAT);
        setLookAtCloseDist(8);

        // Register listener if needed
        if (this instanceof Listener listener) {
            CF.registerEvents(listener);
        }

        // Delegate task
        DelegateTask.delegate(
                this, new PersistentTask() {
                    @Override
                    public void run() {
                        PersistentNPC.this.tick();
                    }
                }.runTaskTimer(0, 1)
        );
    }

    @Override
    public final void sendNpcMessage(@Nonnull Player player, @Nonnull String message) {
        super.sendNpcMessage(player, message);

        sound.play(player);
    }

    @EventLike
    public void onSpawn(@Nonnull Player player) {
    }

    @EventLike
    public void onClick(@Nonnull Player player, @Nonnull ClickType clickType) {
    }

    @Override
    public final void show(@Nonnull Player player) {
        if (!shouldCreate(player)) {
            return;
        }

        super.show(player);
        onSpawn(player);
    }
    
    @Override
    public void showAll() {
        Bukkit.getOnlinePlayers().forEach(this::show);
    }

    public boolean shouldCreate(@Nonnull Player player) {
        return true;
    }

    @Nonnull
    @Override
    public final Key getKey() {
        return key;
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void tick() {
        ++tick;

        // Update the name once a second
        if (tick % 20 == 0) {
            // This will update NPC name
            setBelowHead(player -> {
                if (!hasName()) {
                    return StringArray.empty();
                }

                return StringArray.of(Color.BUTTON.bold() + "CLICK");
            });
        }
    }

    @Nonnull
    protected StringArray blink(@Nonnull String message) {
        return StringArray.of(
                (tick % 2 <= 0 ? "&6&l" : "&e&l") + message
        );
    }
}
