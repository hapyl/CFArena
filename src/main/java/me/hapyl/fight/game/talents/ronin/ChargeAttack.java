package me.hapyl.fight.game.talents.ronin;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.ronin.ChargeAttackData;
import me.hapyl.fight.game.heroes.ronin.RoninData;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class ChargeAttack extends Talent {

    @DisplayField public final short maxChargeTime = 30;
    @DisplayField public final short perfectAttackMin = maxChargeTime / 2 - (maxChargeTime / 10);
    @DisplayField public final short perfectAttackMax = maxChargeTime / 2 + (maxChargeTime / 10);

    @DisplayField private final double baseDamage = 10.0d;
    @DisplayField private final double perfectDamageIncrease = 1.75d;

    @DisplayField private final double cooldownIncreaseIfFailed = 2.5d;

    @DisplayField private final double radius = 2.5d;

    public ChargeAttack(@Nonnull Key key) {
        super(key, "Charge Attack");

        setDescription("""
                Raise your sword and &nprepare&7 to attack.
                
                Use &a&nagain&7 to slash with your sword, dealing &cdamage&7 in small &nAoE&7 in front of you.
                
                If used at the &ncorrect&7 time, the &cdamage&7 is &aincreased&7.
                &8&o;;If not used in time, the attack is cancelled, and the cooldown is increased.
                """);

        setType(TalentType.DAMAGE);
        setItem(Material.IRON_SWORD);

        setCooldownSec(7.5f);
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final RoninData data = player.getPlayerData(HeroRegistry.RONIN);

        if (data.chargeAttack != null) {
            final ChargeAttackData chargeAttack = data.chargeAttack;
            data.cancelChargeAttack(); // cancel after storing the attack

            final boolean isPerfect = chargeAttack.isPerfect();
            final double damage = isPerfect ? baseDamage * perfectDamageIncrease : baseDamage;
            final Location location = player.getLocationInFrontFromEyes(1.5d);

            Collect.nearbyEntities(location, radius, player::isNotSelfOrTeammate)
                    .forEach(entity -> {
                        entity.damage(damage, player, EnumDamageCause.RONIN_HIT);
                    });

            // Fx
            player.spawnWorldParticle(location, Particle.SWEEP_ATTACK, 1, 0, 0, 0, isPerfect ? 5 : 1);
            player.playWorldSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.75f);
            player.playWorldSound(location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.25f);

            if (isPerfect) {
                player.spawnWorldParticle(location, Particle.DUST_PLUME, 5, 0.5, 0.2, 0.5, 0.05f);
                player.playWorldSound(location, Sound.ENTITY_BREEZE_DEATH, 1.25f);
                player.playWorldSound(location, Sound.ENTITY_BREEZE_DEFLECT, 0.75f);
            }

            player.swingMainHand();
            return Response.OK;
        }

        data.chargeAttack = new ChargeAttackData(player, this);

        startCd(player, 2); // icd to prevent double click
        return Response.AWAIT;
    }

    public void failChargeAttack(@Nonnull GamePlayer player) {
        player.getPlayerData(HeroRegistry.RONIN).cancelChargeAttack();
        startCd(player, (int) (getCooldown() * cooldownIncreaseIfFailed));

        // Fx
        player.playSound(Sound.BLOCK_ANVIL_LAND, 0.75f);
    }

}
