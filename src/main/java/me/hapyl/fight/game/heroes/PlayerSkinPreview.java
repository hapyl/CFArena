package me.hapyl.fight.game.heroes;

import me.hapyl.fight.game.cosmetic.skin.Skin;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.equipment.Slot;
import me.hapyl.fight.game.playerskin.PlayerSkin;
import me.hapyl.fight.game.setting.Setting;
import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.ux.Notifier;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import me.hapyl.spigotutils.module.reflect.npc.ItemSlot;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerSkinPreview extends TickingGameTask {

    private static final double FULL_CIRCLE_PLUS_A_LITTLE_BIT = 365;
    private static final double ROTATION_PER_TICK = 2;

    public final Player player;
    public final Hero hero;
    public final PlayerSkin skin;
    public final Equipment equipment;

    protected HumanNPC npc;
    private double rotation = 0;

    public PlayerSkinPreview(@Nonnull Player player, Hero hero, Skin skin) {
        this(
                player,
                skin == null ? hero : skin.getHero().getHero(),
                skin == null ? (Settings.USE_SKINS_INSTEAD_OF_ARMOR.isEnabled(player) ? null : hero.getEquipment()) : skin.getEquipment()
        );
    }

    PlayerSkinPreview(@Nonnull Player player, @Nonnull Hero hero, @Nullable Equipment equipment) {
        this.player = player;
        this.hero = hero;

        final PlayerSkin heroSkin = hero.getSkin();
        this.skin = heroSkin != null ? heroSkin : PlayerSkin.of(player);

        this.equipment = equipment;

        final Location location = player.getLocation();
        final Vector direction = location.getDirection().normalize().setY(0.0d);

        location.add(direction.multiply(2.0d));
        location.add(0.0d, 0.05d, 0.0d);

        final Vector directionTowardsPlayer = player.getLocation()
                .toVector()
                .normalize()
                .setY(0)
                .subtract(location.toVector().normalize().setY(0));
        location.setDirection(directionTowardsPlayer);

        if (!location.getBlock().isEmpty()) {
            Notifier.error(player, "Could not preview skin because there is nowhere to put it! (Move away from blocks)");
            return;
        }

        npc = new HumanNPC(location, null);
        npc.setSkin(skin.getTexture(), skin.getSignature());

        if (equipment != null) {
            npc.setItem(ItemSlot.HEAD, equipment.getItem(Slot.HELMET));
            npc.setItem(ItemSlot.CHEST, equipment.getItem(Slot.CHESTPLATE));
            npc.setItem(ItemSlot.LEGS, equipment.getItem(Slot.LEGGINGS));
            npc.setItem(ItemSlot.FEET, equipment.getItem(Slot.BOOTS));
        }

        npc.setItem(ItemSlot.MAINHAND, hero.getWeapon().getItem());
        npc.show(player);

        runTaskTimer(1, 1);
    }

    @Override
    public void run(int tick) {
        if (npc == null) {
            cancel();
            return;
        }

        final Location location = npc.getLocation();

        if (rotation > FULL_CIRCLE_PLUS_A_LITTLE_BIT) {
            npc.remove();
            cancel();

            // Fx
            PlayerLib.spawnParticle(player, location.add(0, 1, 0), Particle.EXPLOSION_NORMAL, 20, 0.25d, 1d, 0.25d, 0.025f);
            return;
        }

        // Swing
        if (rotation > 0 && rotation % (100 / ROTATION_PER_TICK) == 0) {
            npc.swingMainHand();
            PlayerLib.playSound(player, location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f);
        }

        location.setYaw((float) (location.getYaw() + ROTATION_PER_TICK));
        npc.teleport(location);

        rotation += ROTATION_PER_TICK;
    }

}
