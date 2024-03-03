package me.hapyl.fight.game.heroes.archive.knight;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.util.shield.ShieldBuilder;
import me.hapyl.spigotutils.module.math.Numbers;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemShield {
    private final Map<Integer, ItemStack> shieldMap;

    public ItemShield() {
        shieldMap = new HashMap<>();
        this.createShields();
    }

    private void createShields() {
        // 0
        shieldMap.put(0, new ShieldBuilder()
                .with(PatternType.STRIPE_BOTTOM)
                .with(PatternType.STRIPE_LEFT)
                .with(PatternType.STRIPE_TOP)
                .with(PatternType.STRIPE_RIGHT)
                .with(DyeColor.WHITE, PatternType.BORDER)
                .build());
        // 1
        shieldMap.put(
                1,
                new ShieldBuilder()
                        .with(PatternType.STRIPE_CENTER)
                        .with(PatternType.SQUARE_TOP_LEFT)
                        .with(DyeColor.WHITE, PatternType.CURLY_BORDER)
                        .with(PatternType.STRIPE_BOTTOM)
                        .with(DyeColor.WHITE, PatternType.BORDER)
                        .build()
        );

        // 2
        shieldMap.put(2, new ShieldBuilder()
                .with(PatternType.STRIPE_TOP)
                .with(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE)
                .with(PatternType.STRIPE_BOTTOM)
                .with(PatternType.STRIPE_DOWNLEFT)
                .with(DyeColor.WHITE, PatternType.BORDER)
                .build());

        // 3
        shieldMap.put(3, new ShieldBuilder()
                .with(PatternType.STRIPE_BOTTOM)
                .with(PatternType.STRIPE_MIDDLE)
                .with(PatternType.STRIPE_TOP)
                .with(DyeColor.WHITE, PatternType.CURLY_BORDER)
                .with(PatternType.STRIPE_RIGHT)
                .with(DyeColor.WHITE, PatternType.BORDER)
                .build());

        // 4
        shieldMap.put(4, new ShieldBuilder()
                .with(PatternType.STRIPE_LEFT)
                .with(DyeColor.WHITE, PatternType.HALF_HORIZONTAL_MIRROR)
                .with(PatternType.STRIPE_RIGHT)
                .with(PatternType.STRIPE_MIDDLE)
                .with(DyeColor.WHITE, PatternType.BORDER)
                .build());

        // 5
        shieldMap.put(5, new ShieldBuilder()
                .with(PatternType.STRIPE_BOTTOM)
                .with(DyeColor.WHITE, PatternType.RHOMBUS_MIDDLE)
                .with(PatternType.STRIPE_TOP)
                .with(PatternType.STRIPE_DOWNRIGHT)
                .with(DyeColor.WHITE, PatternType.BORDER)
                .build());

        // 6
        shieldMap.put(6, new ShieldBuilder()
                .with(PatternType.STRIPE_BOTTOM)
                .with(PatternType.STRIPE_RIGHT)
                .with(DyeColor.WHITE, PatternType.HALF_HORIZONTAL)
                .with(PatternType.STRIPE_MIDDLE)
                .with(PatternType.STRIPE_TOP)
                .with(PatternType.STRIPE_LEFT)
                .with(DyeColor.WHITE, PatternType.BORDER)
                .build());

        // 7
        shieldMap.put(7, new ShieldBuilder()
                .with(PatternType.STRIPE_DOWNLEFT)
                .with(PatternType.STRIPE_TOP)
                .with(DyeColor.WHITE, PatternType.BORDER)
                .build());

        // 8
        shieldMap.put(8, new ShieldBuilder()
                .with(PatternType.STRIPE_TOP)
                .with(PatternType.STRIPE_LEFT)
                .with(PatternType.STRIPE_MIDDLE)
                .with(PatternType.STRIPE_BOTTOM)
                .with(PatternType.STRIPE_RIGHT)
                .with(DyeColor.WHITE, PatternType.BORDER)
                .build());

        // 9
        shieldMap.put(9, new ShieldBuilder()
                .with(PatternType.STRIPE_LEFT)
                .with(DyeColor.WHITE, PatternType.HALF_HORIZONTAL_MIRROR)
                .with(PatternType.STRIPE_MIDDLE)
                .with(PatternType.STRIPE_TOP)
                .with(PatternType.STRIPE_RIGHT)
                .with(PatternType.STRIPE_BOTTOM)
                .with(DyeColor.WHITE, PatternType.BORDER)
                .build());
    }

    public void updateTexture(GamePlayer player, int charge) {
        charge = Numbers.clamp(charge, 0, 9);
        final ItemStack offHand = player.getInventory().getItem(EquipmentSlot.OFF_HAND);

        if (offHand != null && offHand.getType() == Material.SHIELD) {
            offHand.setItemMeta(shieldMap.get(charge).getItemMeta());
        }
    }

}
