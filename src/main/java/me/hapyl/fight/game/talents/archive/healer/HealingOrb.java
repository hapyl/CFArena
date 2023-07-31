package me.hapyl.fight.game.talents.archive.healer;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class HealingOrb extends InputTalent {
    public HealingOrb() {
        super("Healing Aura");

        setItem(Material.NETHER_WART);

        leftData.setAction("heal teammate").setCooldownSec(10);
        rightData.setAction("heal yourself").setCooldownSec(15);
    }

    @Nonnull
    @Override
    public Response onLeftClick(Player player) {
        final GameEntity target = Collect.targetEntity(player, 20.0d, 0.8d, null);

        if (target == null) {
            return Response.error("No valid target!");
        }

        Chat.sendMessage(player, "&aHealed %s!", target.getName());

        return Response.OK;
    }

    @Nonnull
    @Override
    public Response onRightClick(Player player) {
        Chat.sendMessage(player, "&aHealing self");

        return Response.OK;
    }

}
