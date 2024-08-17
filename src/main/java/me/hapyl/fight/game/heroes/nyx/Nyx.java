package me.hapyl.fight.game.heroes.nyx;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.event.custom.AttributeTemperEvent;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.ChargeType;
import me.hapyl.fight.game.talents.OverchargeUltimateTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.nyx.NyxPassive;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.task.player.PlayerTickingGameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Nyx extends Hero implements Listener, PlayerDataHandler<NyxData>, UIComponent {

    private final PlayerDataMap<NyxData> nyxDataMap = PlayerMap.newDataMap(NyxData::new);

    public Nyx(@Nonnull DatabaseKey key) {
        super(key, "Nyx");

        setArchetypes(Archetype.SUPPORT, Archetype.HEXBANE, Archetype.DEFENSE, Archetype.POWERFUL_ULTIMATE);
        setAffiliation(Affiliation.THE_WITHERS);
        setGender(Gender.FEMALE);

        final Equipment equipment = getEquipment();

        setItem("757240b2e096de5d541b860a06fa29809e08d5952bcf4bb38e19ca12aac09ef2");

        setDescription("""
                &8&o;;Chaos... brings victory...
                """);

        setUltimate(new NyxUltimate());
    }

    @EventHandler()
    public void handleAttributeChange(AttributeTemperEvent ev) {
        final LivingGameEntity entity = ev.getEntity();
        final LivingGameEntity applier = ev.getApplier();

        if (!(applier instanceof GamePlayer playerApplier) || ev.isBuff()) {
            return;
        }

        final GamePlayer nyx = getNyx(playerApplier);

        // No nyx on the team
        if (nyx == null) {
            return;
        }

        getPassiveTalent().execute(nyx, playerApplier, entity);

        // Decrease stack
        getPlayerData(nyx).decrementChaosStacks();
    }

    @Nonnull
    @Override
    public PlayerDataMap<NyxData> getDataMap() {
        return nyxDataMap;
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.WITHER_ROSE_PATH.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.CHAOS_GROUND.getTalent();
    }

    @Override
    public NyxPassive getPassiveTalent() {
        return (NyxPassive) Talents.NYX_PASSIVE.getTalent();
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final NyxData data = getPlayerData(player);
        final int chaosStacks = data.getChaosStacks();

        return Named.THE_CHAOS.prefix(chaosStacks);
    }

    @Nullable
    private GamePlayer getNyx(@Nonnull GamePlayer player) {
        if (validateNyx(player)) {
            return player;
        }

        return player.getTeam().getPlayers()
                .stream()
                .filter(this::validateNyx)
                .findFirst()
                .orElse(null);
    }

    private boolean validateNyx(GamePlayer player) {
        final NyxPassive passive = getPassiveTalent();

        return validatePlayer(player)
                && getPlayerData(player).getChaosStacks() > 0
                && !passive.hasCd(player);
    }

    // can't use eterna tuple because it's a record, can't override toString
    private static class NyxTuple<V> {

        public final V a;
        public final V b;

        NyxTuple(@Nonnull V a, @Nonnull V b) {
            this.a = a;
            this.b = b;
        }

        @Nonnull
        public String getString(@Nonnull V v) {
            return String.valueOf(v);
        }

        @Override
        public final String toString() {
            return getString(a) + "/" + getString(b);
        }

        @Nonnull
        public final V value(@Nonnull ChargeType type) {
            return type.value(a, b);
        }
    }

    private class NyxUltimate extends OverchargeUltimateTalent {

        @DisplayField private final NyxTuple<Double> range = new NyxTuple<>(5.0d, 8.5d);
        @DisplayField private final NyxTuple<Integer> duration = new NyxTuple<>(60, 100) {
            @Nonnull
            @Override
            public String getString(@Nonnull Integer integer) {
                return Tick.round(integer, "s");
            }
        };

        @DisplayField private final int hitDelay = 3;

        public NyxUltimate() {
            super("Surge", 60, 100);

            setDescription("""
                    Summon a void portal in front of you, after a short casting time
                    """);

            setOverchargeDescription("""
                    Increases the damage by &a+69420%%&7!
                    """);

            setCastDuration(30);
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player, @Nonnull ChargeType type) {
            final Location location = player.getLocation();
            final Vector vector = player.getDirection().setY(0.0d).multiply(2);

            location.add(vector);

            final double distance = this.range.value(type);
            final int duration = this.duration.value(type);

            final TextDisplay voidEntity = Entities.TEXT_DISPLAY.spawn(location, self -> {
                self.setShadowStrength(100.0f);
                self.setShadowRadius(0.0f);
            });

            // All a little grow animation
            new TickingGameTask() {
                private final double increase = distance / getCastDuration();

                @Override
                public void run(int tick) {
                    if (tick > getCastDuration()) {
                        cancel();
                        return;
                    }

                    // Set new strength
                    voidEntity.setShadowRadius((float) (tick * increase));
                }
            }.runTaskTimer(0, 1);

            return new UltimateResponse() {
                @Override
                public void onCastFinished(@Nonnull GamePlayer player) {
                    // Execute
                    new PlayerTickingGameTask(player) {

                        @Override
                        public void onTaskStop() {
                            voidEntity.remove();
                        }

                        @Override
                        public void run(int tick) {

                        }
                    }.runTaskTimer(0, 1);
                }
            };
        }
    }
}
