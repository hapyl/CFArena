package me.hapyl.fight.game.heroes.archive.doctor;

import com.google.common.collect.Maps;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroEquipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.ItemStacks;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.Tick;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import javax.annotation.Nonnull;
import java.util.Map;

public class DrEd extends Hero implements UIComponent {

    private final Weapon ultimateWeapon = new PhysGun();
    private final Map<Player, BlockShield> playerShield;

    public DrEd() {
        super("Dr. Ed");

        setArchetype(Archetype.STRATEGY);

        setDescription("Simply named scientist with not so simple inventions...");
        setItem("3b51e96bddd177992d68278c9d5f1e685b60fbb94aaa709259e9f2781c76f8");

        final HeroEquipment equipment = getEquipment();
        equipment.setChestplate(179, 204, 204);
        equipment.setLeggings(148, 184, 184);
        equipment.setBoots(71, 107, 107);

        setWeapon(new GravityGun());

        setUltimate(new UltimateTalent("Upgrades People, Upgrades!", 70)
                .appendDescription("""
                        Grants Dr. Ed an upgraded version of &a%s&7 for {duration} that is capable of capturing entities' flesh and energy, allowing to manipulate them.
                        """, getWeapon().getName())
                .setDuration(200)
                .setItem(Material.GOLDEN_HORSE_ARMOR));

        playerShield = Maps.newHashMap();
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

    private void scheduleNextShield(Player player, int delay) {
        GameTask.runLater(() -> getShield(player).newElement(), Tick.fromSecond(delay));
    }

    @Override
    public void onStart(Player player) {
        // New shield
        scheduleNextShield(player, 5);
    }

    @Override
    public void onRespawn(Player player) {
        onStart(player);
    }

    @Override
    public void onDeath(Player player) {
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

    public BlockShield getShield(Player player) {
        return playerShield.computeIfAbsent(player, BlockShield::new);
    }

    @Override
    public void useUltimate(Player player) {
        final PlayerInventory inventory = player.getInventory();
        inventory.setItem(4, ultimateWeapon.getItem());
        inventory.setHeldItemSlot(4);

        GameTask.runLater(() -> {
            inventory.setItem(4, ItemStacks.AIR);
            inventory.setHeldItemSlot(0);
        }, getUltimateDuration());
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
    public String getString(Player player) {
        final BlockShield shield = getShield(player);

        if (!shield.exists()) {
            return "";
        }

        return "&6🛡 " + Chat.capitalize(shield.getType());
    }
}
