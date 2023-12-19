package me.hapyl.fight.game.heroes.archive.shaman;

import me.hapyl.fight.CF;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.archive.shaman.ActiveTotem;
import me.hapyl.fight.game.talents.archive.shaman.Totem;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class Shaman extends Hero implements ComplexHero, DisabledHero {

    public Shaman() {
        super("Shaman");

        setWeapon(new Weapon(Material.BAMBOO).setName("Shaman's Weapon").setDamage(5.0d));
        setItem("a90515c41b3e131b623cc04978f101aab2e5b82c892890df991b7c079f91d2bd");

        final Equipment equipment = getEquipment();

        equipment.setChestPlate(110, 94, 74);
        equipment.setLeggings(57, 40, 90);
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                CF.getAlivePlayers(Heroes.SHAMAN)
                        .forEach(player -> {
                            final Totem totemTalent = getFirstTalent();
                            final ActiveTotem totem = totemTalent.getTargetTotem(player);

                            if (totem == null) {
                                totemTalent.defaultAllTotems(player);
                                return;
                            }

                            totem.setActive();
                        });
            }
        }.runTaskTimer(0, 5);
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        return UltimateCallback.OK;
    }

    @Override
    @Nonnull
    public Totem getFirstTalent() {
        return (Totem) Talents.TOTEM.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.TOTEM_SLOWING_AURA.getTalent();
    }

    @Override
    public Talent getThirdTalent() {
        return Talents.TOTEM_HEALING_AURA.getTalent();
    }

    @Override
    public Talent getFourthTalent() {
        return Talents.TOTEM_CYCLONE_AURA.getTalent();
    }

    @Override
    public Talent getFifthTalent() {
        return Talents.TOTEM_ACCELERATION_AURA.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }
}
