package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentHandle;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.storage.tamer.MineOBall;
import me.hapyl.fight.game.talents.storage.tamer.TamerPack;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creature;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.EquipmentSlot;

public class Tamer extends Hero implements Listener, DisabledHero {

    private final double WEAPON_DAMAGE = 8.0d; // since it's a fishing rod, we're storing the damage here
    private final int WEAPON_COOLDOWN = 10;

    public Tamer() {
        super("Tamer", "A former circus pet trainer, with pets that loyal to him only!", Material.FISHING_ROD);
        setItem("fbad693d041db13ff36b81480b06456cd0ad6a57655338b956ea015a150516e2");

        setRole(Role.STRATEGIST);

        final ClassEquipment equipment = getEquipment();

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

        setUltimate(new UltimateTalent("NAME", "DESCRIPTION", 100));
    }

    @Override
    public boolean predicateUltimate(Player player) {
        return getPlayerPack(player) != null;
    }

    @Override
    public String predicateMessage() {
        return "You don't have a pack!";
    }

    @Override
    public void useUltimate(Player player) {
        final TamerPack playerPack = getPlayerPack(player);
        if (playerPack == null) {
            return;
        }

        playerPack.getPack().onUltimate(player, playerPack);
    }

    public TamerPack getPlayerPack(Player player) {
        return TalentHandle.MINE_O_BALL.getPack(player);
    }

    @Override
    public void onStart() {
    }

    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final Player player = input.getPlayer();
        final LivingEntity entity = input.getEntity();

        if (TalentHandle.MINE_O_BALL.isPackEntity(player, entity)) {
            Chat.sendMessage(player, "&cYou cannot damage your own minion!");
            return DamageOutput.CANCEL;
        }

        return null;
    }

    // FIXME: 028, Mar 28, 2023 -> This doesn't work
    // prevent pack members from damaging each other
    @EventHandler()
    public void handleMinionDamage(EntityDamageByEntityEvent ev) {
        if (ev.getEntity() instanceof LivingEntity entity && ev.getDamager() instanceof LivingEntity damager) {
            if (TalentHandle.MINE_O_BALL.isInSamePack(entity, damager)) {
                ev.setCancelled(true);
                ev.setDamage(0.0d);

                if (damager instanceof Creature creature) {
                    creature.setTarget(null);
                }
            }
        }
    }

    @EventHandler()
    public void handleLash(ProjectileHitEvent ev) {
        if (!(ev.getEntity() instanceof FishHook hook) || !(hook.getShooter() instanceof Player player)) {
            return;
        }

        if (!validatePlayer(player, Heroes.TAMER) || player.hasCooldown(Material.FISHING_ROD)) {
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
