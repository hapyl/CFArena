package me.hapyl.fight.game.talents.swooper;

import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlastPack extends ChargedTalent {

    @DisplayField public final int maxAirTime = 300;
    @DisplayField public final int maxLifeTime = 100;
    @DisplayField(suffix = "blocks") public final double explosionRadius = 4.0d;
    @DisplayField public final int stunDuration = 40;
    @DisplayField public final double selfMagnitude = 1.5d;
    @DisplayField public final double otherMagnitude = 0.5d;
    @DisplayField public final double damage = 2.0d;

    protected final PlayerMap<BlastPackEntity> blastPacks = PlayerMap.newMap();

    public BlastPack() {
        super("Blast Pack", """
                Throw an explosive &eC4&7 in front of you that &nsticks&7 to surfaces.
                                
                &nUse&7 &nagain&7 to &4explode&7, damaging &cenemies&7 and moving all &bentities&7.
                """, 2);

        setType(TalentType.MOVEMENT);
        setItem(Material.DETECTOR_RAIL);
        setCooldown(3);
        setRechargeTimeSec(8);
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        super.onStop(instance);

        blastPacks.forEachAndClear(BlastPackEntity::cancel);
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        super.onDeath(player);

        blastPacks.removeAnd(player, BlastPackEntity::cancel);
    }

    @Nullable
    public BlastPackEntity getBlastPack(GamePlayer player) {
        return blastPacks.get(player);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final BlastPackEntity blastPack = blastPacks.remove(player);

        // Explode blast pack
        if (blastPack != null) {
            blastPack.explode();
            return Response.OK;
        }

        // Throw blast pack
        blastPacks.put(player, new BlastPackEntity(this, player));

        player.playWorldSound(Sound.ENTITY_SLIME_JUMP, 0.75f);
        return Response.AWAIT;
    }

}
