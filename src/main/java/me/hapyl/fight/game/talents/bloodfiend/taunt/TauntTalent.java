package me.hapyl.fight.game.talents.bloodfiend.taunt;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public abstract class TauntTalent extends Talent {

    protected final PlayerMap<Taunt> playerTaunt;

    @DisplayField(suffix = "blocks") private final double radius;
    @DisplayField private final int period; // -1 means it's passive

    public TauntTalent(@Nonnull String name, double radius, int period) {
        super(name);

        this.playerTaunt = PlayerMap.newConcurrentMap();
        this.radius = radius;
        this.period = period;

        addNlDescription(
                "Toss a {name} nearby that will taunt &b&nall&c bitten&7 enemies within &6%.0f blocks&7 radius for {duration}."
                        .formatted(radius)
        );
        addDescription();

        addNlDescription("&a&lWhile Active:");
        addNlDescription(getDescription());

        addNlDescription("&c&lHow to Remove:");
        addNlDescription(getHowToRemove());
    }

    public double getRadius() {
        return radius;
    }

    public int getPeriod() {
        return period;
    }

    @Override
    public final void setDescription(@Nonnull String description, Object... format) {
        addDescription(description, format);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Taunt taunt = playerTaunt.remove(player);

        if (taunt != null) {
            taunt.remove();
            player.sendMessage("&eYour previous %s&e was removed!", taunt.getName());
        }

        final Location playerLocation = player.getLocation();
        final Location location = Taunt.pickRandomLocation(playerLocation);

        if (Math.abs(location.getY()) - Math.abs(playerLocation.getY()) > 5) {
            return Response.error("Could not find location to place the taunt!");
        }

        playerTaunt.put(player, createTaunt(player, location));
        startCd(player, 100000);

        return Response.AWAIT;
    }

    @Nullable
    public Taunt getTaunt(@Nonnull GamePlayer player) {
        return playerTaunt.get(player);
    }

    @Nonnull
    public Set<Taunt> getTaunts() {
        return Sets.newHashSet(playerTaunt.values());
    }

    @Nonnull
    public <T extends Taunt> Set<T> getTaunts(Class<T> as) {
        return CFUtils.fetchValues(playerTaunt, as);
    }

    @Nonnull
    public abstract Taunt createTaunt(@Nonnull GamePlayer player, @Nonnull Location location);

    @Nonnull
    public abstract String getDescription();

    @Nonnull
    public abstract String getHowToRemove();

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        final Taunt taunt = playerTaunt.remove(player);

        if (taunt != null) {
            taunt.remove();
        }
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        playerTaunt.values().forEach(Taunt::remove);
        playerTaunt.clear();
    }

    public void removeTaunt(GamePlayer player) {
        playerTaunt.remove(player);
    }

}
