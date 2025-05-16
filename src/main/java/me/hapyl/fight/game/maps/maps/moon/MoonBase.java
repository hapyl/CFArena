package me.hapyl.fight.game.maps.maps.moon;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.util.Direction;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.maps.EnumLevel;
import me.hapyl.fight.game.maps.Level;
import me.hapyl.fight.game.maps.LevelFeature;
import me.hapyl.fight.game.maps.Size;
import me.hapyl.fight.game.maps.features.Turbine;
import me.hapyl.fight.game.maps.features.TurbineFeature;
import me.hapyl.fight.game.maps.supply.Supplies;
import me.hapyl.fight.util.BoundingBoxCollector;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.Set;

public class MoonBase extends Level {

    public static final int GATE_EXIT_ROOM = 0;
    public static final int GATE_LIVING_ROOM = 1;
    public static final int GATE_WATER_ROOM = 2;
    private final Set<MoonRoom> rooms = Sets.newHashSet();
    // This controls which room is opened.
    private int gate = 0;

    public MoonBase(@Nonnull EnumLevel handle) {
        super(handle, "Moon Station");

        setDescription("");
        setMaterial(Material.END_STONE_BRICKS);
        setSize(Size.MEDIUM);
        setTime(9500);
        setTicksBeforeReveal(160);

        // *=* Rooms *=* //

        // Middle room
        addLocation(5522, 63, 0, 90f, 0f);
        addLocation(5502, 62, -15, 0f, 0f);

        // Gate 0 (Exit room)
        rooms.add(new MoonRoomExit());

        addLocation(5523, 62, 40, 145f, 0f, map -> gate == 0);
        addLocation(5490.5, 65.5, 31.0, -90f, 0f, map -> gate == 0);
        addLocation(5499.0, 71, 23.0, -180f, 0f, map -> gate == 0);


        // Gate 1 (Living room)
        rooms.add(new MoonRoomLiving());

        addLocation(5506.0, 62, -37.0, 90f, 0f, map -> gate == 1);
        addLocation(5487, 62, -43, map -> gate == 1);
        addLocation(5517, 62, -34, map -> gate == 1);

        // Gate 2 (Water room)
        rooms.add(new MoonRoomWater());

        // Packs
        addPackLocation(Supplies.HEALTH, 5530, 63, -22);
        addPackLocation(Supplies.HEALTH, 5524, 63, 28);
        addPackLocation(Supplies.HEALTH, 5497, 62, -38);
        addPackLocation(Supplies.HEALTH, 5452, 62, -20);

        addPackLocation(Supplies.ENERGY, 5530, 63, 22);
        addPackLocation(Supplies.ENERGY, 5531, 74, 39);
        addPackLocation(Supplies.ENERGY, 5518, 63, -27);

        // Turbines
        final TurbineFeature turbines = new TurbineFeature();

        turbines.addTurbine(
                new Turbine(
                        new BoundingBoxCollector(5493, 70, -18, 5497, 74, -5),
                        new BoundingBoxCollector(5493, 70, -18, 5497, 74, -17)
                ).setDirections(Direction.SOUTH));

        turbines.addTurbine(
                new Turbine(
                        new BoundingBoxCollector(5450, 64, 15, 5466, 68, 19),
                        new BoundingBoxCollector(5466, 64, 15, 5467, 68, 19)
                ).setDirections(Direction.WEST)
        );

        addFeature(turbines);

        // Toxic Water
        addFeature(new LevelFeature("Electric Water", """
                In a room within the Moon Base, there is a kind of water that... shocks you!
                """) {

            private final double damage = 2.0d;
            private final int damagePeriod = 30;

            @Override
            public void tick(int tick) {
                if (!validateGameAndMap(EnumLevel.MOON_BASE)) {
                    return;
                }

                if (tick % damagePeriod != 0) {
                    return;
                }

                CF.getAlivePlayers()
                        .stream()
                        .filter(LivingGameEntity::isInWater)
                        .forEach(player -> {
                            player.damage(damage, DamageCause.WATER);
                            player.playWorldSound(Sound.ENTITY_SILVERFISH_HURT, 0.75f);
                        });
            }
        });
    }

    @Override
    public void onStart(@Nonnull GameInstance instance) {
        this.gate = new Random().nextInt(0, 3);

        rooms.forEach(room -> {
            room.close();

            if (room.gate == gate) {
                room.open();
            }
        });

        super.onStart(instance);
    }

}
