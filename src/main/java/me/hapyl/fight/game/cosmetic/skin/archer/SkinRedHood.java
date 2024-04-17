package me.hapyl.fight.game.cosmetic.skin.archer;

import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.spigotutils.module.particle.ParticleBuilder;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;

import javax.annotation.Nullable;

public class SkinRedHood extends AbstractSkinArcher {

    private final Color hawkeyeArrowColor = Color.fromRGB(143, 19, 19);
    private final Color tripleShotArrowColor = Color.fromRGB(196, 114, 73);
    private final Color shockDartArrowColor = Color.fromRGB(82, 84, 227);

    private final BlockData hawkeyeData = Material.RED_GLAZED_TERRACOTTA.createBlockData();
    private final BlockData boomData = Material.ORANGE_STAINED_GLASS.createBlockData();

    private final ParticleBuilder blueColor = ParticleBuilder.redstoneDust(Color.fromRGB(255, 161, 89), 1);
    private final ParticleBuilder redColor = ParticleBuilder.redstoneDust(Color.fromRGB(179, 55, 11), 1);

    public SkinRedHood() {
        setName("Red Hood");
        setDescription("""
                Something that happens after the dawn.
                                
                &dThis skin changes talent effects!
                """);

        setRarity(Rarity.EPIC);
        setRubyPrice(50);

        final Equipment equipment = getEquipment();

        equipment.setTexture("c6336c4bef7f74a147e872e721fc536eddc2ffd01a7e48d0b6f3f419fc7d2a");
        equipment.setChestPlate(161, 118, 84);
        equipment.setLeggings(71, 67, 63);
        equipment.setBoots(36, 37, 53);
    }

    @Override
    public Color getHawkeyeArrowColor() {
        return hawkeyeArrowColor;
    }

    @Override
    public Color getTripleShotArrowColor() {
        return tripleShotArrowColor;
    }

    @Nullable
    @Override
    public Color getShockDartArrowColor() {
        return shockDartArrowColor;
    }

    @Nullable
    @Override
    public ParticleBuilder getShockDartBlueColor() {
        return blueColor;
    }

    @Nullable
    @Override
    public ParticleBuilder getShockDartRedColor() {
        return redColor;
    }

    @Override
    public boolean hawkeyeArrowTick(GamePlayer player, Location location) {
        player.spawnWorldParticle(location, Particle.BLOCK_DUST, 5, 0, 0, 0, 0, hawkeyeData);
        player.playWorldSound(location, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT_LAND, 1.75f);

        return true;
    }

    @Override
    public boolean boomArrowTick(GamePlayer gamePlayer, Location location) {
        gamePlayer.spawnWorldParticle(location, Particle.WAX_ON, 2, 0, 0, 0, 1);
        gamePlayer.spawnWorldParticle(location, Particle.BLOCK_DUST, 2, 0, 0, 0, 1, boomData);

        gamePlayer.playWorldSound(location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.75f);

        return true;
    }

}
