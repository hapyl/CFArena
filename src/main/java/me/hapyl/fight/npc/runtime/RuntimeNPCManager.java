package me.hapyl.fight.npc.runtime;

import com.google.common.collect.Maps;
import me.hapyl.fight.Main;
import me.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import me.hapyl.spigotutils.module.util.DependencyInjector;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class RuntimeNPCManager extends DependencyInjector<Main> {

    private final Map<Player, HumanNPC> playerNPCs;

    public RuntimeNPCManager(Main plugin) {
        super(plugin);

        playerNPCs = Maps.newHashMap();
    }

    @Nonnull
    public HumanNPC createNpc(@Nonnull Player player, @Nonnull Location location) {
        final HumanNPC npc = new HumanNPC(location, "", player.getName());
        final HumanNPC previousNpc = playerNPCs.put(player, npc);

        if (previousNpc != null) {
            previousNpc.remove();
        }

        return npc;
    }

    @Nullable
    public HumanNPC getNpc(@Nonnull Player player) {
        return playerNPCs.get(player);
    }

    public void removeNpc(@Nonnull Player player, @Nonnull HumanNPC npc) {
        npc.remove();
        playerNPCs.remove(player, npc);
    }
}
