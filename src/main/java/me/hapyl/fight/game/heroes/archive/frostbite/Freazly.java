package me.hapyl.fight.game.heroes.archive.frostbite;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import org.bukkit.Color;

import javax.annotation.Nonnull;

public class Freazly extends Hero {

    private final double chillAuraRadius = 3.0d;
    private final TemperInstance temper = Temper.CHILL_AURA
            .newInstance()
            .decreaseScaled(AttributeType.SPEED, 10)
            .decreaseScaled(AttributeType.ATTACK_SPEED, 50);

    public Freazly(@Nonnull Heroes handle) {
        super(handle, "Frostbite");

        setArchetype(Archetype.HEXBANE);
        setGender(Gender.UNKNOWN);
        setDescription("A very cold entity to the touch.");
        setItem("cad7486b5d20823d5c24cba1850a600a7744209899828b19ccf93f69f2187058");

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(Color.fromRGB(139, 169, 214));
        equipment.setLeggings(Color.fromRGB(116, 141, 179));
        equipment.setBoots(Color.fromRGB(45, 54, 69));

        setWeapon(new FrostbiteWeapon());
        setUltimate(new FrostbiteUltimate(this, 60));
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                getAlivePlayers().forEach(player -> {
                    Collect.nearbyEntities(player.getLocation(), chillAuraRadius).forEach(entity -> {
                        if (player.isSelfOrTeammate(entity)) {
                            return;
                        }

                        temper.temper(entity, 20);
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

}
