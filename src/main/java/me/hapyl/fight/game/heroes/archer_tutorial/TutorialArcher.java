package me.hapyl.fight.game.heroes.archer_tutorial;


import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.entity.EquipmentSlots;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.UltimateResponse;
import me.hapyl.fight.game.heroes.archer.Archer;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.registry.Key;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TutorialArcher extends Hero implements Disabled, Listener {

    private final Archer archer = HeroRegistry.ARCHER;
    private final double healthThreshold = 25d;

    public TutorialArcher(@Nonnull Key key) {
        super(key, "Archer");

        final HeroAttributes attributes = getAttributes();
        attributes.setCooldownModifier(25);

        final Equipment equipment = getEquipment();
        equipment.setFromEquipment(archer.getEquipment());

        setWeapon(archer.getWeapon());
        setUltimate(new TutorialArcherUltimate());
    }

    @Nullable
    @Override
    public String disableReason() {
        return "Enforce non selectability.";
    }

    @EventHandler()
    public void handleDamage(GameDamageEvent ev) {
        final LivingGameEntity entity = ev.getEntity();
        final double damage = ev.getDamage();

        if (!(entity instanceof GamePlayer player)) {
            return;
        }

        if (!validatePlayer(player)) {
            return;
        }

        // Don't kill the player
        final double health = entity.getHealth();

        if (damage - health <= healthThreshold) {
            ev.multiplyDamage(0.0d);

            if (health <= healthThreshold) {
                entity.heal(healthThreshold - health + damage);
            }
        }
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.setItem(EquipmentSlots.ARROW, new ItemStack(Material.ARROW));
    }

    @Override
    public void onStop(@Nonnull GamePlayer player) {

    }

    @Override
    public Talent getFirstTalent() {
        return TalentRegistry.TRIPLE_SHOT;
    }

    @Override
    public Talent getSecondTalent() {
        return TalentRegistry.SHOCK_DART;
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }

    private class TutorialArcherUltimate extends UltimateTalent {

        public TutorialArcherUltimate() {
            super(TutorialArcher.this, "BOOM BOW", 5);
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            return UltimateResponse.OK;
        }
    }

}
