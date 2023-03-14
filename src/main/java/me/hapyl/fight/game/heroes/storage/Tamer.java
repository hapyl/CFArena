package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.heroes.ClassEquipment;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentHandle;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.EquipmentSlot;

public class Tamer extends Hero implements Listener {

    private final double WEAPON_DAMAGE = 8.0d; // since it's a fishing rod, we're storing the damage here
    private final int WEAPON_COOLDOWN = 10;

    public Tamer() {
        super("Tamer", "A former circus pet trainer, with pets that loyal to him only!", Material.FISHING_ROD);

        setRole(Role.STRATEGIST);

        final ClassEquipment equipment = this.getEquipment();

        equipment.setChestplate(
                ItemBuilder.leatherTunic(Color.fromRGB(14557974))
                        .addEnchant(Enchantment.THORNS, 1)
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

        this.setWeapon(new Weapon(Material.FISHING_ROD)
                .setName("Lash")
                .setDescription("An old lash used to train beasts and monsters.")
                .setId("tamer_weapon")
                .setDamage(2.0d)); // This is melee damage, weapon damage is handled in the event

        this.setUltimate(new UltimateTalent("", "", 100));
    }

    @Override
    public void useUltimate(Player player) {
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
            return new DamageOutput(true);
        }

        return null;
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
    public Talent getFirstTalent() {
        return Talents.MINE_O_BALL.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return null;
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }
}
