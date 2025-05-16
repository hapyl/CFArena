package me.hapyl.fight.game.heroes.engineer;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.attribute.BaseAttributes;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.EntityEffect;
import org.bukkit.Particle;
import org.bukkit.Sound;

public class MechaIndustries extends TimedGameTask implements Removable {

    private final GamePlayer player;
    private final Engineer engineer;
    private final LivingGameEntity golem;

    public MechaIndustries(GamePlayer player, Engineer engineer) {
        super(engineer.getUltimate());

        this.player = player;
        this.engineer = engineer;
        this.golem = CF.createEntity(
                player.getLocation(), Entities.IRON_GOLEM, self -> {
                    self.setPlayerCreated(true);
                    self.setAI(false);

                    final BaseAttributes attributes = new BaseAttributes();
                    final Engineer.EngineerUltimate ultimate = engineer.getUltimate();

                    attributes.setMaxHealth(ultimate.mechaHealth);
                    attributes.setDefense(ultimate.mechaDefense);
                    attributes.setHeight(150);

                    player.hideEntity(self);
                    player.setInvulnerable(true);

                    final LivingGameEntity entity = new LivingGameEntity(self, attributes);
                    entity.setValidState(true);
                    
                    entity.setImmune(DamageCause.SUFFOCATION);
                    entity.setInformImmune(false);

                    return entity;
                }
        );

        player.getTeam().addEntry(golem.getEntry());
        player.addEffect(EffectType.INVISIBLE, 100000);

        // Fx
        player.spawnWorldParticle(Particle.EXPLOSION_EMITTER, 1);

        runTaskTimer(0, 1);
    }

    @Override
    public void remove() {
        cancel();

        golem.remove();
        
        player.setInvulnerable(false);
        player.removeEffect(EffectType.INVISIBLE);

        // Fx
        player.playWorldSound(Sound.ENTITY_IRON_GOLEM_DEATH, 0.75f);
        player.spawnWorldParticle(Particle.EXPLOSION_EMITTER, 1);
    }

    @Override
    public void run(int tick) {
        if (golem.isDead()) {
            engineer.getPlayerData(player).removeMechaIndustries();
            return;
        }

        if (modulo(10) && golem.isInWater()) {
            golem.damage(engineer.ultimateInWaterDamage);
        }

        golem.teleport(player);

        // Fx
        player.spawnWorldParticle(Particle.HAPPY_VILLAGER, 1, 0.8, 0.8, 0.8, 0);
    }

    @Override
    public String toString() {
        return "&f&l🤖 %s &b%s".formatted(golem.getHealthFormatted(), CFUtils.formatTick(maxTick - getTick()));
    }

    public void swing() {
        golem.getEntity().playEffect(EntityEffect.ENTITY_ATTACK);
    }
}
