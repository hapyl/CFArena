package me.hapyl.fight.game.talents.archive.mage;

import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ArcaneMute extends Talent {
    public ArcaneMute() {
        super(
                "Arcane Mute",
                "Use on a targeted player to silence them, preventing them from using talents for {duration}.",
                Material.FEATHER
        );

        setDurationSec(4);
        setCooldownSec(35);
    }

    @Override
    public Response execute(Player player) {
        final Entity target = targetEntityChatGPT(player, 20);

        if (target == null) {
            return Response.error("No valid target!");
        }
        else if (!player.hasLineOfSight(target)) {
            return Response.error("No light of sight!");
        }

        if (target instanceof Player targetPlayer) {
            GamePlayer.getPlayer(targetPlayer).addEffect(GameEffectType.ARCANE_MUTE, getDuration());

            Chat.sendMessage(targetPlayer, "&e&l☠ &cYou have been cursed by Arcane Mute! &8(%s)", player.getName());
            Chat.sendMessage(player, "&aArcane Mute cursed %s.", targetPlayer.getName());
        }
        else {
            return Response.error("Non player target?");
        }

        return Response.OK;
    }

    public Entity targetEntityChatGPT(Player player, int range) {
        final GameEntity gameEntity = Collect.targetEntity(
                player,
                range,
                0.95,
                entity -> entity instanceof Player && entity.hasLineOfSight(player)
        );

        return gameEntity == null ? null : gameEntity.getEntity();
    }

}
