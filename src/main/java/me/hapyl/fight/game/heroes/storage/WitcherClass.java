package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.heroes.ClassEquipment;
import me.hapyl.fight.game.heroes.ComplexHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.heroes.storage.extra.Combo;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.storage.witcher.Irden;
import me.hapyl.fight.game.talents.storage.witcher.Kven;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Handle;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
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

        setInfo("Some say, that he's the most trained Witcher ever; Well versed in any kind of magic...");
        setItemTexture(
                "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTEwOTA1YmU0ZjY3ZTJmY2FkMjkxY2RmOGFlYjJlOWZmNTVmZTkzZjI3YjhjMWYwOTU5MDI0YTNjYjRhNzA1MiJ9fX0=");

        final ClassEquipment equipment = getEquipment();
        equipment.setChestplate(44, 48, 101);
        equipment.setLeggings(60, 66, 69);
        equipment.setBoots(29, 29, 33);

        setWeapon(new Weapon(Material.IRON_SWORD).setName("Aerondight").setDamage(5.0d));

        setUltimate(
                new UltimateTalent(
                        "All the Trainings",
                        String.format(
                                "Remember all your trainings and unleash them at once. Creating infinite %1$s shield and %2$s aura that follows you for {duration}. Both %1$s and %2$s starts their cooldowns.",
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
        final Player player = input.getPlayer();
        final Combo combo = getCombo(player);
        double damage = input.getDamage();
        final LivingEntity entity = input.getEntity();

        if (combo.getEntity() == null && entity != null && entity != player) {
            combo.setEntity(entity);
        }

        if (!combo.validateSameEntity(entity)) {
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
        final Kven kven = (Kven) getThirdTalent();
        final Player player = input.getPlayer();
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
    public Talent getThirdTalent() {
        return Talents.KVEN.getTalent();
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

    private final Handle<Kven> kvenHandle = () -> (Kven) Talents.KVEN.getTalent();

    @Override
    public @Nonnull String getString(Player player) {
        final int shieldLevel = kvenHandle.getHandle().getShieldCharge(player);
        return shieldLevel > 0 ? "&2🛡 &l" + shieldLevel : "";
    }
}
