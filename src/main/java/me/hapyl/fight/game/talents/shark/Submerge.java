package me.hapyl.fight.game.talents.shark;

import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.util.Collect;
import org.bukkit.Location;

public class Submerge extends TimedGameTask {
    
    private final SubmergeTalent talent;
    private final GamePlayer player;
    
    public Submerge(SubmergeTalent talent, GamePlayer player) {
        super(talent);
        
        this.talent = talent;
        this.player = player;
        
        player.getAttributes().addModifier(
                talent.modifierSource, talent, modifier -> modifier
                        .of(AttributeType.HEIGHT, ModifierType.FLAT, -100)
                        .of(AttributeType.SPEED, ModifierType.FLAT, -100)
        );
        
        player.setInvulnerable(true);
        
        runTaskTimer(0, 1);
    }
    
    @Override
    public void onTaskStop() {
        player.setInvulnerable(false);
    }
    
    @Override
    public void run(int tick) {
        player.setVelocity(player.getDirection().normalize().multiply(talent.magnitude).setY(-1));
        
        final Location location = player.getLocation();
        
        Collect.nearbyEntities(location, talent.range, entity -> !entity.isSelfOrTeammate(player)).forEach(entity -> {
            entity.damage(talent.damage, player, DamageCause.FEET_ATTACK);
            entity.setVelocity(entity.getDirection().multiply(-0.75d).setY(0.33d));
        });
    }
}
