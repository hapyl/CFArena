package test;

import com.google.common.collect.Lists;
import me.hapyl.fight.Main;
import me.hapyl.fight.util.Nulls;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.CommandProcessor;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.entity.EntityUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.List;

public class Test {

    // Test push

    private final Main main;
    private boolean b = false;

    public Test(Main main) {
        this.main = main;

        test();
    }

    public void test() {
        if (b) {
            throw new ClassesFightTestHasBeenAlreadyInitiatedYouDumbassException();
        }

        b = true;

        testCommand(new TestCommand("giant") {

            private Giant giant;

            private Location getLocation(Player player) {
                final Location location = player.getLocation();
                location.subtract(0.0d, 7.0d, 0.0d);
                location.setPitch(0.0f);

                location.add(location.getDirection().setY(0.0d).normalize().multiply(-4.0d));
                return location;
            }

            @Override
            public void execute(Player player, ImmutableStringList strings) {
                if (strings.length == 0) {
                    // Spawn giant
                    if (giant == null) {
                        final Location behind = getLocation(player);

                        giant = Entities.GIANT.spawn(behind, self -> {
                            Nulls.runIfNotNull(self.getEquipment(), equipment -> {
                                equipment.setItemInMainHand(new ItemStack(Material.IRON_SWORD));
                                equipment.setItemInOffHand(new ItemStack(Material.GOLDEN_SWORD));
                            });

                            EntityUtils.setCollision(self, EntityUtils.Collision.DENY);
                            self.setInvisible(true);
                            self.setInvulnerable(true);
                        });

                        Chat.sendMessage(player, "&aSpawned giant.");

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (giant == null) {
                                    this.cancel();
                                    return;
                                }

                                giant.teleport(getLocation(player));
                            }
                        }.runTaskTimer(main, 0L, 1L);

                        return;
                    }

                    giant.remove();
                    giant = null;
                    Chat.sendMessage(player, "&cRemoved giant.");
                }

                if (strings.check(0, "swing")) {
                    giant.swingMainHand();
                    Chat.sendMessage(player, "&aSwung giant's hand.");
                }

            }
        });
    }

    private void testCommand(TestCommand testCommand) {
        new CommandProcessor(main).registerCommand(testCommand);
    }

    private static class ClassesFightTestHasBeenAlreadyInitiatedYouDumbassException extends RuntimeException {
    }

    public abstract static class TestCommand extends SimplePlayerAdminCommand {

        public TestCommand(String name) {
            super("test" + name);
        }

        @Override
        protected final void execute(Player player, String[] strings) {
            execute(player, new ImmutableStringList(strings));
        }

        public abstract void execute(Player player, ImmutableStringList strings);

    }

    public static class ImmutableStringList {

        private final List<String> stringList;
        public final int length;

        public ImmutableStringList(String[] init) {
            stringList = Lists.newArrayList(init);
            length = stringList.size();
        }

        public boolean has(int index) {
            return index >= 0 && index < stringList.size();
        }

        @Nonnull
        public String get(int index) {
            if (!has(index)) {
                return "";
            }

            return stringList.get(index);
        }

        public int size() {
            return stringList.size();
        }

        public boolean check(int index, String string) {
            return get(index).equals(string);
        }

    }

}
