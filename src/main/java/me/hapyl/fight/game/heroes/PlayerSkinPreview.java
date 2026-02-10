package me.hapyl.fight.game.heroes;

import me.hapyl.eterna.module.inventory.Equipment;
import me.hapyl.eterna.module.npc.Npc;
import me.hapyl.eterna.module.npc.NpcAnimation;
import me.hapyl.eterna.module.npc.appearance.AppearanceBuilder;
import me.hapyl.eterna.module.npc.appearance.AppearanceHumanoid;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.player.PlayerSkin;
import me.hapyl.fight.Message;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.equipment.Slot;
import me.hapyl.fight.game.setting.EnumSetting;
import me.hapyl.fight.game.skin.Skin;
import me.hapyl.fight.game.task.TickingGameTask;
import net.kyori.adventure.text.Component;
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
    public final me.hapyl.eterna.module.player.PlayerSkin skin;
    public final HeroEquipment equipment;

    protected Npc npc;
    private double rotation = 0;

    public PlayerSkinPreview(@Nonnull Player player, @Nonnull Hero hero, @Nullable Skin skin) {
        this(
                player,
                // Null skin means it's the hero's default skin
                skin == null ? hero : skin.getHero(),
                getProperEquipment(player, hero, skin)
        );
    }

    PlayerSkinPreview(@Nonnull Player player, @Nonnull Hero hero, @Nullable HeroEquipment equipment) {
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
            Message.error(player, "Could not preview skin because there is nowhere to put it! (Move away from blocks)");
            return;
        }

        npc = new Npc(location, Component.empty(), AppearanceBuilder.ofMannequin(skin));
        
        final AppearanceHumanoid appearance = npc.getAppearance(AppearanceHumanoid.class);
        final Equipment.Builder builder = Equipment.builder();
        
        if (equipment != null) {
            builder.helmet(equipment.getItem(Slot.HELMET));
            builder.chestPlate(equipment.getItem(Slot.CHESTPLATE));
            builder.leggings(equipment.getItem(Slot.LEGGINGS));
            builder.body(equipment.getItem(Slot.BOOTS));
        }

        builder.mainHand(hero.getWeapon().createItem());
        
        appearance.setEquipment(builder.build());
        
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
            npc.destroy();
            cancel();

            // Fx
            PlayerLib.spawnParticle(player, location.add(0, 1, 0), Particle.POOF, 20, 0.25d, 1d, 0.25d, 0.025f);
            return;
        }

        // Swing
        if (rotation > 0 && rotation % (100 / ROTATION_PER_TICK) == 0) {
            npc.playAnimation(NpcAnimation.SWING_MAIN_HAND);
            PlayerLib.playSound(player, location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f);
        }

        location.setYaw((float) (location.getYaw() + ROTATION_PER_TICK));
        npc.setLocation(location);

        rotation += ROTATION_PER_TICK;
    }

    private static HeroEquipment getProperEquipment(Player player, Hero hero, Skin skin) {
        final boolean useSkinsInsteadOfArmorEnabled = EnumSetting.USE_SKINS_INSTEAD_OF_ARMOR.isEnabled(player);
        final HeroEquipment equipment = skin != null ? skin.getEquipment() : hero.getEquipment();

        // If the hero has a skin and setting enabled = return null
        if (hero.getSkin() != null && useSkinsInsteadOfArmorEnabled) {
            return null;
        }

        return equipment;
    }

}
