package me.hapyl.fight.game.heroes.zealot;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.block.display.DisplayEntity;
import me.hapyl.eterna.module.math.Geometry;
import me.hapyl.eterna.module.math.geometry.WorldParticle;
import me.hapyl.eterna.module.util.BukkitUtils;

import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.zealot.BrokenHeartRadiation;
import me.hapyl.fight.game.talents.zealot.FerociousStrikes;
import me.hapyl.fight.game.talents.zealot.MalevolentHitshield;
import me.hapyl.fight.game.task.player.PlayerTickingGameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
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

    public Zealot(@Nonnull Key key) {
        super(key, "Zealot");

        setDescription("""
                A space ranger with a single goal of maintaining order.
                """);

        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE);
        profile.setAffiliation(Affiliation.SPACE);
        profile.setGender(Gender.MALE);
        profile.setRace(Race.ALIEN);

        setItem("131530db74bac84ad9e322280c56c4e0199fbe879883b76c9cf3fd8ff19cf025");
        setWeapon(new ZealotWeapon(this));

        final HeroAttributes attributes = getAttributes();
        attributes.setFerocity(25);

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

        if (player == null || cause != EnumDamageCause.FEROCITY || player.isUsingUltimate()) {
            return;
        }

        final ZealotData data = getPlayerData(player);
        data.incrementFerociousHits();
    }

    @Nonnull
    @Override
    public ZealotUltimate getUltimate() {
        return (ZealotUltimate) super.getUltimate();
    }

    @Override
    public BrokenHeartRadiation getFirstTalent() {
        return TalentRegistry.BROKEN_HEART_RADIATION;
    }

    @Override
    public MalevolentHitshield getSecondTalent() {
        return TalentRegistry.MALEVOLENT_HITSHIELD;
    }

    @Override
    public FerociousStrikes getPassiveTalent() {
        return TalentRegistry.FEROCIOUS_STRIKES;
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

    public class ZealotUltimate extends UltimateTalent {

        private final DisplayData giantSword = BDEngine.parse(
                "/summon block_display ~-0.5 ~-0.5 ~-0.5 {Passengers:[{id:\"minecraft:item_display\",item:{id:\"minecraft:golden_sword\",Count:1},item_display:\"none\",transformation:[2.6043f,3.5194f,-2.4148f,-0.2500f,3.4151f,-3.4151f,-1.2941f,1.2500f,-2.5602f,-0.9753f,-4.1826f,-0.5000f,0.0000f,0.0000f,0.0000f,1.0000f]}]}"
        );

        private final Zealot hero;

        @DisplayField public final double baseDamage = 4.0d;
        @DisplayField public final double landingOffset = 10.0d;
        @DisplayField public final double distance = 5.0d;
        @DisplayField public final int impactTime = 15;
        @DisplayField public final double directionOffset = 2.5d;
        @DisplayField public final double landingSpeed = Math.PI / 14;

        public ZealotUltimate(@Nonnull Zealot hero) {
            super(Zealot.this, "Maintain Order", 60);

            setDescription("""
                    Command a &egiant sword&7 to &afall down&7 from the &bsky&7.
                    
                    Upon landing, &4explodes&7 violently, inflicting %s on nearby &cenemies&7 based on your %s stacks.
                    """.formatted(AttributeType.FEROCITY, Named.FEROCIOUS_STRIKE)
            );

            this.hero = hero;

            setType(TalentType.DAMAGE);
            setItem(Material.GOLDEN_SWORD);
            setDurationSec(12);

            setSound(Sound.ENTITY_WITHER_HURT, 0.0f);
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            final ZealotData data = hero.getPlayerData(player);

            if (data.ferociousHits <= 0) {
                return UltimateResponse.error("No " + Named.FEROCIOUS_STRIKE + " &cstacks!");
            }

            final Location location = player.getLocation();
            location.setPitch(0.0f);

            final Vector direction = location.getDirection().setY(0.0d);

            location.add(direction.multiply(directionOffset));

            final Location landingLocation = BukkitUtils.anchorLocation(location);
            final DisplayEntity entity = giantSword.spawnInterpolated(landingLocation.clone().add(0, landingOffset, 0));

            new PlayerTickingGameTask(player) {
                private final double y = entity.getHead().getLocation().getY();
                private double traveled = 0.0d;
                private int landedAt = -1;

                @Override
                public void run(int tick) {
                    // Fx
                    Geometry.drawPolygon(landingLocation, 5, distance, new WorldParticle(Particle.CRIT));

                    // Land
                    if (traveled >= landingOffset) {
                        if (landedAt == -1) {
                            landedAt = tick;

                            // Fx
                            player.playWorldSound(landingLocation, Sound.ITEM_SHIELD_BREAK, 0.75f);
                        }

                        // Damage
                        if (tick - landedAt >= impactTime) {
                            cancel();

                            final int ferociousHits = data.ferociousHits;

                            data.ferociousHits = 0;

                            Collect.nearbyEntities(landingLocation, distance).forEach(entity -> {
                                if (player.isSelfOrTeammate(entity)) {
                                    return;
                                }

                                entity.executeFerocity(baseDamage, player, ferociousHits, true);
                            });

                            // Fx
                            player.playWorldSound(landingLocation, Sound.ENTITY_GENERIC_EXPLODE, 0.75f);
                            player.spawnWorldParticle(landingLocation.add(0, 2.5, 0), Particle.CRIT, 20, 0.1d, 0.5d, 0.1, 1.0f);
                        }
                        return;
                    }

                    final double cos = Math.cos(landingSpeed);
                    final Location location = entity.getHead().getLocation();

                    traveled = Math.min(traveled + cos, landingOffset);

                    location.setY(y - traveled);
                    entity.teleport(location);
                }

                @Override
                public void onTaskStop() {
                    entity.remove();
                }
            }.runTaskTimer(0, 1);

            return UltimateResponse.OK;
        }
    }
}
