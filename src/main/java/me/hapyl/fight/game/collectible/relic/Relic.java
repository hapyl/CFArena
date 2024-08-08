package me.hapyl.fight.game.collectible.relic;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.game.challenge.ChallengeType;
import me.hapyl.fight.game.collectible.BlockFaceInt;
import me.hapyl.fight.game.maps.GameMaps;
import me.hapyl.fight.util.BlockLocation;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class Relic {

    private final Type type;
    private final BlockLocation location;
    private BlockFace blockFace;
    private GameMaps zone;
    private int id;

    public Relic(Type type, BlockLocation location) {
        this.type = type;
        this.zone = GameMaps.SPAWN;
        this.location = location;
        this.blockFace = BlockFace.NORTH_WEST;
        this.id = -1;
    }

    public Relic(Type type, int x, int y, int z) {
        this(type, new BlockLocation(x, y, z));
    }

    public Type getType() {
        return type;
    }

    public BlockLocation getLocation() {
        return location;
    }

    public GameMaps getZone() {
        return zone;
    }

    public Relic setZone(GameMaps zone) {
        this.zone = zone;
        return this;
    }

    public BlockFace getBlockFace() {
        return blockFace;
    }

    /**
     * @see #setBlockFace(BlockFace)
     * @deprecated magic numbers
     */
    @Deprecated
    public Relic setBlockFace(int rotation) {
        return setBlockFace(BlockFaceInt.get(rotation));
    }

    /**
     * Sets this relic direction.
     * <p>
     * <code>/skullblockface</code> in game to get the direction.
     *
     * @param blockFace - New block face.
     */
    public Relic setBlockFace(@Nonnull BlockFace blockFace) {
        this.blockFace = blockFace;
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean hasFound(Player player) {
        return PlayerDatabase.getDatabase(player).collectibleEntry.hasFound(this);
    }

    public void give(Player player) {
        if (hasFound(player)) {
            return;
        }

        PlayerDatabase.getDatabase(player).collectibleEntry.addFound(this);

        // Progress bond
        ChallengeType.COLLECT_RELIC.progress(player);

        // Fx
        Chat.sendMessage(player, "&d&l%s RELIC &aYou have found a relic!".formatted(getType()));
        PlayerLib.playSound(player, Sound.AMBIENT_CAVE, 2.0f);
    }

    @Override
    public String toString() {
        return "{Id=%s, Zone=%s, Loc=%s}".formatted(id, zone.name(), location);
    }
}
