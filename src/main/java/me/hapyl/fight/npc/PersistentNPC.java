package me.hapyl.fight.npc;

import me.hapyl.eterna.module.annotate.EventLike;
import me.hapyl.eterna.module.npc.ClickType;
import me.hapyl.eterna.module.npc.Npc;
import me.hapyl.eterna.module.npc.NpcProperties;
import me.hapyl.eterna.module.npc.appearance.AppearanceBuilder;
import me.hapyl.eterna.module.npc.tag.TagLayout;
import me.hapyl.eterna.module.npc.tag.TagPart;
import me.hapyl.eterna.module.reflect.Skin;
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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

@AutoRegisteredListener
public class PersistentNPC extends Npc implements Ticking, Keyed, Delegate {
    
    @Nonnull protected final Key key;
    @Nonnull protected PersistentNPCSound sound;
    
    protected int tick;
    
    public PersistentNPC(@Nonnull Key key, double x, double y, double z, @Nullable Component name, @Nonnull Skin skin) {
        this(key, x, y, z, 0.0f, 0.0f, name, skin);
    }
    
    public PersistentNPC(@Nonnull Key key, double x, double y, double z, float yaw, float pitch, @Nullable Component name, @Nonnull Skin skin) {
        this(key, BukkitUtils.defLocation(x, y, z, yaw, pitch), name, skin);
    }
    
    protected PersistentNPC(@Nonnull Key key, @Nonnull Location location, @Nullable Component name, @Nonnull Skin skin) {
        super(location, name != null ? name : Component.empty(), AppearanceBuilder.ofMannequin(skin));
        
        this.key = key;
        this.sound = new PersistentNPCSound();
        
        final NpcProperties properties = getProperties();
        properties.setLookAtClosePlayerDistance(8);
        
        setTagLayout(
                name != null
                ? new TagLayout(TagPart.name(), TagPart.literal(Component.text("CLICK", Color.GOLD, TextDecoration.BOLD)))
                : new TagLayout()
        );
        
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
    public void sendMessage(@Nonnull Player player, @Nonnull Component message) {
        super.sendMessage(player, message);
        this.sound.play(player);
    }
    
    @EventLike
    public void onSpawn(@Nonnull Player player) {
        super.onSpawn(player);
    }
    
    @Override
    public void onClick(@Nonnull Player player, @Nonnull ClickType clickType) {
        super.onClick(player, clickType);
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
        super.tick();
        ++tick;
    }
    
    @Nonnull
    protected Component blink(@Nonnull String message) {
        return Component.text(message, tick % 2 <= 0 ? Color.GOLD : Color.YELLOW, TextDecoration.BOLD);
    }
}
