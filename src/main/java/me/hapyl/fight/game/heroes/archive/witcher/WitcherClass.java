package me.hapyl.fight.game.heroes.archive.witcher;

import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.witcher.Irden;
import me.hapyl.fight.game.talents.archive.witcher.Kven;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class WitcherClass extends Hero implements ComplexHero, UIComponent, PlayerElement {

    private final Map<Player, Combo> combos = new HashMap<>();

    public WitcherClass() {
        super("The Witcher");

        setRole(Role.MELEE);
        setArchetype(Archetype.DAMAGE);

        setDescription("Some say that he's the most trained Witcher ever; Well versed in any kind of magic...");
        setItem("910905be4f67e2fcad291cdf8aeb2e9ff55fe93f27b8c1f0959024a3cb4a7052");

        final HeroEquipment equipment = getEquipment();
        equipment.setChestplate(44, 48, 101);
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
    public void useUltimate(Player player) {
        Talents.KVEN.startCd(player);
        Talents.IRDEN.startCd(player);

        PlayerLib.addEffect(player, PotionEffectType.DAMAGE_RESISTANCE, getUltimateDuration(), 1);

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

    }

    @Override
    public void onStart(Player player) {
        combos.put(player, new Combo(player));
    }

    @Override
    public void onStop() {
        combos.clear();
    }

    public Combo getCombo(Player player) {
        return combos.computeIfAbsent(player, Combo::new);
    }

    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final Player player = input.getBukkitPlayer();
        final Combo combo = getCombo(player);
        final LivingGameEntity entity = input.getDamagerAsLiving();

        double damage = input.getDamage();

        if (combo.getEntity() == null && entity != null && entity.isNot(player)) {
            combo.setEntity(entity.getEntity());
        }

        if (!combo.validateSameEntity(entity == null ? null : entity.getEntity())) {
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
            damage += damage * ((comboHits - 2) * 0.15);

            // fx
            PlayerLib.playSound(player, Sound.ITEM_SHIELD_BREAK, 1.75f);
            Chat.sendTitle(player, "        &6Combo", "          &4&lx" + (comboHits - 2), 0, 25, 25);
        }

        return new DamageOutput(damage);

    }

    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        final Kven kven = getThirdTalent();
        final Player player = input.getBukkitPlayer();

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
    public @Nonnull String getString(Player player) {
        final int shieldLevel = getThirdTalent().getShieldCharge(player);
        return shieldLevel > 0 ? "&2🛡 &l" + shieldLevel : "";
    }
}
