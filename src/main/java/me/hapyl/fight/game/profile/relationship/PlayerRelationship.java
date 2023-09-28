package me.hapyl.fight.game.profile.relationship;

import me.hapyl.fight.game.profile.PlayerProfile;
import org.bukkit.entity.Player;

// stores relationship between players
public class PlayerRelationship {

    private final PlayerProfile profile;

    public PlayerRelationship(PlayerProfile profile) {
        this.profile = profile;
    }

    public Relationship getRelationship(Player player) {
        if (player == null) {
            return Relationship.UNSPECIFIED;
        }

        // TODO (hapyl): 009, Sep 9:
        return Relationship.NORMAL;
    }

}
