package me.hapyl.fight.game.heroes.archive.frostbite;

import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroPlaque;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import org.bukkit.Color;

import javax.annotation.Nonnull;

public class Freazly extends Hero implements HeroPlaque {

    public Freazly() {
        super("Frostbite");

        setArchetype(Archetype.HEXBANE);
        setDescription("A very cold entity to the touch.");
        setItem("cad7486b5d20823d5c24cba1850a600a7744209899828b19ccf93f69f2187058");

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(Color.fromRGB(139, 169, 214));
        equipment.setLeggings(Color.fromRGB(116, 141, 179));
        equipment.setBoots(Color.fromRGB(45, 54, 69));

        setWeapon(new FrostbiteWeapon());
        setUltimate(new FrostbiteUltimate(80));
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                getAlivePlayers().forEach(player -> {
                    Collect.nearbyEntities(player.getLocation(), 1.0d).forEach(entity -> {
                        if (entity.equals(player)) {
                            return;
                        }

                        entity.addEffect(GameEffectType.CHILL_AURA, 20, true);
                    });
                });
            }
        }.runTaskTimer(0, 10);
    }

    @Nonnull
    @Override
    public FrostbiteUltimate getUltimate() {
        return (FrostbiteUltimate) super.getUltimate();
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        new EternalFreeze(player, getUltimate());

        return UltimateCallback.OK;
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.ICICLES.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.ICE_CAGE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.CHILL_AURA.getTalent();
    }

    @Nonnull
    @Override
    public String text() {
        return "&a&lREWORKED!";
    }

    @Override
    public long until() {
        return HeroPlaque.super.until();
    }
}
