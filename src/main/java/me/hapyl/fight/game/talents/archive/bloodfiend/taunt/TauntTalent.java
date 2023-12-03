package me.hapyl.fight.game.talents.archive.bloodfiend.taunt;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.bloodfield.Bloodfiend;
import me.hapyl.fight.game.heroes.archive.bloodfield.BloodfiendData;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public abstract class TauntTalent<T extends Taunt> extends Talent {

    protected final Map<Player, T> playerTaunt;

    public TauntTalent(@Nonnull String name) {
        super(name);

        this.playerTaunt = Maps.newConcurrentMap();

        addNlDescription("Toss a {name} nearby that will taunt the &bmost &brecently &cbitten&7 player for {duration}.");
        addDescription();

        addNlDescription("&a&lWhile Active:");
        addNlDescription(getDescription());

        addNlDescription("&c&lHow to Remove:");
        addNlDescription(getHowToRemove());

        addNlDescription("&4&lIf not Removed:");
        addDescription("""
                Explodes, dealing &c%s &c❤ &7to the &btaunted&7 player.
                                
                If &btaunted&7 player dies, the {name} will be removed.
                """, getExplosionDamage());
    }

    @Override
    public final void setDescription(@Nonnull String description, Object... format) {
        addDescription(description, format);
    }

    @Override
    public Response execute(Player player) {
        final GamePlayer mostRecentBitPlayer = getMostRecentBitPlayer(player);

        if (mostRecentBitPlayer == null) {
            return Response.error("No one to taunt!");
        }

        final T taunt = getTaunt(player);

        if (taunt != null) {
            taunt.remove();
            Chat.sendMessage(player, "&aYour previous %s was removed!", taunt.getName());
        }

        playerTaunt.put(player, createTaunt(player, mostRecentBitPlayer));
        return Response.OK;
    }

    @Nullable
    public T getTaunt(Player player) {
        return playerTaunt.get(player);
    }

    public abstract T createTaunt(Player player, GamePlayer target);

    @Nonnull
    public abstract String getDescription();

    @Nonnull
    public abstract String getHowToRemove();

    public double getExplosionDamage() {
        return 100.0d;
    }

    @Nonnull
    public EnumDamageCause getDamageCause() {
        return EnumDamageCause.ENTITY_ATTACK;
    }

    @Override
    public void onDeath(Player player) {
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

    public void removeTaunt(Player player) {
        playerTaunt.remove(player);
    }

    @Nullable
    protected GamePlayer getMostRecentBitPlayer(Player player) {
        final Bloodfiend bloodfiend = Heroes.BLOODFIEND.getHero(Bloodfiend.class);
        final BloodfiendData data = bloodfiend.getData(player);

        return data.getMostRecentBitPlayer();
    }
}
