package me.hapyl.fight.game.heroes.vampire;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.vampire.BatSwarm;
import me.hapyl.fight.game.talents.vampire.Bloodshift;
import me.hapyl.fight.game.talents.vampire.VampirePassive;
import me.hapyl.fight.game.ui.UIComplexComponent;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.List;

public class Vampire extends Hero implements Listener, UIComplexComponent, PlayerDataHandler<VampireData> {

    private final PlayerDataMap<VampireData> vampireData = PlayerMap.newDataMap(VampireData::new);

    public Vampire(@Nonnull DatabaseKey key) {
        super(key, "Vampire");

        setArchetypes(Archetype.DAMAGE, Archetype.SELF_SUSTAIN, Archetype.SELF_BUFF);
        setGender(Gender.MALE);
        setRace(Race.VAMPIRE);
        setAffiliation(Affiliation.CHATEAU);

        setItem("8d44756e0b4ece8d746296a3d5e297e1415f4ba17647ffe228385383d161a9");

        final HeroAttributes attributes = getAttributes();
        attributes.setHealth(90);

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(Color.BLACK);
        equipment.setLeggings(Color.BLACK);
        equipment.setBoots(Color.BLACK);

        setWeapon(Material.GHAST_TEAR, "Vampire's Fang", 5.0d);

        setUltimate(new VampireUltimate());
    }

    @EventHandler
    public void handleGameDamageEvent(GameDamageEvent ev) {
        final GameEntity damager = ev.getDamager();
        final double damage = ev.getDamage();

        if (!(damager instanceof GamePlayer player) || !validatePlayer(player)) {
            return;
        }

        final VampireData data = getPlayerData(player);
        final VampireState state = data.getState();
        final Bloodshift bloodshift = getFirstTalent();

        if (state == VampireState.DAMAGE) {
            // Make sure we have enough health to deal damage
            final double health = player.getHealth();
            final double healthDrain = damage * bloodshift.healthDrainPerOneDamage;

            if (health <= healthDrain) {
                player.sendSubtitle("&cNot enough health to deal damage!", 0, 10, 0);
                ev.setCancelled(true);
                return;
            }

            // Drain health
            player.setHealth(health - healthDrain);

            // Multiple damage
            ev.multiplyDamage(bloodshift.calculateDamage(player));

            // Fx
        }
        else {
            // Regenerate health
            final double healthRegen = damage * bloodshift.healthRegenPerOneDamage;

            player.heal(healthRegen);
            ev.setCancelled(true);
        }
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        updateLegion(player);
    }

    @Override
    public void onRespawn(@Nonnull GamePlayer player) {
        updateLegion(player);
    }

    private void updateLegion(GamePlayer player) {
        int heroCount = (int) player.getTeam().getPlayers().stream()
                .filter(teammate -> teammate.getHero().getAffiliation() == Affiliation.CHATEAU)
                .count();

        getPassiveTalent().getLegionIncrease(heroCount).temper(player, Constants.INFINITE_DURATION);

    }

    @Override
    public Bloodshift getFirstTalent() {
        return TalentRegistry.BLOODSHIFT;
    }

    @Override
    public BatSwarm getSecondTalent() {
        return TalentRegistry.BAT_SWARM;
    }

    @Override
    public VampirePassive getPassiveTalent() {
        return TalentRegistry.VANPIRE_PASSIVE;
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        vampireData.remove(player);
    }

    @Override
    public void onPlayersRevealed(@Nonnull GameInstance instance) {
    }

    @Override
    public List<String> getStrings(@Nonnull GamePlayer player) {
        return List.of();
    }

    @Nonnull
    @Override
    public PlayerDataMap<VampireData> getDataMap() {
        return vampireData;
    }

    private class VampireUltimate extends UltimateTalent {

        public VampireUltimate() {
            super(Vampire.this, "Vampire Ultimate", 5);
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            return UltimateResponse.OK;
        }
    }
}
