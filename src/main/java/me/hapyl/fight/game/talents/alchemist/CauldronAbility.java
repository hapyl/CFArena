package me.hapyl.fight.game.talents.alchemist;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class CauldronAbility extends Talent implements Listener {

    private final PlayerMap<AlchemicalCauldron> cauldrons = PlayerMap.newMap();
    private final ItemStack missingStickItem = new ItemBuilder(Material.CLAY_BALL)
            .setName("&cStick is Missing!")
            .setSmartLore("Your stick is currently brewing a potion! Click the cauldron to get it back.")
            .asIcon();

    public CauldronAbility() {
        super("Brewing Pot", """
                Place a Brewing Cauldron to brew a Magic Potion. Put your Brewing Stick in it and wait!
                                
                Once ready, claim you potion and enhance yourself with the following effects:
                                
                &a- &7Drinking a potion will grant double effects. &8(5 charges)
                                
                &a- &7Hitting an enemy will apply random effect. &8(10 charges)
                """
        );

        setType(TalentType.ENHANCE);
        setItem(Material.CAULDRON);
        setCooldownSec(120);
    }

    @EventHandler()
    public void handleInteraction(PlayerInteractEvent ev) {
        if (!Manager.current().isGameInProgress()) {
            return;
        }

        final GamePlayer player = CF.getPlayer(ev.getPlayer());

        if (player == null) {
            return;
        }

        final Block clickedBlock = ev.getClickedBlock();

        if (ev.getHand() == EquipmentSlot.OFF_HAND
                || ev.getAction() != Action.RIGHT_CLICK_BLOCK
                || clickedBlock == null
                || clickedBlock.getType() != Material.WATER_CAULDRON) {
            return;
        }

        if (!HeroRegistry.ALCHEMIST.isSelected(player)) {
            return;
        }

        // Prevent wrong clicks by adding a tiny cooldown
        if (player.hasCooldown(HeroRegistry.ALCHEMIST.getWeapon().getMaterial()) || player.hasCooldown(missingStickItem.getType())) {
            return;
        }

        final AlchemicalCauldron cauldron = cauldrons.get(player);
        if (cauldron == null || !cauldron.compareBlock(clickedBlock)) {
            return;
        }

        ev.setCancelled(true);
        switch (cauldron.getStatus()) {

            case NEUTRAL, PAUSED -> {
                cauldron.setStatus(AlchemicalCauldron.Status.BREWING);
                changeItem(player, false);
            }

            case BREWING -> {
                cauldron.setStatus(AlchemicalCauldron.Status.PAUSED);
                changeItem(player, true);
            }

            case FINISHED -> {
                cauldron.finish();
                cauldron.clear();
                changeItem(player, true);
            }

        }
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        cauldrons.values().forEach(AlchemicalCauldron::clear);
        cauldrons.clear();
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        final AlchemicalCauldron cauldron = cauldrons.remove(player);

        if (cauldron != null) {
            cauldron.clear();
        }
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Block targetBlock = getTargetBlock(player);

        if (targetBlock == null) {
            return Response.error("Invalid target block!");
        }

        if (!targetBlock.getType().isAir()) {
            return Response.error("Target block is occupied!");
        }

        if (cauldrons.containsKey(player)) {
            return Response.error("You already have a cauldron!");
        }

        cauldrons.put(player, new AlchemicalCauldron(player, targetBlock.getLocation().clone()));
        return Response.OK;

    }

    private void changeItem(GamePlayer player, boolean flag) {
        GameTask.runLater(() -> {
            if (flag) {
                final Weapon weapon = HeroRegistry.ALCHEMIST.getWeapon();

                player.setItem(HotbarSlots.WEAPON, weapon.getItem());
                player.setCooldown(weapon.getMaterial(), 10);
            }
            else {
                player.setItem(HotbarSlots.WEAPON, missingStickItem);
                player.setCooldown(missingStickItem.getType(), 10);
            }
        }, 1);
    }

    private Block getTargetBlock(GamePlayer player) {
        final Block targetBlock = player.getTargetBlockExact(5);

        if (targetBlock == null) {
            return null;
        }
        return targetBlock.getRelative(BlockFace.UP);
    }

}
