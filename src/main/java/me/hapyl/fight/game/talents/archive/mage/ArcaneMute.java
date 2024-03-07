package me.hapyl.fight.game.talents.archive.mage;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ArcaneMute extends Talent {

    @DisplayField private final double maxDistance = 20;

    public ArcaneMute() {
        super(
                "Arcane Mute",
                "Use on a &etargeted&7 player to &bsilence&7 them, &cpreventing&7 them from using &atalents&7 for {duration}."
        );

        setType(Type.IMPAIR);
        setItem(Material.FEATHER);
        setDurationSec(4);
        setCooldownSec(20);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final LivingGameEntity target = getTargetEntity(player, maxDistance);

        if (target == null) {
            return Response.error("No valid target!");
        }
        else if (!player.hasLineOfSight(target)) {
            return Response.error("No light of sight!");
        }

        target.addEffect(Effects.ARCANE_MUTE, getDuration());

        target.sendMessage("&e&l☠ &cYou have been cursed by Arcane Mute! &8(%s)", player.getName());
        player.sendMessage("&aArcane Mute cursed %s.", target.getName());

        return Response.OK;
    }

    @Nullable
    public LivingGameEntity getTargetEntity(GamePlayer player, double range) {
        return Collect.targetEntityDot(
                player,
                range,
                0.95,
                entity -> entity.is(Player.class) && entity.hasLineOfSight(player)
        );
    }

}
