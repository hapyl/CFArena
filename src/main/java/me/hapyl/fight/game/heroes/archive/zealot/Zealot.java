package me.hapyl.fight.game.heroes.archive.zealot;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.event.InstanceEntityData;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.WeakEntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.archive.zealot.BrokenHeartRadiation;
import me.hapyl.fight.game.talents.archive.zealot.MaledictionVeil;
import me.hapyl.fight.game.talents.archive.zealot.MalevolentHitshield;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;

public class Zealot extends Hero implements Listener {

    protected final Equipment abilityEquipment;

    private final PlayerMap<ZealotSwords> playerSwords = PlayerMap.newMap();

    public Zealot() {
        super("Zealot");

        setArchetype(Archetype.HEXBANE);
        setItem("131530db74bac84ad9e322280c56c4e0199fbe879883b76c9cf3fd8ff19cf025");
        setWeapon(new ZealotWeapon(this));

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(104, 166, 232, TrimPattern.SILENCE, TrimMaterial.DIAMOND);
        equipment.setLeggings(Material.DIAMOND_LEGGINGS, TrimPattern.SILENCE, TrimMaterial.DIAMOND);
        equipment.setBoots(Material.DIAMOND_BOOTS, TrimPattern.SILENCE, TrimMaterial.DIAMOND);

        abilityEquipment = new Equipment();
        abilityEquipment.setHelmet(getItem());
        abilityEquipment.setChestPlate(104, 166, 232, TrimPattern.SILENCE, TrimMaterial.GOLD);
        abilityEquipment.setLeggings(Material.GOLDEN_LEGGINGS, TrimPattern.SILENCE, TrimMaterial.GOLD);
        abilityEquipment.setBoots(Material.GOLDEN_BOOTS, TrimPattern.RIB, TrimMaterial.GOLD);

        setUltimate(new ZealotUltimate());
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        playerSwords.removeAnd(player, ZealotSwords::remove);
    }

    @Override
    public void onStop() {
        playerSwords.forEachAndClear(ZealotSwords::remove);
    }

    @EventHandler()
    public void handleSwing(PlayerInteractEvent ev) {
        if (ev.getAction() == Action.PHYSICAL || ev.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        final Player player = ev.getPlayer();

        if (!validatePlayer(player)) {
            return;
        }

        swingSwordsIfCan(player);
    }

    @EventHandler()
    public void handleSwing(EntityDamageByEntityEvent ev) {
        final Entity damager = ev.getDamager();

        if (!(damager instanceof Player player)) {
            return;
        }

        if (!validatePlayer(player)) {
            return;
        }

        swingSwordsIfCan(player);
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final InstanceEntityData damagerData = instance.getDamagerData();
        final LivingGameEntity entity = instance.getEntity();

        if (damagerData == null) {
            return;
        }

        final WeakEntityAttributes attributes = damagerData.getAttributes();
        final MaledictionVeil passiveTalent = getPassiveTalent();
        final boolean hasDebuff = entity.getAttributes().hasTemper(Temper.MALEDICTION_VEIL);
        final double ferocity = attributes.get(AttributeType.FEROCITY);

        if (ferocity > 0 && hasDebuff) {
            attributes.addSilent(AttributeType.FEROCITY, passiveTalent.ferocityRate);
        }
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        final ZealotSwords oldSwords = playerSwords.put(player, new ZealotSwords(player, getUltimate()) {
            @Override
            public void onTaskStop() {
                super.onTaskStop();
                playerSwords.remove(player, this);
            }
        });

        if (oldSwords != null) {
            oldSwords.cancel();
        }

        return UltimateCallback.OK;
    }

    @Nonnull
    @Override
    public ZealotUltimate getUltimate() {
        return (ZealotUltimate) super.getUltimate();
    }

    @Override
    public void onStart() {
        final MaledictionVeil passive = getPassiveTalent();

        new TickingGameTask() {
            @Override
            public void run(int tick) {
                getAlivePlayers().forEach(player -> {
                    Collect.nearbyEntities(player.getLocation(), passive.radius).forEach(entity -> {
                        if (player.isSelfOrTeammate(entity)) {
                            return;
                        }

                        passive.temperInstance.temper(entity, passive.getDuration());
                    });
                });
            }
        }.runTaskTimer(0, 5);
    }

    @Override
    public BrokenHeartRadiation getFirstTalent() {
        return (BrokenHeartRadiation) Talents.BROKEN_HEART_RADIATION.getTalent();
    }

    @Override
    public MalevolentHitshield getSecondTalent() {
        return (MalevolentHitshield) Talents.MALEVOLENT_HITSHIELD.getTalent();
    }

    @Override
    public MaledictionVeil getPassiveTalent() {
        return (MaledictionVeil) Talents.MALEDICTION_VEIL.getTalent();
    }

    private void swingSwordsIfCan(Player bukkitPlayer) {
        final GamePlayer player = CF.getPlayer(bukkitPlayer);

        if (player == null) {
            return;
        }

        final ZealotSwords swords = playerSwords.get(player);

        if (swords == null) {
            return;
        }

        swords.swing();
    }
}
