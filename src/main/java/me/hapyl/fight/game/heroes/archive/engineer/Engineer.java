package me.hapyl.fight.game.heroes.archive.engineer;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.engineer.Construct;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Engineer extends Hero implements Listener, PlayerDataHandler, UIComponent, DisplayFieldProvider {

    public static final int MAX_IRON = 10;

    @DisplayField public final double ultimateInWaterDamage = 10;
    @DisplayField public final int ultimateHitCd = 5;

    public final Weapon ironFist = new Weapon(Material.IRON_BLOCK).setDamage(10.0d).setName("&6&lIron Fist");

    private final int ironRechargeRate = 60;
    private final PlayerMap<EngineerData> playerData = PlayerMap.newMap();

    public Engineer(@Nonnull Heroes handle) {
        super(handle, "Engineer");

        setArchetype(Archetype.STRATEGY);

        setDescription("""
                A Genius with 12 PHDs. He made all of his buildings himself. Though, he uses just two of those.
                And your best hope - Not pointed at you.
                """);
        setItem("55f0bfea3071a0eb37bcc2ca6126a8bdd79b79947734d86e26e4d4f4c7aa9");

        setWeapon(new Weapon(Material.IRON_HOE)
                .setName("Prototype Wrench")
                .setDescription("""
                        A Prototype Wrench for all the needs.
                        It... Probably hurts to be hit with it."""
                ).setDamage(5.0d));

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(255, 0, 0);
        equipment.setLeggings(0, 0, 0);
        equipment.setBoots(0, 0, 0);

        setUltimate(new UltimateTalent(
                this, "Mecha-Industries", """
                Instantly create a &fmech suit&7 and pilot it for {duration}.
                                
                The suit provides &cattack&7 power.
                &8;;Looks like a wire sticking out of it, probably should keep away from water.
                """, 70
        ).setItem(Material.IRON_SWORD)
                .setDurationSec(25)
                .setCooldownSec(35)
                .setSound(Sound.BLOCK_ANVIL_USE, 0.25f));

        copyDisplayFieldsToUltimate();
    }

    @EventHandler()
    public void handleEntityInteractEvent(PlayerInteractAtEntityEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());
        final EquipmentSlot hand = ev.getHand();
        final Material cooldownMaterial = getPassiveTalent().getMaterial();

        if (player == null || !validatePlayer(player) || hand == EquipmentSlot.OFF_HAND) {
            return;
        }

        final ItemStack heldItem = player.getHeldItem();

        // Didn't click with iron
        if (heldItem.getType() != cooldownMaterial) {
            return;
        }

        final Entity entity = ev.getRightClicked();
        final EngineerData data = getPlayerData(player);
        final Construct construct = data.getConstruct();

        if (construct == null || !construct.checkEntity(entity)) {
            return;
        }

        // internal cd
        if (player.hasCooldown(cooldownMaterial)) {
            return;
        }

        player.setCooldown(cooldownMaterial, 10);

        final int iron = data.getIron();

        if (iron < construct.getUpgradeCost()) {
            player.sendMessage("&6&l🔧 &cNot enough iron to upgrade!");
            return;
        }

        if (!construct.levelUp()) {
            return;
        }

        data.subtractIron(construct.getUpgradeCost());
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        playerData.removeAnd(player, EngineerData::remove);
    }

    @Nullable
    public Construct getConstruct(GamePlayer player) {
        return getPlayerData(player).getConstruct();
    }

    @EventHandler()
    public void handlePlayerSwing(PlayerInteractEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());
        final Action action = ev.getAction();

        if (ev.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        if (action != Action.LEFT_CLICK_BLOCK && action != Action.LEFT_CLICK_AIR) {
            return;
        }

        swingMechaHand(player);
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer damager = instance.getDamagerAsPlayer();

        swingMechaHand(damager);
    }

    @Override
    public boolean processInvisibilityDamage(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, double damage) {
        return !isUsingUltimate(player);
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        final EngineerData data = getPlayerData(player);
        data.createMechaIndustries(this);

        player.schedule(data::removeMechaIndustries, getUltimateDuration());

        return UltimateCallback.OK;
    }

    public int getIron(GamePlayer player) {
        return getPlayerData(player).getIron();
    }

    public void subtractIron(GamePlayer player, int amount) {
        addIron(player, -amount);
    }

    public void addIron(GamePlayer player, int amount) {
        getPlayerData(player).addIron(amount);
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                Heroes.ENGINEER.getAlivePlayers().forEach(player -> addIron(player, 1));
            }
        }.runTaskTimer(ironRechargeRate, ironRechargeRate);
    }

    @Override
    public void onStop() {
        playerData.forEachAndClear(EngineerData::remove);
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.ENGINEER_SENTRY.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.ENGINEER_TURRET.getTalent();
    }

    @Nullable
    @Override
    public Talent getThirdTalent() {
        return Talents.ENGINEER_RECALL.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.ENGINEER_PASSIVE.getTalent();
    }

    public void removeConstruct(@Nonnull GamePlayer player) {
        final EngineerData data = getPlayerData(player);
        final Construct construct = data.setConstruct(null);

        if (construct == null) {
            return;
        }

        construct.remove();
    }

    public void setConstruct(@Nonnull GamePlayer player, @Nonnull Construct construct) {
        final EngineerData data = getPlayerData(player);

        data.setConstruct(construct);
        construct.runTaskTimer(0, 1);
    }

    @Nonnull
    @Override
    public EngineerData getPlayerData(@Nonnull GamePlayer player) {
        return playerData.computeIfAbsent(player, fn -> new EngineerData(player, this));
    }

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final EngineerData data = getPlayerData(player);
        final MechaIndustries mecha = data.getMechaIndustries();

        return mecha != null ? mecha.toString() : "";
    }

    private void swingMechaHand(GamePlayer player) {
        getPlayerData(player).swingMechaIndustriesHand();
    }
}
