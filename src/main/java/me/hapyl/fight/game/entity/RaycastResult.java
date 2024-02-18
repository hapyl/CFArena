package me.hapyl.fight.game.entity;

import me.hapyl.fight.CF;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nullable;

/**
 * Represents a {@link Raycast} result.
 * <p>
 * A rey cast result includes a hit {@link Block} and/or a hit {@link LivingEntity}.
 */
public class RaycastResult {

    private Block hitBlock;
    private LivingEntity hitEntity;

    RaycastResult() {
    }

    /**
     * Gets the hit {@link Block} or null if didn't hit anything.
     *
     * @return the hit block or null.
     */
    @Nullable
    public Block getHitBlock() {
        return hitBlock;
    }

    /**
     * Gets the hit {@link LivingEntity} or null if didn't hit anything.
     *
     * @return the hit entity or null.
     */
    @Nullable
    public LivingEntity getHitEntity() {
        return hitEntity;
    }

    /**
     * Gets the hit {@link LivingGameEntity} or null if didn't hit anything.
     *
     * @return the hit game entity or null.
     */
    @Nullable
    public LivingGameEntity getHitGameEntity() {
        return CF.getEntity(hitEntity);
    }

    /**
     * Returns true if both {@link Block} and {@link LivingEntity} were hit; false otherwise.
     *
     * @return true if both block and entity were hit; false otherwise.
     */
    public boolean isHitBoth() {
        return hitBlock != null && hitEntity != null;
    }

    /**
     * Returns true if either {@link Block} or {@link LivingEntity} were hit; false otherwise.
     *
     * @return true if either block or entity were hit; false otherwise.
     */
    public boolean isHitEither() {
        return hitBlock != null || hitEntity != null;
    }

    /**
     * Returns true if a {@link Block} was hit; false otherwise.
     *
     * @return true if a block was hit; false otherwise.
     */
    public boolean isHitBlock() {
        return hitBlock != null;
    }

    protected void setHitBlock(Block block) {
        if (hitBlock == null) {
            hitBlock = block;
        }
    }

    /**
     * Returns true if a {@link LivingEntity} was hit; false otherwise.
     *
     * @return true if an entity was hit; false otherwise.
     */
    public boolean isHitEntity() {
        return hitEntity != null;
    }

    protected void setHitEntity(LivingEntity entity) {
        if (hitEntity == null) {
            hitEntity = entity;
        }
    }
}
