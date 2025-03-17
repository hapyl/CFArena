package me.hapyl.fight.game.heroes.troll;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.achievement.AchievementRegistry;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.entity.commission.CommissionEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.troll.LastLaughPassive;
import me.hapyl.fight.game.talents.troll.Repulsor;
import me.hapyl.fight.game.talents.troll.TrollSpin;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import javax.annotation.Nonnull;

public class Troll extends Hero implements Listener, PlayerDataHandler<TrollData> {

    private final PlayerDataMap<TrollData> trollData = PlayerMap.newDataMap(TrollData::new);

    public Troll(@Nonnull Key key) {
        super(key, "Troll");

        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.STRATEGY, Archetype.MELEE);
        profile.setGender(Gender.UNKNOWN);

        setDescription("Not a good fighter... but definitely a good troll!");
        setItem("9626c019c8b41c7b249ae9bb6760c4e6980051cf0d6895cb3e6846d81245ad11");

        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(255, 204, 84);
        equipment.setLeggings(255, 204, 84);
        equipment.setBoots(255, 204, 84);

        setWeapon(Weapon.builder(Material.STICK, Key.ofString("stickonator"))
                        .name("Stickonator")
                        .description("""
                                - What's brown and sticky?
                                - What?
                                - A stick!
                                - ...
                                """)
                        .enchant(Enchantment.KNOCKBACK, 1)
                        .damage(4.0)
        );

        setUltimate(new TrollUltimate());
    }

    @EventHandler
    public void handleCobwebClear(PlayerInteractEvent ev) {
        final GamePlayer player = CF.getPlayer(ev.getPlayer());
        final Block block = ev.getClickedBlock();

        if (player == null || block == null || ev.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        trollData.forEach((troll, data) -> data.clearCobweb(player, block));
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer damager = instance.getDamagerAsPlayer();
        final LivingGameEntity entity = instance.getEntity();

        if (damager == null) {
            return;
        }

        if (entity instanceof CommissionEntity named && named.isBossOrMiniboss()) {
            return;
        }

        final LastLaughPassive passiveTalent = getPassiveTalent();

        if (damager.random.checkBound(1 - passiveTalent.chance)) {
            entity.setLastDamager(damager);
            entity.dieBy(DamageCause.TROLL_LAUGH);

            entity.sendMessage("&a%s had the last laugh!".formatted(damager.getName()));

            final AchievementRegistry registry = Registries.achievements();
            entity.asPlayer(registry.TROLL_LAUGHING_OUT_LOUD_VICTIM::complete);

            // Fx
            damager.sendMessage("&aYou laughed at %s!".formatted(entity.getName()));
            damager.playWorldSound(Sound.ENTITY_EVOKER_PREPARE_WOLOLO, 2.0f);

            registry.TROLL_LAUGHING_OUT_LOUD.complete(damager.getPlayer());
        }
    }

    @Override
    public TrollSpin getFirstTalent() {
        return TalentRegistry.TROLL_SPIN;
    }

    @Override
    public Repulsor getSecondTalent() {
        return TalentRegistry.REPULSOR;
    }

    @Override
    public LastLaughPassive getPassiveTalent() {
        return TalentRegistry.TROLL_PASSIVE;
    }

    @Nonnull
    @Override
    public PlayerDataMap<TrollData> getDataMap() {
        return trollData;
    }

    private class TrollUltimate extends UltimateTalent {
        public TrollUltimate() {
            super(Troll.this, "Sticky Situation", 40);

            setDescription("""
                    Spawn a batch of &fcobwebs&7 at your current location.
                    
                    Touching the &fcobweb&7 clears it and &eimpairs&7 the entity.
                    
                    &8&o;;Only one batch can exist at the same time.
                    """
            );

            setSound(Sound.ENTITY_SPIDER_AMBIENT, 1.0f);
            setType(TalentType.IMPAIR);
            setItem(Material.COBWEB);
            setCooldownSec(20);
        }

        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player) {
            return execute(() -> {
                final TrollData data = getPlayerData(player);

                data.createCobweb();
            });
        }
    }
}
