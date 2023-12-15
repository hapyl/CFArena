package me.hapyl.fight.game.talents.archive.knight;

import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class StoneCastle extends Talent implements Listener {

    @DisplayField(scaleFactor = 100) protected final double defenseIncrease = 1.0d;
    @DisplayField(scaleFactor = 100) protected final double kbResistanceIncrease = 0.5d;
    @DisplayField(scaleFactor = 100) protected final double ccResistanceIncrease = 0.5d;
    @DisplayField protected final double distance = 8.0d;

    private final double damageSplitSelf = 0.7d;
    private final double damageSplitOther = 1 - damageSplitSelf;

    private final PlayerMap<Castle> castleMap = PlayerMap.newConcurrentMap();
    private final TemperInstance instance = Temper.STONE_CASTLE.newInstance()
            .increase(AttributeType.DEFENSE, defenseIncrease)
            .increase(AttributeType.KNOCKBACK_RESISTANCE, kbResistanceIncrease)
            .increase(AttributeType.CROWD_CONTROL_RESISTANCE, ccResistanceIncrease);

    public StoneCastle() {
        super("Castle of Stone");

        setDescription("""
                Erect a castle of stone at your current location.
                                
                When a &ateammate&7 &b&nwithin&7 the castle takes &cdamage&7, the damage is &asplit&7 between you.
                                
                You also receive a %s, %s and %s increase.
                """, AttributeType.DEFENSE, AttributeType.KNOCKBACK_RESISTANCE, AttributeType.CROWD_CONTROL_RESISTANCE);

        addAttributeDescription("Damage Split", "%.0f%%/%.0f%%".formatted(damageSplitSelf * 100, damageSplitOther * 100));

        setType(Type.DEFENSE);
        setItem(Material.PURPUR_PILLAR);
        setDurationSec(12);
        setCooldownSec(10);
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        castleMap.removeAnd(player, Castle::remove);
    }

    @Override
    public void onStop() {
        castleMap.forEachAndClear(Castle::remove);
    }

    @EventHandler()
    public void handleDamageEvent(GameDamageEvent ev) {
        final LivingGameEntity entity = ev.getEntity();

        if (!(entity instanceof GamePlayer player)) {
            return;
        }

        final GameTeam team = player.getTeam();
        for (GamePlayer teammate : team.getPlayers()) {
            final Castle castle = castleMap.get(teammate);

            // Don't split self-damage
            if (castle == null || teammate.equals(player)) {
                continue;
            }

            if (!castle.isEntityWithin(player) || !castle.isEntityWithin(castle.getPlayer())) {
                return;
            }

            final double damage = ev.getDamage();
            final double castledDamage = damage * damageSplitOther;
            final double splitDamage = damage * damageSplitSelf;

            ev.setDamage(castledDamage);
            teammate.damageTick(splitDamage, EnumDamageCause.STONE_CASTLE, 1);
            break;
        }
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Castle oldCastle = castleMap.remove(player);

        if (oldCastle != null) {
            oldCastle.remove();
        }

        instance.temper(player, getDuration());

        castleMap.put(player, new Castle(this, player) {
            @Override
            public void onLastTick() {
                remove();
                castleMap.remove(player, this);
            }
        });

        return Response.OK;
    }
}
