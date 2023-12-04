package me.hapyl.fight.game.heroes.archive.knight;

import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.Discharge;
import me.hapyl.fight.game.talents.archive.knight.StoneCastle;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.ItemStacks;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.shield.PatternTypes;
import me.hapyl.fight.util.shield.ShieldBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.spigotmc.event.entity.EntityMountEvent;

import javax.annotation.Nonnull;

public class BlastKnight extends Hero implements DisabledHero, PlayerElement, UIComponent, Listener, PlayerDataHandler {

    private final PlayerMap<BlastKnightData> dataMap = PlayerMap.newMap();

    private final ItemStack shieldItem = new ShieldBuilder(DyeColor.BLACK)
            .with(DyeColor.WHITE, PatternTypes.DLS)
            .with(DyeColor.PURPLE, PatternTypes.MR)
            .with(DyeColor.BLACK, PatternTypes.DLS)
            .with(DyeColor.PINK, PatternTypes.MC)
            .with(DyeColor.BLACK, PatternTypes.FLO)
            .build();

    private final Material shieldRechargeCdItem = Material.HORSE_SPAWN_EGG;

    public BlastKnight() {
        super("Blast Knight");

        setArchetype(Archetype.DEFENSE);
        setAffiliation(Affiliation.KINGDOM);

        setDescription("Royal Knight with high-end technology gadgets.");
        setItem("e2dfde6c2c8f0a7adf7ae4e949a804fedf95c6b9562767eae6c22a401cd02cbd");

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(Color.BLUE);
        equipment.setLeggings(Material.CHAINMAIL_LEGGINGS);
        equipment.setBoots(Material.IRON_BOOTS);

        setWeapon(Material.IRON_SWORD, "Royal Sword", "", 8.0d);
        setUltimate(new UltimateTalent(
                "Royal Horse",
                "Call upon the Royal Horse for {duration}. The horse is fast, strong and comfortable. So comfortable in fact that it doubles you damage while riding.",
                60
        ).setCooldownSec(60).setDuration(1200).setItem(Material.SADDLE));

    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        dataMap.removeAnd(player, PlayerData::remove);
    }

    @Override
    public void onStop() {
        dataMap.forEachAndClear(PlayerData::remove);
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {

        return UltimateCallback.OK;
    }

    @EventHandler()
    public void handleEntityDeath(EntityDeathEvent ev) {
        //final Entity entity = ev.getEntity();
        //if (entity instanceof Horse horse && horseMap.containsValue(horse)) {
        //    ev.getDrops().clear();
        //    horseMap.values().remove(horse);
        //}
    }

    @EventHandler()
    public void handleHorseInteract(EntityMountEvent ev) {
        //if (ev.getMount() instanceof Horse horse && ev.getEntity() instanceof Player player && horseMap.containsValue(horse)) {
        //    final GamePlayer gamePlayer = CF.getPlayer(player);
        //
        //    if (gamePlayer == null || horseMap.get(gamePlayer) == horse) {
        //        return;
        //    }
        //
        //    ev.setCancelled(true);
        //    gamePlayer.sendMessage("&cThis is not your Horse!");
        //    gamePlayer.playSound(Sound.ENTITY_HORSE_ANGRY, 1.0f);
        //}
    }

    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final GamePlayer player = input.getDamagerAsPlayer();

        //if (player == null) {
        //    return null;
        //}
        //
        //final Horse playerHorse = getPlayerHorse(player);
        //final LivingGameEntity victim = input.getEntity();
        //
        //if (!isUsingUltimate(player) || playerHorse == null || victim == player) {
        //    return null;
        //}
        //
        //if (victim.is(playerHorse)) {
        //    return DamageOutput.CANCEL;
        //}
        //
        //if (playerHorse.getPassengers().contains(player.getPlayer())) {
        //    victim.setVelocity(victim.getLocation().getDirection().normalize().multiply(-1.0d));
        //
        //    return new DamageOutput(input.getDamage() * 1.5d);
        //}

        return null;
    }

    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        final GamePlayer player = input.getEntityAsPlayer();
        final GameEntity damager = input.getDamager();
        final double damage = input.getDamage();

        if (!player.isBlocking() || damager == null || damage > 0.0d) {
            return DamageOutput.OK;
        }

        final double dot = player.dot(damager.getLocation());

        if (dot <= 0.6d) {
            return DamageOutput.OK;
        }

        final BlastKnightData data = getPlayerData(player);

        if (data.isShieldOnCooldown()) {
            return DamageOutput.OK;
        }

        data.incrementShieldCharge();

        // Interrupt shield
        player.setItem(EquipmentSlot.OFF_HAND, null);
        player.schedule(() -> player.setItem(EquipmentSlot.OFF_HAND, shieldItem), 1);

        // Fx
        player.playSound(Sound.ITEM_SHIELD_BREAK, 1.0f);

        return new DamageOutput(0.0d, false);
    }

    @Nonnull
    @Override
    public BlastKnightData getPlayerData(@Nonnull GamePlayer player) {
        return dataMap.computeIfAbsent(player, BlastKnightData::new);
    }

    public int getShieldCharge(GamePlayer player) {
        return getPlayerData(player).getShieldCharge();
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.setItem(EquipmentSlot.OFF_HAND, shieldItem);
    }

    @Override
    public void onRespawn(@Nonnull GamePlayer player) {
        onStart(player);
    }

    @Override
    public StoneCastle getFirstTalent() {
        return (StoneCastle) Talents.STONE_CASTLE.getTalent();
    }

    @Override
    public Discharge getSecondTalent() {
        return (Discharge) Talents.DISCHARGE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.SHIELDED.getTalent();
    }

    @Override
    public @Nonnull String getString(@Nonnull GamePlayer player) {
        if (player.hasCooldown(shieldRechargeCdItem)) {
            return "&7🛡 &l" + BukkitUtils.roundTick(player.getCooldown(shieldRechargeCdItem)) + "s";
        }

        return "&f🛡 &l" + getShieldCharge(player);
    }

    private void explodeShield(GamePlayer player) {
        final PlayerInventory inventory = player.getInventory();
        inventory.setItem(EquipmentSlot.OFF_HAND, ItemStacks.AIR);
        //shieldCharge.put(player, 0);

        player.setCooldown(shieldRechargeCdItem, 200);

        GameTask.runLater(() -> {
            //inventory.setItem(EquipmentSlot.OFF_HAND, itemShield);
            //shield.updateTexture(player, 0);
        }, 200);

        // Explode
        Collect.nearbyEntities(player.getLocation(), 10.0d).forEach(entity -> {
            if (entity.equals(player)) {
                return;
            }

            entity.damage(30.0d, player, EnumDamageCause.NOVA_EXPLOSION);
            entity.setVelocity(entity.getLocation().getDirection().multiply(-2.0d));
        });

        // Fx
        final Location location = player.getEyeLocation();
        PlayerLib.spawnParticle(location, Particle.SMOKE_NORMAL, 50, 10.0d, 0.5d, 10.0d, 0.5f);
        PlayerLib.spawnParticle(location, Particle.FIREWORKS_SPARK, 50, 10.0d, 0.5d, 10.0d, 0.5f);
        PlayerLib.spawnParticle(location, Particle.EXPLOSION_LARGE, 1, 0.0d, 0.5d, 0.0d, 0.0f);

        PlayerLib.playSound(location, Sound.ITEM_SHIELD_BREAK, 0.0f);
        PlayerLib.playSound(location, Sound.ENTITY_BLAZE_HURT, 0.0f);
    }
}
