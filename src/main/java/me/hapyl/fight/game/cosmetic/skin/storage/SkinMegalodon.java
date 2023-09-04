package me.hapyl.fight.game.cosmetic.skin.storage;

import me.hapyl.fight.game.cosmetic.skin.Skin;
import me.hapyl.fight.game.heroes.Equipment;
import me.hapyl.fight.game.heroes.Heroes;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class SkinMegalodon extends Skin {
    public SkinMegalodon() {
        super(Heroes.SHARK, "Megalodon");

        final Equipment equipment = getEquipment();
        equipment.setTexture("b86df01f51556ba8c8e781b0bb0f8d69496bf40a8845a9a3d456638d9b242ee6");
    }

    @Override
    public void onTick(Player player, int tick) {

    }

    @Override
    public void onKill(Player player, LivingEntity victim) {

    }

    @Override
    public void onDeath(Player player, LivingEntity killer) {

    }

    @Override
    public void onMove(Player player, Location to) {

    }

    @Override
    public void onStandingStill(Player player) {

    }
}
