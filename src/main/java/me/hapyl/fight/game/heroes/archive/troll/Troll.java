package me.hapyl.fight.game.heroes.archive.troll;

import me.hapyl.fight.event.DamageInput;
import me.hapyl.fight.event.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.GamePlayer;
import me.hapyl.fight.game.IGamePlayer;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroEquipment;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Troll extends Hero {

    private final Map<Player, Set<Block>> blocks = new HashMap<>();

    public Troll() {
        super("Troll");

        setRole(Role.MELEE);
        setArchetype(Archetype.STRATEGY);

        setInfo("Not a good fighter... but definitely a good troll!");
        setItem("9626c019c8b41c7b249ae9bb6760c4e6980051cf0d6895cb3e6846d81245ad11");

        final HeroEquipment equipment = getEquipment();
        equipment.setChestplate(255, 204, 84);
        equipment.setLeggings(255, 204, 84);
        equipment.setBoots(255, 204, 84);

        setWeapon(new Weapon(Material.STICK).setName("Stickonator")
                .setDescription("- What's brown and sticky?__- What?__- A stick!__- ...")
                .setDamage(4.0)
                .addEnchant(Enchantment.KNOCKBACK, 1));

        setUltimate(new UltimateTalent(
                "Sticky Situation",
                "Spawns a batch of cobweb at your position that is only visible for your opponents.__Only one batch can exist at the same time.",
                40
        ).setSound(Sound.ENTITY_SPIDER_AMBIENT, 1.0f)
                .setItem(Material.COBWEB)
                .setCooldownSec(20));

    }

    @Override
    public void useUltimate(Player player) {
        Bukkit.getOnlinePlayers().forEach(target -> {
            if (target == player) {
                return;
            }
            Chat.sendMessage(target, "&aAh... Sticky! &e&lPUNCH &athe cobweb to remove it!");
        });
        clearCobweb(player);
        createCobweb(player);
    }

    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        if (Math.random() >= 0.98) {
            final LivingEntity entity = input.getEntity();
            final Player killer = input.getPlayer();

            if (entity instanceof Player target) {
                final IGamePlayer player = GamePlayer.getPlayer(target);

                player.setLastDamager(killer);
                player.setLastDamageCause(EnumDamageCause.TROLL_LAUGH);
                player.die(true);

                player.playSound(Sound.ENTITY_WITCH_CELEBRATE, 2.0f);
                player.sendMessage("&a%s had the last laugh!", killer.getName());

                Achievements.LAUGHING_OUT_LOUD_VICTIM.complete(player.getPlayer());
                return null;
            }
            else if (entity != null) {
                entity.remove();
            }
            else {
                return null;
            }

            Chat.sendMessage(killer, "&aYou laughed at %s!", entity.getName());
            PlayerLib.playSound(killer, Sound.ENTITY_WITCH_CELEBRATE, 2.0f);

            Achievements.LAUGHING_OUT_LOUD.complete(killer);
        }

        return null;
    }

    private void clearCobweb(Player player) {
        final Set<Block> blocks = Troll.this.blocks.get(player);
        if (blocks == null) {
            return;
        }

        blocks.forEach(location -> location.getState().update(false, false));
    }

    private void createCobweb(Player player) {
        final Location location = player.getLocation().clone().subtract(2, 0, 2);
        final Set<Block> hashSet = blocks.computeIfAbsent(player, t -> new HashSet<>());

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                location.add(i, 0, j);
                if (!location.getBlock().getType().isSolid()) {
                    hashSet.add(location.getBlock());
                    Bukkit.getOnlinePlayers().forEach(target -> {
                        if (target == player) {
                            return;
                        }
                        target.sendBlockChange(location, Material.COBWEB.createBlockData());
                    });
                }
                location.subtract(i, 0, j);
            }
        }
    }

    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        return null;
    }

    @Override
    public void onStop() {
        blocks.values().forEach(locations -> locations.forEach(block -> block.getState().update(false, false)));
        blocks.clear();
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
