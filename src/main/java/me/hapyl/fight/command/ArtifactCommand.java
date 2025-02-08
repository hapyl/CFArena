package me.hapyl.fight.command;

import me.hapyl.eterna.module.command.SimplePlayerAdminCommand;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.ArtifactEntry;
import me.hapyl.fight.game.artifact.Artifact;
import me.hapyl.fight.game.artifact.Type;
import me.hapyl.fight.registry.Registries;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;

public class ArtifactCommand extends SimplePlayerAdminCommand {

    private final List<String> artifactsNames;

    /**
     * Creates a new simple command
     *
     * @param name - Name of the command.
     */
    public ArtifactCommand(String name) {
        super(name);

        artifactsNames = Registries.getArtifacts().values().stream().map(artifact -> artifact.getKey().toString()).toList();

        addCompleterValues(2, "has", "add", "remove", "get");
    }

    @Override
    protected void execute(Player player, String[] args) {
        // artifact <player> <has, add, remove> <artifact>
        // artifact <player> <get>              <type>

        if (args.length != 3) {
            Message.error(player, "Invalid usage!");
            return;
        }

        final Player target = getArgument(args, 0).toPlayer();
        final String argument = getArgument(args, 1).toString();

        if (target == null) {
            Message.error(player, "This player is not online!");
            return;
        }

        final PlayerDatabase database = CF.getDatabase(target);
        final ArtifactEntry entry = database.artifactEntry;

        if (argument.equalsIgnoreCase("get")) {
            final Type type = getArgument(args, 2).toEnum(Type.class);

            if (type == null) {
                Message.error(player, "Invalid type!");
                return;
            }

            final Artifact selected = entry.getSelected(type);

            if (selected == null) {
                Message.success(player, "{%s} doesn't have any {%s} selected.".formatted(target.getName(), type.name()));
            }
            else {
                Message.success(player, "{%s} has {%s} {%s} selected.".formatted(target.getName(), selected.getName(), type.name()));
            }

            return;
        }

        final String artifactId = getArgument(args, 2).toString();
        final Artifact artifact = Registries.getArtifacts().get(artifactId);

        if (artifact == null) {
            Message.error(player, "Could not find that artifact!");
            return;
        }

        switch (argument.toLowerCase()) {
            case "has" -> {
                final boolean isOwned = entry.isOwned(artifact);

                Message.info(player, "{%s} {%s} this artifact!".formatted(target.getName(), isOwned ? "owns" : "does not own"));
            }

            case "add" -> {
                if (entry.isOwned(artifact)) {
                    Message.error(player, "{%s} already owns this artifact!".formatted(target.getName()));
                    return;
                }

                entry.setOwned(artifact, true);
                Message.success(player, "Gave {%s} artifact to {%s}!".formatted(artifact.getName(), target.getName()));
            }

            case "remove" -> {
                if (!entry.isOwned(artifact)) {
                    Message.error(player, "{%s} does not own this artifact!".formatted(target.getName()));
                    return;
                }

                entry.setOwned(artifact, false);
                Message.success(player, "Removed {%s} from {%s}!".formatted(artifact.getName(), target.getName()));
            }

            default -> {
                Message.error(player, "Invalid operation!");
            }
        }
    }

    @Nullable
    @Override
    protected List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 3 && args[1].equalsIgnoreCase("get")) {
            return completerSort(Type.values(), args);
        }

        if (args.length == 3) {
            return completerSort(artifactsNames, args);
        }
        return null;
    }
}
