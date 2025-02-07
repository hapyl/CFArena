package me.hapyl.fight.game.heroes.echo;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.reflect.Reflect;
import me.hapyl.eterna.module.util.Promise;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.vehicle.Vehicle;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.entity.TileEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class EchoWorld extends TickingGameTask {

    private static final BlockFace[] RELATIVE_FACES = {
            BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST
    };

    private static final double Y_OFFSET = 0.6;

    private static final int MIN_Y = 50;
    private static final int MAX_Y = 150;

    private static final Set<Material> FORCE_CULL_BLOCKS = Set.of(
            Material.AIR,
            Material.CAVE_AIR,
            Material.VOID_AIR,
            Material.BARRIER,
            Material.STRUCTURE_VOID,
            Material.LIGHT,
            Material.RAIL,
            Material.POWERED_RAIL,
            Material.DETECTOR_RAIL,
            Material.ACTIVATOR_RAIL,

            // Optimization
            Material.WHITE_STAINED_GLASS,
            Material.WHITE_STAINED_GLASS_PANE,
            Material.GRAY_STAINED_GLASS,
            Material.GRAY_STAINED_GLASS_PANE,
            Material.LIGHT_GRAY_STAINED_GLASS,
            Material.LIGHT_GRAY_STAINED_GLASS_PANE,
            Material.TINTED_GLASS
    );

    protected final EchoData data;
    protected final GamePlayer player;

    private final GameEntity body;
    private final Vehicle vehicle;
    private final Set<BlockInf> affectedBlocks;

    public EchoWorld(@Nonnull EchoData data, @Nonnull GamePlayer player) {
        this.data = data;
        this.player = player;

        this.affectedBlocks = new HashSet<>();
        this.body = createBody(player);
        this.vehicle = player.startRiding(EchoVehicle::new);

        runTaskTimer(0, 5);
    }

    public void leave() {
        affectedBlocks.forEach(inf -> inf.reset(player));

        // Clean up
        vehicle.remove();

        final Location location = body.getLocation().add(0, Y_OFFSET, 0);

        player.schedule(() -> player.teleport(location), 1);
        player.show();

        body.forceRemove();
    }

    @Override
    public void onTaskStop() {
        leave();
        affectedBlocks.clear();
    }

    @Override
    public void run(int tick) {
        // Force player to be within the circle
        final Location playerLocation = player.getLocation();
        final Location location = body.getLocation();

        final double distance = playerLocation.distanceSquared(location);

        if (distance >= (TalentRegistry.ECHO_WORLD.radius * TalentRegistry.ECHO_WORLD.radius)) {
            final Vector vector = location.toVector().subtract(playerLocation.toVector()).normalize();
            vector.multiply(2);

            vehicle.move(vector);
        }

        // Fx
    }

    protected void createEchoWorld() {
        final Location location = player.getLocation();
        final World world = player.getWorld();

        final int centerX = location.getBlockX();
        final int centerZ = location.getBlockZ();
        final int radius = TalentRegistry.ECHO_WORLD.radius;

        for (int x = centerX - radius; x < centerX + radius; x++) {
            for (int z = centerZ - radius; z < centerZ + radius; z++) {
                if ((centerX - x) * (centerX - x) + (centerZ - z) * (centerZ - z) <= (radius * radius)) {
                    // Affect all Y
                    for (int y = MIN_Y; y < MAX_Y; y++) {
                        final Block block = world.getBlockAt(x, y, z);

                        // This really needs optimization
                        if (shouldCull(block)) {
                            continue;
                        }

                        affectedBlocks.add(new BlockInf(block, block.getBlockData(), block.getState()));
                    }
                }
            }
        }

        // We can actually do this async, since it's just a packet
        Promise.promise(() -> affectedBlocks.forEach(block -> {
            final BlockData echoData = BlockBrightness.getBlock(block.block);

            player.sendBlockChange(block.block, echoData);
        }));
    }

    private GameEntity createBody(GamePlayer player) {
        final EntityEquipment playerEquipment = player.getEquipment();
        final Location location = player.getLocationAnchored();
        location.subtract(0, Y_OFFSET, 0);
        location.setPitch(40.0f);

        return CF.createEntity(location, Entities.ARMOR_STAND, self -> {
            self.setInvisible(true);
            self.setGravity(false);
            self.setBasePlate(false);

            CFUtils.lockArmorStand(self);

            self.setHeadPose(new EulerAngle(Math.toRadians(20), 0, 0));
            self.setLeftArmPose(new EulerAngle(Math.toRadians(-10), 0, Math.toRadians(-10)));
            self.setRightArmPose(new EulerAngle(Math.toRadians(-10), 0, Math.toRadians(10)));
            self.setLeftLegPose(new EulerAngle(Math.toRadians(-90), Math.toRadians(-20), 0));
            self.setRightLegPose(new EulerAngle(Math.toRadians(-90), Math.toRadians(20), 0));

            final EntityEquipment equipment = self.getEquipment();
            equipment.setHelmet(playerEquipment.getHelmet());
            equipment.setChestplate(playerEquipment.getChestplate());
            equipment.setLeggings(playerEquipment.getLeggings());

            return new LivingGameEntity(self) {
                @Override
                public void onDamageTaken(@Nonnull DamageInstance instance) {
                    final LivingGameEntity damager = instance.getDamager();

                    if (!(damager instanceof GamePlayer gamePlayer)) {
                        instance.setCancelled(true);
                        return;
                    }

                    if (player.isSelfOrTeammate(gamePlayer)) {
                        return;
                    }

                    forceRemove();
                }
            };
        });
    }

    private static boolean shouldCull(Block block) {
        final Material material = block.getType();

        // Force cull
        if (FORCE_CULL_BLOCKS.contains(material)) {
            return true;
        }

        // Only show if at least one face is visible
        for (BlockFace face : RELATIVE_FACES) {
            final Block relative = block.getRelative(face);

            if (!relative.getType().isOccluding()) {
                return false;
            }
        }

        return true;
    }

    private record BlockInf(Block block, BlockData data, BlockState state) {
        public void reset(@Nonnull GamePlayer gamePlayer) {
            final Player player = gamePlayer.getPlayer();
            final WorldServer world = Reflect.getMinecraftWorld(player.getWorld());

            Promise.promise(() -> {
                        player.sendBlockChange(block.getLocation(), data);
                    })
                    .then(() -> {
                        // Heads are bugging, have to actually update
                        // the state for the player
                        // Even though we can send the update async, we'd have to
                        // wait for the block change to actually update
                        if (state instanceof Skull) {
                            final TileEntity tileEntity = world.getBlockEntity(
                                    new BlockPosition(block.getX(), block.getY(), block.getZ()),
                                    false
                            );

                            if (tileEntity != null) {
                                final PacketPlayOutTileEntityData packet = PacketPlayOutTileEntityData.a(tileEntity);

                                gamePlayer.sendPacket(packet);
                            }
                        }
                    });
        }
    }
}
