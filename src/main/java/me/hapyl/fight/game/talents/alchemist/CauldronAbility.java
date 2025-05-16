package me.hapyl.fight.game.talents.alchemist;

import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CauldronAbility extends Talent implements Listener {
    
    @DisplayField public final double health = 50;
    
    @DisplayField(suffix = " blocks") public final double cauldronRadius = 7d;
    @DisplayField(suffix = " blocks") public final double splashRadius = 4.5d;
    
    @DisplayField public final int interval = 40;
    
    private final PlayerMap<AlchemicalCauldron> cauldronMap = PlayerMap.newMap();
    
    private final List<CauldronPotion> potionList = List.of(
            new CauldronPotion(Color.fromRGB(181, 4, 18), Color.fromRGB(209, 52, 65)) {
                private final double damage = 5;
                
                @Override
                public void onHit(@Nonnull LivingGameEntity entity, @Nonnull GamePlayer alchemist) {
                    entity.damage(damage, alchemist, DamageCause.POTION);
                }
            },
            
            new CauldronPotionStatTemper(AttributeType.ATTACK, -0.2d, 60, Color.fromRGB(115, 3, 13), Color.fromRGB(138, 85, 90)),
            new CauldronPotionStatTemper(AttributeType.DEFENSE, -0.3d, 60, Color.fromRGB(1, 107, 15), Color.fromRGB(42, 122, 52))
    );
    
    public CauldronAbility(@Nonnull Key key) {
        super(key, "Brewing Pot");
        
        setDescription("""
                       Place a brewing pot in front of you.
                       &8&o;;Put your stick inside to start brewing!
                       
                       While brewing, &bpotions&7 will splash around the cauldron, affecting your &cenemies&7.
                       
                       After the cauldron has finishes brewing, apply &4&l💢 &eAlchemical Madness&7 to yourself and all enemies.
                       &8&o;;Grants you a random positive effect and a random negative effects to enemies.
                       
                       &c&lThe cauldron can be destroyed!
                       """);
        
        setTexture("e712289a6dac7c79c6effcd39db399c6ab6cb9d07f545eb72781678ec9ca013c");
        setType(TalentType.ENHANCE);
        
        setDurationSec(30);
        setCooldownSec(120);
    }
    
    @Nonnull
    public CauldronPotion pickRandomPotion() {
        return CollectionUtils.randomElementOrFirst(potionList);
    }
    
    @Override
    public void onStop(@Nonnull GameInstance instance) {
        cauldronMap.values().forEach(AlchemicalCauldron::cancel);
        cauldronMap.clear();
    }
    
    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        final AlchemicalCauldron cauldron = cauldronMap.remove(player);
        
        if (cauldron != null) {
            cauldron.cancel();
        }
    }
    
    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocationInFrontFromEyes(2);
        location.setYaw(0.0f);
        location.setPitch(0.0f);
        
        final AlchemicalCauldron previousCauldron = cauldronMap.put(player, new AlchemicalCauldron(player, LocationHelper.anchor(location), this));
        
        if (previousCauldron != null) {
            previousCauldron.cancel();
        }
        
        return Response.OK;
    }
    
}
