package me.hapyl.fight.game.heroes.inferno;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.game.weapons.ability.DummyAbility;
import me.hapyl.fight.terminology.EnumTerm;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;

public class Inferno extends Hero implements PlayerDataHandler<InfernoData>, UIComponent, Listener {

    private final PlayerDataMap<InfernoData> playerDataMap = PlayerMap.newDataMap(InfernoData::new);

    public Inferno(@Nonnull Key key) {
        super(key, "Inferno");

        setDescription("""
                What is the right hand of the Demon King doing here?
                
                Is it regret, banishment... or perhaps boredom?
                """);

        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE, Archetype.SELF_SUSTAIN, Archetype.HEXBANE);
        profile.setGender(Gender.OTHER);
        profile.setAffiliation(Affiliation.HELL);
        profile.setRace(Race.DEMON);

        final HeroAttributes attributes = getAttributes();
        attributes.setCritChance(-100);
        attributes.setKnockbackResistance(70d);
        attributes.setEffectResistance(70d);
        attributes.setAttackSpeed(80);

        final HeroEquipment equipment = getEquipment();
        setItem("3ec891e2104626342ded1f8d9a14e2be42b2da0c2c6026f99ac1c6ef9ab2915c");

        equipment.setChestPlate(24, 16, 19, TrimPattern.TIDE, TrimMaterial.GOLD);
        equipment.setLeggings(76, 35, 22, TrimPattern.SILENCE, TrimMaterial.NETHERITE);
        equipment.setBoots(Material.GOLDEN_BOOTS, TrimPattern.SILENCE, TrimMaterial.GOLD);

        setWeapon(new InfernoWeapon());
        setUltimate(new InfernoUltimate(this));
    }

    @EventHandler
    public void handlePlayerArmSwingEvent(PlayerArmSwingEvent ev) {
        final GamePlayer player = CF.getPlayer(ev);

        if (!validatePlayer(player)) {
            return;
        }

        final InfernoData data = getPlayerData(player);

        if (data.currentDemon == null) {
            return;
        }

        data.currentDemon.entity().swingMainHand();
    }

    @Override
    public void processDamageAsVictim(@Nonnull DamageInstance instance) {
        final DamageCause cause = instance.getCause();

        if (cause.isFireDamage()) {
            instance.setCancelled(true);
        }
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getDamagerAsPlayer();

        if (!validatePlayer(player) || instance.isCancelled()) {
            return;
        }

        final InfernoData data = getPlayerData(player);

        // Handle typhoeus damage
        if (data.currentDemon == null || data.currentDemon.type() != InfernoDemonType.TYPHOEUS || !instance.getCause().isDirectDamage()) {
            return;
        }

        data.typhoeusDamage.add(new InfernoData.DamageData(instance.getDamage(), System.currentTimeMillis()));
    }

    @Override
    public boolean processInvisibilityDamage(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, double damage) {
        final InfernoData data = getPlayerData(player);

        return data.currentDemon == null;
    }

    @Override
    public boolean isValidIfInvisible(@Nonnull GamePlayer player) {
        // Demons redirect the damage
        return false;
    }

    @Override
    public Talent getFirstTalent() {
        return TalentRegistry.DEMON_SPLIT_QUAZII;
    }

    @Override
    public Talent getSecondTalent() {
        return TalentRegistry.DEMON_SPLIT_TYPHOEUS;
    }

    @Override
    public Talent getThirdTalent() {
        return TalentRegistry.FIRE_PIT;
    }

    @Override
    public Talent getPassiveTalent() {
        return TalentRegistry.DEMON_KIND;
    }

    @Nonnull
    @Override
    public PlayerDataMap<InfernoData> getDataMap() {
        return playerDataMap;
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final InfernoData data = getPlayerData(player);
        final InfernoDemon demon = data.currentDemon;

        if (demon == null) {
            return "";
        }

        return "&4👿 %s &c%s".formatted(demon.type().getName(), demon.getTimeLeft());
    }

    private static class InfernoWeapon extends Weapon {
        protected InfernoWeapon() {
            super(Material.BLAZE_ROD, Key.ofString("inferno_weapon"));

            setName("Demonhand");
            setDescription("""
                    So demonic!
                    """);

            setDamage(6.66d);
            damageCause(DamageCause.DEMON_HAND);

            setAbility(
                    AbilityType.ATTACK, DummyAbility.of(
                            "Hell", """
                                    Your attacks &ccannot&7 &9crit&7, but instead deal %s; truly painful!
                                    """.formatted(EnumTerm.TRUE_DAMAGE)
                    )
            );
        }
    }
}
