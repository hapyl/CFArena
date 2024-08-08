package me.hapyl.fight.game.talents.aurora;

import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.eterna.module.math.Tick;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class EtherealArrow extends AuroraArrowTalent {

    // Tempers are enums, have to static them
    private final Temper[] tempers = {
            Temper.AURORA_BUFF_1,
            Temper.AURORA_BUFF_2,
            Temper.AURORA_BUFF_3,
    };

    @DisplayField(percentage = true) private final double critRateBoost = 0.1d;
    @DisplayField(percentage = true) private final double critDamageBoost = 0.2d;
    @DisplayField private final short maxStacks = (short) tempers.length;
    @DisplayField private final int buffDuration = Tick.fromSecond(6);

    public EtherealArrow() {
        super("Ethereal Arrows", ChatColor.AQUA, 3);

        setDescription("""
                Equip {name} that &bapplies&7 a stack of %s to hit &ateammates.
                                
                &6%s
                &8▷&7 Increases %s by {critRateBoost}.
                &8▷&7 Increases %s by {critDamageBoost}.
                                
                &8;;Each stack has independent duration.
                """.formatted(Named.ETHEREAL_SPIRIT, Named.ETHEREAL_SPIRIT.getName(), AttributeType.CRIT_CHANCE, AttributeType.CRIT_DAMAGE));

        setItem(Material.PRISMARINE_CRYSTALS);
        setCooldownSec(8);
    }

    @Override
    public void onHit(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, @Nonnull DamageInstance instance) {
        if (!player.isTeammate(entity)) {
            return;
        }

        final EntityAttributes attributes = entity.getAttributes();

        for (Temper temper : tempers) {
            if (attributes.hasTemper(temper)) {
                continue;
            }

            attributes.increaseTemporary(temper, AttributeType.CRIT_CHANCE, critRateBoost, buffDuration);
            attributes.increaseTemporary(temper, AttributeType.CRIT_DAMAGE, critDamageBoost, buffDuration);

            // Only display the first buff?
            if (temper == Temper.AURORA_BUFF_1) {
                entity.spawnBuffDisplay(Named.ETHEREAL_SPIRIT.toString(), 15);
            }

            Debug.info("Buffed %s".formatted(entity.getName()));

            return;
        }

        player.sendMessage("&c%s's buff is at full stacks!".formatted(entity.getName()));
    }

}
