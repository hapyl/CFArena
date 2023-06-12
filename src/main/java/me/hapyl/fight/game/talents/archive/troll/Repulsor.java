package me.hapyl.fight.game.talents.archive.troll;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Repulsor extends Talent {

    @DisplayField(suffix = "blocks") private final double radius = 10.0d;

    public Repulsor() {
        super("Repulsor", "Propels all nearby opponents high up into the sky!", Type.COMBAT);

        setItem(Material.IRON_BOOTS);
        setCooldown(200);
    }

    @Override
    public Response execute(Player player) {
        PlayerLib.playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.8f);

        Collect.nearbyPlayers(player.getLocation(), radius).forEach(victim -> {
            if (victim == player || GameTeam.isTeammate(player, victim)) {
                return;
            }

            Chat.sendMessage(victim, "&aWhoosh!");
            victim.setVelocity(new Vector(0.0d, 1.0d, 0.0d));
        });

        return Response.OK;
    }
}
