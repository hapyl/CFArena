package me.hapyl.fight.game.talents.bounty_hunter;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.effect.EffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.bounty_hunter.BountyHunterData;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SmokeBombTalent extends Talent {
    
    @DisplayField(percentage = true) public final double healthThreshold = 0.5;
    
    @DisplayField private final double radius = 3.0;
    @DisplayField private final double smokeRadius = 3.0d;
    
    @DisplayField private final double speedIncrease = 50;
    @DisplayField private final int speedIncreaseDuration = Tick.fromSeconds(3);
    
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("smoke_bomb"));
    
    public SmokeBombTalent(@Nonnull Key key) {
        super(key, "Smoke Bomb");
        
        setDescription("""
                       &f;;This talent can only be used once after your health falls below {healthThreshold} &c❤&f.
                       
                       Throw a smoke bomb that creates a field of smoke that &3blinds&7 everyone inside it.
                       
                       You also get a small %s boost.
                       
                       &8&o;;`It's called tactical retreat!`
                       """.formatted(AttributeType.SPEED)
        );
        
        setType(TalentType.SUPPORT);
        setTexture("984a68fd7b628d309667db7a55855b54abc23f3595bbe43216211be5fe57014");
        
        setCooldownSec(Constants.INDEFINITE_COOLDOWN);
        setDurationSec(5);
    }
    
    @Override
    public void onStart(@Nonnull GamePlayer player) {
        startCooldown(player, Constants.INDEFINITE_COOLDOWN);
    }
    
    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final BountyHunterData data = HeroRegistry.BOUNTY_HUNTER.getPlayerData(player);
        
        final double health = player.getHealth();
        final double maxHealth = player.getMaxHealth();
        
        if (data.hasUsedSmokeBomb) {
            return Response.error("Already used smoke bomb!");
        }
        
        if (health > maxHealth * healthThreshold) {
            return Response.error("Your health is above %.0f%%!".formatted(healthThreshold * 100));
        }
        
        data.hasUsedSmokeBomb = true;
        createSmoke(player);
        
        return Response.OK;
    }
    
    @Override
    public boolean isOnCooldown(@Nonnull GamePlayer player) {
        // Never has cooldown, special ability just like all of bh abilities what the fuck is she so special or something
        return false;
    }
    
    public void trigger(@Nonnull GamePlayer player) {
        stopCooldown(player);
        
        // Fx
        player.sendTitle("&8\uD83D\uDCA3&7&l\uD83D\uDCA3&8\uD83D\uDCA3", "&aꜱᴍᴏᴋᴇ ʙᴏᴍʙ ᴛʀɪɢɢᴇʀᴇᴅ!", 5, 15, 5);
        
        player.playSound(Sound.ENTITY_CREEPER_PRIMED, 1.25f);
        player.playSound(Sound.ENTITY_CREEPER_HURT, 0.75f);
        player.playSound(Sound.ENTITY_ELDER_GUARDIAN_HURT, 0.75f);
    }
    
    public void createSmoke(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final double halfRadius = radius * 0.5;
        
        // Temper
        player.getAttributes().addModifier(modifierSource, speedIncreaseDuration, modifier -> modifier.of(AttributeType.SPEED, ModifierType.FLAT, speedIncrease));
        
        new TickingGameTask() {
            @Override
            public void run(int tick) {
                if (tick >= getDuration()) {
                    cancel();
                    return;
                }
                
                // Blind entities inside
                Collect.nearbyEntities(location, radius).forEach(entity -> entity.addEffect(EffectType.BLINDNESS, 30));
                
                // Fx - As much as I want to make a fancy sphere with particles it kills my fps
                player.spawnWorldParticle(location, Particle.LARGE_SMOKE, 50, halfRadius, 1.25, halfRadius, 0.05f);
            }
        }.runTaskTimer(0, 1);
        
        // Fx
        player.playWorldSound(Sound.BLOCK_FIRE_EXTINGUISH, 0.75f);
    }
    
}
