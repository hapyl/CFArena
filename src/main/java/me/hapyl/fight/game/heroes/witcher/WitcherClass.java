package me.hapyl.fight.game.heroes.witcher;


import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.*;
import me.hapyl.fight.game.talents.witcher.Irden;
import me.hapyl.fight.game.talents.witcher.Kven;
import me.hapyl.fight.game.task.player.PlayerTimedGameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class WitcherClass extends Hero implements ComplexHero, UIComponent {

    private final PlayerMap<Combo> combos = PlayerMap.newMap();

    @DisplayField private final double defenseIncrease = 2.0d;
    @DisplayField private final double damageMultiplierPerCombo = 0.1d;

    public WitcherClass(@Nonnull Key key) {
        super(key, "The Witcher");

        setArchetypes(Archetype.DAMAGE, Archetype.MELEE, Archetype.HEXBANE, Archetype.DEFENSE);
        setGender(Gender.MALE);

        setDescription("Some say that he's the most trained Witcher ever; Well versed in any kind of magic...");
        setItem("910905be4f67e2fcad291cdf8aeb2e9ff55fe93f27b8c1f0959024a3cb4a7052");

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(44, 48, 101);
        equipment.setLeggings(60, 66, 69);
        equipment.setBoots(29, 29, 33);

        setWeapon(
                new Weapon(Material.IRON_SWORD)
                        .setName("Aerondight")
                        .setDescription("""
                                Light, sharp as a razor, and fits the hand neatly.
                                """)
                        .setDamage(5.0d));

        setUltimate(new WitcherUltimate());
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        combos.put(player, new Combo(player));
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        combos.clear();
    }

    public Combo getCombo(GamePlayer player) {
        return combos.computeIfAbsent(player, Combo::new);
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getDamagerAsPlayer();
        final LivingGameEntity entity = instance.getEntity();

        if (player == null) {
            return;
        }

        final Combo combo = getCombo(player);

        if (combo.getEntity() == null && entity != player) {
            combo.setEntity(entity.getEntity());
        }

        if (!combo.validateSameEntity(entity.getEntity()) || combo.isTimedOut()) {
            combo.reset();
            return;
        }

        if (!combo.validateCanCombo()) {
            return;
        }

        combo.incrementCombo();

        final int comboHits = combo.getCombo();

        // combo starts at 2 hits
        if (comboHits > 2) {
            final double damageMultiplier = (1 + (comboHits - 2) * damageMultiplierPerCombo);

            // Fx
            player.sendTitle("    &6&lCOMBO!", "     &4&lx" + (comboHits - 2), 0, 25, 25);
            player.playSound(Sound.ITEM_SHIELD_BREAK, 1.75f);

            instance.multiplyDamage(damageMultiplier);
        }
    }

    @Override
    public void processDamageAsVictim(@Nonnull DamageInstance instance) {
        final Kven kven = getThirdTalent();
        final GamePlayer player = instance.getEntityAsPlayer();

        if (kven.getShieldCharge(player) > 0) {
            kven.removeShieldCharge(player);

            instance.setCancelled(true);
        }
    }

    @Override
    public Talent getFirstTalent() {
        return TalentRegistry.AARD;
    }

    @Override
    public Talent getSecondTalent() {
        return TalentRegistry.IGNY;
    }

    @Override
    @Nonnull
    public Kven getThirdTalent() {
        return TalentRegistry.KVEN;
    }

    @Override
    public Talent getFourthTalent() {
        return TalentRegistry.AKCIY;
    }

    @Override
    public Talent getFifthTalent() {
        return TalentRegistry.IRDEN;
    }

    @Override
    public Talent getPassiveTalent() {
        return TalentRegistry.COMBO_SYSTEM;
    }

    @Override
    public @Nonnull String getString(@Nonnull GamePlayer player) {
        final int shieldLevel = getThirdTalent().getShieldCharge(player);
        return shieldLevel > 0 ? "&2🛡 &l" + shieldLevel : "";
    }

    private class WitcherUltimate extends UltimateTalent {
        public WitcherUltimate() {
            super(WitcherClass.this, "All the Trainings", 80);

            setDescription("""
                    Remember all your trainings and unleash them at once.
                    
                    Gaining a %1$s increase and the %2$s aura that follows you for {duration}.
                    &8;;After the duration ends, %3$s and %2$s start their cooldown.
                    """.formatted(AttributeType.DEFENSE, TalentRegistry.IRDEN.getName(), TalentRegistry.KVEN.getName()));

            setType(TalentType.ENHANCE);
            setItem(Material.DRAGON_BREATH);
            setDurationSec(10);
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            final EntityAttributes attributes = player.getAttributes();
            final Kven kven = TalentRegistry.KVEN;
            final Irden irden = TalentRegistry.IRDEN;

            kven.startCd(player, 10000);
            irden.startCd(player, 10000);

            attributes.increaseTemporary(Temper.WITCHER, AttributeType.DEFENSE, defenseIncrease, getUltimateDuration());

            new PlayerTimedGameTask(player, getClass(), getUltimate()) {
                @Override
                public void run(int tick) {
                    irden.affect(player, player.getLocation(), tick);
                }

                @Override
                public void onLastTick() {
                    kven.startCd(player);
                    irden.startCd(player);
                }
            }.runTaskTimer(0, 1);

            return UltimateResponse.OK;
        }
    }
}
