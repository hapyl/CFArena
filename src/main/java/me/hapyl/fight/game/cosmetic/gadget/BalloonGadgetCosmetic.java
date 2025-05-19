package me.hapyl.fight.game.cosmetic.gadget;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.player.input.InputKey;
import me.hapyl.eterna.module.player.input.PlayerInput;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.List;

public class BalloonGadgetCosmetic extends Gadget implements Listener {

    private final List<String> balloonTextures = List.of(
            "52dd11da04252f76b6934bc26612f54f264f30eed74df89941209e191bebc0a2",
            "4f85522ee815d110587fffc74113f419d929598e2463b8ce9d39caa9fb6ff5ab",
            "7a2df315b43583b1896231b77bae1a507dbd7e43ad86c1cfbe3b2b8ef3430e9e",
            "cb1ae7a471729651b5667b81694e492808c5090c2b168f0a9190fd002ee50a26",
            "f052be1c06a4a325129d6f41bb84f0ea1ca6f9f69ebdfff4316e742451c79c21"
    );

    private final PotionEffect potionEffect = new PotionEffect(PotionEffectType.LEVITATION, 2, 1, false, false, false);

    private final int duration = Tick.fromSeconds(10);
    private final int balloonAnimationDuration = 10;

    private final double balloonStartingSize = 0.3d;
    private final double balloonYOffset = 2.5d;
    private final double tickYDifference = balloonYOffset / balloonAnimationDuration;

    public BalloonGadgetCosmetic(@Nonnull Key key) {
        super(key, "Inflatable Balloon");

        setDescription("""
                Inflate a balloon and launch it high in the sky to float.
                &8&o;;Sneak to pop the balloon early!
                """);

        setRarity(Rarity.EPIC);
        setTexture(balloonTextures.getFirst());

        setCooldownSec(20);
    }

    @EventHandler
    public void handleEntityUnleashEvent(EntityUnleashEvent ev) {
        if (ev.getReason() == EntityUnleashEvent.UnleashReason.DISTANCE) {
            ev.setCancelled(true);
            ev.setDropLeash(false);
        }
    }

    @Nonnull
    @Override
    public Response execute(@Nonnull Player player) {
        final Entity targetEntity = player.getTargetEntity(3);
        final Player target = targetEntity instanceof Player playerTarget ? playerTarget : player;

        new BalloonEntity(target);

        // Notify if not self
        if (player != target) {
            final PlayerProfile playerProfile = CF.getProfile(player);

            Message.info(target, "&c \uD83C\uDF88&r {%s} tied a balloon to you!".formatted(playerProfile.display().toString()));
        }

        return Response.ok();
    }

    public void createBalloon(@Nonnull Player player) {
        new BalloonEntity(player);
    }

    private ItemStack randomBalloonAsItem() {
        return ItemBuilder.playerHeadUrl(CollectionUtils.randomElementOrFirst(balloonTextures)).build();
    }

    public class BalloonEntity extends TickingGameTask {

        private final Player player;
        private final LivingEntity balloonEntity;
        private final LivingEntity leashEntity;

        public BalloonEntity(Player player) {
            this.player = player;
            this.balloonEntity = Entities.ARMOR_STAND.spawn(
                    getBalloonLocation().subtract(0, balloonYOffset, 0), self -> {
                        self.getEquipment().setHelmet(randomBalloonAsItem());
                        self.setInvisible(true);
                        self.setVisibleByDefault(false);

                        CFUtils.lockArmorStand(self);
                    }
            );

            this.leashEntity = Entities.BAT.spawn(
                    player.getLocation(), self -> {
                        self.setAI(false);
                        self.setAwake(true);
                        self.setSilent(true);
                        self.setInvisible(true);
                        self.setLeashHolder(this.balloonEntity);
                    }
            );

            // Fx
            PlayerLib.playSound(player.getLocation(), Sound.ENTITY_BREEZE_INHALE, 0.75f);

            runTaskTimer(0, 1);
        }

        @Override
        public void run(int tick) {
            // Add a little balloon animation
            if (tick <= balloonAnimationDuration) {
                final double scale = balloonStartingSize + (((1 - balloonStartingSize) * tick) / 10);
                final Location location = getBalloonLocation().subtract(0, balloonYOffset - tick * tickYDifference - scale, 0);

                CFUtils.setAttributeValue(balloonEntity, Attribute.SCALE, scale);

                // Show the entity at the first tick because it causes 1 tick jitter that I hate
                if (tick == 0) {
                    CFUtils.showEntity(balloonEntity);
                }

                balloonEntity.teleport(location);
                return;
            }

            if (tick >= duration || PlayerInput.isKeyHeld(player, InputKey.SHIFT) || !Manager.current().isLobby()) {
                cancel();
                return;
            }

            player.addPotionEffect(potionEffect);

            // Sync leash entity
            final Location location = player.getLocation();
            location.add(0, player.getEyeHeight() - 1d, 0);

            leashEntity.teleport(location);

            // Sync balloon
            final Location balloonLocation = getBalloonLocation();
            final double y = Math.sin(Math.toRadians(tick * 10)) * 0.5d;

            LocationHelper.offset(balloonLocation, 0, y, 0, () -> balloonEntity.teleport(balloonLocation));
        }

        @Override
        public void onTaskStop() {
            balloonEntity.remove();
            leashEntity.remove();

            // Fx
            final Location location = balloonEntity.getEyeLocation();

            PlayerLib.spawnParticle(location, Particle.POOF, 10, 0.25, 0.25, 0.25, 0.05f);
            PlayerLib.playSound(location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 0.75f);
        }

        private Location getBalloonLocation() {
            final Location location = player.getLocation();
            location.add(0, balloonYOffset, 0);
            location.add(LocationHelper.getVectorToTheRight(location).multiply(0.5d));

            return location;
        }

    }
}
