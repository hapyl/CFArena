package me.hapyl.fight.anticheat;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.fight.database.async.AntiCheatAsynchronousDocument;
import me.hapyl.fight.infraction.HexID;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public enum AntiCheatCheck {

    CPS(5, FailAction.kick("You're clicking way too fast!")),

    ;

    private final int maxFails;
    private final FailAction failAction;

    AntiCheatCheck(int maxFails, FailAction failAction) {
        this.maxFails = maxFails;
        this.failAction = failAction;
    }

    public int getMaxFails() {
        return maxFails;
    }

    public void punish(Player player) {
        failAction.doPunish(player);
    }

    public enum PunishmentType {
        KICK,

        /**
         * @deprecated not implemented needs named ban system DB and shit -h
         */
        @Deprecated
        BAN
    }

    private static class FailAction {

        private final PunishmentType type;
        private final String message;

        private FailAction(PunishmentType type, String message) {
            this.type = type;
            this.message = message;
        }

        public final void doPunish(Player player) {
            final PunishmentReport report = new PunishmentReport(
                    HexID.random(),
                    player.getUniqueId(),
                    message,
                    System.currentTimeMillis()
            );

            punish(player, report);
            AntiCheatAsynchronousDocument.post(report);
        }

        public void punish(Player player, PunishmentReport report) {
            player.kickPlayer(Chat.format("""
                    %s
                    
                    &c&lYou have been kicked!
                    
                    &cReason:
                    &7&o%s
                    
                    &8Punishment Id: %s
                    """.formatted(
                    AntiCheat.PREFIX,
                    message,
                    report.getPunishmentId()
            )));
        }

        @Nonnull
        public static FailAction kick(@Nonnull String message) {
            return new FailAction(PunishmentType.KICK, message);
        }

        @Nonnull
        @Deprecated
        public static FailAction ban(@Nonnull String message) {
            throw new NotImplementedException();
        }

    }

}
