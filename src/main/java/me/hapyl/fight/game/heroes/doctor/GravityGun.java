package me.hapyl.fight.game.heroes.doctor;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.game.weapons.ability.Ability;
import me.hapyl.fight.game.weapons.ability.AbilityType;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GravityGun extends Weapon {

    private final PlayerMap<ActiveElement> elements = PlayerMap.newMap();

    public GravityGun() {
        super(Material.IRON_HORSE_ARMOR, Key.ofString("dr_ed_gun"));

        setDamage(1.0d);
        setName("Dr. Ed's Gravity Energy Capacitor Mk. 3");
        setDescription("A tool that is capable of absorbing block elements.");

        setAbility(AbilityType.RIGHT_CLICK, new BlockHarvest());

        GameTask.scheduleCancelTask(() -> {
            elements.values().forEach(ActiveElement::remove);
            elements.clear();
        });
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

    private ActiveElement getElement(GamePlayer player) {
        return elements.getOrDefault(player, null);
    }

    private boolean hasElement(GamePlayer player) {
        return this.getElement(player) != null;
    }

    public class BlockHarvest extends Ability {

        public BlockHarvest() {
            super("Block Harvest", """
                    Right-click on a block to harvest an element from it.
                    
                    Right-click again with an element equipped to launch it forward, damaging up to &bone &7opponents on its way.
                    
                    &8;;The damage and cooldown are based on the element.
                    """);
        }

        @Nullable
        @Override
        public Response execute(@Nonnull GamePlayer player, @Nonnull ItemStack item) {
            final Block targetBlock = player.getTargetBlockExact(7);

            // Throw
            if (hasElement(player)) {
                final ActiveElement element = getElement(player);

                element.stopTask();
                element.throwEntity();

                setElement(player, null);
                startCooldown(player, element.getCooldown());

                return Response.AWAIT;
            }

            // Pick Up
            if (targetBlock == null) {
                return Response.error("&cNo valid block in sight!");
            }

            if (ElementType.getElement(targetBlock.getType()) == ElementType.NULL) {
                return Response.error("&cTarget block does not have any valid elements...");
            }

            if (!targetBlock.getType().isBlock()) {
                return Response.error("&cTarget block is not a block?");
            }

            final ActiveElement element = new ActiveElement(player, targetBlock);
            startCooldown(player, 2); // fix instant throw
            element.startTask();
            setElement(player, element);

            // This spamming chat like a lot, changed to a block pickup sound instead.
            player.playSound(targetBlock.getBlockData().getSoundGroup().getPlaceSound(), 1.0f);
            return Response.AWAIT;
        }
    }

}
