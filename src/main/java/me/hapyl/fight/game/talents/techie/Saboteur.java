package me.hapyl.fight.game.talents.techie;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.techie.BugType;
import me.hapyl.fight.game.heroes.techie.Techie;
import me.hapyl.fight.game.heroes.techie.TechieData;
import me.hapyl.fight.game.maps.supply.HackedSupplyInstance;
import me.hapyl.fight.game.maps.supply.SupplyInstance;
import me.hapyl.fight.game.maps.supply.SupplyPlatform;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.Plural;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class Saboteur extends TechieTalent {
    
    public static final ModifierSource modifierSource = new ModifierSource(Key.ofString("saboteur"));
    
    @DisplayField public final double hackedSupplyDamage = 10;
    @DisplayField public final int impairDuration = 100;
    @DisplayField(scale = 100) public final double hackedSupplyAttackReduction = -0.25;
    @DisplayField public final double hackedSupplySpeedReduction = -30; // 30%
    
    @DisplayField private final double hackDistance = 8.0d;
    
    public Saboteur(@Nonnull Key key) {
        super(key, "Saboteur");
        
        setType(TalentType.IMPAIR);
        setMaterial(Material.IRON_TRAPDOOR);
        setCooldownSec(8);
        setCastingTime(10);
    }
    
    @Nonnull
    @Override
    public String getHackDescription() {
        return """
               hack all &copponents&7 in front of you, &bimplanting&7 a random &f%s&7 onto them.
               
               &6Bugs:
               %s&7: %s
               %s&7: %s
               %s&7: %s
               
               This ability can also hack &aSupply Packs&7, rendering them &4unobtainable&7, but &eimpairing&7 and &bimplanting&7 a random &fbug&7 onto an &cenemy&7 when they pick it up.
               """.formatted(
                Named.BUG,
                BugType.TYPE_A.getName(), BugType.TYPE_A.getDescription(),
                BugType.TYPE_D.getName(), BugType.TYPE_D.getDescription(),
                BugType.TYPE_S.getName(), BugType.TYPE_S.getDescription()
        );
    }
    
    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        Manager.current()
               .currentInstanceOptional()
               .ifPresent(instance -> {
                   instance.supplies()
                           .platforms()
                           .forEach(platform -> {
                               if (platform.instance() instanceof HackedSupplyInstance hackedInstance && hackedInstance.player().equals(player)) {
                                   // If it's hacked then it exists, so just respawn it
                                   platform.respawn();
                               }
                           });
               });
    }
    
    @Override
    public void onHack(@Nonnull GamePlayer player) {
        final Techie hero = HeroRegistry.TECHIE;
        final Location location = player.getLocation();
        final TechieData data = hero.getPlayerData(player);
        
        int hackedPacks = 0;
        int hackedEnemies = 0;
        
        // Hack supply pack
        final GameInstance gameInstance = Manager.current().currentInstanceOrNull();
        
        if (gameInstance == null) {
            return;
        }
        
        for (SupplyPlatform platform : gameInstance.supplies().platforms()) {
            final SupplyInstance instance = platform.instance();
            
            if (instance == null || instance instanceof HackedSupplyInstance || instance.location().distance(location) > hackDistance) {
                continue;
            }
            
            if (!platform.hackSupply(player)) {
                continue;
            }
            
            hackedPacks++;
        }
        
        // Hack enemies
        for (LivingGameEntity entity : Collect.nearbyEntities(location, hackDistance)) {
            if (player.isSelfOrTeammate(entity)) {
                continue;
            }
            
            data.bugRandomly(entity);
            hackedEnemies++;
        }
        
        if (hackedPacks == 0 && hackedEnemies == 0) {
            player.sendMessage("&c🕸 &4Didn't hack anything!");
        }
        else {
            final StringBuilder builder = new StringBuilder("&b🕸 &3Hacked ");
            
            if (hackedPacks > 0) {
                builder.append(hackedPacks).append(" supply %s".formatted(Plural.pluralize("pack", hackedPacks)));
            }
            
            if (hackedEnemies > 0) {
                if (hackedPacks > 0) {
                    builder.append(" and ");
                }
                
                builder.append(hackedEnemies).append(Plural.pluralize(" enemy", hackedEnemies));
            }
            
            player.sendMessage(builder + "!");
        }
    }
    
}
