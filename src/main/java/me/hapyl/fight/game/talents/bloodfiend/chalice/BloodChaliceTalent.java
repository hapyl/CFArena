package me.hapyl.fight.game.talents.bloodfiend.chalice;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.bloodfiend.taunt.TauntTalent;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class BloodChaliceTalent extends TauntTalent {

    @DisplayField(percentage = true) public final double healingPercent = 0.6d;

    public BloodChaliceTalent(@Nonnull DatabaseKey key) {
        super(key, "Blood Chalice", 6, -1);

        setType(TalentType.SUPPORT);
        setItem(Material.SKELETON_SKULL);
        setDurationSec(15);
        setCooldownSec(15);
    }

    @Nonnull
    @Override
    public String getDescription() {
        return """
                The chalice will convert &c{healingPercent}&7 of damage dealt into healing.
                &8&o;;Only against taunted entities.
                """;
    }

    @Nonnull
    @Override
    public String getHowToRemove() {
        return """
                &4Unremovable.
                """;
    }

    @Nonnull
    @Override
    public BloodChalice createTaunt(@Nonnull GamePlayer player, @Nonnull Location location) {
        return new BloodChalice(this, player, location);
    }

}
