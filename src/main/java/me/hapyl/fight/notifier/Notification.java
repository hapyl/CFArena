package me.hapyl.fight.notifier;

import com.google.common.collect.Lists;
import me.hapyl.fight.terminology.Term;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public enum Notification {

    READ_PATCH_NOTES(
            Notify.string("&aRead the latest patch notes here:"),
            Notify.link("&e&lCLICK HERE", "https://github.com/hapyl/CFArena/discussions/categories/patch-notes")
    ),

    HELP_COMMAND(
            "&aLost? See the help screen:",
            "&e/help"
    ),

    REPORT_BUGS(
            Notify.string("&aFound a bug? Report it here:"),
            Notify.link("&e&lCLICK HERE", "https://github.com/hapyl/CFArena/issues")
    ),

    TERM(
            "&aSeeing some unfamiliar phrase or",
            "&awonder what words like %s&a mean?".formatted(Term.TERM_STYLER.style("this")),
            "&aUse &e/term (query)&a to learn about a game term."
    ),
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
