package me.hapyl.fight.npc;

import me.hapyl.eterna.module.inventory.Equipment;
import me.hapyl.eterna.module.player.dialog.DialogEntry;
import me.hapyl.eterna.module.player.dialog.DialogInstance;
import me.hapyl.eterna.module.player.dialog.DialogNpcEntry;
import me.hapyl.eterna.module.player.dialog.DialogOptionEntry;
import me.hapyl.eterna.module.reflect.npc.ClickType;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.database.entry.Metadata;
import me.hapyl.fight.gui.commission.CommissionGUI;
import me.hapyl.fight.quest.NpcBoundDialog;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class CommissionerNPC extends PersistentNPC {

    public static final Metadata HAS_UNLOCKED_COMMISSIONS = new Metadata("has_unlocked_commissions");

    private final Metadata hasTalkedToCommissioner;
    private final int minLevelToDoCommissions = 10;

    private final Dialog dialog;

    public CommissionerNPC(@Nonnull Key key) {
        super(key, -6.5d, 62.0d, -11.5d, "&cCommissioner");

        setSkin(
                "ewogICJ0aW1lc3RhbXAiIDogMTY4NjUxNjM2MTgwNiwKICAicHJvZmlsZUlkIiA6ICI1ZWQ4OTJiN2UyZGU0ZjYyYjIyNmFjNjQwZDA0YmJiOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJmcm9zdGVkc3Vuc2hpbmUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTc5MmI2OTk3ZDczOWY1MzViZWVkM2FiMWQ0YWVhZGZhNzY3NzdiZjhlMzhhNjY2ZjU0ZjgyZmY5Zjg1ODE4NiIKICAgIH0KICB9Cn0=",
                "jlO8vD0Vq9kb1CAljQRVhpfktSvlUOVqcQ0qXFh0xcZ1uT8z/PcDC29cXsVQ+GmHgiIElwXkgvJ0G1GIbkpm+2nBSAqOEXiKcaJvw41gusha0whJ9AQ4HRzcuSpX0eU/MjDH7/iB4z3cN13GKCa7fg7bp4o0rcqAsAvoOeKJK9ThHdaDoKPs7IsdCppIU237Q4B7yfD8hzcZSptd29dFkjvFXfw85IU2vj7ZbInWjmFB3QctoMHCMt8ncn/oW0zWf/l6SEegIJcHYSCNzCy8a66OBddL3MtKb57X/Yw6+ipSqE4PxUqLM/HxfML9BsX+BusER/bsf4Unp4JIDMn3JQzbS4tARRahB3WPfYoJn7Xt/uPiFFgxH58x5xP7jwNnbgNTvt7m87D0tqdu5VeM9KUDOqM5yAWwMdothENX8L4NNpdu1VeJ3I0XesP7DVsGU049SiqiKCuZVFhItoNIdH655ceOvDWsvdpYd/BJRaLkC9BYm8KbbG8YRjyzugeFVm+c3lIxXf6Hwe5omvLbiI6cefErtXBoADXRHBd76eQuxB7pz6swa6Iy2jQxw2YoAuL1dPugwqskQ5nrusf9+T09OIIzag36nXTGSHtL1MJ3MN1t1/NTLHhgcp2adTfEGkgh2Qjo5TaFYfh8jfDS6hnUflwknXkaW8WP3gOicaE="
        );

        setEquipment(
                Equipment.builder()
                         .mainHand(Material.WRITABLE_BOOK)
                         .chestPlate(Material.CHAINMAIL_CHESTPLATE)
                         .leggings(Material.CHAINMAIL_LEGGINGS)
                         .build()
        );

        setLookAtCloseDist(0);

        sound = new PersistentNPCSound(Sound.ENTITY_ZOMBIFIED_PIGLIN_HURT, 0.75f);
        hasTalkedToCommissioner = new Metadata("has_talked_to_commissioner");
        dialog = new Dialog();
    }

    @Override
    public void onClick(@Nonnull Player player, @Nonnull ClickType clickType) {
        if (hasTalkedToCommissioner.has(player)) {
            new CommissionGUI(player);
        }
        else {
            dialog.start(player);
        }
    }

    @Override
    public void tick() {
        super.tick();

        final Location location = getLocation();
        final Player nearestPlayer = BukkitUtils.getNearestEntity(location, 5, 5, 5, Player.class);

        // If nearest player, look at them
        if (nearestPlayer != null) {
            lookAt(nearestPlayer);
        }
        // Else shake head
        else {
            final float yaw = (float) (30 * Math.sin((2 * Math.PI / 15) * tick) - 20);

            setHeadRotation(yaw, 45f);
        }
    }

    private class Dialog extends NpcBoundDialog {

        public Dialog() {
            super(CommissionerNPC.this);

            addEntry(
                    CommissionerNPC.this,
                    "What do I do, what do I do...?",
                    "How can rent be so expensive...?"
            );

            addEntry(new DialogOptionEntry()
                    .setOption(
                            1, DialogOptionEntry.builder()
                                                .prompt("What's wrong?")
                                                .advanceDialog(true)
                    )
            );

            addEntry(
                    CommissionerNPC.this,
                    "Oh, hello {player}...",
                    "It's nothing... I just got the bill for this month's rent, and...",
                    "Let's say it's a little on the bigger side..."
            );

            addEntry(new DialogOptionEntry()
                    .setOption(
                            1, DialogOptionEntry.builder()
                                                .prompt("Who are you?")
                                                .add(
                                                        CommissionerNPC.this,
                                                        "Ah yes, let me introduce myself.",
                                                        "I'm a {npc_name}&f who came here to expand my business.",
                                                        "My business consists of clearing perilous areas of monsters.",
                                                        "I myself is a war veteran, but after 214 years, my bones started to give way...",
                                                        "So I decided to create a business where I hire brave warriors to clear the areas for me.",
                                                        "The area is free of monsters, they get paid, I get paid. Sounds like a win-win to me."
                                                )
                    )
            );

            addEntry(new DialogOptionEntry()
                    .setOption(1, DialogOptionEntry.builder().prompt("I can help!").advanceDialog(true))
                    .setOption(2, DialogOptionEntry.builder().prompt("I'm a brave warrior!").advanceDialog(true))
            );

            addEntry(new DialogEntry() {
                @Override
                public void run(@Nonnull DialogInstance dialog) {
                    final Player player = dialog.getPlayer();
                    final boolean hasUnlockedCommissions = HAS_UNLOCKED_COMMISSIONS.get(player, false);

                    if (!hasUnlockedCommissions) {
                        dialog.hijackEntries(List.of(
                                new DialogNpcEntry(CommissionerNPC.this, "I appreciate that you want to help, but I'm afraid it's too dangerous for you."),
                                new DialogNpcEntry(CommissionerNPC.this, "Come back when you're stronger!"),
                                DialogEntry.of(DialogInstance::cancel)
                        ));
                    }
                }

                @Override
                public int getDelay() {
                    return 10;
                }
            });

            addEntry(
                    CommissionerNPC.this,
                    "Yes... you are strong indeed!",
                    "In that case, would you like a part-time job?",
                    "You will be paid when you clear the commission.",
                    "Just be careful, I can't pay you if you're dead!",
                    "You can also bring your friends, I'll pay everyone equally.",
                    "Talk to me again to accept a commission."
            );

            addEntry(DialogEntry.of(instance -> hasTalkedToCommissioner.set(instance.getPlayer(), true)));
        }

    }

}
