package me.hapyl.fight.game.talents.bloodfiend.candlebane;

import com.google.common.collect.Lists;
import me.hapyl.fight.fx.EntityFollowingParticle;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.talents.bloodfiend.taunt.Taunt;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class Candlebane extends Taunt {

    private static final Particle.DustTransition TRANSITION = new Particle.DustTransition(
            Color.fromRGB(207, 120, 120),
            Color.fromRGB(166, 28, 28),
            1
    );

    private static final ItemStack CANDLE_TEXTURE
            = ItemBuilder.playerHeadUrl("c3b5f5b6823fbfbc848c20562b07e2d414f685e996e31d10460efa61a822aa11").asIcon();

    private static final long CLICK_DELAY = 100;

    private final LinkedList<ArmorStand> parts;
    private final double heightOffset;
    private final Queue<ClickType> clicks;

    private ArmorStand display;
    private long lastClick;

    public Candlebane(CandlebaneTalent talent, GamePlayer player, Location location) {
        super(talent, player, location);

        final Location playerLocation = player.getLocation();

        this.clicks = Lists.newLinkedList();
        this.parts = Lists.newLinkedList();

        // Add clicks
        for (int i = 0; i < talent.pillarClicks; i++) {
            clicks.offer(i % 2 == 0 ? ClickType.RIGHT : ClickType.LEFT);
        }

        heightOffset = 0.06d;

        for (int i = 0; i < talent.pillarHeight; i++) {
            final ArmorStand stand = createStand(playerLocation);

            if (i % 2 == 0) {
                stand.setHeadPose(new EulerAngle(0.0d, Math.toRadians(45.0d), 0.0d));
            }

            playerLocation.add(0.0d, 0.6d, 0.0d);
            parts.add(stand);
        }

        tauntParticle = new TauntParticle(3) {
            @Override
            public void draw(@Nonnull Location location) {
                player.spawnWorldParticle(location, Particle.FLAME, 1);
            }

            @Override
            protected double yOffset() {
                return 3;
            }

            @Override
            protected double slope() {
                return 0.6d;
            }

            @Override
            protected double piIncrement() {
                return 48;
            }
        };
    }

    @Override
    public void onAnimationStep(@Nonnull Location location) {
        location.setYaw(location.getYaw() + 15);
        update(location);
    }

    @Override
    public void onAnimationEnd() {
        display = spawnEntity(Entities.ARMOR_STAND_MARKER, getDisplayLocation(), self -> {
            self.setGravity(false);
            self.setInvisible(true);
            self.setInvulnerable(true);
        });

        update(location);
        start(getTalent().getDuration());
    }

    @Nullable
    public ClickType getCurrentClick() {
        return clicks.peek();
    }

    public final void click(@Nonnull GamePlayer whoClicked, @Nonnull ClickType click) {
        if (System.currentTimeMillis() - this.lastClick <= CLICK_DELAY) {
            return;
        }

        final ClickType currentClick = getCurrentClick();

        if (currentClick != click) {
            whoClicked.sendSubtitle("&c&lWRONG CLICK!", 0, 10, 10);

            whoClicked.playSound(location, Sound.ENTITY_CHICKEN_EGG, 0.0f);
            whoClicked.playSound(location, Sound.BLOCK_LAVA_POP, 0.0f);
            whoClicked.playSound(location, Sound.ENTITY_CAT_HISS, 1.0f);

            // Push back as a 'punishment' for wrong click
            whoClicked.setVelocity(whoClicked.getDirection().normalize().multiply(-0.55).setY(0.33d));
            return;
        }

        clicks.poll();
        lastClick = System.currentTimeMillis();

        update(location);

        // Fx
        player.playSound(
                location,
                Sound.BLOCK_NOTE_BLOCK_PLING,
                2.0f - (1.5f / getTalent().pillarClicks * clicks.size())
        );

        // Compacted
        if (clicks.isEmpty()) {
            remove();

            // Fx
            player.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1.75f);
            player.spawnParticle(location, Particle.HAPPY_VILLAGER, 15, 0.25d, 0.25d, 0.25d, 0.0f);
        }
    }

    public void remove() {
        super.remove();

        parts.forEach(Entity::remove);
        parts.clear();

        if (display != null) {
            display.remove();
        }

        // Fx
        player.spawnWorldParticle(
                location.add(0.0d, getTalent().pillarHeight / 2.0d, 0.0d),
                Particle.POOF,
                15,
                0.25d,
                1.0d,
                0.25d,
                0.0f
        );
    }

    @Nonnull
    @Override
    public CandlebaneTalent getTalent() {
        return (CandlebaneTalent) super.getTalent();
    }

    @Nonnull
    @Override
    public String getCharacter() {
        return "&6&lⅡ";
    }

    @Override
    public void tick(int tick) {
        final int timeLeft = getTimeLeft();
        final ArmorStand last = parts.peekLast();
        final ClickType currentClick = getCurrentClick();

        display.teleport(getDisplayLocation());
        display.setCustomName(Chat.format("&4&k| &e%s's %s &4&k|".formatted(player.getName(), getName())));
        display.setCustomNameVisible(true);

        if (last != null) {
            last.setCustomName(Chat.format("%s &c%s".formatted(currentClick == null
                    ? "&8&lNONE!"
                    : currentClick.toString(), CFUtils.decimalFormatTick(timeLeft)
            )));
            last.setCustomNameVisible(true);
        }
    }

    @Override
    public void tick(@Nonnull Collection<LivingGameEntity> entities) {
        final CandlebaneTalent talent = getTalent();

        entities.forEach(entity -> {
            new EntityFollowingParticle(2, getDisplayLocation(), entity) {
                @Override
                public void draw(int tick, @Nonnull Location location) {
                    final double y = Math.sin(Math.toRadians(tick * 20)) * 0.75d;

                    CFUtils.offsetLocation(location, y, () -> {
                        player.spawnWorldParticle(location, Particle.LAVA, 1, 0.1d, 0.1d, 0.1d, 0.0f);
                        player.spawnWorldParticle(location, Particle.FLAME, 1);
                        player.spawnWorldParticle(location, Particle.SMALL_FLAME, 3, 0.1d, 0.1d, 0.1d, 0.05f);
                    });
                }

                @Override
                public void onHit(@Nonnull Location location) {
                    entity.damageNoKnockback(talent.damagePerInterval, player, EnumDamageCause.CANDLEBANE);
                }
            }.runTaskTimer(0, 1);
        });
    }

    public boolean isPart(Entity entity) {
        for (ArmorStand part : parts) {
            if (part.equals(entity)) {
                return true;
            }
        }

        return false;
    }

    @Nonnull
    private Location getDisplayLocation() {
        final ArmorStand last = parts.peekLast();

        if (last == null) {
            return location;
        }

        return last.getLocation().add(0.0d, 2.25d, 0.0d);
    }

    private ArmorStand createStand(Location location) {
        return spawnEntity(Entities.ARMOR_STAND, location, self -> {
            self.setInvulnerable(true);
            self.setInvisible(true);
            self.setHelmet(CANDLE_TEXTURE);
            self.setGravity(false);

            CFUtils.lockArmorStand(self);
        });
    }

    private void update(Location updateLocation) {
        final Location location = updateLocation.clone();
        final double y = heightOffset * clicks.size();

        for (int i = 0; i < getTalent().pillarHeight; i++) {
            final ArmorStand stand = parts.get(i);

            if (i % 2 == 0) {
                stand.setHeadPose(new EulerAngle(0.0d, Math.toRadians(45.0d), 0.0d));
            }

            stand.teleport(location);
            location.add(0.0d, y, 0.0d);
        }
    }

}
