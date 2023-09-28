package me.hapyl.fight.game.talents.archive.bloodfiend.candlebane;

import com.google.common.collect.Lists;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.TalentReference;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.archive.bloodfiend.taunt.Taunt;
import me.hapyl.fight.util.CFUtils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.Queue;

public class Candlebane extends Taunt implements TalentReference<CandlebaneTalent> {

    private static final Equipment UNDEAD_EQUIPMENT = new Equipment();

    private static final ItemStack CANDLE_TEXTURE
            = ItemBuilder.playerHeadUrl("c3b5f5b6823fbfbc848c20562b07e2d414f685e996e31d10460efa61a822aa11").asIcon();
    private static final long CLICK_DELAY = 100;

    static {
        UNDEAD_EQUIPMENT.setTexture("aef904c66f4cd357eb80a1e405783f521d284503435aede603c89db15e685709");
        UNDEAD_EQUIPMENT.setChestPlate(51, 40, 38);
        UNDEAD_EQUIPMENT.setLeggings(38, 32, 31);
        UNDEAD_EQUIPMENT.setBoots(28, 11, 8);
    }

    private final CandlebaneTalent reference;
    private final LinkedList<ArmorStand> parts;
    private final double heightOffset;
    private final Queue<ClickType> clicks;
    //private final Set<Husk> undead;

    private ArmorStand display;
    private long lastClick;

    public Candlebane(CandlebaneTalent reference, Player player, GamePlayer target) {
        super(player, target);

        final Location location = player.getLocation();

        this.reference = reference;
        this.clicks = new LinkedList<>();
        this.parts = Lists.newLinkedList();
        //this.undead = Sets.newHashSet();

        for (int i = 0; i < reference.pillarClicks; i++) {
            this.clicks.offer(ClickType.random());
        }

        this.heightOffset = 0.06d;

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
    public double getDamage() {
        return reference.getExplosionDamage();
    }

    @Override
    @Nonnull
    public EnumDamageCause getDamageCause() {
        return reference.getDamageCause();
    }

    @Override
    public void onAnimationStep(Location location) {
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

    public final void click(@Nonnull Player whoClicked, @Nonnull ClickType click) {
        if (System.currentTimeMillis() - this.lastClick <= CLICK_DELAY) {
            return;
        }

        if (this.target.isNot(whoClicked)) {
            return;
        }

        final ClickType currentClick = getCurrentClick();

        if (currentClick != click) {
            CF.getPlayerOptional(whoClicked).ifPresent(gamePlayer -> {
                gamePlayer.damage(1, this.player);
                gamePlayer.sendSubtitle("&c&lWRONG CLICK!", 0, 10, 10);

                gamePlayer.playPlayerSound(initialLocation, Sound.ENTITY_CHICKEN_EGG, 0.0f);
                gamePlayer.playPlayerSound(initialLocation, Sound.BLOCK_LAVA_POP, 0.0f);
                gamePlayer.playPlayerSound(initialLocation, Sound.ENTITY_CAT_HISS, 1.0f);
            });
            return;
        }

        clicks.poll();
        lastClick = System.currentTimeMillis();
        update(initialLocation);

        // Fx
        asPlayers(player -> {
            PlayerLib.playSound(
                    player,
                    initialLocation,
                    Sound.BLOCK_NOTE_BLOCK_PLING,
                    2.0f - (1.5f / reference.pillarClicks * clicks.size())
            );
        });

        if (clicks.isEmpty()) {
            remove();

            // Fx
            asPlayers(player -> {
                PlayerLib.playSound(player, initialLocation, Sound.ENTITY_PLAYER_LEVELUP, 1.75f);
                PlayerLib.spawnParticle(initialLocation, Particle.VILLAGER_HAPPY, 15, 0.25d, 0.25d, 0.25d, 0.0f);
            });
        }
    }

    public void remove() {
        super.remove();

        parts.forEach(Entity::remove);
        parts.clear();

        //undead.forEach(Husk::remove);
        //undead.clear();

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
            target.damage(reference.damagePerInterval, player, EnumDamageCause.CANDLEBANE);
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

    private void spawnUndead() {
        //undead.add(
        //        Entities.HUSK.spawn(initialLocation.clone().add(0.0d, 1.5d, 0.0d), self -> {
        //            self.setVisibleByDefault(false);
        //            self.setAdult();
        //            self.setHealth(1);
        //
        //            Utils.modifyAttribute(self, Attribute.GENERIC_ATTACK_SPEED, attribute -> {
        //                attribute.setBaseValue(1.0d);
        //            });
        //
        //            self.setCustomName(Chat.format("&2&l\uD83E\uDDDF"));
        //            self.setCustomNameVisible(true);
        //
        //            UNDEAD_EQUIPMENT.equip(self);
        //
        //            self.setTarget(target.getPlayer());
        //            asPlayers(player -> {
        //                player.showEntity(Main.getPlugin(), self);
        //            });
        //        }));
        //
        //target.sendMessage("&2&l\uD83E\uDDDF &aAn undead has spawned!");
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
