package me.hapyl.fight.game.talents.engineer;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EngineerRecall extends Talent {
    private final int cdIfNoConst = 5 * 20;

    @DisplayField(percentage = true) private final double regainPercent = 0.5d;

    public EngineerRecall(@Nonnull Key key) {
        super(key, "Recall");

        setDescription("""
                Destroy the &ncurrent&7 Construct and regain {regainPercent} of its total cost.
                """);

        setType(TalentType.ENHANCE);
        setMaterial(Material.IRON_PICKAXE);

        setCooldownSec(15);
    }

    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        return Response.OK;
    }
}
