package me.hapyl.fight.game.heroes.frostbite;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.TimedGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public class EternalFreeze extends TimedGameTask {

    private static final ItemStack[] ITEMS = {
            new ItemStack(Material.WHITE_WOOL),
            new ItemStack(Material.SNOW_BLOCK),
            new ItemStack(Material.POWDER_SNOW),
            new ItemStack(Material.PACKED_ICE),
            new ItemStack(Material.BLUE_ICE)
    };


    private final GamePlayer player;
    private final Location location;
    private final FrostbiteUltimate ultimate;

    private final List<ArmorStand> blocks;
    private final double quality = Math.PI / 16;
    private final int speed = 2;
    private final TemperInstance temper;
    private double distance = 1.0d;
    private double theta = 0.0d;

    public EternalFreeze(GamePlayer player, FrostbiteUltimate ultimate) {
        super(ultimate.getDuration());

        this.player = player;
        this.location = player.getLocation();
        this.ultimate = ultimate;
        this.blocks = Lists.newArrayList();
        this.temper = Temper.ETERNAL_FREEZE
                .newInstance()
                .decrease(
                        AttributeType.CRIT_CHANCE,
                        ultimate.critChanceReduction
                ).decrease(
                        AttributeType.CRIT_DAMAGE,
                        ultimate.critDamageReduction
                ).increase(
                        AttributeType.COOLDOWN_MODIFIER,
                        ultimate.cooldownIncrease
                );

        prepare();
        setIncrement(speed);
        runTaskTimer(0, speed);

        // Fx
        player.playWorldSound(location, Sound.ITEM_ELYTRA_FLYING, 0.0f);
    }

    public void onTick(@Nonnull ArmorStand block, @Nonnull Location location) {
        // Teleport armor stands
        block.teleport(location);

        // For particles, we have to add a little Y offset because of armor stands
        location.add(0.0d, 1.975, 0.0d);
        player.spawnWorldParticle(location, Particle.SNOWFLAKE, 3, 0.5d, 0.5d, 0.5d, 0.25f);
        player.spawnWorldParticle(location, Particle.ITEM_SNOWBALL, 3, 0.5d, 0.5d, 0.5d, 0.25f);
        player.spawnWorldParticle(location, Particle.SPIT, 1, 0.2d, 0.2d, 0.2d, 0.25f);
        location.subtract(0.0d, 1.975, 0.0d);

        // Sound FX
        if (modulo(40)) {
            PlayerLib.playSound(location, Sound.ENTITY_PLAYER_HURT_FREEZE, 0.75f);
        }
    }

    public void onTickEntity(@Nonnull LivingGameEntity entity) {
        final EntityAttributes attributes = entity.getAttributes();

        temper.temper(entity, ultimate.debuffDuration);

        // Fx
        entity.setFreezeTicks(100);
    }

    @Override
    public void run(int tick) {
        if (distance < ultimate.distance) {
            distance = Math.min(distance + Math.sin(Math.toRadians(tick) * 2), ultimate.distance);
        }

        Collect.nearbyEntities(location, ultimate.distance).forEach(entity -> {
            if (entity.equals(player)) {
                return;
            }

            onTickEntity(entity);
        });

        // Fx
        final double spread = Math.PI * 2 / Math.max(blocks.size(), 1);
        int index = 1;

        for (ArmorStand block : blocks) {
            final double x = Math.sin(theta + spread * index) * distance;
            final double y = Math.sin(Math.toRadians(tick) + spread * index) * 0.5d;
            final double z = Math.cos(theta + spread * index) * distance;

            location.add(x, y, z);
            onTick(block, location);
            location.subtract(x, y, z);
            index++;
        }

        theta = theta >= Math.PI * 2 ? 0 : theta + quality;
    }

    @Override
    public void onTaskStop() {
        blocks.forEach(ArmorStand::remove);
        blocks.clear();

        // This adds kind of "fade out" to the ultimate.
        PlayerLib.playSound(location, Sound.ITEM_ARMOR_EQUIP_ELYTRA, 0.0f);
        PlayerLib.playSound(location, Sound.ITEM_ARMOR_EQUIP_LEATHER, 0.0f);

        PlayerLib.stopSound(Sound.ITEM_ELYTRA_FLYING);
    }

    private void prepare() {
        for (int i = 0; i < ultimate.blockCount; i++) {
            final int index = i;

            blocks.add(Entities.ARMOR_STAND_MARKER.spawn(location, self -> {
                self.setSilent(true);
                self.setInvisible(true);
                self.setGravity(false);
                self.setHelmet(ITEMS[index % ITEMS.length]);
            }));
        }
    }
}
