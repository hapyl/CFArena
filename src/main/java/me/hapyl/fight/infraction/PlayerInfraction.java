package me.hapyl.fight.infraction;

import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class PlayerInfraction {

    private final PlayerProfile profile;
    private final Player player;

    public PlayerInfraction(PlayerProfile profile) {
        this.profile = profile;
        this.player = profile.getPlayer();
    }

    public boolean hasActive(InfractionType type) {
        return false;
    }

    @Nonnull
    public Infraction getActive(InfractionType type) {
        return new Infraction() {
            @Nonnull
            @Override
            public HexID getID() {
                return HexID.NULL;
            }

            @Override
            public long getTimestamp() {
                return 0;
            }

            @Override
            public long getDuration() {
                return 0;
            }
        };
    }

    public void inform(Infraction infraction) {
        Chat.sendCenterMessage(player, "&4&lINFRACTION!");
        Chat.sendCenterMessage(player, "&cYou are currently muted!");
    }

}
