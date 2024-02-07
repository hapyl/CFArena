package me.hapyl.fight.event;

import me.hapyl.fight.CF;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.chat.Gradient;
import me.hapyl.spigotutils.module.chat.gradient.Interpolator;
import me.hapyl.spigotutils.module.chat.gradient.Interpolators;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;

// handles server-related events
public class ServerHandler implements Listener {

    private static final WeekDayGradient[] gradients =
            {
                    new WeekDayGradient(new Color(44, 62, 80), new Color(52, 152, 219)),
                    new WeekDayGradient(new Color(0, 128, 128), new Color(0, 255, 255)),
                    new WeekDayGradient(new Color(255, 138, 101), new Color(255, 223, 186)),
                    new WeekDayGradient(new Color(93, 109, 126), new Color(39, 60, 117)),
                    new WeekDayGradient(new Color(255, 94, 77), new Color(255, 175, 84)),
                    new WeekDayGradient(new Color(173, 216, 230), new Color(240, 248, 255)),
                    new WeekDayGradient(new Color(255, 184, 77), new Color(255, 94, 77)),
            };

    private String[] serverMessageOfTheDay;

    @EventHandler()
    public void handleServerList(ServerListPingEvent ev) {
        if (serverMessageOfTheDay == null) {

            serverMessageOfTheDay = new String[] {
                    // Header
                    Chat.format("                    &6&l%s            &8v%s".formatted(
                            "CLASSES FIGHT",
                            CF.getVersionNoSnapshot()
                    )),

                    // Footer
                    centerText("&f&m●-●&7( %s &7)&f&m●-●&r".formatted(CF.getVersionTopic()))
            };
        }

        ev.setMaxPlayers(Bukkit.getOnlinePlayers().size() + 1);
        ev.setMotd(serverMessageOfTheDay[0] + "\n" + serverMessageOfTheDay[1]);
    }

    public String centerText(String text) {
        return centerText(text, 50);
    }

    /**
     * <a href="https://www.spigotmc.org/threads/center-motds-and-messages.354209/">Author.</a>
     */
    public String centerText(String text, int maxLength) {
        char[] chars = text.toCharArray();
        boolean isBold = false;
        double length = 0;
        ChatColor pholder = null;

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '&' && chars.length != (i + 1) && (pholder = ChatColor.getByChar(chars[i + 1])) != null) {
                if (pholder != ChatColor.UNDERLINE && pholder != ChatColor.ITALIC
                        && pholder != ChatColor.STRIKETHROUGH && pholder != ChatColor.MAGIC) {
                    isBold = (chars[i + 1] == 'l');
                    length--;
                    i += isBold ? 1 : 0;
                }
            }
            else {
                length++;
                length += (isBold ? (chars[i] != ' ' ? 0.1555555555555556 : 0) : 0);
            }
        }

        double spaces = (maxLength - length) / 2;

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < spaces; i++) {
            builder.append(' ');
        }

        String copy = builder.toString();
        builder.append(text).append(copy);

        return Chat.format(builder.toString());
    }

    @Nonnull
    public static WeekDayGradient getTodayGradient() {
        final LocalDate now = LocalDate.now();
        final DayOfWeek dayOfWeek = now.getDayOfWeek();

        return getGradient(dayOfWeek);
    }

    @Nonnull
    public static WeekDayGradient getGradient(DayOfWeek day) {
        return gradients[day.ordinal()];
    }

    public enum Target {
        CHAT(80),
        MESSAGE_OF_THE_DAY(45);

        private final int length;

        Target(int length) {
            this.length = length;
        }
    }

    public record WeekDayGradient(Color from, Color to, Interpolator interpolator) {

        public WeekDayGradient(Color from, Color to) {
            this(from, to, Interpolators.QUADRATIC_FAST_TO_SLOW);
        }

        @Nonnull
        public String colorString(@Nonnull String string) {
            return new Gradient(string).makeBold().rgb(from, to, interpolator);
        }
    }

}
