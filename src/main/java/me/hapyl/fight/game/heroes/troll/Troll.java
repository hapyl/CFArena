package me.hapyl.fight.game.heroes.troll;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.achievement.AchievementRegistry;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.registry.Registries;
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

public class Troll extends Hero implements Listener {

    private final PlayerMap<StickyCobweb> cobwebs = PlayerMap.newMap();

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

        for (StickyCobweb value : cobwebs.values()) {
            value.clear(player, block);
        }
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        clearCobwebs(player);
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        cobwebs.values().forEach(StickyCobweb::remove);
        cobwebs.clear();
    }

    public void clearCobwebs(GamePlayer player) {
        final StickyCobweb oldCobwebs = cobwebs.remove(player);

        if (oldCobwebs != null) {
            oldCobwebs.remove();
        }
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer killer = instance.getDamagerAsPlayer();
        final LivingGameEntity entity = instance.getEntity();

        if (killer == null) {
            return;
        }

        if (Math.random() >= 0.98) {
            entity.setLastDamager(killer);
            entity.dieBy(EnumDamageCause.TROLL_LAUGH);

            entity.playSound(Sound.ENTITY_WITCH_CELEBRATE, 2.0f);
            entity.sendMessage("&a%s had the last laugh!".formatted(killer.getName()));

            final AchievementRegistry registry = Registries.getAchievements();
            entity.asPlayer(registry.TROLL_LAUGHING_OUT_LOUD_VICTIM::complete);

            // Fx
            killer.sendMessage("&aYou laughed at %s!".formatted(entity.getName()));
            killer.playSound(Sound.ENTITY_WITCH_CELEBRATE, 2.0f);

            registry.TROLL_LAUGHING_OUT_LOUD.complete(killer.getPlayer());
        }
    }

    @Override
    public Talent getFirstTalent() {
        return TalentRegistry.TROLL_SPIN;
    }

    @Override
    public Talent getSecondTalent() {
        return TalentRegistry.REPULSOR;
    }

    @Override
    public Talent getPassiveTalent() {
        return TalentRegistry.TROLL_PASSIVE;
    }

    private class TrollUltimate extends UltimateTalent {
        public TrollUltimate() {
            super(Troll.this, "Sticky Situation", 40);

            setDescription("""
                    Spawns a batch of cobwebs at your position that is only visible for your opponents.
                    
                    &8;;Only one batch can exist at the same time.
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
                clearCobwebs(player);
                cobwebs.put(player, new StickyCobweb(player));
            });
        }
    }
}
