package me.hapyl.fight.game.talents.shaman;


import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TotemImprisonment extends Talent {

    @DisplayField protected final short height = 3;
    @DisplayField protected final int buildingSpeed = 2;

    public TotemImprisonment(@Nonnull Key key) {
        super(key, "Imprisonment");

        setDescription("""
                Imprison the &etarget&7 &cenemy&7 in a stone cage, preventing them from moving.
                """
        );

        setMaterial(Material.COBBLESTONE_WALL);
        setType(TalentType.IMPAIR);
        setCooldownSec(16);
        setDurationSec(6);
    }

    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final LivingGameEntity target = Collect.targetEntityDot(player, 15, 0.7d, entity -> {
            return !player.isSelfOrTeammate(entity) && player.hasLineOfSight(entity);
        });

        if (target == null) {
            return Response.error("No valid target!");
        }

        if (target.hasEffectResistanceAndNotify()) {
            player.sendMessage(
                    AttributeType.EFFECT_RESISTANCE.getCharacter() + " %s has resisted your %s!".formatted(target.getName(), getName()));
            return Response.OK;
        }

        new TotemPrison(this, target, player);
        return Response.OK;
    }
}
