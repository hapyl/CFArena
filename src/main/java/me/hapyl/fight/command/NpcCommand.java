package me.hapyl.fight.command;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.equipment.Slot;
import me.hapyl.fight.game.playerskin.PlayerSkin;
import me.hapyl.fight.npc.runtime.RuntimeNPCManager;
import me.hapyl.fight.ux.Message;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import me.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import me.hapyl.spigotutils.module.reflect.npc.ItemSlot;
import me.hapyl.spigotutils.module.util.Enums;
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

            Message.success(player, "Created NPC!");
            return;
        }

        final HumanNPC npc = manager.getNpc(player);

        if (npc == null) {
            Message.error(player, "You must have an NPC! &e/npc create");
            return;
        }

        switch (arg0) {
            case "remove" -> {
                manager.removeNpc(player, npc);

                Message.success(player, "Removed NPC!");
            }

            case "tp" -> {
                npc.teleport(player.getLocation());

                Message.success(player, "Teleported to your location!");
            }
        }

        if (args.length > 1) {
            final String arg1 = getArgument(args, 1).toString().toLowerCase();

            switch (arg0) {
                case "equip" -> {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    item = item.getType().isAir() ? null : item;

                    final Heroes hero = getArgument(args, 1).toEnum(Heroes.class);

                    if (hero != null) {
                        final Equipment equipment = hero.getHero().getEquipment();

                        npc.setItem(ItemSlot.HEAD, equipment.getItem(Slot.HELMET));
                        npc.setItem(ItemSlot.CHEST, equipment.getItem(Slot.CHESTPLATE));
                        npc.setItem(ItemSlot.LEGS, equipment.getItem(Slot.LEGGINGS));
                        npc.setItem(ItemSlot.FEET, equipment.getItem(Slot.BOOTS));
                        npc.setItem(ItemSlot.MAINHAND, hero.getHero().getWeapon().getItem());

                        return;
                    }

                    switch (arg1) {
                        case "helmet" -> {
                            npc.setItem(ItemSlot.HEAD, item);

                            Message.success(player, "Changed helmet!");
                        }
                        case "chest" -> {
                            npc.setItem(ItemSlot.CHEST, item);

                            Message.success(player, "Changed chestplate!");
                        }
                        case "legs" -> {
                            npc.setItem(ItemSlot.LEGS, item);

                            Message.success(player, "Changed leggings!");
                        }
                        case "feet" -> {
                            npc.setItem(ItemSlot.FEET, item);

                            Message.success(player, "Changed boots!");
                        }

                        case "weapon" -> {
                            npc.setItem(ItemSlot.MAINHAND, item);

                            Message.success(player, "Changed mainhand item!");
                        }

                        case "offhand" -> {
                            npc.setItem(ItemSlot.OFFHAND, item);

                            Message.success(player, "Changed offhand item!");
                        }

                        default -> Message.error(
                                player,
                                "Invalid argument! Must be either 'helmet', 'chest', 'legs', 'feet', 'weapon' or 'offhand'!"
                        );
                    }
                }

                case "skin" -> {
                    final Heroes hero = Enums.byName(Heroes.class, arg1);

                    if (hero == null) {
                        npc.setSkin(arg1);
                        Message.success(player, "Set skin to '%s!".formatted(arg1));
                    }
                    else {
                        final PlayerSkin heroSkin = hero.getHero().getSkin();

                        if (heroSkin == null) {
                            Message.error(player, "This hero doesn't have a skin!");
                            return;
                        }

                        npc.setSkin(heroSkin.getTexture(), heroSkin.getSignature());
                        Message.success(player, "Applied %s skin to the NPC!".formatted(hero.getName()));
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
                        Message.success(player, "Rotated NPC!");
                    }
                    else {
                        Message.error(player, "Not enough arguments, provide 'yaw:float' and 'pitch:float'!");
                    }
                }
            }
        }
    }
}
