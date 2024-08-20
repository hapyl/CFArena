package me.hapyl.fight.game.talents.ender;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.PassiveTalent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class EnderPassive extends PassiveTalent {

    @DisplayField private final double healing = 4.0d;
    @DisplayField private final double damage = 1.0d;
    @DisplayField(percentage = true) private final double attackBoost = 0.2d;
    @DisplayField private final int attackBoostDuration = 30;

    public EnderPassive(@Nonnull DatabaseKey key) {
        super(key, "Ender Skin");

        setDescription("""
                With great power, comes great... strength!
                Your skin is too weak for the &bwater&7, though on &dteleport&7:
                
                &8- &7Heal for &c&l{healing} &c❤&7.
                &8- &7Deal &c&l{damage}&7 damage in small &eAoE&7.
                &8- &7Gain &c{attackBoost} %s boost for a short duration.
                """.formatted(AttributeType.ATTACK)
        );

        setItem(Material.ENDER_EYE);
    }

    // Handles the passive ability
    public void handleTeleport(@Nonnull GamePlayer gamePlayer) {
        gamePlayer.heal(healing);
        gamePlayer.getAttributes().increaseTemporary(Temper.ENDER_TELEPORT, AttributeType.ATTACK, attackBoost, attackBoostDuration);

        Collect.nearbyEntities(gamePlayer.getLocation(), 1.0d).forEach(entity -> {
            if (gamePlayer.isSelfOrTeammate(entity)) {
                return;
            }

            entity.damage(damage, gamePlayer, EnumDamageCause.ENDER_TELEPORT);
        });
    }
}
