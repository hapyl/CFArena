package me.hapyl.fight.game.talents.himari;

import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.registry.Key;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class SpikeBarrier extends HimariTalent implements Listener {
    public SpikeBarrier(@Nonnull Key key) {
        super(key,"Spike Barrier");
        setDescription("""
                &8;;This Talent is unlocked only if you roll it out from Lucky Day.
                
                Creates a spike barrier around Himari, absorbing 50 percent of incoming damage.
                Additionally, it reflects 50 percent of blocked damage back to the sender.
                Shield is disabled after 20 seconds of usage.
                """);

        setItem(Material.SHIELD);
        setDurationSec(20);
        setCooldownSec(99999); //since not unlocked yet
        setType(TalentType.DEFENSE);
    }

    @EventHandler
    public void handleGameDamageEvent(GameDamageEvent ev) {
        Debug.info("Spike shield parameters setup");

        // Get the target player
        GamePlayer targetPlayer = (GamePlayer) ev.getEntity();
        final GameEntity damager = ev.getDamager();
        final double damage = ev.getDamage();
        Debug.info("Damage and damager fetched successfully!");

        // Absorb half the damage
        double reducedDamage = damage * 0.5;
        ev.multiplyDamage(0.5d);
        Debug.info("Damage reduced by 50%.");

        // Reflect half of the reduced damage back to the attacker
        if (damager instanceof LivingGameEntity) {
            LivingGameEntity dealer = (LivingGameEntity) damager;
            dealer.damage(reducedDamage, targetPlayer, EnumDamageCause.SPIKE_SHIELD);
            Debug.info("Spike Shield - Reflected " + reducedDamage + " damage back to attacker.");
        }
    }

    @Override
    public @Nullable Response executeHimari(@NotNull GamePlayer player) {
        Debug.info("Executing Spike Barrier for player: " + player.getName());

        // Mock an incoming damage event to simulate Spike Barrier activation
        DamageInstance damageInstance = new DamageInstance(player, 5);
        GameDamageEvent mockEvent = new GameDamageEvent(damageInstance);

      //  handleGameDamageEvent(mockEvent, player);

        // Start the cooldown after activation
        setCooldown(9999);
        Debug.info("Spike Barrier activated for 20 seconds.");

        return Response.ok();
    } //FIXME: Stuff above doesn't work at all, look into it when you can.
}
