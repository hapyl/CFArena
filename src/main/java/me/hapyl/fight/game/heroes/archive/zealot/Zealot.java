package me.hapyl.fight.game.heroes.archive.zealot;

import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.archive.zealot.BrokenHeartRadiation;
import me.hapyl.fight.game.talents.archive.zealot.FerociousStrikes;
import me.hapyl.fight.game.talents.archive.zealot.MalevolentHitshield;
import me.hapyl.fight.game.task.player.PlayerTickingGameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.spigotutils.module.block.display.BlockStudioParser;
import me.hapyl.spigotutils.module.block.display.DisplayData;
import me.hapyl.spigotutils.module.block.display.DisplayEntity;
import me.hapyl.spigotutils.module.math.Geometry;
import me.hapyl.spigotutils.module.math.geometry.WorldParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class Zealot extends Hero implements Listener, PlayerDataHandler<ZealotData>, UIComponent {

    protected final Equipment abilityEquipment;

    private final PlayerDataMap<ZealotData> zealotData = PlayerMap.newDataMap(ZealotData::new);

    private final DisplayData giantSword = BlockStudioParser.parse(
            "/summon block_display ~-0.5 ~-0.5 ~-0.5 {Passengers:[{id:\"minecraft:item_display\",item:{id:\"minecraft:golden_sword\",Count:1},item_display:\"none\",transformation:[2.6043f,3.5194f,-2.4148f,-0.2500f,3.4151f,-3.4151f,-1.2941f,1.2500f,-2.5602f,-0.9753f,-4.1826f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
    );

    public Zealot(@Nonnull Heroes handle) {
        super(handle, "Zealot");

        setDescription("""
                A space ranger with a single goal of maintaining order.
                """);

        setArchetype(Archetype.DAMAGE);
        setAffiliation(Affiliation.SPACE);
        setGender(Gender.MALE);
        setRace(Race.ALIEN);

        setItem("131530db74bac84ad9e322280c56c4e0199fbe879883b76c9cf3fd8ff19cf025");
        setWeapon(new ZealotWeapon(this));

        final HeroAttributes attributes = getAttributes();
        attributes.setFerocity(50);

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(104, 166, 232, TrimPattern.SILENCE, TrimMaterial.DIAMOND);
        equipment.setLeggings(Material.DIAMOND_LEGGINGS, TrimPattern.SILENCE, TrimMaterial.DIAMOND);
        equipment.setBoots(Material.DIAMOND_BOOTS, TrimPattern.SILENCE, TrimMaterial.DIAMOND);

        abilityEquipment = new Equipment();
        abilityEquipment.setHelmet(getItem());
        abilityEquipment.setChestPlate(104, 166, 232, TrimPattern.SILENCE, TrimMaterial.GOLD);
        abilityEquipment.setLeggings(Material.GOLDEN_LEGGINGS, TrimPattern.SILENCE, TrimMaterial.GOLD);
        abilityEquipment.setBoots(Material.GOLDEN_BOOTS, TrimPattern.RIB, TrimMaterial.GOLD);

        setUltimate(new ZealotUltimate(this));
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getDamagerAsPlayer();
        final EnumDamageCause cause = instance.getCause();

        if (player == null || cause != EnumDamageCause.FEROCITY || isUsingUltimate(player)) {
            return;
        }

        final ZealotData data = getPlayerData(player);
        data.incrementFerociousHits();
    }

    @Override
    public boolean predicateUltimate(@Nonnull GamePlayer player) {
        return getPlayerData(player).ferociousHits > 0;
    }

    @Override
    public String predicateMessage(@Nonnull GamePlayer player) {
        return "No " + Named.FEROCIOUS_STRIKE + " &cstacks!";
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        final ZealotUltimate ultimate = getUltimate();

        final Location location = player.getLocation();
        location.setPitch(0.0f);

        final Vector direction = location.getDirection().setY(0.0d);

        location.add(direction.multiply(ultimate.directionOffset));

        final Location landingLocation = CFUtils.anchorLocation(location);
        final DisplayEntity entity = giantSword.spawnInterpolated(landingLocation.clone().add(0, ultimate.landingOffset, 0));

        new PlayerTickingGameTask(player) {
            private final double y = entity.getHead().getLocation().getY();
            private double traveled = 0.0d;
            private int landedAt = -1;

            @Override
            public void run(int tick) {
                // Fx
                Geometry.drawPolygon(landingLocation, 5, ultimate.distance, new WorldParticle(Particle.CRIT));

                // Land
                if (traveled >= ultimate.landingOffset) {
                    if (landedAt == -1) {
                        landedAt = tick;

                        // Fx
                        player.playWorldSound(landingLocation, Sound.ITEM_SHIELD_BREAK, 0.75f);
                    }

                    // Damage
                    if (tick - landedAt >= ultimate.impactTime) {
                        cancel();

                        final ZealotData playerData = getPlayerData(player);
                        final int ferociousHits = playerData.ferociousHits;

                        playerData.ferociousHits = 0;

                        Collect.nearbyEntities(landingLocation, ultimate.distance).forEach(entity -> {
                            if (player.isSelfOrTeammate(entity)) {
                                return;
                            }

                            entity.executeFerocity(ultimate.baseDamage, player, ferociousHits, true);
                        });

                        // Fx
                        player.playWorldSound(landingLocation, Sound.ENTITY_GENERIC_EXPLODE, 0.75f);
                        player.spawnWorldParticle(landingLocation.add(0, 2.5, 0), Particle.CRIT, 20, 0.1d, 0.5d, 0.1, 1.0f);
                    }
                    return;
                }

                final double cos = Math.cos(ultimate.landingSpeed);
                final Location location = entity.getHead().getLocation();

                traveled = Math.min(traveled + cos, ultimate.landingOffset);

                location.setY(y - traveled);
                entity.teleport(location);
            }

            @Override
            public void onTaskStop() {
                entity.remove();
            }
        }.runTaskTimer(0, 1);

        return UltimateCallback.OK;
    }

    @Nonnull
    @Override
    public ZealotUltimate getUltimate() {
        return (ZealotUltimate) super.getUltimate();
    }

    @Override
    public BrokenHeartRadiation getFirstTalent() {
        return (BrokenHeartRadiation) Talents.BROKEN_HEART_RADIATION.getTalent();
    }

    @Override
    public MalevolentHitshield getSecondTalent() {
        return (MalevolentHitshield) Talents.MALEVOLENT_HITSHIELD.getTalent();
    }

    @Override
    public FerociousStrikes getPassiveTalent() {
        return (FerociousStrikes) Talents.FEROCIOUS_STRIKES.getTalent();
    }

    @Nonnull
    @Override
    public PlayerDataMap<ZealotData> getDataMap() {
        return zealotData;
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final ZealotData data = getPlayerData(player);
        final int ferociousHits = data.ferociousHits;
        final boolean isMaxed = ferociousHits == FerociousStrikes.maxStrikes;

        return "&4%s %s%s".formatted(Named.FEROCIOUS_STRIKE.getCharacter(), ferociousHits, isMaxed ? " &lMAX!" : "");
    }
}
