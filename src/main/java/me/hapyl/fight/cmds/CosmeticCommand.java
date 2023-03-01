package me.hapyl.fight.cmds;

import me.hapyl.fight.game.cosmetic.Cosmetics;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.database.Database;
import me.hapyl.fight.game.database.entry.CosmeticEntry;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import me.hapyl.spigotutils.module.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CosmeticCommand extends SimplePlayerAdminCommand {
    public CosmeticCommand(String name) {
        super(name);

        setDescription("Allows to preview cosmetics.");
        setUsage("/cosmetic <cosmetic>");
        addCompleterValues(1, Cosmetics.values());
        addCompleterValues(3, Cosmetics.values());
        addCompleterValues(1, "set", "has", "give", "remove");
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (args.length == 3) {

            final String action = args[0];
            final Player target = Bukkit.getPlayer(args[1]);
            final Cosmetics cosmetic = Validate.getEnumValue(Cosmetics.class, args[2]);

            if (target == null) {
                Chat.sendMessage(player, "&cInvalid player!");
                return;
            }

            if (cosmetic == null) {
                Chat.sendMessage(player, "&cInvalid cosmetic! &7Valid cosmetics: %s", Arrays.toString(Cosmetics.values()));
                return;
            }

            final Database database = Database.getDatabase(target);
            final CosmeticEntry cosmetics = database.getCosmetics();
            final Type cosmeticType = cosmetic.getType();

            switch (action) {
                case "set" -> {
                    if (cosmetics.getSelected(cosmeticType) == cosmetic) {
                        cosmetics.unsetSelected(cosmeticType);
                        Chat.sendMessage(player, "&aUnset %s's %s cosmetic!", target.getName(), cosmeticType.name());
                        return;
                    }

                    cosmetics.setSelected(cosmeticType, cosmetic);
                    Chat.sendMessage(player, "&aSet %s's %s cosmetic to %s", target.getName(), cosmeticType.name(), cosmetic.name());
                }

                case "has" -> {
                    final boolean hasCosmetic = cosmetics.hasCosmetic(cosmetic);
                    Chat.sendMessage(player, "&a%s %s %s!", target.getName(), hasCosmetic ? "has" : "does not have", cosmetic.name());
                }

                case "give" -> {
                    cosmetics.addOwned(cosmetic);
                    Chat.sendMessage(player, "&aGave %s to %s", cosmetic.name(), target.getName());
                }

                case "remove" -> {
                    cosmetics.removeOwned(cosmetic);
                    Chat.sendMessage(player, "&aRemoved %s from %s", cosmetic.name(), target.getName());
                }

                default -> {
                    Chat.sendMessage(player, "&cInvalid action! &7Valid actions: set, has, give, remove");
                    return;
                }
            }

            return;
        }

        final Cosmetics cosmetic = Validate.getEnumValue(Cosmetics.class, args[0]);

        if (cosmetic == null) {
            Chat.sendMessage(player, "&cInvalid cosmetic! &7Valid cosmetics: %s", Arrays.toString(Cosmetics.values()));
            return;
        }

        cosmetic.getCosmetic().onDisplay(player);
        Chat.sendMessage(player, "&aDisplaying cosmetic %s", cosmetic.name());
    }
}
