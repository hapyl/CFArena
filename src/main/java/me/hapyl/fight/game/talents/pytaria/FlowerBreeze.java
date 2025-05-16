package me.hapyl.fight.game.talents.pytaria;


import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.ModifierSource;
import me.hapyl.fight.game.attribute.ModifierType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class FlowerBreeze extends Talent {
    
    @DisplayField(scale = 100) public final double attackIncrease = 0.3d;
    @DisplayField(scale = 100) public final double defenseIncrease = 1.5d;
    @DisplayField private final double healthSacrifice = 15.0d;
    
    private final Material[] flowers = {
            Material.POPPY,
            Material.DANDELION,
            Material.ALLIUM,
            Material.RED_TULIP,
            Material.ORANGE_TULIP,
            Material.PINK_TULIP,
            Material.WHITE_TULIP,
            Material.OXEYE_DAISY,
            Material.CORNFLOWER,
            Material.AZURE_BLUET
    };
    
    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("flower_breeze"));
    
    public FlowerBreeze(@Nonnull Key key) {
        super(key, "Flower Breeze");
        
        setDescription("""
                       Feel the breeze of beautiful &dflowers&7 that &4hurts&7 you but increases your &c%s&7 and &b%s&7 for {duration}.
                       
                       &8;;This ability cannot kill.
                       """.formatted(AttributeType.ATTACK, AttributeType.DEFENSE)
        );
        
        setType(TalentType.ENHANCE);
        setMaterial(Material.RED_DYE);
        setDurationSec(3);
        setCooldownSec(16);
    }
    
    @Override
    public @Nullable Response execute(@Nonnull GamePlayer player) {
        final Location location = player.getLocation();
        final World world = player.getWorld();
        
        player.playWorldSound(Sound.ENTITY_HORSE_BREATHE, 0.0f);
        player.addPotionEffect(PotionEffectType.SLOWNESS, 2, 10);
        
        // can't go lower than 1 heart
        player.setHealth(Math.max(2, player.getHealth() - healthSacrifice));
        player.getAttributes().addModifier(
                modifierSource, this, modifier -> modifier
                        .of(AttributeType.ATTACK, ModifierType.MULTIPLICATIVE, attackIncrease)
                        .of(AttributeType.DEFENSE, ModifierType.MULTIPLICATIVE, defenseIncrease)
        );
        
        // Fx
        new TimedGameTask(20) {
            private final double distance = 1.25d;
            
            @Override
            public void run(int tick) {
                final double x = Math.sin(tick) * distance;
                final double y = 2.0d / maxTick * tick;
                final double z = Math.cos(tick) * distance;
                
                dropItem(location -> location.add(x, y, z), location -> location.subtract(x, y, z));
                dropItem(location -> location.subtract(x, y, z), location -> location.add(x, y, z));
                
                final float pitch = 0.5f + (1.5f / maxTick * tick);
                player.playWorldSound(location, Sound.BLOCK_LAVA_POP, pitch);
                player.playWorldSound(location, Sound.BLOCK_AZALEA_PLACE, pitch);
            }
            
            private void dropItem(Consumer<Location> preConsumer, Consumer<Location> postConsumer) {
                preConsumer.accept(location);
                world.dropItem(
                        location, new ItemStack(CollectionUtils.randomElement(flowers, flowers[0])), self -> {
                            self.setPickupDelay(10000);
                            self.setTicksLived(5990);
                        }
                );
                postConsumer.accept(location);
            }
        }.runTaskTimer(0, 1);
        
        return Response.OK;
    }
}
