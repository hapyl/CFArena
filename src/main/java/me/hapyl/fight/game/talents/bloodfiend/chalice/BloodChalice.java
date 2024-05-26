package me.hapyl.fight.game.talents.bloodfiend.chalice;

import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.bloodfiend.taunt.Taunt;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Collection;

public class BloodChalice extends Taunt {

    private static final ItemStack[] CHALICE_TEXTURES = {
            createTexture("492f08c1294829d471a8e0109a06fb6ae717e5faf3e0808408a66d889227dac7"),
            createTexture("d135853b09d40700c3ee3ce388185067f229d85add03c838bd819675257b6889"),
            createTexture("7838abd9dc57f4e805486236c1896bc962bd89719ab8fb0a4093d8c0f654dacc")
    };

    private static final ItemStack BLOOD_TEXTURE
            = ItemBuilder.playerHeadUrl("c0340923a6de4825a176813d133503eff186db0896e32b6704928c2a2bf68422").asIcon();

    private final ArmorStand[] stand;

    public BloodChalice(BloodChaliceTalent reference, GamePlayer player, Location location) {
        super(reference, player, location);
        final Location playerLocation = player.getLocationBehindFromEyes(1);

        this.stand = new ArmorStand[2];

        this.stand[0] = spawnEntity(Entities.ARMOR_STAND, playerLocation, self -> {
            self.setHelmet(CHALICE_TEXTURES[0]);
            self.setGravity(false);
            self.setInvulnerable(true);
            self.setInvisible(true);
            self.setSilent(true);

            CFUtils.lockArmorStand(self);
        });

        tauntParticle = new TauntParticle(2) {
            @Override
            public void draw(@Nonnull Location location) {
                player.spawnWorldParticle(location, Particle.DRIPPING_LAVA, 1);
                player.spawnWorldParticle(location, Particle.FALLING_LAVA, 1);
            }

            @Override
            protected double yOffset() {
                return 1.5d;
            }

            @Override
            protected double slope() {
                return 0.5d;
            }

            @Override
            protected double piIncrement() {
                return 64;
            }
        };
    }

    @Override
    public void onAnimationStep(@Nonnull Location location) {
        location.setYaw(location.getYaw() + 15);
        stand[0].teleport(location);
    }

    @Override
    public void onAnimationEnd() {
        this.location.add(0.0d, 1.5d, 0.0d);

        this.stand[1] = spawnEntity(Entities.ARMOR_STAND, location, self -> {
            self.setMarker(true);
            self.setSmall(true);
            self.setInvulnerable(true);
            self.setInvisible(true);
            self.setSilent(true);
            self.setHelmet(BLOOD_TEXTURE);
        });

        start(getTalent().getDuration());
    }

    @Override
    public void tick(int tick) {
        final int timeLeft = getTimeLeft();
        final Location baseLocation = stand[0].getLocation();

        if (stand[0].isDead()) {
            remove();
            return;
        }

        stand[0].setCustomName(Chat.format("&4&k| &e%s's %s %s &4&k|".formatted(
                player.getName(),
                getName(),
                CFUtils.decimalFormatTick(timeLeft)
        )));
        stand[0].setCustomNameVisible(true);

        final Location location = stand[1].getLocation();

        // Fx
        final double y = 1.5d + (Math.sin(Math.toRadians(tick * 2)) / 2);

        baseLocation.add(0, y, 0);

        location.setYaw(location.getYaw() + 5);
        baseLocation.setYaw(location.getYaw());

        stand[1].teleport(baseLocation);

        player.spawnWorldParticle(
                baseLocation.add(0.0d, 0.76d, 0.0d),
                Particle.DUST,
                1,
                0.2d,
                0.2d,
                0.2d,
                new Particle.DustOptions(Color.RED, 1)
        );
    }

    @Override
    public void tick(@Nonnull Collection<LivingGameEntity> entities) {
    }

    @Nonnull
    @Override
    public String getCharacter() {
        return "&4&l🍷";
    }

    @Override
    public void remove() {
        super.remove();

        final Location location = stand[0].getLocation().add(0.0d, 1.5d, 0.0d);

        CFUtils.forEach(stand, Entity::remove);

        // Fx
        player.playSound(location, Sound.BLOCK_GLASS_BREAK, 0.0f);
        player.spawnParticle(location, Particle.POOF, 15, 0.1d, 0.1d, 0.1d, 0.05f);
    }

    private static ItemStack createTexture(String texture) {
        return ItemBuilder.playerHeadUrl(texture).asIcon();
    }
}
