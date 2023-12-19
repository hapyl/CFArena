package me.hapyl.fight.game.talents.archive.bloodfiend.candlebane;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.HeroReference;
import me.hapyl.fight.game.TalentReference;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.bloodfield.Bloodfiend;
import me.hapyl.fight.game.talents.archive.bloodfiend.taunt.Taunt;
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
import java.util.LinkedList;
import java.util.Queue;

public class Candlebane extends Taunt implements TalentReference<CandlebaneTalent>, HeroReference<Bloodfiend> {

    private static final Particle.DustTransition TRANSITION = new Particle.DustTransition(
            Color.fromRGB(207, 120, 120),
            Color.fromRGB(166, 28, 28),
            1
    );

    private static final ItemStack CANDLE_TEXTURE
            = ItemBuilder.playerHeadUrl("c3b5f5b6823fbfbc848c20562b07e2d414f685e996e31d10460efa61a822aa11").asIcon();

    private static final long CLICK_DELAY = 100;

    private final CandlebaneTalent reference;
    private final Bloodfiend hero;
    private final LinkedList<ArmorStand> parts;
    private final double heightOffset;
    private final Queue<ClickType> clicks;

    private ArmorStand display;
    private long lastClick;

    public Candlebane(CandlebaneTalent reference, GamePlayer player, GamePlayer target) {
        super(player, target);

        final Location location = player.getLocation();

        this.reference = reference;
        this.hero = Heroes.BLOODFIEND.getHero(Bloodfiend.class);
        this.clicks = Lists.newLinkedList();
        this.parts = Lists.newLinkedList();

        // Add clicks
        for (int i = 0; i < reference.pillarClicks; i++) {
            clicks.offer(i % 2 == 0 ? ClickType.RIGHT : ClickType.LEFT);
        }

        heightOffset = 0.06d;

        for (int i = 0; i < reference.pillarHeight; i++) {
            final ArmorStand stand = createStand(location);

            if (i % 2 == 0) {
                stand.setHeadPose(new EulerAngle(0.0d, Math.toRadians(45.0d), 0.0d));
            }

            location.add(0.0d, 0.6d, 0.0d);
            parts.add(stand);
        }
    }

    @Override
    @Nonnull
    public EnumDamageCause getDamageCause() {
        return reference.getDamageCause();
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

        update(initialLocation);
        start(reference.getDuration());
    }

    @Override
    public void onTaskStop() {
        reference.removeTaunt(player);
    }

    @Nullable
    public ClickType getCurrentClick() {
        return clicks.peek();
    }

    public final void click(@Nonnull GamePlayer whoClicked, @Nonnull ClickType click) {
        if (System.currentTimeMillis() - this.lastClick <= CLICK_DELAY) {
            return;
        }

        if (!target.equals(whoClicked)) {
            return;
        }

        final ClickType currentClick = getCurrentClick();

        if (currentClick != click) {
            whoClicked.damage(1, player);
            whoClicked.sendSubtitle("&c&lWRONG CLICK!", 0, 10, 10);

            whoClicked.playSound(initialLocation, Sound.ENTITY_CHICKEN_EGG, 0.0f);
            whoClicked.playSound(initialLocation, Sound.BLOCK_LAVA_POP, 0.0f);
            whoClicked.playSound(initialLocation, Sound.ENTITY_CAT_HISS, 1.0f);
            return;
        }

        clicks.poll();
        lastClick = System.currentTimeMillis();

        update(initialLocation);

        // Fx
        asPlayers(player -> {
            player.playSound(
                    initialLocation,
                    Sound.BLOCK_NOTE_BLOCK_PLING,
                    2.0f - (1.5f / reference.pillarClicks * clicks.size())
            );
        });

        // Compacted
        if (clicks.isEmpty()) {
            remove();

            // Fx
            asPlayers(player -> {
                player.playSound(initialLocation, Sound.ENTITY_PLAYER_LEVELUP, 1.75f);
                player.spawnParticle(initialLocation, Particle.VILLAGER_HAPPY, 15, 0.25d, 0.25d, 0.25d, 0.0f);
            });
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
        spawnParticle(
                initialLocation.add(0.0d, reference.pillarHeight / 2.0d, 0.0d),
                Particle.EXPLOSION_NORMAL,
                15,
                0.25d,
                1.0d,
                0.25d,
                0.0f
        );
    }

    @Nonnull
    @Override
    public String getName() {
        return reference.getName();
    }

    @Nonnull
    @Override
    public String getCharacter() {
        return "&6&lⅡ";
    }

    @Override
    public void run(int tick) {
        final int timeLeft = getTimeLeft();
        final ArmorStand last = parts.getLast();
        final ClickType currentClick = getCurrentClick();

        // Damage
        if (tick % reference.interval == 0) {
            target.setLastDamager(player);
            target.damage(reference.damagePerInterval, EnumDamageCause.CANDLEBANE);

            // Draw lines
            hero.drawTentacleParticles(initialLocation.clone().add(0, 3, 0), target.getLocation(), draw -> {
                player.spawnWorldParticle(draw, Particle.DUST_COLOR_TRANSITION, 2, 0.1, 0.1, 0.1, TRANSITION);
            });
        }

        display.teleport(getDisplayLocation());
        display.setCustomName(Chat.format("&bTaunting " + target.getName()));
        display.setCustomNameVisible(true);

        last.setCustomName(Chat.format(
                "%s &c%s",
                currentClick == null ? "&8&lNONE!" : currentClick.toString(),
                CFUtils.decimalFormatTick(timeLeft)
        ));
        last.setCustomNameVisible(true);
    }

    @Nonnull
    @Override
    public CandlebaneTalent getTalent() {
        return reference;
    }

    public boolean isPart(int entityId) {
        for (ArmorStand part : parts) {
            if (part.getEntityId() == entityId) {
                return true;
            }
        }

        return false;
    }

    @Nonnull
    @Override
    public Bloodfiend getHero() {
        return hero;
    }

    @Nonnull
    private Location getDisplayLocation() {
        final ArmorStand last = parts.getLast();

        if (last == null) {
            return initialLocation;
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
        final CandlebaneTalent talent = getTalent();
        final double y = heightOffset * clicks.size();

        for (int i = 0; i < talent.pillarHeight; i++) {
            final ArmorStand stand = parts.get(i);

            if (i % 2 == 0) {
                stand.setHeadPose(new EulerAngle(0.0d, Math.toRadians(45.0d), 0.0d));
            }

            stand.teleport(location);
            location.add(0.0d, y, 0.0d);
        }
    }
}
