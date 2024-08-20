package me.hapyl.fight.game.talents.aurora;

import me.hapyl.fight.database.key.DatabaseKey;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.aurora.AuroraData;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;

import javax.annotation.Nonnull;

public abstract class AuroraArrowTalent extends Talent {

    private final int maxArrows;
    private final ChatColor color;

    public AuroraArrowTalent(@Nonnull DatabaseKey key, @Nonnull String name, @Nonnull ChatColor color, int maxArrows) {
        super(key, name);

        this.maxArrows = maxArrows;
        this.color = color;

        setType(TalentType.SUPPORT);
    }

    public int getMaxArrows() {
        return maxArrows;
    }

    @Nonnull
    public ChatColor getColor() {
        return color;
    }

    @Nonnull
    public String getString(int arrows) {
        return (color + "➵").repeat(arrows) + "&8➵".repeat(maxArrows - arrows);
    }

    public void onShoot(@Nonnull GamePlayer player, @Nonnull Arrow arrow) {
    }

    public void onHit(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, @Nonnull DamageInstance instance) {
    }

    public void onTick(@Nonnull GamePlayer player, @Nonnull Arrow arrow, int tick) {
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final AuroraData data = HeroRegistry.AURORA.getPlayerData(player);

        data.setArrow(this);
        startCd(player, 10000);

        return Response.AWAIT;
    }
}
