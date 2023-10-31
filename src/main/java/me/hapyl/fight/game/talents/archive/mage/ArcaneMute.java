package me.hapyl.fight.game.talents.archive.mage;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.Collect;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    public Response execute(@Nonnull GamePlayer player) {
        final LivingGameEntity target = getTargetEntity(player, 20);

        if (target == null) {
            return Response.error("No valid target!");
        }
        else if (!player.hasLineOfSight(target)) {
            return Response.error("No light of sight!");
        }

        target.addEffect(GameEffectType.ARCANE_MUTE, getDuration());

        target.sendMessage("&e&l☠ &cYou have been cursed by Arcane Mute! &8(%s)", player.getName());
        player.sendMessage("&aArcane Mute cursed %s.", target.getName());

        return Response.OK;
    }

    @Nullable
    public LivingGameEntity getTargetEntity(GamePlayer player, int range) {
        return Collect.targetEntity(
                player,
                range,
                0.95,
                entity -> entity.is(Player.class) && entity.hasLineOfSight(player)
        );
    }

}
