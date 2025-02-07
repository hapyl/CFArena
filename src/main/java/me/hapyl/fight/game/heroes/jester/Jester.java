package me.hapyl.fight.game.heroes.jester;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.event.custom.GameDeathEvent;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.PlayerDataHandler;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.jester.MusicBoxTalent;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class Jester extends Hero implements Disabled, UIComponent, PlayerDataHandler<JesterData>, Listener {

    private final PlayerDataMap<JesterData> playerData = PlayerMap.newDataMap(JesterData::new);

    /**
     * <ul>
     *     <li>Talent 1:
     *     <br>
     *     Place a box with music. When enemy nearby you can tp.
     *     Confetti fx, damage, -DEFENSE
     *
     *     <li>Talent 2:
     *     <br>
     *     Throw a cake that deals damage ( 1 ), blindness and slow.
     *
     *     <li>Passive:
     *     <br>
     *     On kill enemies do the same as the box.
     *
     *     <li>Ultimate ( silent, only teammates can see )
     *     <br>
     *     Joker * ( silent )
     *     Killer is always jester for any kills other than self or teammate
     *
     *     <li>Weapon
     *     <br>
     *     Staff, random damage ( 6-12 )
     * </ul>
     */
    public Jester(@Nonnull Key key) {
        super(key, "Jester");

        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(212, 11, 11, TrimPattern.SHAPER, TrimMaterial.GOLD);
        equipment.setLeggings(32, 17, 43, TrimPattern.SILENCE, TrimMaterial.REDSTONE);
        equipment.setBoots(32, 17, 43, TrimPattern.SNOUT, TrimMaterial.GOLD);

        setItem("fb3440ab90a690d73dfa068ebbb6c7efa88e8898e3d6460e2c0886eea658352d");

        setUltimate(new JesterUltimate());
    }

    @EventHandler
    public void handleTheJoker(GameDeathEvent ev) {
        final LivingGameEntity entity = ev.getEntity();
        final GameEntity killer = ev.getKiller();

        if (!(killer instanceof GamePlayer playerKiller)) {
            return;
        }

        // This is a confetti check
        if (validatePlayer(playerKiller)) {
            spawnConfetti(playerKiller, entity.getLocation());
        }

        // Find joker target
        final GamePlayer theJoker = findTheJoker();

        if (theJoker == null) {
            return;
        }

        // Make sure it's not the joker nor their teammate
        if (theJoker.isSelfOrTeammate(playerKiller)) {
            return;
        }

        ev.setCancelled(true);

        entity.setLastDamager(theJoker);
        entity.dieBy(EnumDamageCause.THE_JOKER);
    }

    public void spawnConfetti(@Nonnull GamePlayer player, @Nonnull Location location) {

    }

    @Nullable
    public GamePlayer findTheJoker() {
        final List<GamePlayer> potentialsJokers = Lists.newArrayList();

        for (GamePlayer player : getAlivePlayers()) {
            if (player.isUsingUltimate()) {
                potentialsJokers.add(player);
            }
        }

        return potentialsJokers.size() == 1 ? potentialsJokers.getFirst() : CollectionUtils.randomElement(potentialsJokers);
    }

    @Override
    public MusicBoxTalent getFirstTalent() {
        return TalentRegistry.MUSIC_BOX;
    }

    @Override
    public Talent getSecondTalent() {
        return TalentRegistry.TAKE_A_CAKE_TO_THE_FACE;
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final int durationLeft = (int) getUltimateDurationLeft(player);

        if (durationLeft > 0) {
            return "&d%s &f%s".formatted(Named.THE_JOKER.getCharacter(), CFUtils.formatTick(durationLeft));
        }

        return "";
    }

    @Nonnull
    @Override
    public PlayerDataMap<JesterData> getDataMap() {
        return playerData;
    }

    public class JesterUltimate extends UltimateTalent {

        JesterUltimate() {
            super(Jester.this, "The Joker", 60);

            setDescription("""
                    Unleash the hidden card up your sleeve and become %1$s for {duration}.
                    
                    &6%2$s:
                    While active, any kill will be credited to your team.
                    &8&o;;Excluding self or teammate kills.
                    
                    &8&o;;If the are multiple Jokers, the kill will be credited randomly.
                    """.formatted(Named.THE_JOKER, Named.THE_JOKER.getName()));

            setItem(Material.PUFFERFISH);

            setDurationSec(30.0f);
            setCooldownSec(35.0f);

            setSound(Sound.ENTITY_WITCH_CELEBRATE, 1.25f);

            setSilent(true);
        }


        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player) {
            return execute(() -> {
            });
        }
    }
}
