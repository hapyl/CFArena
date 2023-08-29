package me.hapyl.fight.game.heroes.archive.bloodfield;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroEquipment;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.heroes.archive.bloodfield.impel.Impel;
import me.hapyl.fight.game.heroes.archive.bloodfield.impel.ImpelInstance;
import me.hapyl.fight.game.heroes.archive.bloodfield.impel.Type;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.bloodfiend.BloodChalice;
import me.hapyl.fight.game.talents.archive.bloodfiend.Candlebane;
import me.hapyl.fight.game.talents.archive.bloodfiend.Chalice;
import me.hapyl.fight.game.talents.archive.bloodfiend.TwinClaws;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.ui.UIComplexComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.protocol.PlayerMount;
import me.hapyl.fight.util.Utils;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class Bloodfiend extends Hero implements Listener, UIComplexComponent {

    @DisplayField public final short impelTimes = 3;
    @DisplayField public final int impelDuration = 50;
    @DisplayField public final int impelCd = 15;
    @DisplayField public final double impelDamage = 50.0d;

    private final Map<Player, BloodfiendData> playerData = Maps.newConcurrentMap();

    public Bloodfiend() {
        super("Bloodfiend");

        setDescription("""
                A vampire prince with a sunscreen.
                """);
        setArchetype(Archetype.STRATEGY);
        setItem("5aa29ea961757dc3c90bfabf302c5abe9d308fb4a7d3864e5788ad2cc9160aa2");

        final HeroAttributes attributes = getAttributes();
        attributes.setHealth(80.0d);

        final HeroEquipment equipment = getEquipment();

        //equipment.setChestplate(26, 1, 1, TrimPattern.TIDE, TrimMaterial.IRON);
        equipment.setChestplate(0, 0, 0);
        equipment.setLeggings(0, 0, 0);
        equipment.setBoots(0, 0, 0);

        setWeapon(new Weapon(Material.GHAST_TEAR).setName("Vampire's Fang").setDamage(4.0d));

        final UltimateTalent ultimate = new UltimateTalent("Impel", 65)
                .setItem(Material.MOOSHROOM_SPAWN_EGG)
                .setDuration(impelDuration * impelTimes)
                .setCastDuration(30);

        ultimate.setDescription("""
                After a short casting time, impel all &cbitten &cenemies&7 for {duration}.
                                
                While impelled, enemies must obey &b%s &7of yours commands.
                                
                If failed to obey a command, they will suffer &c%s&7 damage.
                """, impelTimes, impelDamage);

        setUltimate(ultimate);
    }

    @Override
    public void onStop() {
        playerData.values().forEach(BloodfiendData::reset);
        playerData.clear();
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                playerData.forEach((player, data) -> {
                    data.tick();
                });
            }
        }.runTaskTimer(1, 1);
    }

    @Override
    public void onStart(Player player) {
        getData(player).cooldownFlight(true);
    }

    @Override
    public void onRespawn(Player player) {
        getData(player).cooldownFlight(true);
    }

    @Nullable
    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final Player player = input.getDamagerAsBukkitPlayer();
        final LivingGameEntity gameEntity = input.getEntity();

        if (!(gameEntity instanceof GamePlayer gamePlayer) || player == null) {
            return DamageOutput.OK;
        }

        final BloodfiendData data = getData(player);
        data.addSucculence(gamePlayer);

        return DamageOutput.OK;
    }

    @Nonnull
    public BloodfiendData getData(Player player) {
        return playerData.computeIfAbsent(player, BloodfiendData::new);
    }

    @Override
    public void onDeath(Player player) {
        final BloodfiendData data = playerData.remove(player);

        if (data == null) {
            return;
        }

        data.reset();
    }

    @Override
    public UltimateCallback castUltimate(Player player) {
        // FIXME (hapyl): 030, Aug 30: Maybe make player invulnerable while casting
        final BloodfiendData data = getData(player);
        final Set<GamePlayer> succulencePlayers = data.getSucculencePlayers();
        final Location location = player.getLocation().add(0.0d, 0.5d, 0.0d);

        PlayerMount.mount(player, location);
        playSoundAtTick(
                location,
                Sound.ENTITY_ELDER_GUARDIAN_CURSE,
                0.75f,
                0.1f,
                0, 2, 3, 5, 6, 9, 10, 12
        );

        // Draw particles
        succulencePlayers.forEach(target -> {
            drawTentacleParticles(location, target.getLocation());
        });

        // Spawn bats
        Set<Bat> fxBats = Sets.newHashSet();

        final Location eyeLocation = player.getEyeLocation();

        for (int i = 0; i < 10; i++) {
            fxBats.add(Entities.BAT.spawn(eyeLocation, self -> {
                self.setAwake(true);
                self.setInvulnerable(true);
            }));
        }

        PlayerLib.playSound(location, Sound.ENTITY_BAT_TAKEOFF, 0.25f);

        return new UltimateCallback() {
            @Override
            public void callback(@Nonnull Player player) {
                PlayerMount.dismount(player);
                fxBats.forEach(Bat::remove);
                fxBats.clear();
            }
        };
    }

    @Override
    public void useUltimate(Player player) {
        getData(player).newImpelInstance(this, player).nextImpel(0);
    }

    @Override
    public TwinClaws getFirstTalent() {
        return (TwinClaws) Talents.TWIN_CLAWS.getTalent();
    }

    @Override
    public BloodChalice getSecondTalent() {
        return (BloodChalice) Talents.BLOOD_CHALICE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.SUCCULENCE.getTalent();
    }

    // impel handles
    @EventHandler()
    public void handleImpelClick(PlayerInteractEvent ev) {
        final Player player = ev.getPlayer();
        final float pitch = player.getLocation().getPitch();

        workImpel(player, (impel, gamePlayer) -> {
            if (pitch <= -50) {
                impel.complete(gamePlayer, Type.CLICK_UP);
            }
            else if (pitch >= 40) {
                impel.complete(gamePlayer, Type.CLICK_DOWN);
            }
        });

    }

    @EventHandler()
    public void handleImpelSneak(PlayerToggleSneakEvent ev) {
        final Player player = ev.getPlayer();

        workImpel(player, (impel, gamePlayer) -> {
            impel.complete(gamePlayer, Type.SNEAK);
        });
    }

    @EventHandler()
    public void handleImpelJump(PlayerMoveEvent ev) {
        final Player player = ev.getPlayer();
        final Location to = ev.getTo();
        final Location from = ev.getFrom();

        if (to == null || (to.getY() <= from.getY()) || player.isOnGround()) {
            return;
        }

        workImpel(player, (impel, gamePlayer) -> {
            impel.complete(gamePlayer, Type.JUMP);
        });
    }

    @Nullable
    public ImpelInstance getPlayerImpel(Player player) {
        for (BloodfiendData data : playerData.values()) {
            final ImpelInstance impel = data.getImpelInstance();
            if (impel == null || !impel.isPlayer(player)) {
                continue;
            }

            return impel;
        }

        return null;
    }

    public void workImpel(Player player, BiConsumer<Impel, GamePlayer> consumer) {
        final ImpelInstance impelInstance = getPlayerImpel(player);

        if (impelInstance == null) {
            return;
        }

        final GamePlayer gamePlayer = CF.getPlayer(player);

        if (gamePlayer == null) {
            return;
        }

        final Impel impel = impelInstance.getImpel();

        if (impel == null) {
            return;
        }

        consumer.accept(impel, gamePlayer);
    }

    @EventHandler()
    public void handleFlight(PlayerToggleFlightEvent ev) {
        final Player player = ev.getPlayer();
        final BloodfiendData data = getData(player);

        if (!validatePlayer(player)) {
            return;
        }

        if (data.isFlying()) {
            data.stopFlying();
            return;
        }

        if (data.hasFlightCooldown()) {
            ev.setCancelled(true);
            return;
        }

        data.startFlying();
    }

    @Nullable
    @Override
    public List<String> getStrings(Player player) {
        final BloodfiendData data = getData(player);
        final int succulencePlayers = data.getSucculencePlayersCount();
        final int flightCooldown = data.getFlightCooldown();

        final TwinClaws twinClaws = getFirstTalent();
        final BloodChalice bloodChalice = getSecondTalent();

        final Candlebane pillar = twinClaws.getPillar(player);
        final Chalice chalice = bloodChalice.getChalice(player);

        return List.of(
                succulencePlayers > 0 ? "&c&l🦇 &f" + succulencePlayers : "",
                flightCooldown > 0 ? "&2&l\uD83D\uDD4A &f" + Utils.decimalFormat(flightCooldown) : "",
                pillar != null ? "&6&lⅡ &f" + Utils.decimalFormat(pillar.getTimeLeft()) : "",
                chalice != null ? "&4&l🍷 &f" + Utils.decimalFormat(chalice.getTimeLeft()) : ""
        );
    }

    public void drawTentacleParticles(Location start, Location end) {
        final double distance = start.distance(end);
        final double step = distance / 30 * 2;

        final Vector vector = end.clone().subtract(start).toVector().normalize().multiply(step);
        final Location location = start.clone();

        new TickingGameTask() {
            private double d;

            @Override
            public void run(int tick) {
                if (d >= distance) {
                    cancel();
                    return;
                }

                final double y = Math.sin(d / distance * Math.PI * 3) * 0.75d;

                location.add(vector);
                location.add(0, y, 0);

                PlayerLib.spawnParticle(location, Particle.LAVA, 1, 0.1d, 0.1d, 0.1d, 0.0f);
                PlayerLib.spawnParticle(location, Particle.FLAME, 1);
                PlayerLib.spawnParticle(location, Particle.SMALL_FLAME, 3, 0.1d, 0.1d, 0.1d, 0.05f);

                location.subtract(0, y, 0);
                d += step;
            }
        }.runTaskTimer(0, 1);
    }

    private void playSoundAtTick(Location location, Sound sound, final float pitch, float pitchIncrease, int... ticks) {
        final int lastTick = ticks[ticks.length - 1];

        new TickingGameTask() {
            private float currentPitch = pitch;

            @Override
            public void run(int tick) {
                if (tick >= lastTick) {
                    cancel();
                    return;
                }

                if (isTick()) {
                    PlayerLib.playSound(location, sound, currentPitch);
                    currentPitch += pitchIncrease;
                }
            }

            private boolean isTick() {
                for (int tick : ticks) {
                    if (getTick() == tick) {
                        return true;
                    }
                }

                return false;
            }
        }.runTaskTimer(0, 1);
    }

}
