package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroEquipment;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.storage.tamer.MineOBall;
import me.hapyl.fight.game.talents.storage.tamer.Pack;
import me.hapyl.fight.game.talents.storage.tamer.TamerPack;
import me.hapyl.fight.game.talents.storage.tamer.TamerPacks;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.EquipmentSlot;

public class Tamer extends Hero implements Listener {

    private final double WEAPON_DAMAGE = 8.0d; // since it's a fishing rod, we're storing the damage here
    private final int WEAPON_COOLDOWN = 10;

    public Tamer() {
        super("Tamer", "A former circus pet trainer, with pets that loyal to him only!", Material.FISHING_ROD);
        setItem("fbad693d041db13ff36b81480b06456cd0ad6a57655338b956ea015a150516e2");

        setRole(Role.STRATEGIST);

        final HeroEquipment equipment = getEquipment();

        equipment.setChestplate(
                ItemBuilder.leatherTunic(Color.fromRGB(14557974))
                        .addEnchant(Enchantment.THORNS, 3)
                        .cleanToItemSack()
        );

        equipment.setLeggings(
                ItemBuilder.leatherPants(Color.fromRGB(3176419))
                        .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                        .cleanToItemSack()
        );

        equipment.setBoots(
                ItemBuilder.leatherBoots(Color.fromRGB(2490368))
                        .addAttribute(
                                Attribute.GENERIC_MOVEMENT_SPEED,
                                -0.15d,
                                AttributeModifier.Operation.MULTIPLY_SCALAR_1,
                                EquipmentSlot.FEET
                        )
                        .cleanToItemSack()
        );

        setWeapon(new Weapon(Material.FISHING_ROD)
                .setName("Lash")
                .setDescription("An old lash used to train beasts and monsters.")
                .setId("tamer_weapon")
                .setDamage(2.0d)); // This is melee damage, weapon damage is handled in the event

        final UltimateTalent ultimate = new UltimateTalent(
                "Mimicry",
                "Instantly mimic your current pack to gain it's blessings!____",
                50
        ).setDuration(400);

        for (TamerPacks value : TamerPacks.values()) {
            final Pack pack = value.getPack();

            ultimate.addDescription("&b&l" + pack.getName());
            ultimate.addDescription(pack.getMimicryDescription());
        }

        setUltimate(ultimate);
    }

    @Override
    public boolean predicateUltimate(Player player) {
        final TamerPack pack = getPlayerPack(player);
        return pack != null && pack.isAlive();
    }

    @Override
    public String predicateMessage(Player player) {
        final TamerPack pack = getPlayerPack(player);

        return pack == null ? "You don't have a pack!" : "Your pack is dead!";
    }

    @Override
    public void useUltimate(Player player) {
        final TamerPack playerPack = getPlayerPack(player);

        if (playerPack == null) {
            return;
        }

        playerPack.getPack().onUltimate(player, playerPack);
    }

    @Override
    public void onUltimateEnd(Player player) {
        executeTamerPackOnUltimateEnd(player);
    }

    @Override
    public void onDeath(Player player) {
        executeTamerPackOnUltimateEnd(player);
    }

    // Cleaned up the code a little
    public void executeTamerPackOnUltimateEnd(Player player) {
        final TamerPack pack = getPlayerPack(player);

        if (pack == null) {
            return;
        }

        pack.getPack().onUltimateEnd(player, pack);
        pack.removeAll();
    }

    @Override
    public void onStart() {
    }

    public TamerPack getPlayerPack(Player player) {
        return getFirstTalent().getPack(player);
    }

    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final Player player = input.getPlayer();
        final LivingEntity entity = input.getEntity();

        if (getFirstTalent().isPackEntity(player, entity)) {
            Chat.sendMessage(player, "&cYou cannot damage your own minion!");
            return DamageOutput.CANCEL;
        }

        return null;
    }

    // prevent pack members from damaging each other and the owner
    @EventHandler()
    public void handleMinionDamage(EntityDamageByEntityEvent ev) {
        final Entity entity = ev.getEntity();
        Entity damager = ev.getDamager();

        // root to shooter
        if (damager instanceof Projectile projectile && projectile.getShooter() instanceof LivingEntity shooter) {
            damager = shooter;
        }

        // Only allow living<->living damage
        if (!(entity instanceof LivingEntity livingEntity) || !(damager instanceof LivingEntity livingDamager)) {
            return;
        }

        final MineOBall mineOBall = getFirstTalent();
        if (!mineOBall.isPackEntity(livingDamager)) {
            return;
        }

        // Cancel event, set damage using GamePlayer
        final double finalDamage = ev.getFinalDamage();

        ev.setCancelled(true);
        ev.setDamage(0.0d);

        // cancel if friendly
        if (mineOBall.isInSamePackOrOwner(entity, damager)) {
            if (damager instanceof Creature creature) {
                creature.setTarget(null);
            }
            return;
        }

        GamePlayer.damageEntity(livingEntity, finalDamage, mineOBall.getOwner(livingDamager), EnumDamageCause.MINION);
    }

    @EventHandler()
    public void handleLash(ProjectileHitEvent ev) {
        if (!(ev.getEntity() instanceof FishHook hook) || !(hook.getShooter() instanceof Player player)) {
            return;
        }

        if (!validatePlayer(player) || player.hasCooldown(Material.FISHING_ROD)) {
            return;
        }

        if (ev.getHitBlock() != null) {
            hook.remove();
            return;
        }

        if (ev.getHitEntity() instanceof LivingEntity living) {
            GamePlayer.damageEntity(living, WEAPON_DAMAGE, player, EnumDamageCause.LEASHED);
            hook.remove();
        }

        player.setCooldown(Material.FISHING_ROD, WEAPON_COOLDOWN);
    }

    @Override
    public MineOBall getFirstTalent() {
        return (MineOBall) Talents.MINE_O_BALL.getTalent();
    }

    // Changes to dev

    @Override
    public Talent getSecondTalent() {
        return null;
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }
}
