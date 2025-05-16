package me.hapyl.fight.game.talents.inferno;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.terminology.EnumTerm;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class FirePitTalent extends Talent {

    protected final int[][] firePitsSpawnOffsets = {
            { 0, 0 },
            { 3, 3 },
            { 3, -3 },
            { -3, 3 },
            { -3, -3 }
    };

    protected final int[][] firePitsOffsets = {
            { 0, 0 },
            { 1, 0 },
            { 0, 1 },
            { -1, 0 },
            { 0, -1 }
    };

    protected final BlockData[] firePitsMaterials = {
            Material.BLACK_TERRACOTTA.createBlockData(),
            Material.BROWN_TERRACOTTA.createBlockData(),
            Material.ORANGE_TERRACOTTA.createBlockData(),
            Material.RED_TERRACOTTA.createBlockData()
    };

    @DisplayField protected final short pitCount = 5;
    @DisplayField protected final int transformDelay = 20;
    @DisplayField(percentage = true) protected final double damage = 2d;

    private final Set<FirePitHandler> handlerList = Sets.newHashSet();

    public FirePitTalent(@Nonnull Key key) {
        super(key, "Fire Pit");

        setDescription("""
                Summon &6{pitCount}&7 fire pits around you which transform to soul fire after &b{transformDelay}&7.
                
                Stepping in fire deals &4{damage}&7 of &cenemy&7's %s as %s.
                """.formatted(AttributeType.MAX_HEALTH, EnumTerm.TRUE_DAMAGE));

        setMaterial(Material.FIRE_CHARGE);
        setType(TalentType.DAMAGE);

        setDurationSec(2.5f);
        setCooldownSec(20.0f);
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        CFUtils.clearCollectionAnd(handlerList, FirePitHandler::extinguish);
    }

    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        handlerList.add(new FirePitHandler(player, this));
        return Response.OK;
    }

}
