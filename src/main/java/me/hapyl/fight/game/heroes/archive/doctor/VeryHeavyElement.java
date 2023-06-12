package me.hapyl.fight.game.heroes.archive.doctor;

import org.bukkit.Material;

public class VeryHeavyElement extends Element {

    public VeryHeavyElement() {
        setDamage(20);
        setCd(60);

        setMaterials(
                Material.ANCIENT_DEBRIS,
                Material.RAW_COPPER_BLOCK,
                Material.IRON_BLOCK,
                Material.CHIPPED_ANVIL,
                Material.DAMAGED_ANVIL,
                Material.RESPAWN_ANCHOR,
                Material.ANVIL,
                Material.RAW_IRON_BLOCK,
                Material.RAW_GOLD_BLOCK,
                Material.BEDROCK,
                Material.CRYING_OBSIDIAN
        );
    }

}
