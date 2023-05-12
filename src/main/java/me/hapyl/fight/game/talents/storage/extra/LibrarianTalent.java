package me.hapyl.fight.game.talents.storage.extra;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.storage.Librarian;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.Numbers;
import org.bukkit.entity.Player;

public abstract class LibrarianTalent extends Talent {

    protected Response ERROR = new Response(null, Response.Type.ERROR);

    public LibrarianTalent(String name) {
        super(name);

        setAutoAdd(false);
        setAltUsage("You must use your grimoire to cast this spell!");
    }

    public abstract Response executeGrimoire(Player player);

    @Override
    public final Response execute(Player player) {
        if (Heroes.LIBRARIAN.getHero(Librarian.class).hasICD(player)) {
            return ERROR; // should never happen
        }

        final Response response = executeGrimoire(player);

        if (response.isOk()) {
            Heroes.LIBRARIAN.getHero(Librarian.class).removeSpellItems(player, this);
            Chat.sendMessage(player, "&aUsed %s!", this.getName());
        }

        return response;
    }

    @Override
    public void addDescription(String description, Object... format) {
        this.addDescription(Chat.format(description, format));
    }

    public void addDescription(String description) {
        super.addDescription(description.replace("<scaled>", formatValues("")));
    }

    public abstract double[] getValues();

    public abstract int getGrimoireCd();

    public String formatValues(String suffix) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < getValues().length; i++) {
            if (i != 0) {
                builder.append("&7/");
            }

            builder.append("&b").append(getCurrentValue(i)).append(suffix);
        }
        return builder.toString().trim();
    }

    public double getCurrentValue(int level) {
        return getValues()[Numbers.clamp(level, 0, 3)];
    }

    public double getCurrentValue(Player player) {
        return getCurrentValue(Heroes.LIBRARIAN.getHero(Librarian.class).getGrimoireLevel(player));
    }
}
