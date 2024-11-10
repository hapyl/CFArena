package me.hapyl.fight.build;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.fight.Notifier;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

/// fixme: -> REMOVEME
public class ReplacerData {

    private final UUID uuid;
    private Material tool;

    private BlockData data;

    public ReplacerData(UUID uuid) {
        this.uuid = uuid;
        this.tool = Material.STICK;
        this.data = null;
    }

    @Nonnull
    public Material getTool() {
        return tool;
    }

    public void setTool(@Nonnull Material tool) {
        this.tool = tool;
    }

    @Nullable
    public BlockData getBlockData() {
        return data;
    }

    public void setBlockData(@Nullable BlockData data) {
        this.data = data;

        final Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            Notifier.success(player, "Changed material to %s!".formatted(data == null ? "None!" : Chat.capitalize(data.getMaterial())));
        }
    }

}
