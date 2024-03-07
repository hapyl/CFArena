package me.hapyl.fight.game.cosmetic.skin.bk;

import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.skin.Skin;
import me.hapyl.fight.game.heroes.Affiliation;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import org.bukkit.Color;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class SkinRoyalKnight extends Skin {

    public SkinRoyalKnight(@Nonnull Heroes hero) {
        super(hero);

        setName("Royal Knight");
        setDescription("""
                This attire was worn before the modernization of the Kingdom.
                """);

        setRarity(Rarity.RARE);
        setRubyPrice(10);

        final Equipment equipment = getEquipment();

        equipment.setTexture("e2dfde6c2c8f0a7adf7ae4e949a804fedf95c6b9562767eae6c22a401cd02cbd");
        equipment.setChestPlate(Color.BLUE);
        equipment.setLeggings(Material.CHAINMAIL_LEGGINGS);
        equipment.setBoots(Material.IRON_BOOTS);
    }

}
