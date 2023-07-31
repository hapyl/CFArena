package me.hapyl.fight.game.talents.archive.witcher;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Akciy extends Talent {

    public Akciy() {
        super("Axii", """
                Stuns your target opponent for {duration} or until they get hit.
                                
                Stunned opponent is immovable and cannot use their abilities.
                """);

        setDuration(100);
        setCooldownSec(40);
        setItem(Material.SLIME_BALL);
    }

    @Override
    public Response execute(Player player) {
        final Player target = Collect.targetPlayer(player, 50.0d);

        if (target == null) {
            return Response.error("No valid target!");
        }

        final GamePlayer targetGamePlayer = CF.getOrCreatePlayer(target);

        targetGamePlayer.addEffect(GameEffectType.STUN, getDuration());

        targetGamePlayer.sendMessage("&c%s stunned you!", player.getName());
        Chat.sendMessage(player, "&aStunned %s!", target.getName());

        return Response.OK;
    }
}
