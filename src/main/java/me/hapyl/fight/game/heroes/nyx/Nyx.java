package me.hapyl.fight.game.heroes.nyx;

import me.hapyl.fight.event.custom.TalentUseEvent;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.nyx.NyxPassive;
import me.hapyl.fight.game.talents.techie.Talent;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class Nyx extends Hero implements Listener, PlayerDataHandler<NyxData>, UIComponent, Disabled {

    private final PlayerDataMap<NyxData> nyxDataMap = PlayerMap.newDataMap(NyxData::new);

    public Nyx(@Nonnull Heroes handle) {
        super(handle, "Nyx");

        setArchetype(Archetype.SUPPORT);
        setAffiliation(Affiliation.THE_WITHERS);
        setGender(Gender.FEMALE);

        setDescription("""
                &8&o;;Chaos... brings victory...
                """);

    }

    @EventHandler()
    public void handleTalentUse(TalentUseEvent ev) {
        final GamePlayer player = ev.getPlayer();
        final Talent talent = ev.getTalent();

        final NyxPassive passiveTalent = getPassiveTalent();

        if (!passiveTalent.isValidTalentType(talent.getType())) {
            return;
        }

        GamePlayer nyx;

        if (validateNyx(player)) {
            nyx = player;
        }
        else {
            nyx = player.getTeam().getPlayers()
                    .stream()
                    .filter(this::validateNyx)
                    .findFirst()
                    .orElse(null);
        }

        // No Nyx in the team
        if (nyx == null) {
            return;
        }

        // Check for enemies
        final LivingGameEntity target = Collect.nearestEntityPrioritizePlayers(player.getLocation(), 100, player::isEnemy);

        if (target == null) {
            return;
        }

        final NyxData nyxData = getPlayerData(nyx);

        nyxData.decrementChaosStacks();
        passiveTalent.execute(nyx, player, target);
    }

    @Nonnull
    @Override
    public PlayerDataMap<NyxData> getDataMap() {
        return nyxDataMap;
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.TRIPLE_SHOT.getTalent();
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
