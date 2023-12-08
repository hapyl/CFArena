package me.hapyl.fight.game.talents.archive.tamer;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.InputTalent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class TamingTheTime extends InputTalent implements TamerTalent {

    @DisplayField private final double attackSpeedIncrease = 1.0d;
    @DisplayField private final double speedIncrease = 0.1d;

    public TamingTheTime() {
        super("Taming the Time");

        setDescription("""
                Equip concentrated time.
                """);

        leftData.setAction("Impair Enemies");
        leftData.setDescription("""
                Hinder all enemies by &eimpairing&7 their movement, decreasing their %s and %s.
                """, AttributeType.SPEED, AttributeType.ATTACK_SPEED);
        leftData.setType(Type.IMPAIR);
        leftData.setDurationSec(3);
        leftData.setCooldownSec(30);

        rightData.setAction("Accelerate");
        rightData.setDescription("""
                Enhance yourself by increasing your %s and %s.
                """, AttributeType.SPEED, AttributeType.ATTACK_SPEED);
        rightData.setType(Type.ENHANCE);
        rightData.copyDurationAndCooldownFrom(leftData);

        setItem(Material.CLOCK);
    }

    @Nonnull
    @Override
    public Response onLeftClick(@Nonnull GamePlayer player) {
        final int duration = getDuration(player);

        Collect.enemyPlayers(player).forEach(enemy -> {
            Temper.TAMING_THE_TIME.newInstance()
                    .decrease(AttributeType.ATTACK_SPEED, attackSpeedIncrease)
                    .decrease(AttributeType.SPEED, speedIncrease)
                    .temper(enemy, duration);
        });

        return Response.OK;
    }

    @Nonnull
    @Override
    public Response onRightClick(@Nonnull GamePlayer player) {
        final int duration = getDuration(player);

        Temper.TAMING_THE_TIME.newInstance()
                .increase(AttributeType.ATTACK_SPEED, attackSpeedIncrease)
                .increase(AttributeType.SPEED, speedIncrease)
                .temper(player, duration);

        return Response.OK;
    }
}
