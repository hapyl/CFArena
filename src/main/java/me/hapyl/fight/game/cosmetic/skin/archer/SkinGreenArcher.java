package me.hapyl.fight.game.cosmetic.skin.archer;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.spigotutils.module.particle.ParticleBuilder;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nullable;

public class SkinGreenArcher extends AbstractSkinArcher {

    public SkinGreenArcher() {
        final Equipment equipment = getEquipment();

        equipment.setTexture("c354874f47c11649c783e79bc488a7973ec5922f9bedf38723c4d26be4ba1769");
        equipment.setChestPlate(58, 126, 82, TrimPattern.SHAPER, TrimMaterial.COPPER);
        equipment.setLeggings(106, 56, 46);
        equipment.setBoots(106, 56, 46, TrimPattern.WARD, TrimMaterial.COPPER);
    }

    @Nullable
    @Override
    public Color getHawkeyeArrowColor() {
        return null;
    }

    @Nullable
    @Override
    public Color getTripleShotArrowColor() {
        return null;
    }

    @Nullable
    @Override
    public Color getShockDartArrowColor() {
        return null;
    }

    @Nullable
    @Override
    public ParticleBuilder getShockDartBlueColor() {
        return null;
    }

    @Nullable
    @Override
    public ParticleBuilder getShockDartRedColor() {
        return null;
    }

    @Override
    public boolean hawkeyeArrowTick(GamePlayer player, Location location) {
        return false;
    }

    @Override
    public boolean boomArrowTick(GamePlayer gamePlayer, Location location) {
        return false;
    }

}
