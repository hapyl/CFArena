package me.hapyl.fight.game.talents.healer;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.util.Collect;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class HealingOrb extends InputTalent {
    public HealingOrb(@Nonnull Key key) {
        super(key, "Healing Aura");

        setMaterial(Material.NETHER_WART);

        leftData.setAction("heal teammate").setCooldownSec(10);
        rightData.setAction("heal yourself").setCooldownSec(15);
    }

    @Nonnull
    @Override
    public Response onLeftClick(@Nonnull GamePlayer player) {
        final LivingGameEntity target = Collect.targetEntityDot(player, 20.0d, 0.8d, predicate -> true);

        if (target == null) {
            return Response.error("No valid target!");
        }

        player.sendMessage("&aHealed %s!".formatted(target.getName()));

        return Response.OK;
    }

    @Nonnull
    @Override
    public Response onRightClick(@Nonnull GamePlayer player) {
        player.sendMessage("&aHealing self");

        return Response.OK;
    }

}
