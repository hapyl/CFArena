package me.hapyl.fight.game.talents.archive.healer;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Material;

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
    public Response onLeftClick(@Nonnull GamePlayer player) {
        final LivingGameEntity target = Collect.targetEntity(player, 20.0d, 0.8d, null);

        if (target == null) {
            return Response.error("No valid target!");
        }

        player.sendMessage("&aHealed %s!", target.getName());

        return Response.OK;
    }

    @Nonnull
    @Override
    public Response onRightClick(@Nonnull GamePlayer player) {
        player.sendMessage("&aHealing self");

        return Response.OK;
    }

}
