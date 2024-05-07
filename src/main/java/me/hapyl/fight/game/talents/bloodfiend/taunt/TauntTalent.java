package me.hapyl.fight.game.talents.bloodfiend.taunt;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.bloodfield.Bloodfiend;
import me.hapyl.fight.game.heroes.bloodfield.BloodfiendData;
import me.hapyl.fight.game.talents.Talent;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public abstract class TauntTalent<T extends Taunt> extends Talent {

    protected final Map<GamePlayer, T> playerTaunt;

    public TauntTalent(@Nonnull String name) {
        super(name);

        this.playerTaunt = Maps.newConcurrentMap();

        addNlDescription("Toss a {name} nearby that will taunt the &bmost &brecently &cbitten&7 player for {duration}.");
        addDescription();

        addNlDescription("&a&lWhile Active:");
        addNlDescription(getDescription());

        addNlDescription("&c&lHow to Remove:");
        addNlDescription(getHowToRemove());
    }

    @Override
    public final void setDescription(@Nonnull String description, Object... format) {
        addDescription(description, format);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final GamePlayer mostRecentBitPlayer = getMostRecentBitPlayer(player);

        if (mostRecentBitPlayer == null) {
            return Response.error("No one to taunt!");
        }

        final T taunt = getTaunt(player);

        if (taunt != null) {
            taunt.remove();
            player.sendMessage("&aYour previous %s was removed!", taunt.getName());
        }

        final Location playerLocation = player.getLocation();
        final Location location = Taunt.pickRandomLocation(playerLocation);

        if (Math.abs(location.getY()) - Math.abs(playerLocation.getY()) > 5) {
            return Response.error("Could not find location to place the taunt!");
        }

        playerTaunt.put(player, createTaunt(player, mostRecentBitPlayer, location));
        startCd(player, 100000);

        return Response.AWAIT;
    }

    @Nullable
    public T getTaunt(@Nonnull GamePlayer player) {
        return playerTaunt.get(player);
    }

    @Nonnull
    public abstract T createTaunt(@Nonnull GamePlayer player, @Nonnull GamePlayer target, @Nonnull Location location);

    @Nonnull
    public abstract String getDescription();

    @Nonnull
    public abstract String getHowToRemove();

    @Nonnull
    public EnumDamageCause getDamageCause() {
        return EnumDamageCause.ENTITY_ATTACK;
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        final T taunt = playerTaunt.remove(player);

        if (taunt != null) {
            taunt.remove();
        }
    }

    @Override
    public void onStop() {
        playerTaunt.values().forEach(Taunt::remove);
        playerTaunt.clear();
    }

    public void removeTaunt(GamePlayer player) {
        playerTaunt.remove(player);
    }

    @Nullable
    protected GamePlayer getMostRecentBitPlayer(GamePlayer player) {
        final Bloodfiend bloodfiend = Heroes.BLOODFIEND.getHero(Bloodfiend.class);
        final BloodfiendData data = bloodfiend.getData(player);

        return data.getMostRecentBitPlayer();
    }
}
