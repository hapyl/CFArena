package me.hapyl.fight.command;

import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import me.hapyl.eterna.module.player.PlayerSkin;
import me.hapyl.eterna.module.reflect.npc.HumanNPC;
import me.hapyl.eterna.module.reflect.npc.ItemSlot;
import me.hapyl.fight.Main;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.equipment.Slot;
import me.hapyl.fight.npc.runtime.RuntimeNPCManager;
import me.hapyl.fight.Notifier;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NpcCommand extends SimplePlayerAdminCommand {
    public NpcCommand(String name) {
        super(name);

        addCompleterValues(1, "create", "remove", "tp", "equip", "skin", "rotate");
    }

    // npc
    @Override
    protected void execute(Player player, String[] args) {
        final RuntimeNPCManager manager = Main.getPlugin().getNpcManager();
        final String arg0 = getArgument(args, 0).toString().toLowerCase();

        if (arg0.equalsIgnoreCase("create")) {
            manager.createNpc(player, player.getLocation()).show(player);

            Notifier.success(player, "Created NPC!");
            return;
        }

        final HumanNPC npc = manager.getNpc(player);

        if (npc == null) {
            Notifier.error(player, "You must have an NPC! &e/npcf create");
            return;
        }

        switch (arg0) {
            case "remove" -> {
                manager.removeNpc(player, npc);

                Notifier.success(player, "Removed NPC!");
            }

            case "tp" -> {
                npc.teleport(player.getLocation());

                Notifier.success(player, "Teleported to your location!");
            }
        }

        if (args.length > 1) {
            final String arg1 = getArgument(args, 1).toString().toLowerCase();

            switch (arg0) {
                case "equip" -> {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    item = item.getType().isAir() ? null : item;

                    final Hero hero = HeroRegistry.ofStringOrNull(getArgument(args, 1).toString());

                    if (hero != null) {
                        final Equipment equipment = hero.getEquipment();

                        npc.setItem(ItemSlot.HEAD, equipment.getItem(Slot.HELMET));
                        npc.setItem(ItemSlot.CHEST, equipment.getItem(Slot.CHESTPLATE));
                        npc.setItem(ItemSlot.LEGS, equipment.getItem(Slot.LEGGINGS));
                        npc.setItem(ItemSlot.FEET, equipment.getItem(Slot.BOOTS));
                        npc.setItem(ItemSlot.MAINHAND, hero.getWeapon().getItem());

                        return;
                    }

                    switch (arg1) {
                        case "helmet" -> {
                            npc.setItem(ItemSlot.HEAD, item);

                            Notifier.success(player, "Changed helmet!");
                        }
                        case "chest" -> {
                            npc.setItem(ItemSlot.CHEST, item);

                            Notifier.success(player, "Changed chestplate!");
                        }
                        case "legs" -> {
                            npc.setItem(ItemSlot.LEGS, item);

                            Notifier.success(player, "Changed leggings!");
                        }
                        case "feet" -> {
                            npc.setItem(ItemSlot.FEET, item);

                            Notifier.success(player, "Changed boots!");
                        }

                        case "weapon" -> {
                            npc.setItem(ItemSlot.MAINHAND, item);

                            Notifier.success(player, "Changed mainhand item!");
                        }

                        case "offhand" -> {
                            npc.setItem(ItemSlot.OFFHAND, item);

                            Notifier.success(player, "Changed offhand item!");
                        }

                        default -> Notifier.error(
                                player,
                                "Invalid argument! Must be either 'helmet', 'chest', 'legs', 'feet', 'weapon' or 'offhand'!"
                        );
                    }
                }

                case "skin" -> {
                    final Hero hero = HeroRegistry.ofStringOrNull(arg1);

                    if (hero == null) {
                        npc.setSkin(arg1);
                        Notifier.success(player, "Set skin to '%s!".formatted(arg1));
                    }
                    else {
                        final PlayerSkin heroSkin = hero.getSkin();

                        if (heroSkin == null) {
                            Notifier.error(player, "This hero doesn't have a skin!");
                            return;
                        }

                        npc.setSkin(heroSkin.getTexture(), heroSkin.getSignature());
                        Notifier.success(player, "Applied %s skin to the NPC!".formatted(hero.getName()));
                    }
                }

                case "rotate" -> {
                    if (args.length == 3) {
                        final float yaw = getArgument(args, 1).toFloat();
                        final float pitch = getArgument(args, 2).toFloat();

                        final Location location = npc.getLocation();
                        location.setYaw(yaw);
                        location.setPitch(pitch);

                        npc.teleport(location);
                        Notifier.success(player, "Rotated NPC!");
                    }
                    else {
                        Notifier.error(player, "Not enough arguments, provide 'yaw:float' and 'pitch:float'!");
                    }
                }
            }
        }
    }
}
