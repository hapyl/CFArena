package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.heroes.ClassEquipment;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.heroes.storage.extra.TamerPack;
import me.hapyl.fight.game.heroes.storage.extra.TamerPacks;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Nulls;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.EquipmentSlot;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class Tamer extends Hero implements Listener {

    private final Map<Player, TamerPack> tamerPackMap = new HashMap<>();

    public Tamer() {
        super("Tamer", "A former circus pet trainer, with pets that loyal to him only!", Material.FISHING_ROD);

        setRole(Role.STRATEGIST);

        final ClassEquipment equipment = this.getEquipment();
        equipment.setChestplate(ItemBuilder.leatherTunic(Color.fromRGB(14557974))
                                        .addEnchant(Enchantment.THORNS, 1)
                                        .cleanToItemSack());
        equipment.setLeggings(ItemBuilder.leatherPants(Color.fromRGB(3176419))
                                      .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                                      .cleanToItemSack());
        equipment.setBoots(ItemBuilder.leatherBoots(Color.fromRGB(2490368))
                                   .addAttribute(
                                           Attribute.GENERIC_MOVEMENT_SPEED,
                                           -0.15d,
                                           AttributeModifier.Operation.MULTIPLY_SCALAR_1,
                                           EquipmentSlot.FEET
                                   )
                                   .cleanToItemSack());

        this.setWeapon(new Weapon(Material.FISHING_ROD).setName("Lash")
                               .setInfo("An old lash used to train beasts and monsters.")
                               .setId("tamer_weapon")
                               .setDamage(2.0d));

        this.setUltimate(new UltimateTalent(
                "Mine 'o Ball",
                "Summon a pack of beasts that will attack nearby opponents.",
                25
        ).setItem(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZlNDc2NDA4NDM3NDRjZDU3OTY5NzlkMTE5NmZiOTM4MzE3ZWM0MmIwOWZjY2IyYzU0NWVlNGM5MjVhYzJiZCJ9fX0="));

    }

    @Override
    public void useUltimate(Player player) {
        createNewPack(player);
    }

    private void createNewPack(Player player) {
        final TamerPack oldPack = getPack(player);
        if (oldPack != null) {
            oldPack.recall();
        }

        final TamerPack pack = TamerPacks.newRandom();
        pack.spawn(player);
        tamerPackMap.put(player, pack);

        // Fx
        Chat.sendMessage(player, "&a☀ You just summoned &e%s&a!", pack.getName());
    }

    @Nullable
    public TamerPack getPack(Player player) {
        return tamerPackMap.get(player);
    }

    @Override
    public void onStop() {
        tamerPackMap.values().forEach(TamerPack::remove);
        tamerPackMap.clear();
    }

    @Override
    public void onStart() {
        // This controls 'AI' of the packs.
        new GameTask() {
            @Override
            public void run() {
                Manager.current().getCurrentGame().getAlivePlayers().forEach(gp -> {
                    final Player player = gp.getPlayer();
                    final TamerPack pack = getPack(player);

                    if (pack == null) {
                        return;
                    }

                    pack.updateEntitiesNames(player);
                    pack.getEntities().forEach(entity -> {
                        final Location location = entity.getLocation();

                        // Teleport to owner if too far away
                        if (location.distance(player.getLocation()) >= 50.0d) {
                            entity.teleport(player);
                        }

                        if (!(entity instanceof Creature creature)) {
                            return;
                        }

                        final LivingEntity target = creature.getTarget();

                        // if target is null or has died then change it
                        if (target == null || (target instanceof Player playerTarget && !GamePlayer.getPlayer(
                                playerTarget).isAlive())) {

                            final Player nearest = Utils.getNearestPlayer(location, 30.0d, player);
                            if (nearest == null) {
                                return;
                            }

                            creature.setTarget(nearest);
                            Bukkit.getPluginManager()
                                    .callEvent(new EntityTargetLivingEntityEvent(
                                            entity,
                                            nearest,
                                            EntityTargetEvent.TargetReason.CLOSEST_PLAYER
                                    ));

                            // Fx
                            PlayerLib.spawnParticle(location, Particle.LAVA, 5, 0.2d, 0.8d, 0.2d, 0.0f);
                            PlayerLib.playSound(location, Sound.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, 2.0f);

                        }
                    });

                });
            }
        }.runTaskTimer(0, 20);
    }

    // Don't allow to target owner. (Happens on spawn since we're the closest.)
    @EventHandler()
    public void handleEntityTargetLivingEntityEvent(EntityTargetLivingEntityEvent ev) {
        final Entity entity = ev.getEntity();
        final LivingEntity target = ev.getTarget();

        if (entity instanceof LivingEntity living && target instanceof Player player && validatePlayer(
                player,
                Heroes.TAMER
        )) {
            final TamerPack pack = getPack(player);
            if (pack != null && pack.isInPack(living)) {
                ev.setTarget(null);
                ev.setCancelled(true);
            }
        }
    }

    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final Player player = input.getPlayer();
        final LivingEntity entity = input.getEntity();

        if (isPackEntity(player, entity)) {
            entity.setNoDamageTicks(5);
            Chat.sendMessage(player, "&cYou cannot damage your own minion!");
            return new DamageOutput(true);
        }

        return null;
    }

    private boolean isPackEntity(Player player, LivingEntity entity) {
        final TamerPack pack = getPack(player);
        return entity != null && pack != null && pack.isInPack(entity);
    }

    @Override
    public void onDeath(Player player) {
        Nulls.runIfNotNull(tamerPackMap.get(player), TamerPack::recall);
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
            GamePlayer.damageEntity(living, weaponDamage, player, EnumDamageCause.LEASHED);
            hook.remove();
        }

        player.setCooldown(Material.FISHING_ROD, weaponCooldown);

    }

    private final double weaponDamage = 6.0d;
    private final int weaponCooldown = 10;

    @Override
    public Talent getFirstTalent() {
        return null;
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
