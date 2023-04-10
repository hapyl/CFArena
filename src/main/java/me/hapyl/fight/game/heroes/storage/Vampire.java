package me.hapyl.fight.game.heroes.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.heroes.ClassEquipment;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.heroes.storage.extra.VampireData;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComplexComponent;
import me.hapyl.fight.util.Utils;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.entity.Entities;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.GUI;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.visibility.Visibility;
import me.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Vampire extends Hero implements Listener, UIComplexComponent {

    /**
     * [SUMMARY]
     * 1 -- Summon a bat that will periodically attack opponents. Grant's stacks.
     * 2 -- Launch a swarm of bats that will rapidly attack opponents. Does not grant stacks.
     *
     * Passive -- Blood Thirst
     *
     * Your health is constantly drained.
     * ____Whenever you hit an opponent you will gain a stack of blood, up to 10 stacks.
     * ____Drink the blood to increase your damage and heal yourself.
     * __Healing, damage boost, duration and cooldown is based on the amount of stacks you used.
     *
     * ULTIMATE -> {}
     */

    private final Map<Player, VampireData> vampireData;

    public final int MAX_BLOOD_STACKS = 10;
    private final int BLOOD_POOL_COOLDOWN = 30;
    private final double HEALTH_PENALTY = 0.5d;

    private final double HEAL_PER_STACK = 2.0d;
    private final double DAMAGE_PER_STACK = 2.5d; // This is a multiplier

    private final int DURATION_BASE = 30;
    private final int DURATION_PER_STACK = 5;

    private final Material BLOOD_MATERIAL = Material.REDSTONE;

    public Vampire() {
        super("Vampire");

        vampireData = Maps.newHashMap();

        setRole(Role.MELEE);
        setItem("8d44756e0b4ece8d746296a3d5e297e1415f4ba17647ffe228385383d161a9");

        final ClassEquipment equipment = getEquipment();
        equipment.setChestplate(Color.BLACK);
        equipment.setLeggings(Color.BLACK);
        equipment.setBoots(Color.BLACK);

        setWeapon(Material.GHAST_TEAR, "Fang", 5.0d);

        setUltimate(new UltimateTalent(
                "Sanguineous Morphology",
                "Transform into a bat and fly freely for {duration}.____After duration ends, transform back into vampire and gain the opposite amount of blood you had upon casting and summon &eDracula Jr&7.__&8Eg: 10 -> 0, 7 -> 3, 2 -> 8 etc.____You cannot deal damage nor gain blood during the duration!",
                60
        ).setDurationSec(6).setCdSec(20).setTexture("473af69ed9bf67e2f5403dd7d28bbe32034749bbfb635ac1789a412053cdcbf0"));
    }

    @Override
    public void useUltimate(Player player) {
        final VampireData data = vampireData.get(player);
        final int bloodAtUse = data.getBlood();
        final int bloodAfterUse = MAX_BLOOD_STACKS - bloodAtUse;

        getFirstTalent().startCd(player, 99999);
        getSecondTalent().startCd(player, 99999);

        Utils.hidePlayer(player);

        final Bat bat = Entities.BAT.spawn(player.getLocation(), self -> {
            self.setCustomName(player.getCustomName());
            self.setInvulnerable(true);
            self.setAwake(true);
            self.setAI(false);

            // Hide for everyone but the player
            final Visibility visibility = Visibility.of(self);

            visibility.setCanSee(player, false);
            visibility.hide();
        });

        player.setAllowFlight(true);
        player.setFlying(true);
        player.setFlySpeed(0.1f);

        new GameTask() {
            private final Set<Entity> hitEntities = Sets.newHashSet();
            private int duration = getUltimateDuration();

            @Override
            public void run() {
                if (duration-- <= 0) {
                    player.setFlying(false);
                    player.setAllowFlight(false);

                    Utils.showPlayer(player);
                    bat.remove();

                    data.setBlood(bloodAfterUse);
                    updateBloodPool(player);

                    getFirstTalent().execute(player);
                    getFirstTalent().stopCd(player);
                    getSecondTalent().stopCd(player);

                    Chat.sendMessage(player, "&4&l❧ &4Blood Pool refreshed &c%s &4%s &c%s&4!", bloodAtUse, GUI.ARROW_FORWARD, bloodAfterUse);

                    hitEntities.clear();
                    this.cancel();
                    return;
                }

                // Sync bat
                bat.teleport(player);

                //Utils.getEntitiesInRange(player, 5.0d)
                //        .stream()
                //        .filter(entity -> !(entity instanceof Bat) && !hitEntities.contains(entity))
                //        .forEach(entity -> {
                //            hitEntities.add(entity);
                //
                //            GamePlayer.damageEntity(entity, 10.0d, player, EnumDamageCause.LIGHTNING);
                //            player.getWorld().strikeLightningEffect(entity.getLocation());
                //        });

            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.VAMPIRE_PET.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.BAT_SWARM.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.BLOOD_THIRST.getTalent();
    }

    @Override
    public void onDeath(Player player) {
        vampireData.remove(player);
    }

    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final Player player = input.getPlayer();
        final VampireData data = getData(player);

        if (isUsingUltimate(player)) {
            if (input.getDamageCause() == EnumDamageCause.LIGHTNING) {
                return null;
            }

            Chat.sendMessage(player, "&4&l❧ &cCannot deal while in ultimate form!");
            return DamageOutput.CANCEL;
        }

        if (!player.hasCooldown(BLOOD_MATERIAL) && data.getBlood() < MAX_BLOOD_STACKS) {
            data.addBlood(1);
            player.setCooldown(BLOOD_MATERIAL, BLOOD_POOL_COOLDOWN);
            updateBloodPool(player);
        }

        if (data.isExpired()) {
            data.resetDamageMultiplier();
            return null;
        }

        // Handle damage multiplier
        final double damage = input.getDamage();
        return new DamageOutput(damage + (damage * (data.getDamageMultiplier() / 10)));
    }

    @EventHandler()
    public void handleHandleBloodDrinking(PlayerInteractEvent ev) {
        final Player player = ev.getPlayer();
        final PlayerInventory inventory = ev.getPlayer().getInventory();

        if (ev.getHand() == EquipmentSlot.OFF_HAND ||
                !(ev.getAction() == Action.RIGHT_CLICK_BLOCK || ev.getAction() == Action.RIGHT_CLICK_AIR) ||
                !validatePlayer(player) || inventory.getHeldItemSlot() != 7) {
            return;
        }

        if (inventory.getItemInMainHand().getType() != BLOOD_MATERIAL || player.hasCooldown(BLOOD_MATERIAL) || isUsingUltimate(player)) {
            return;
        }

        final VampireData data = getData(player);
        final double damageMultiplier = DAMAGE_PER_STACK * data.getBlood();
        final double health = HEAL_PER_STACK * data.getBlood();
        final int duration = getDuration(data.getBlood());

        data.setDamageMultiplier(damageMultiplier, duration);
        Chat.sendMessage(
                player,
                "&c&lBLOOD! &a+&l%s &a❤ &7and &4+&l%s%% &4Damage &7for %ss!",
                health,
                damageMultiplier,
                BukkitUtils.roundTick(duration)
        );

        player.setCooldown(Material.REDSTONE, duration + 80);
        GamePlayer.getPlayer(player).heal(health);

        data.setBlood(0);
        updateBloodPool(player);

        inventory.setHeldItemSlot(0);

        // Fx
        PlayerLib.playSound(player, Sound.ENTITY_WITCH_DRINK, 0.0f);
        PlayerLib.playSound(player, Sound.ITEM_HONEY_BOTTLE_DRINK, 0.0f);
    }

    public int getDuration(int blood) {
        return (DURATION_BASE + (DURATION_PER_STACK * blood));
    }

    @Override
    public void onPlayersReveal() {
        new GameTask() {
            @Override
            public void run() {
                Manager.current().getCurrentGame().getAlivePlayers(Heroes.VAMPIRE).forEach(player -> {
                    if (!Manager.current().isGameInProgress() || isUsingUltimate(player.getPlayer())) {
                        return;
                    }

                    // Keep in mind players have 100 base health
                    if (player.getHealth() > HEALTH_PENALTY) {
                        player.setHealth(player.getHealth() - HEALTH_PENALTY);
                    }
                });
            }
        }.runTaskTimer(0, 20);
    }

    public VampireData getData(Player player) {
        return vampireData.computeIfAbsent(player, key -> new VampireData(player));
    }

    private void updateBloodPool(Player player) {
        final PlayerInventory inventory = player.getInventory();
        final int bloodStacks = getData(player).getBlood();

        if (bloodStacks == 0) {
            inventory.setItem(7, ItemBuilder.of(Material.GRAY_DYE, "&cNo Blood!", "Deal damage to increase blood stacks!").asIcon());
        }
        else {
            inventory.setItem(
                    7,
                    ItemBuilder.of(Material.REDSTONE)
                            .setAmount(bloodStacks)
                            .setName("&c&lBlood &7(Right Click to Consume)")
                            .setSmartLore("Consuming blood will heal you for %s and increase your damage by %s%%!".formatted(
                                    HEAL_PER_STACK * bloodStacks, (DAMAGE_PER_STACK * bloodStacks)))
                            .build()
            );
        }
    }

    @Override
    public List<String> getStrings(Player player) {
        final int bloodCd = player.getCooldown(BLOOD_MATERIAL);
        final VampireData data = getData(player);
        final List<String> strings = Lists.newArrayList();

        final double damageMultiplier = data.getDamageMultiplier();

        if (bloodCd > 0) {
            strings.add("&4&l❧ &4" + BukkitUtils.roundTick(bloodCd));
        }

        if (!data.isExpired()) {
            strings.add("&c&c\uD83D\uDDE1 &l%s &7(%s)".formatted(damageMultiplier, data.getTimeLeftFormatter()));
        }

        return strings;
    }

}
