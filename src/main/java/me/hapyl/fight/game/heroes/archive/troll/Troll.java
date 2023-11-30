package me.hapyl.fight.game.heroes.archive.troll;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.weapons.Weapon;
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

    public Troll() {
        super("Troll");

        setArchetype(Archetype.STRATEGY);

        setDescription("Not a good fighter... but definitely a good troll!");
        setItem("9626c019c8b41c7b249ae9bb6760c4e6980051cf0d6895cb3e6846d81245ad11");

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(255, 204, 84);
        equipment.setLeggings(255, 204, 84);
        equipment.setBoots(255, 204, 84);

        setWeapon(new Weapon(Material.STICK).setName("Stickonator")
                .setDescription("""
                        - What's brown and sticky?
                        - What?
                        - A stick!
                        - ...
                        """)
                .setDamage(4.0)
                .addEnchant(Enchantment.KNOCKBACK, 1));

        setUltimate(new UltimateTalent(
                "Sticky Situation", """
                Spawns a batch of cobwebs at your position that is only visible for your opponents.
                                
                &8;;Only one batch can exist at the same time.
                """, 40
        ).setSound(Sound.ENTITY_SPIDER_AMBIENT, 1.0f)
                .setType(Talent.Type.IMPAIR)
                .setItem(Material.COBWEB)
                .setCooldownSec(20));
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
    public void onStop() {
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
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        clearCobwebs(player);

        cobwebs.put(player, new StickyCobweb(player));
        return UltimateCallback.OK;
    }

    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final GamePlayer killer = input.getDamagerAsPlayer();
        final LivingGameEntity entity = input.getEntity();

        if (killer == null) {
            return null;
        }

        if (Math.random() >= 0.98) {
            entity.setLastDamager(killer);
            entity.dieBy(EnumDamageCause.TROLL_LAUGH);

            entity.playSound(Sound.ENTITY_WITCH_CELEBRATE, 2.0f);
            entity.sendMessage("&a%s had the last laugh!", killer.getName());

            entity.asPlayer(Achievements.LAUGHING_OUT_LOUD_VICTIM::complete);

            // Fx
            killer.sendMessage("&aYou laughed at %s!", entity.getName());
            killer.playSound(Sound.ENTITY_WITCH_CELEBRATE, 2.0f);

            Achievements.LAUGHING_OUT_LOUD.complete(killer.getPlayer());
        }

        return null;
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.TROLL_SPIN.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.REPULSOR.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.TROLL_PASSIVE.getTalent();
    }
}
