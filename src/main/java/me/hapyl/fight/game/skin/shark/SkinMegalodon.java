package me.hapyl.fight.game.skin.shark;

import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.skin.trait.SkinTraitOnMove;
import me.hapyl.fight.game.skin.trait.SkinTraitType;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;

public class SkinMegalodon extends AbstractSkinShark implements Disabled {

    public SkinMegalodon() {
        setName("Megalodon");
        setDescription("""
                There is nothing fiercer that a megalodon!
                """);

        setRarity(Rarity.LEGENDARY);
        setRubyPrice(100);

        final Equipment equipment = getEquipment();

        equipment.setTexture("b86df01f51556ba8c8e781b0bb0f8d69496bf40a8845a9a3d456638d9b242ee6");
        equipment.setChestPlate(76, 103, 145);
        equipment.setLeggings(95, 131, 184);
        equipment.setBoots(49, 78, 158);

        setTrait(SkinTraitType.MOVE, new TraitMove());
    }

    private class TraitMove extends SkinTraitOnMove {

        public TraitMove() {
            super("Ripples & Waves", """
                    Displays ripples and waves below your feet!
                    """);
        }

        @Override
        public void onMove(@Nonnull GamePlayer player, @Nonnull Location to) {
            player.spawnWorldParticle(to, Particle.FALLING_WATER, 1);
        }

    }

}
