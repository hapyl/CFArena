package me.hapyl.fight.game.heroes.bloodfield;

import com.google.common.collect.Sets;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.task.TickingGameTask;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.Set;

public class Impel extends TickingGameTask {
    
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");
    
    protected final Set<GamePlayer> targets;
    
    private final ImpelInstance instance;
    private final ImpelType type;
    
    public Impel(@Nonnull ImpelInstance instance, @Nonnull ImpelType type) {
        this.instance = instance;
        this.targets = Sets.newHashSet(instance.targets);
        this.type = type;
        
        // Fx
        this.targets.forEach(player -> {
            player.playSound(Sound.ENTITY_BAT_TAKEOFF, 0.0f);
            player.playSound(Sound.ENTITY_BAT_HURT, 0.0f);
        });
    }
    
    public void onFail(@Nonnull GamePlayer player) {
        final double damage = instance.data.bloodfiend.getUltimate().impelDamage;
        
        player.damage(damage, instance.player, DamageCause.IMPEL);
        player.triggerDebuff(instance.player);
        
        player.sendMessage("&6&l🦇 &eFailed to obey %s's command! &c-%.0f &c❤".formatted(
                instance.player.toString(),
                damage
        ));
        
        player.sendSubtitle("&eImpel: &4&lFAILED! &c-%s ❤".formatted((int) damage), 0, 20, 5);
        
        player.playSound(Sound.ENTITY_BLAZE_HURT, 0.25f);
        player.playSound(Sound.ENTITY_ZOMBIE_HURT, 0.25f);
        player.playSound(Sound.ENTITY_PLAYER_BREATH, 1.0f);
    }
    
    public void onImpelStop() {
    }
    
    public void onComplete(@Nonnull GamePlayer player) {
        player.sendSubtitle("&eImpel: &a&l✔", 0, 20, 5);
        player.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.75f);
    }
    
    public void complete(@Nonnull GamePlayer player, @Nonnull ImpelType type) {
        if (this.type != type || !targets.contains(player)) {
            return;
        }
        
        targets.remove(player);
        onComplete(player);
    }
    
    @Override
    public void onTaskStop() {
        targets.clear();
    }
    
    @Override
    public void run(int tick) {
        final Bloodfiend.BloodfiendUltimate ultimate = instance.data.bloodfiend.getUltimate();
        
        // Remove death or respawning players
        instance.targets.removeIf(GamePlayer::isDeadOrRespawning);
        targets.removeIf(GamePlayer::isDeadOrRespawning);
        
        final String timeLeft = decimalFormat.format((ultimate.impelDuration - tick) / 20d);
        
        targets.forEach(player -> {
            player.sendSubtitle("&eImpel: &b&l%s &c%ss".formatted(type.getName(), timeLeft), 0, 5, 0);
        });
        
        if (tick > ultimate.impelDuration) {
            // Fail players
            targets.forEach(this::onFail);
            
            onImpelStop();
            cancel();
        }
    }
    
}
