package me.hapyl.fight.game.talents.archive.bloodfiend;

import com.google.common.collect.Lists;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.TalentHandle;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.bloodfield.Bloodfiend;
import me.hapyl.fight.game.heroes.archive.bloodfield.BloodfiendData;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import javax.annotation.Nonnull;
import java.util.LinkedList;

public class Candlebane extends TickingGameTask implements TalentHandle<TwinClaws> {

    private static final ItemStack CANDLE_TEXTURE
            = ItemBuilder.playerHeadUrl("c3b5f5b6823fbfbc848c20562b07e2d414f685e996e31d10460efa61a822aa11").asIcon();

    private static final long CLICK_DELAY = 50;

    private final Player player;
    private final Location startLocation;
    private final LinkedList<ArmorStand> parts;
    private final double heightOffset;
    private final ArmorStand display;
    private long lastClick;
    private int clicksLeft;
    private Click click;

    public Candlebane(Player player, Location startLocation) {
        final TwinClaws talent = getTalent();

        Utils.anchorLocation(startLocation);

        this.player = player;
        this.startLocation = startLocation.subtract(0.0d, 1.25d, 0.0d);
        this.click = Click.LEFT;
        this.parts = Lists.newLinkedList();
        this.clicksLeft = talent.pillarClicks;
        this.heightOffset = 0.06d;

        final Location location = startLocation.clone();

        for (int i = 0; i < talent.pillarHeight; i++) {
            final ArmorStand stand = createStand(location);

            if (i % 2 == 0) {
                stand.setHeadPose(new EulerAngle(0.0d, Math.toRadians(45.0d), 0.0d));
            }

            location.add(0.0d, 0.6d, 0.0d);
            parts.add(stand);
        }

        display = Entities.ARMOR_STAND_MARKER.spawn(location, self -> {
            self.setGravity(false);
            self.setInvisible(true);
            self.setInvulnerable(true);
        });

        update();
        runTaskTimer(0, 1);
    }

    public void click(Player player, Click click) {
        if (System.currentTimeMillis() - this.lastClick <= CLICK_DELAY) {
            return;
        }

        if (this.player == player) {
            Chat.sendMessage(player, "&aThis is your Candlebane!");
            return;
        }

        if (this.click != click) {
            CF.getPlayerOptional(player).ifPresent(gamePlayer -> {
                gamePlayer.damage(1, this.player);
                gamePlayer.sendSubtitle("&cWrong Click!", 0, 10, 10);

                gamePlayer.playSound(startLocation, Sound.ENTITY_CHICKEN_EGG, 0.0f);
                gamePlayer.playSound(startLocation, Sound.BLOCK_LAVA_POP, 0.0f);
                gamePlayer.playSound(startLocation, Sound.ENTITY_CAT_HISS, 1.0f);
            });
            return;
        }

        this.click = this.click == Click.LEFT ? Click.RIGHT : Click.LEFT;
        this.clicksLeft--;
        this.lastClick = System.currentTimeMillis();
        update();

        // Fx
        PlayerLib.playSound(startLocation, Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f - (clicksLeft / 10.0f));

        if (clicksLeft <= 0) {
            remove();

            // Fx
            PlayerLib.playSound(startLocation, Sound.ENTITY_PLAYER_LEVELUP, 1.75f);
            PlayerLib.spawnParticle(startLocation.add(0.0d, 1.75d, 0.0d), Particle.VILLAGER_HAPPY, 15, 0.2d, 0.2d, 0.2d, 0.0f);
        }
    }

    public void remove() {
        parts.forEach(Entity::remove);
        parts.clear();

        display.remove();
        cancel();
    }

    @Override
    public void run(int tick) {
        final TwinClaws talent = getTalent();
        final Bloodfiend bloodfiend = Heroes.BLOODFIEND.getHero(Bloodfiend.class);
        final BloodfiendData data = bloodfiend.getData(player);
        final int pillarDuration = talent.pillarDuration;

        // Kill if not removed
        if (tick >= pillarDuration) {
            remove();

            data.getSucculencePlayers().forEach(target -> {
                target.setLastDamageCause(EnumDamageCause.TWIN_PILLAR);
                target.setLastDamager(CF.getOrCreatePlayer(player));
                target.die(true);

                target.sendMessage("&6&lⅡ &e%s's Candlebane took your life!", player.getName());
            });
            return;
        }

        // Fx
        if (tick % 10 == 0) {
            data.getSucculencePlayers().forEach(target -> {
                target.playSound(startLocation, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 0.0f);
            });
        }

        final int timeLeft = getTimeLeft();
        final ArmorStand last = parts.getLast();

        display.teleport(last.getLocation().add(0.0d, 2.25d, 0.0d));
        display.setCustomName(Chat.format("&b%s's Candlebane", player.getName()));
        display.setCustomNameVisible(true);

        last.setCustomName(Chat.format("%s &c%s", click.toString(), Utils.decimalFormat(timeLeft)));
        last.setCustomNameVisible(true);
    }

    public int getTimeLeft() {
        final TwinClaws talent = getTalent();
        return talent.pillarDuration - getTick();
    }

    @Nonnull
    @Override
    public TwinClaws getTalent() {
        return Talents.TWIN_CLAWS.getTalent(TwinClaws.class);
    }

    public boolean isPart(int entityId) {
        for (ArmorStand part : parts) {
            if (part.getEntityId() == entityId) {
                return true;
            }
        }

        return false;
    }

    private ArmorStand createStand(Location location) {
        return Entities.ARMOR_STAND.spawn(location, self -> {
            self.setInvulnerable(true);
            self.setInvisible(true);
            self.setHelmet(CANDLE_TEXTURE);
            self.setGravity(false);

            Utils.lockArmorStand(self);
        });
    }

    private void update() {
        final Location location = startLocation.clone();
        final TwinClaws talent = getTalent();
        final double y = heightOffset * clicksLeft;

        for (int i = 0; i < talent.pillarHeight; i++) {
            final ArmorStand stand = parts.get(i);

            if (i % 2 == 0) {
                stand.setHeadPose(new EulerAngle(0.0d, Math.toRadians(45.0d), 0.0d));
            }

            stand.teleport(location);
            location.add(0.0d, y, 0.0d);
        }
    }

    public enum Click {
        LEFT(ChatColor.YELLOW),
        RIGHT(ChatColor.GOLD);

        public final ChatColor color;

        Click(ChatColor color) {
            this.color = color;
        }

        @Override
        public String toString() {
            return color + ChatColor.BOLD.toString() + name();
        }
    }
}
