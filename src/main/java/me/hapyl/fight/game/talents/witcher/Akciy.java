package me.hapyl.fight.game.talents.witcher;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.event.custom.TalentPreconditionEvent;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.Collect;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class Akciy extends Talent implements Listener {

    protected final Map<LivingGameEntity, AxiiData> axiiDatamap = Maps.newHashMap();

    public Akciy(@Nonnull Key key) {
        super(key, "Axii");

        setDescription("""
                Stun the &etarget&7 &cenemy for {duration} or until they &nget&7 hit.
                """
        );

        setType(TalentType.IMPAIR);
        setMaterial(Material.SLIME_BALL);
        setDuration(100);
        setCooldownSec(40);
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        final AxiiData data = axiiDatamap.remove(player);

        if (data != null) {
            data.cancel();
        }
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        axiiDatamap.values().forEach(AxiiData::cancel);
        axiiDatamap.clear();
    }

    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final LivingGameEntity target = Collect.targetEntityDot(player, 50.0d, 0.8d, entity -> !player.isSelfOrTeammate(entity));

        if (target == null) {
            return Response.error("No valid target!");
        }

        if (target.hasEffectResistanceAndNotify(player)) {
            player.sendMessage("&a%s has resisted your %s!".formatted(target.getName(), getName()));
            return Response.OK;
        }

        stun(target, player, getDuration());

        target.sendMessage("&c%s stunned you!".formatted(player.getName()));
        player.sendMessage("&aStunned %s!".formatted(target.getName()));
        return Response.OK;
    }

    @EventHandler()
    public void handleGameDamageEvent(GameDamageEvent.Process ev) {
        final LivingGameEntity entity = ev.getEntity();
        final GameEntity damager = ev.getDamager();

        // If stunned then unstun
        if (axiiDatamap.containsKey(entity)) {
            unStun(entity);
            return;
        }

        // Prevent damaging while stunned
        if (damager instanceof LivingGameEntity livingDamager && axiiDatamap.containsKey(livingDamager)) {
            ev.setCancelled(true);
        }
    }

    @EventHandler()
    public void handleTalentUse(TalentPreconditionEvent ev) {
        if (!axiiDatamap.containsKey(ev.getPlayer())) {
            return;
        }

        ev.setCancelled(true, "Stunned!");
    }

    @EventHandler()
    public void handleBowShoot(EntityShootBowEvent ev) {
        final LivingGameEntity entity = CF.getEntity(ev.getEntity());

        if (entity == null) {
            return;
        }

        if (!isStunned(entity)) {
            return;
        }

        ev.setCancelled(true);
    }

    public boolean isStunned(@Nonnull LivingGameEntity entity) {
        return axiiDatamap.containsKey(entity);
    }

    public void stun(@Nonnull LivingGameEntity entity, @Nonnull GamePlayer stunner, int duration) {
        unStun(entity);
        axiiDatamap.put(entity, new AxiiData(this, entity, stunner, duration));
    }

    private void unStun(LivingGameEntity entity) {
        final AxiiData data = axiiDatamap.remove(entity);

        if (data == null) {
            return;
        }

        data.cancel();
    }

}
