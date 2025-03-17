package me.hapyl.fight.game.heroes.nyx;

import me.hapyl.eterna.module.block.display.BDEngine;
import me.hapyl.eterna.module.block.display.DisplayData;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.eterna.module.util.Tuple;
import me.hapyl.fight.event.custom.AttributeTemperEvent;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.talents.ChargeType;
import me.hapyl.fight.game.talents.OverchargeUltimateTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.nyx.NyxPassive;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.ParticleDrawer;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

public class Nyx extends Hero implements Listener, PlayerDataHandler<NyxData>, UIComponent, ParticleDrawer {

    private final PlayerDataMap<NyxData> nyxDataMap = PlayerMap.newDataMap(NyxData::new);

    private final Particle.DustTransition dustData = new Particle.DustTransition(
            Color.fromRGB(66, 16, 181),
            Color.fromRGB(153, 62, 163),
            1
    );

    public Nyx(@Nonnull Key key) {
        super(key, "Nyx");

        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.SUPPORT, Archetype.HEXBANE, Archetype.DEFENSE, Archetype.POWERFUL_ULTIMATE);
        profile.setAffiliation(Affiliation.THE_WITHERS);
        profile.setGender(Gender.FEMALE);

        final HeroAttributes attributes = getAttributes();
        attributes.setDefense(80);
        attributes.setAttackSpeed(85);

        final HeroEquipment equipment = getEquipment();

        equipment.setChestPlate(38, 22, 38, TrimPattern.RAISER, TrimMaterial.NETHERITE);
        equipment.setLeggings(22, 28, 28, TrimPattern.DUNE, TrimMaterial.NETHERITE);
        equipment.setBoots(Material.NETHERITE_BOOTS, TrimPattern.DUNE, TrimMaterial.NETHERITE);

        setDescription("""
                `Chaos... brings victory...`
                """);

        setItem("e4e7d05432c07cbbe6414def96196f434ffc8759a528202463257f42f304670d");

        setWeapon(new NyxWeapon());


        setUltimate(new NyxUltimate());
    }

    @EventHandler()
    public void handleAttributeChange(AttributeTemperEvent ev) {
        final LivingGameEntity entity = ev.getEntity();
        final LivingGameEntity applier = ev.getApplier();

        if (!(applier instanceof GamePlayer playerApplier) || ev.isBuff()) {
            return;
        }

        final GamePlayer nyx = getNyx(playerApplier);

        // No nyx on the team
        if (nyx == null) {
            return;
        }

        getPassiveTalent().execute(nyx, playerApplier, entity);

        // Decrease stack
        getPlayerData(nyx).decrementChaosStacks();
    }

    @Override
    public void onStart(@Nonnull GameInstance instance) {
        new TickingGameTask() {
            @Override
            public void run(int tick) {
                nyxDataMap.values().forEach(Ticking::tick);
            }
        }.runTaskTimer(0, 2);
    }

    @Nonnull
    @Override
    public PlayerDataMap<NyxData> getDataMap() {
        return nyxDataMap;
    }

    @Override
    public Talent getFirstTalent() {
        return TalentRegistry.WITHER_ROSE_PATH;
    }

    @Override
    public Talent getSecondTalent() {
        return TalentRegistry.CHAOS_GROUND;
    }

    @Override
    public NyxPassive getPassiveTalent() {
        return TalentRegistry.NYX_PASSIVE;
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final NyxData data = getPlayerData(player);
        final int chaosStacks = data.getChaosStacks();

        return "&5%s &d%s".formatted(Named.THE_CHAOS.getCharacter(), chaosStacks);
    }

    public void drawParticle(@Nonnull Location location) {
        final World world = location.getWorld();

        world.spawnParticle(Particle.DUST, location, 1, dustData);
        world.spawnParticle(Particle.WITCH, location, 1);
    }

    @Nullable
    private GamePlayer getNyx(@Nonnull GamePlayer player) {
        if (validateNyx(player)) {
            return player;
        }

        return player.getTeam().getPlayers()
                     .stream()
                     .filter(this::validateNyx)
                     .findFirst()
                     .orElse(null);
    }

    private boolean validateNyx(GamePlayer player) {
        final NyxPassive passive = getPassiveTalent();

        return validatePlayer(player)
                && getPlayerData(player).getChaosStacks() > 0
                && player.isAlive() // Yeah, kinda make sure the player is alive
                && !passive.hasCd(player);
    }

    public static class NyxTuple<V> extends Tuple<V, V> {

        public NyxTuple(@Nonnull V v, @Nonnull V v2) {
            super(v, v2);
        }

        @Nonnull
        public String getString(@Nonnull V v) {
            return String.valueOf(v);
        }

        @Nonnull
        public final String differenceInPercent() {
            if (a() instanceof Number numA && b() instanceof Number numB) {
                return "&b%.0f%%&7".formatted((1 - numA.doubleValue() / numB.doubleValue()) * 100);
            }

            return "&4not applicable";
        }

        @Override
        public final String toString() {
            return getString(a()) + "/" + getString(b());
        }

        @Nonnull
        public final V value(@Nonnull ChargeType type) {
            return type.value(a(), b());
        }

        @Nonnull
        public static <V> NyxTuple<V> of(@Nonnull V a, @Nonnull V b, @Nonnull Function<V, String> toString) {
            return new NyxTuple<>(a, b) {
                @Nonnull
                @Override
                public String getString(@Nonnull V v) {
                    return toString.apply(v);
                }
            };
        }

    }

    final class NyxUltimate extends OverchargeUltimateTalent {

        @DisplayField final NyxTuple<Double> distance = NyxTuple.of(4.0d, 5.5d, String::valueOf);
        @DisplayField final NyxTuple<Double> damage = NyxTuple.of(1.0d, 2.0d, String::valueOf);
        @DisplayField final NyxTuple<Double> energyDecrease = NyxTuple.of(1.0, 2.0d, String::valueOf);

        @DisplayField final NyxTuple<Integer> duration = NyxTuple.of(40, 55, v -> Tick.round(v) + "s");
        @DisplayField final NyxTuple<Integer> chaosRegen = NyxTuple.of(2, 3, String::valueOf);

        @DisplayField final int hitDelay = 2;
        @DisplayField final double overchargedEnergyDecrease = 40.0d;

        @DisplayField final int castDuration = 21; // not using cast duration because void portal handles it differently

        public DisplayData spear = BDEngine.parse(
                "/summon block_display ~-0.5 ~-0.5 ~-0.5 {Passengers:[{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:chain\",Properties:{axis:\"x\"}},transformation:[0f,-1f,0f,0.5f,1f,0f,0f,0f,0f,0f,1f,-0.5f,0f,0f,0f,1f]},{id:\"minecraft:block_display\",block_state:{Name:\"minecraft:chain\",Properties:{axis:\"x\"}},transformation:[0f,-1f,0f,0.5f,1f,0f,0f,1f,0f,0f,1f,-0.5f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:stone_sword\",Count:1},item_display:\"none\",transformation:[0.7071f,0.7071f,0f,0f,-0.7071f,0.7071f,0f,2.25f,0f,0f,1f,0f,0f,0f,0f,1f]},{id:\"minecraft:item_display\",item:{id:\"minecraft:stone_sword\",Count:1},item_display:\"none\",transformation:[0f,0f,-1f,0f,-0.7071f,0.7071f,0f,2.25f,0.7071f,0.7071f,0f,0f,0f,0f,0f,1f]}]}");

        public NyxUltimate() {
            super(Nyx.this, "Impalement", 60, 100);

            setDescription("""
                    Summon a &8void portal&7 in front of you.
                    
                    After a short casting time, spears of chaos will rise from the portal, dealing &crapid damage&7 and reducing %s to &cenemies&7 within.
                    """.formatted(Named.ENERGY)
            );

            setOverchargeDescription("""
                    &8› &7Increases the range by %s.
                    &8› &7Increases the damage by %s.
                    &8› &7Increases the duration by %s.
                    
                    Adds a &cfinal slash&7, that decreases all &cenemies&7 %s by &b%s&7.
                    &8&oThe slash is considered to be an effect.
                    """.formatted(
                    distance.differenceInPercent(),
                    damage.differenceInPercent(),
                    duration.differenceInPercent(),
                    Named.ENERGY,
                    overchargedEnergyDecrease
            ));

            setItem(Material.DRIED_KELP);
        }

        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, @Nonnull ChargeType type) {
            return execute(() -> {
                final Location location = BukkitUtils.anchorLocation(
                        player.getLocation().add(player.getDirection().setY(0.0d).multiply(2))
                );

                location.setYaw(0.0f);
                location.setPitch(0.0f);

                new VoidPortal(player, location, this, type);
            });
        }

    }
}
