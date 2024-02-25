package me.hapyl.fight.game.heroes.archive.archer_tutorial;

import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.entity.EquipmentSlot;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.DisabledHero;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.heroes.archive.archer.Archer;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TutorialArcher extends Hero implements DisabledHero, Listener {

    private final Archer archer = Heroes.ARCHER.getHero(Archer.class);
    private final double healthThreshold = 25d;

    public TutorialArcher(@Nonnull Heroes handle) {
        super(handle, "Archer");

        final HeroAttributes attributes = getAttributes();
        attributes.setCooldownModifier(25);

        final Equipment equipment = getEquipment();
        equipment.setFromEquipment(archer.getEquipment());

        setWeapon(archer.getWeapon());
        setUltimate(new UltimateTalent(this, "BOOM BOW", 5));
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
        player.setItem(EquipmentSlot.ARROW, new ItemStack(Material.ARROW));
    }

    @Override
    public void onStop(@Nonnull GamePlayer player) {

    }

    @Nullable
    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        return null;
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.TRIPLE_SHOT.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.SHOCK_DARK.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }

}
