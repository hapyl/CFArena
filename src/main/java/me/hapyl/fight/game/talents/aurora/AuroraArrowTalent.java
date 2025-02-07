package me.hapyl.fight.game.talents.aurora;


import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.aurora.AuroraData;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class AuroraArrowTalent extends Talent {

    private final int maxArrows;
    private final ChatColor color;

    @DisplayField private final double homingRadius;
    @DisplayField private final double homingStrength;

    public AuroraArrowTalent(@Nonnull Key key, @Nonnull String name, @Nonnull ChatColor color, int maxArrows, double homingRadius, double homingStrength) {
        super(key, name);

        this.maxArrows = maxArrows;
        this.color = color;
        this.homingRadius = homingRadius;
        this.homingStrength = homingStrength;

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

    public void onMove(@Nonnull GamePlayer player, @Nonnull Location location) {

    }

    @OverridingMethodsMustInvokeSuper
    public final void onTick(@Nonnull GamePlayer player, @Nonnull Arrow arrow, int tick) {
        // Home towards teammates
        final LivingGameEntity target = findHomingTarget(player, arrow.getLocation());
        final Location location = arrow.getLocation();

        if (tick % 2 == 0) {
            onMove(player, location);
        }

        if (target == null) {
            return;
        }

        final Vector vector = target.getLocation()
                .add(0, 1, 0)
                .toVector()
                .subtract(arrow.getLocation().toVector())
                .normalize()
                .multiply(homingStrength);

        arrow.setVelocity(vector);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final AuroraData data = HeroRegistry.AURORA.getPlayerData(player);

        if (data.hasBond()) {
            return Response.error("Unable to use right now!");
        }

        data.setArrow(this);
        startCdIndefinitely(player);

        return Response.AWAIT;
    }

    private LivingGameEntity findHomingTarget(GamePlayer player, Location location) {
        return Collect.nearestEntity(location, homingRadius, player::isTeammate);
    }
}
