package me.hapyl.fight.game.cosmetic.skin.storage;

import me.hapyl.fight.game.cosmetic.skin.Skin;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class SkinMegalodon extends Skin {

    public SkinMegalodon() {
        super(Heroes.SHARK);

        setName("Megalodon");
        setDescription("""
                There is nothing fiercer that a megalodon!
                """);

        setRubyPrice(1_000);

        final Equipment equipment = getEquipment();

        equipment.setTexture("b86df01f51556ba8c8e781b0bb0f8d69496bf40a8845a9a3d456638d9b242ee6");
    }

}
