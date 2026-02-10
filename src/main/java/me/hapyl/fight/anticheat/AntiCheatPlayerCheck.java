package me.hapyl.fight.anticheat;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.Map;

public class AntiCheatPlayerCheck {

    private final Player player;
    private final Map<AntiCheatCheck, AntiCheatCheckData> data;

    public AntiCheatPlayerCheck(Player player) {
        this.player = player;
        this.data = Maps.newHashMap();
    }

    public void fail(AntiCheatCheck check) {
        this.data.computeIfAbsent(check, fn -> new AntiCheatCheckData(player, check)).fail();
    }

}
