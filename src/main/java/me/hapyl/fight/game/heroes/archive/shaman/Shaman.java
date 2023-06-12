package me.hapyl.fight.game.heroes.archive.shaman;

import me.hapyl.fight.game.IGamePlayer;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.archive.shaman.ActiveTotem;
import me.hapyl.fight.game.talents.archive.shaman.Totem;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Shaman extends Hero implements ComplexHero, DisabledHero {

    public Shaman() {
        super("Shaman");

        setWeapon(new Weapon(Material.BAMBOO).setName("Shaman's Weapon").setDamage(5.0d));
        setItem("a90515c41b3e131b623cc04978f101aab2e5b82c892890df991b7c079f91d2bd");

        final HeroEquipment equipment = getEquipment();

        equipment.setChestplate(110, 94, 74);
        equipment.setLeggings(57, 40, 90);
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                Manager.current()
                        .getCurrentGame()
                        .getAlivePlayers(Heroes.SHAMAN)
                        .stream()
                        .map(IGamePlayer::getPlayer)
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
    public void useUltimate(Player player) {
    }

    @Override
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
