package me.hapyl.fight.game.heroes.storage.extra;

import me.hapyl.fight.game.talents.storage.extra.Element;
import me.hapyl.fight.game.talents.storage.extra.ElementType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class GravityGun extends Weapon {

    private final Map<Player, Element> elements = new HashMap<>();

    public GravityGun() {
        super(Material.IRON_HORSE_ARMOR);

        setDamage(1.0d);
        setId("dr_ed_gun");
        setName("Dr. Ed's Gravity Energy Capacitor Mk. 3");
        setDescription(
                "A tool that is capable of absorbing blocks elements.____&e&lRIGHT CLICK &7a block to harvest element from it.____&e&lRIGHT CLICK &7again with element equipped to launch it forward, damaging up to &bone &7opponents on it's way.____The damage and cooldown is based on the element."
        );

        GameTask.scheduleCancelTask(() -> {
            elements.values().forEach(Element::remove);
            elements.clear();
        });
    }

    private Element getElement(Player player) {
        return elements.getOrDefault(player, null);
    }

    private boolean hasElement(Player player) {
        return this.getElement(player) != null;
    }

    public void setElement(Player player, @Nullable Element element) {
        if (element == null) {
            this.elements.remove(player);
            return;
        }
        this.elements.put(player, element);
    }

    public void remove(Player player) {
        final Element element = getElement(player);
        if (element != null) {
            element.stopTask();
            element.remove();
        }

        this.elements.remove(player);
    }

    @Override
    public void onRightClick(Player player, ItemStack item) {
        if (player.hasCooldown(getMaterial())) {
            return;
        }

        final Block targetBlock = player.getTargetBlockExact(7);

        // throw
        if (hasElement(player)) {
            final Element element = getElement(player);
            element.stopTask();
            element.throwEntity();
            this.setElement(player, null);
            return;
        }

        // pick up
        if (targetBlock == null) {
            Chat.sendMessage(player, "&cNo valid block in sight!");
            return;
        }

        if (ElementType.getElement(targetBlock.getType()) == ElementType.NULL) {
            Chat.sendMessage(player, "&cTarget block does not have any valid elements...");
            return;
        }

        if (!targetBlock.getType().isBlock()) {
            Chat.sendMessage(player, "&cTarget block is not a block?");
            return;
        }

        final Element element = new Element(player, targetBlock);
        player.setCooldown(getType(), 2); // fix instant throw
        element.startTask();
        setElement(player, element);

        // This spams chat like a lot, changed to a block pickup sound instead.
        PlayerLib.playSound(player, targetBlock.getBlockData().getSoundGroup().getPlaceSound(), 1.0f);

        //Chat.sendMessage(player, "&aPicked up element of %s!", Chat.capitalize(targetBlock.getType()));
    }
}
