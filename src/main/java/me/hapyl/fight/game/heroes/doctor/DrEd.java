package me.hapyl.fight.game.heroes.doctor;

import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.UltimateResponse;
import me.hapyl.fight.game.loadout.HotbarSlots;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;

public class DrEd extends Hero implements UIComponent {

    private final PhysGun ultimateWeapon = new PhysGun();
    private final PlayerMap<BlockShield> playerShield;

    public DrEd(@Nonnull Heroes handle) {
        super(handle, "Dr. Ed");

        setArchetypes(Archetype.STRATEGY, Archetype.RANGE, Archetype.POWERFUL_ULTIMATE);
        setGender(Gender.MALE);

        setDescription("Simply named scientist with not so simple inventions...");
        setItem("3b51e96bddd177992d68278c9d5f1e685b60fbb94aaa709259e9f2781c76f8");

        final HeroAttributes attributes = getAttributes();
        attributes.setSpeed(115);
        attributes.setDefense(125);

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(237, 235, 235, TrimPattern.VEX, TrimMaterial.IRON);
        equipment.setLeggings(Material.IRON_LEGGINGS, TrimPattern.VEX, TrimMaterial.IRON);
        equipment.setBoots(71, 107, 107);

        setWeapon(new GravityGun());
        setUltimate(new DrEdUltimate());

        playerShield = PlayerMap.newMap();
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                playerShield.forEach((player, shield) -> {
                    final Entity entity = shield.getEntity();

                    if (!shield.exists()) {
                        return;
                    }

                    // Collision Check
                    final Location location = entity.getLocation().add(0.0d, 0.75d, 0.0d);
                    final LivingGameEntity nearest = Collect.nearestEntity(location, 0.3d, player);

                    if (nearest != null) {
                        final Material material = shield.getMaterial();

                        nearest.damage(shield.getType().getElement().getDamage(), player, EnumDamageCause.BLOCK_SHIELD);

                        shield.remove();
                        scheduleNextShield(player, 10);

                        // Fx
                        PlayerLib.playSound(location, material.createBlockData().getSoundGroup().getBreakSound(), 0.0f);
                        return;
                    }

                    shield.update();
                });
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        // New shield
        scheduleNextShield(player, 5);
    }

    @Override
    public void onRespawn(@Nonnull GamePlayer player) {
        onStart(player);
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        getShield(player).remove();

        if (getWeapon() instanceof GravityGun weapon) {
            weapon.remove(player);
        }
    }

    @Override
    public void onStop() {
        playerShield.values().forEach(BlockShield::remove);
        playerShield.clear();
    }

    public BlockShield getShield(GamePlayer player) {
        return playerShield.computeIfAbsent(player, BlockShield::new);
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.CONFUSION_POTION.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.HARVEST.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.BLOCK_SHIELD.getTalent();
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final BlockShield shield = getShield(player);

        if (!shield.exists()) {
            return "";
        }

        return "&6🛡 " + Chat.capitalize(shield.getType());
    }

    private void scheduleNextShield(GamePlayer player, int delay) {
        GameTask.runLater(() -> getShield(player).newElement(), Tick.fromSecond(delay));
    }

    private class DrEdUltimate extends UltimateTalent {
        public DrEdUltimate() {
            super("Upgrades People, Upgrades!", 70);

            setDescription("""
                    Grants Dr. Ed an upgraded version of &a%s&7 for {duration} that is capable of capturing entities' flesh and energy, allowing manipulating them.
                    """.formatted(getWeapon().getName()));

            setType(TalentType.IMPAIR);
            setItem(Material.GOLDEN_HORSE_ARMOR);
            setDurationSec(10);
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            player.setItemAndSnap(HotbarSlots.HERO_ITEM, ultimateWeapon.getItem());

            return new UltimateResponse() {
                @Override
                public void onUltimateEnd(@Nonnull GamePlayer player) {
                    ultimateWeapon.stop(player);
                }
            };
        }
    }
}
