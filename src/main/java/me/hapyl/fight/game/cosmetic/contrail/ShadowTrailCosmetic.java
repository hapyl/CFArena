package me.hapyl.fight.game.cosmetic.contrail;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.npc.Npc;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Rarity;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Queue;

public class ShadowTrailCosmetic extends ContrailCosmetic implements Disabled {

    private final Map<Player, Queue<Npc>> humans;

    public ShadowTrailCosmetic(@Nonnull Key key) {
        super(
                key,
                "Shadow Trail",
                "There is something trailing behind you!",
                Rarity.MYTHIC,
                ContrailType.of("special", "It will summon shadow clones as you move!")
        );

        setIcon(Material.PLAYER_HEAD);

        humans = Maps.newConcurrentMap();
    }

    @Override
    public void onMove(@Nonnull Display display, int tick) {
        // TODO @Dec 15, 2025 (xanyjl) -> Do something idk
    }

}
