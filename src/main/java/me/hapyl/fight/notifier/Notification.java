package me.hapyl.fight.notifier;

import com.google.common.collect.Lists;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public enum Notification {

    READ_PATCH_NOTES(
            Notify.string("&aRead the latest patch notes here:"),
            Notify.link("&e&lCLICK HERE", "https://hapyl.github.io/patch_notes/classes_fight")
    ),

    HELP_COMMAND("&aLost? See the help screen:", "&e/help"),
    TRIAL("&aTry out the new Trial mode!", "&e/trial &c(it's broken wasn't me (real))"),
    ;

    private final List<Notify> notifies;

    Notification(String... strings) {
        this();

        for (String string : strings) {
            this.notifies.add(Notify.string(string));
        }
    }

    Notification(Notify... string) {
        this();

        Collections.addAll(this.notifies, string);
    }

    Notification() {
        this.notifies = Lists.newArrayList();
    }

    public void forEachNotifier(Player player) {
        this.notifies.forEach(notify -> notify.sendString(player));
    }

    public List<Notify> getNotifies() {
        return notifies;
    }
}
