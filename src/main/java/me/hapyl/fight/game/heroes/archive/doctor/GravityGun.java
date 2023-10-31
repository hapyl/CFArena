package me.hapyl.fight.game.heroes.archive.doctor;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.RightClickable;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GravityGun extends Weapon implements RightClickable {

    private final PlayerMap<ActiveElement> elements = PlayerMap.newMap();

    public GravityGun() {
        super(Material.IRON_HORSE_ARMOR);

        setDamage(1.0d);
        setId("dr_ed_gun");
        setName("Dr. Ed's Gravity Energy Capacitor Mk. 3");
        setDescription("A tool that is capable of absorbing block elements.");

        setAbility(AbilityType.RIGHT_CLICK, Ability.of("Block Harvest", """
                Right-click on a block to harvest an element from it.
                                
                Right-click again with an element equipped to launch it forward, damaging up to &bone &7opponents on its way.
                                
                &a;;The damage and cooldown are based on the element.
                """, this));

        GameTask.scheduleCancelTask(() -> {
            elements.values().forEach(ActiveElement::remove);
            elements.clear();
        });
    }

    private ActiveElement getElement(GamePlayer player) {
        return elements.getOrDefault(player, null);
    }

    private boolean hasElement(GamePlayer player) {
        return this.getElement(player) != null;
    }

    public void setElement(GamePlayer player, @Nullable ActiveElement element) {
        if (element == null) {
            this.elements.remove(player);
            return;
        }
        this.elements.put(player, element);
    }

    public void remove(GamePlayer player) {
        final ActiveElement element = getElement(player);

        if (element != null) {
            element.stopTask();
            element.remove();
        }

        this.elements.remove(player);
    }

    @Override
    public void onRightClick(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
        if (player.hasCooldown(getMaterial())) {
            return;
        }

        final Block targetBlock = player.getTargetBlockExact(7);

        // throw
        if (hasElement(player)) {
            final ActiveElement element = getElement(player);
            element.stopTask();
            element.throwEntity();
            this.setElement(player, null);
            return;
        }

        // pick up
        if (targetBlock == null) {
            player.sendMessage("&cNo valid block in sight!");
            return;
        }

        if (ElementType.getElement(targetBlock.getType()) == ElementType.NULL) {
            player.sendMessage("&cTarget block does not have any valid elements...");
            return;
        }

        if (!targetBlock.getType().isBlock()) {
            player.sendMessage("&cTarget block is not a block?");
            return;
        }

        final ActiveElement element = new ActiveElement(player, targetBlock);
        player.setCooldown(getType(), 2); // fix instant throw
        element.startTask();
        setElement(player, element);

        // This spams chat like a lot, changed to a block pickup sound instead.
        player.playSound(targetBlock.getBlockData().getSoundGroup().getPlaceSound(), 1.0f);
    }
}
