package me.hapyl.fight.game.heroes.archive.witcher;

import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.ComplexHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.witcher.Irden;
import me.hapyl.fight.game.talents.archive.witcher.Kven;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class WitcherClass extends Hero implements ComplexHero, UIComponent, PlayerElement {

    private final PlayerMap<Combo> combos = PlayerMap.newMap();

    public WitcherClass() {
        super("The Witcher");

        setArchetype(Archetype.DAMAGE);

        setDescription("Some say that he's the most trained Witcher ever; Well versed in any kind of magic...");
        setItem("910905be4f67e2fcad291cdf8aeb2e9ff55fe93f27b8c1f0959024a3cb4a7052");

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(44, 48, 101);
        equipment.setLeggings(60, 66, 69);
        equipment.setBoots(29, 29, 33);

        setWeapon(new Weapon(Material.IRON_SWORD).setName("Aerondight").setDamage(5.0d));

        setUltimate(
                new UltimateTalent(
                        "All the Trainings",
                        String.format(
                                "Remember all your trainings and unleash them at once. Creating infinite %1$s shield and %2$s aura that follows you for {duration}.____Both %1$s and %2$s starts their cooldowns.",
                                Talents.KVEN.getName(),
                                Talents.IRDEN.getName()
                        ), 80
                ).setDuration(200).setItem(Material.DRAGON_BREATH));
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        Talents.KVEN.startCd(player);
        Talents.IRDEN.startCd(player);

        player.getAttributes().increaseTemporary(Temper.WITCHER, AttributeType.DEFENSE, 1.0d, getUltimateDuration());

        new GameTask() {
            private int tick = getUltimateDuration();

            @Override
            public void run() {
                if (tick-- < 0) {
                    this.cancel();
                    return;
                }

                ((Irden) Talents.IRDEN.getTalent()).affect(player, player.getLocation(), tick);
            }
        }.runTaskTimer(0, 1);

        return UltimateCallback.OK;
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        combos.put(player, new Combo(player));
    }

    @Override
    public void onStop() {
        combos.clear();
    }

    public Combo getCombo(GamePlayer player) {
        return combos.computeIfAbsent(player, Combo::new);
    }

    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final GamePlayer player = input.getDamagerAsPlayer();
        final LivingGameEntity entity = input.getEntity();

        if (player == null) {
            return null;
        }

        final Combo combo = getCombo(player);
        double damage = input.getDamage();

        if (combo.getEntity() == null && entity != player) {
            combo.setEntity(entity.getEntity());
        }

        if (!combo.validateSameEntity(entity.getEntity())) {
            combo.reset();
        }

        if (combo.validateCanCombo()) {
            combo.incrementCombo();
        }
        else {
            combo.reset();
            return null;
        }

        final int comboHits = combo.getCombo();

        // combo starts at 2 hits
        if (comboHits > 2) {
            damage = Math.max(damage, damage * (1 + (comboHits - 2) * 0.1d));

            // Fx
            player.sendTitle("    &6&lCOMBO!", "     &4&lx" + (comboHits - 2), 0, 25, 25);
            player.playSound(Sound.ITEM_SHIELD_BREAK, 1.75f);
        }

        return new DamageOutput(damage);
    }

    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        final Kven kven = getThirdTalent();
        final GamePlayer player = input.getEntityAsPlayer();

        if (kven.getShieldCharge(player) > 0) {
            kven.removeShieldCharge(player);

            return DamageOutput.CANCEL;
        }
        return null;
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.AARD.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.IGNY.getTalent();
    }

    @Override
    @Nonnull
    public Kven getThirdTalent() {
        return (Kven) Talents.KVEN.getTalent();
    }

    @Override
    public Talent getFourthTalent() {
        return Talents.AKCIY.getTalent();
    }

    @Override
    public Talent getFifthTalent() {
        return Talents.IRDEN.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.COMBO_SYSTEM.getTalent();
    }

    @Override
    public @Nonnull String getString(@Nonnull GamePlayer player) {
        final int shieldLevel = getThirdTalent().getShieldCharge(player);
        return shieldLevel > 0 ? "&2🛡 &l" + shieldLevel : "";
    }
}
