package me.hapyl.fight.game.talents.aurora;

import me.hapyl.eterna.module.math.Tick;

import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class EtherealArrow extends AuroraArrowTalent {

    @DisplayField(percentage = true) public final double critRateBoost = 0.1d;
    @DisplayField(percentage = true) public final double critDamageBoost = 0.2d;

    @DisplayField public final short maxStacks = 3;
    @DisplayField public final int buffDuration = Tick.fromSecond(6);

    public EtherealArrow(@Nonnull Key key) {
        super(key, "Ethereal Arrows", ChatColor.AQUA, 3, 5.0d, 0.85d);

        setDescription("""
                Equip {name} that &bapplies&7 a stack of %s to hit &ateammate.
                
                &6%s
                &8▷&7 Increases %s by {critRateBoost}.
                &8▷&7 Increases %s by {critDamageBoost}.
                
                &7&o;;Ethereal arrows home towards nearby teammates.
                """.formatted(Named.ETHEREAL_SPIRIT, Named.ETHEREAL_SPIRIT.getName(), AttributeType.CRIT_CHANCE, AttributeType.CRIT_DAMAGE)
        );

        setType(TalentType.SUPPORT);
        setItem(Material.PRISMARINE_SHARD);

        setCooldownSec(15);
    }

    @Override
    public void onHit(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, @Nonnull DamageInstance instance) {
        if (!player.isTeammate(entity)) {
            return;
        }

        HeroRegistry.AURORA.getPlayerData(player).buff(entity);
    }

    public TemperInstance getBuff(int i) {
        return Temper.AURORA_BUFF.newInstance()
                .increase(AttributeType.CRIT_CHANCE, critRateBoost * i)
                .increase(AttributeType.CRIT_DAMAGE, critDamageBoost * i);
    }
}
