package me.hapyl.fight.game;

import me.hapyl.fight.Main;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TitleAnimation {

    public TitleAnimation() {
        sendTitle("&e&lTHE FIGHT BEGINS");

        new BukkitRunnable() {

            private int frame = 0;

            @Override
            public void run() {

                switch (frame++) {
                    case 10 -> sendTitle("&c&lT&e&lHE FIGHT BEGINS");
                    case 12 -> sendTitle("&6&lT&c&lH&e&lE FIGHT BEGINS");
                    case 14 -> sendTitle("&e&lT&6&lH&c&lE &e&lFIGHT BEGINS");
                    case 16 -> sendTitle("&e&lTH&6&lE &c&lF&e&lIGHT BEGINS");
                    case 18 -> sendTitle("&e&lTHE &6&lF&c&lI&e&lGHT BEGINS");
                    case 20 -> sendTitle("&e&lTHE F&6&lI&c&lG&e&lHT BEGINS");
                    case 22 -> sendTitle("&e&lTHE FI&6&lG&c&lH&e&lT BEGINS");
                    case 24 -> sendTitle("&e&lTHE FIG&6&lH&c&lT &e&lBEGINS");
                    case 26 -> sendTitle("&e&lTHE FIGH&6&lT &c&lB&e&lEGINS");
                    case 28 -> sendTitle("&e&lTHE FIGHT &6&lB&c&lE&e&lGINS");
                    case 30 -> sendTitle("&e&lTHE FIGHT B&6&lE&c&lG&e&lINS");
                    case 32 -> sendTitle("&e&lTHE FIGHT BE&6&lG&c&lI&e&lNS");
                    case 34 -> sendTitle("&e&lTHE FIGHT BEG&6&lI&c&lN&e&lS");
                    case 36 -> sendTitle("&e&lTHE FIGHT BEGI&6&lN&c&lS");
                    case 38 -> sendTitle("&e&lTHE FIGHT BEGIN&6&lS");
                    case 40 -> sendTitle("&e&lTHE FIGHT BEGINS");
                    case 43 -> sendTitle("&c&lTHE FIGHT BEGINS");
                    case 46 -> sendTitle("&6&lTHE FIGHT BEGINS");
                    case 49 -> sendTitle("&c&lTHE FIGHT BEGINS");
                    case 52 -> sendTitle("&6&lTHE FIGHT BEGINS");
                    case 55 -> sendTitle("&c&lTHE FIGHT BEGINS");
                    case 58 -> {
                        sendTitle("&e&lTHE FIGHT BEGINS");
                        cancel();
                    }
                }
            }

        }.runTaskTimer(Main.getPlugin(), 0, 1);
    }

    public void sendTitle(String string) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Chat.sendTitle(player, "&c☠", string, 0, 20, 10);
        }
    }

}
