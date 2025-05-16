package me.hapyl.fight.game.talents.swooper;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.talents.ChargedTalent;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class BlastPack extends ChargedTalent {
    
    public final DisplayData display = BDEngine.parse(
            "/summon block_display ~-0.5 ~-0.5 ~-0.5 {Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:red_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0f,-1.6875f,0f,0.30078125f,1f,0f,0f,-0.4375f,0f,0f,1f,-0.3649775002f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:red_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0f,-1.6875f,0f,0.35078125f,1f,0f,0f,-0.4375f,0f,0f,1f,-0.4899775002f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:red_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0f,-1.6875f,0f,0.32640625f,1f,0f,0f,-0.4375f,0f,0f,1f,-0.6149775002f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:red_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0f,-1.6875f,0f,0.30578125f,1f,0f,0f,-0.3125f,0f,0f,1f,-0.5524775002f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:red_candle\",Properties:{candles:\"1\",lit:\"false\"}},transformation:[0f,-1.6875f,0f,0.33890625f,1f,0f,0f,-0.3125f,0f,0f,1f,-0.4274775002f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:dried_kelp_block\",Properties:{}},transformation:[0.125f,0f,0f,0.07953125f,0f,0.125f,0f,0.12625f,0f,0f,0.258f,-0.1181025002f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:dried_kelp_block\",Properties:{}},transformation:[0.125f,0f,0f,0.07953125f,0f,0.128f,0f,-0.0025f,0f,0f,0.382f,-0.1831025002f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:dried_kelp_block\",Properties:{}},transformation:[0.125f,0f,0f,-0.19359375f,0f,0.125f,0f,0.12625f,0f,0f,0.258f,-0.1181025002f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:dried_kelp_block\",Properties:{}},transformation:[0.125f,0f,0f,-0.19359375f,0f,0.128f,0f,-0.0025f,0f,0f,0.382f,-0.1831025002f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:blackstone_wall\",Properties:{up:\"true\"}},transformation:[0f,0.292f,0f,-0.13734375f,-0.1771135748f,0f,-0.0615388627f,0.285f,-0.0615388627f,0f,0.1771135748f,-0.1631025002f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;82144539,-1309275189,-611081251,1014569388],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNhNTNjNWIyOGVhYzZmMTY4Yzg0MmQ5ZjAyNTU1YWQwMDZhZTcxZjdhYTViMTQwMTY2MWI5MDY3ZmJkNjA4ZiJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.375f,0f,0f,0.00640625f,0f,0.2545243977f,-0.1356092138f,0.218125f,0f,0.0987235076f,0.3496214255f,-0.0437275002f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-356177143,-644133217,-1155794416,-1388133217],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDU3MGJmNzVmYzBiYmZlNDcyYzhmNTAyNGQwMWYwNzc1YzNlNzlhNTRkOTgxNzEyNGVlOWNhNWM4OTcyMjViZiJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.3675f,0f,0f,0.00578125f,0f,0.1875f,0f,0.2575f,0f,0f,0.2415f,-0.0668525002f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:player_head\",Count:1,components:{\"minecraft:profile\":{id:[I;-2043269440,-535716270,-1089120078,225729444],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDU3MGJmNzVmYzBiYmZlNDcyYzhmNTAyNGQwMWYwNzc1YzNlNzlhNTRkOTgxNzEyNGVlOWNhNWM4OTcyMjViZiJ9fX0=\"}]}}},item_display:\"none\",transformation:[0.3675f,0f,0f,0.00578125f,0f,0f,0.1795f,0.07875f,0f,-0.1875f,0f,-0.1837275002f,0f,0f,0f,1f]}]}"
    );
    
    @DisplayField public final int maxAirTime = 300;
    @DisplayField public final int maxLifeTime = 100;
    @DisplayField(suffix = " blocks") public final double explosionRadius = 4.0d;
    @DisplayField public final int stunDuration = 40;
    @DisplayField public final double selfMagnitude = 1.5d;
    @DisplayField public final double otherMagnitude = 0.5d;
    @DisplayField public final double damage = 2.0d;
    
    protected final PlayerMap<BlastPackEntity> blastPacks = PlayerMap.newMap();
    
    public BlastPack(@Nonnull Key key) {
        super(key, "Blast Pack", 2);
        
        setDescription("""
                       Throw an explosive &eC4&7 in front of you that &nsticks&7 to surfaces.
                       
                       &nUse&7 &nagain&7 to &4explode&7, damaging &cenemies&7 and moving all &bentities&7.
                       """
        );
        
        setType(TalentType.MOVEMENT);
        setMaterial(Material.DETECTOR_RAIL);
        setCooldownSec(8);
    }
    
    @Override
    public void onStop(@Nonnull GameInstance instance) {
        super.onStop(instance);
        
        blastPacks.forEachAndClear(BlastPackEntity::cancel);
    }
    
    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        super.onDeath(player);
        
        blastPacks.removeAnd(player, BlastPackEntity::cancel);
    }
    
    @Nonnull
    @Override
    public Response execute(@Nonnull GamePlayer player, int charges) {
        final BlastPackEntity blastPack = blastPacks.remove(player);
        
        // Explode blast pack
        if (blastPack != null) {
            blastPack.explode();
            return Response.OK;
        }
        
        // Throw blast pack
        blastPacks.put(player, new BlastPackEntity(this, player));
        
        player.playWorldSound(Sound.ENTITY_SLIME_JUMP, 0.75f);
        return Response.AWAIT;
        
    }
    
}
