package me.hapyl.fight.game.collectible.relic;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.custom.RelicFindEvent;
import me.hapyl.fight.game.challenge.ChallengeType;
import me.hapyl.fight.game.collectible.BlockFaceInt;
import me.hapyl.fight.game.maps.EnumLevel;
import me.hapyl.fight.util.BlockLocation;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class Relic {

    private final Type type;
    private final BlockLocation location;

    private BlockFace blockFace;
    private EnumLevel zone;
    private int id;

    public Relic(@Nonnull Type type, @Nonnull BlockLocation location) {
        this.type = type;
        this.zone = EnumLevel.SPAWN;
        this.location = location;
        this.blockFace = BlockFace.NORTH_WEST;
        this.id = -1;
    }

    public Relic(@Nonnull Type type, int x, int y, int z) {
        this(type, new BlockLocation(x, y, z));
    }

    @Nonnull
    public Type getType() {
        return type;
    }

    @Nonnull
    public BlockLocation getLocation() {
        return location;
    }

    @Nonnull
    public EnumLevel getZone() {
        return zone;
    }

    public Relic setZone(@Nonnull EnumLevel zone) {
        this.zone = zone;
        return this;
    }

    @Nonnull
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

    public final void setId(int id) {
        if (this.id != -1) {
            throw new IllegalStateException("Cannot reassign relic id!");
        }

        this.id = id;
    }

    public void take(@Nonnull Player player) {
        CF.getDatabase(player).collectibleEntry.removeFound(this);
    }

    public boolean hasFound(@Nonnull Player player) {
        return CF.getDatabase(player).collectibleEntry.hasFound(this);
    }

    public void give(@Nonnull Player player) {
        if (hasFound(player)) {
            return;
        }

        if (!new RelicFindEvent(player, this).callEvent()) {
            return;
        }

        CF.getDatabase(player).collectibleEntry.addFound(this);

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
