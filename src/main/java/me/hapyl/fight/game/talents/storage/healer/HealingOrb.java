package me.hapyl.fight.game.talents.storage.healer;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class HealingOrb extends InputTalent {
    public HealingOrb() {
        super("Healing Aura");

        setItem(Material.NETHER_WART);

        setLeftClick("heal teammate");
        setRightClick("heal yourself");

        setCdLeftSec(10);
        setCdRightSec(15);
    }

    @Nonnull
    @Override
    public Response onLeftClick(Player player) {
        final LivingEntity target = Utils.getTargetEntity(player, 20.0d, 0.8d, null);

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
