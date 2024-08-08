package me.hapyl.fight.game.heroes.nyx;

import me.hapyl.fight.event.custom.AttributeTemperEvent;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.nyx.NyxPassive;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Nyx extends Hero implements Listener, PlayerDataHandler<NyxData>, UIComponent, Disabled {

    private final PlayerDataMap<NyxData> nyxDataMap = PlayerMap.newDataMap(NyxData::new);

    public Nyx(@Nonnull Heroes handle) {
        super(handle, "Nyx");

        setArchetypes(Archetype.SUPPORT);
        setAffiliation(Affiliation.THE_WITHERS);
        setGender(Gender.FEMALE);

        setDescription("""
                &8&o;;Chaos... brings victory...
                """);
    }

    @EventHandler()
    public void handleAttributeChange(AttributeTemperEvent ev) {
        final LivingGameEntity entity = ev.getEntity();
        final Temper temper = ev.getTemper();
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
    }

    @Nullable
    private GamePlayer getNyx(@Nonnull GamePlayer player) {
        if (validatePlayer(player)) {
            return player;
        }

        return player.getTeam().getPlayers()
                .stream()
                .filter(this::validateNyx)
                .findFirst()
                .orElse(null);
    }

    @Nonnull
    @Override
    public PlayerDataMap<NyxData> getDataMap() {
        return nyxDataMap;
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.WITHER_IMITATION.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return null;
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

        return "&7&l\uD83E\uDEA8 &7%s".formatted(chaosStacks);
    }

    private boolean validateNyx(GamePlayer player) {
        return validatePlayer(player) && getPlayerData(player).getChaosStacks() > 0;
    }

}
