package me.hapyl.fight.game.talents.storage.troll;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Repulsor extends Talent {
    public Repulsor() {
        super("Repulsor", "Propels all nearby opponents high up into the sky!", Type.COMBAT);
        this.setItem(Material.IRON_BOOTS);
        this.setCd(200);
    }

    @Override
    public Response execute(Player player) {
        PlayerLib.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.8f);

        Utils.getPlayersInRange(player.getLocation(), 10).forEach(inRange -> {
            if (inRange == player) {
                return;
            }

            Chat.sendMessage(inRange, "&aWhoosh!");
            inRange.setVelocity(new Vector(0.0d, 1.0d, 0.0d));
        });

        return Response.OK;
    }
}
