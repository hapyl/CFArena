package me.hapyl.fight.command;

import me.hapyl.fight.game.maps.features.Booster;
import me.hapyl.fight.game.maps.features.MathBooster;
import me.hapyl.fight.util.BlockLocation;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.chat.LazyEvent;
import me.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public class DebugBooster extends SimplePlayerAdminCommand {
    public DebugBooster(String name) {
        super(name);
        setUsage("debugBooster (vecX) (vecY) (vecZ)");
    }

    @Override
    protected void execute(Player player, String[] args) {
        if (args.length == 6) {
            final double[] values = new double[6];
            for (int i = 0; i < args.length; i++) {
                values[i] = NumberConversions.toDouble(args[i]) + 0.5d;
            }

            new MathBooster(
                    values[0], values[1], values[2],
                    values[3], values[4], values[5]
            ).launch();
            return;
        }

        if (args.length == 3) {

            final double[] vectors = getVectors(args);
            final BlockLocation location = new BlockLocation(player.getLocation());
            final Vector vector = new Vector(vectors[0], vectors[1], vectors[2]);
            final Booster booster = new Booster(location, vector, true);

            booster.launch(true);

            final String string = "%s, %s, %s, %s, %s, %s".formatted(
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    vector.getX(),
                    vector.getY(),
                    vector.getZ()
            );

            Chat.sendClickableHoverableMessage(
                    player,
                    LazyEvent.suggestCommand(string),
                    LazyEvent.showText("&eClick to copy vector."),
                    "&aLaunching piggy with vector " + vector
            );

            return;
        }

        sendInvalidUsageMessage(player);
    }

    private double[] getVectors(String[] str) {
        if (str.length < 3) {
            return new double[] { 0.0d, 0.0d, 0.0d };
        }
        return new double[] { NumberConversions.toDouble(str[0]), NumberConversions.toDouble(str[1]), NumberConversions.toDouble(str[2]) };
    }

}
