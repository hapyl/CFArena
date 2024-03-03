package me.hapyl.fight.game.heroes.archive.shaman;

import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.event.custom.GameEntityHealEvent;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.UltimateResponse;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.archive.shaman.TotemTalent;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;

public class Shaman extends Hero implements PlayerDataHandler<ShamanData>, UIComponent, Listener {

    private final PlayerDataMap<ShamanData> shamanData = PlayerMap.newDataMap(ShamanData::new);

    private final double damageIncreasePerOverheal = 0.05;
    private final double maxOverhealUse = 10;
    private final double maxOverhealDistance = 25;

    public Shaman(@Nonnull Heroes handle) {
        super(handle, "Shaman");

        setAffiliation(Affiliation.THE_JUNGLE);
        setArchetype(Archetype.SUPPORT);
        setGender(Gender.MALE);

        setDescription("""
                An orc from the jungle. Always rumbles about something.
                """);

        setWeapon(new ShamanWeapon());
        setItem("a90515c41b3e131b623cc04978f101aab2e5b82c892890df991b7c079f91d2bd");

        final HeroAttributes attributes = getAttributes();

        attributes.setHealth(75);
        attributes.setAttack(50);
        attributes.setDefense(75);
        attributes.setVitality(50); // to balance self-healing
        attributes.setMending(200);
        attributes.setEffectResistance(30);

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(110, 94, 74);
        equipment.setLeggings(57, 40, 90);

        setUltimate(new ShamanUltimate());
    }

    @EventHandler()
    public void handleOverhealGain(GameEntityHealEvent ev) {
        if (!(ev.getHealer() instanceof GamePlayer player)) {
            return;
        }

        if (!validatePlayer(player)) {
            return;
        }

        final double excessHealing = ev.getExcessHealing();

        if (excessHealing <= 0) {
            return;
        }

        final ShamanData data = getPlayerData(player);
        data.increaseOverheal(excessHealing);
    }

    @EventHandler()
    public void handleOverhealDamage(GameDamageEvent ev) {
        final LivingGameEntity entity = ev.getEntity();

        if (!(ev.getDamager() instanceof LivingGameEntity damager)) {
            return;
        }

        final GameTeam team = damager.getTeam();

        if (team == null) {
            return;
        }

        for (GamePlayer player : team.getPlayers()) {
            if (!validatePlayer(player)) {
                continue;
            }

            if (damager.getLocation().distance(player.getLocation()) >= maxOverhealDistance) {
                continue;
            }

            final ShamanData data = getPlayerData(player);
            final double overhealCapped = Math.min(data.getOverheal(), maxOverhealUse);

            if (overhealCapped <= 0) {
                continue;
            }

            final double damageIncrease = 1 + overhealCapped * damageIncreasePerOverheal;

            ev.multiplyDamage(damageIncrease);
            data.decreaseOverheal(overhealCapped);

            // Spawn display to notify that the damage is increased
            entity.spawnBuffDisplay("&2🐍", 20);
            entity.playWorldSound(Sound.ENTITY_CAT_HISS, 1.25f);
            return;
        }
    }

    @Override
    @Nonnull
    public TotemTalent getFirstTalent() {
        return (TotemTalent) Talents.TOTEM.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.TOTEM_IMPRISONMENT.getTalent();
    }

    @Override
    public Talent getThirdTalent() {
        return Talents.SHAMAN_MARK.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.OVERHEAL.getTalent();
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final ShamanData data = getPlayerData(player);

        return "%s &a%.0f".formatted(Named.OVERHEAL.getCharacter(), data.getOverheal()) + (data.isOverheadMaxed() ? " &lMAX!" : "");
    }

    @Nonnull
    @Override
    public PlayerDataMap<ShamanData> getDataMap() {
        return shamanData;
    }
}
