package me.hapyl.fight.game.heroes.storage;

import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.heroes.ClassEquipment;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.Role;
import me.hapyl.fight.game.heroes.storage.extra.MoonwalkerUltimate;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentHandle;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Moonwalker extends Hero implements PlayerElement, UIComponent {

    public Moonwalker() {
        super("Moonwalker");

        this.setMinimumLevel(3);
        this.setRole(Role.RANGE);

        this.setInfo("A traveller from another planet... or, should I say moon? Brings his skills and... planets... with himself!");
        this.setItem(Material.END_STONE);

        final ClassEquipment equipment = this.getEquipment();
        equipment.setHelmet(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWNmOGZiZDc2NTg2OTIwYzUyNzM1MTk5Mjc4NjJmZGMxMTE3MDVhMTg1MWQ0ZDFhYWM0NTBiY2ZkMmIzYSJ9fX0=");
        equipment.setChestplate(199, 199, 194);
        equipment.setLeggings(145, 145, 136);
        equipment.setBoots(53, 53, 49);

        this.setWeapon(new Weapon(Material.BOW) {
            @Override
            public void onLeftClick(Player player, ItemStack item) {
                if (player.hasCooldown(Material.BOW)) {
                    return;
                }

                final Arrow arrow = player.launchProjectile(Arrow.class);
                arrow.setDamage(this.getDamage() / 2.0d);
                arrow.setCritical(false);
                arrow.setShooter(player);
                player.setCooldown(Material.BOW, 20);

                // fx
                PlayerLib.playSound(player, Sound.ENTITY_ARROW_SHOOT, 1.25f);
            }
        }
                .setName("Stinger")
                .setDescription(
                        "A unique bow made of unknown materials, seems to have two firing modes.__&e&lLEFT &e&lCLICK &7to fire quick arrow that deals 50% of normal damage.")
                               .setDamage(4.5d)
                               .setId("MOON_WEAPON"));

        // moved to its own class because it was unreadable lol
        setUltimate(new MoonwalkerUltimate());

    }

    @Nullable
    public Block getTargetBlock(Player player) {
        return player.getTargetBlockExact(25);
    }

    @Override
    public boolean predicateUltimate(Player player) {
        return getTargetBlock(player) != null;
    }

    @Override
    public String predicateMessage() {
        return "Not a valid block!";
    }

    @Override
    public void useUltimate(Player player) {
        ((MoonwalkerUltimate) getUltimate()).useUltimate(player);
    }

    @Override
    public void onStart(Player player) {
        player.getInventory().setItem(9, new ItemStack(Material.ARROW));
        PlayerLib.addEffect(player, PotionEffectType.SLOW_FALLING, 999999, 2);
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                Heroes.MOONWALKER.getAlivePlayers().forEach(gp -> {
                    final Player player = gp.getPlayer();
                    final Block targetBlock = getTargetBlock(player);
                    if (!player.isSneaking() || targetBlock == null) {
                        return;
                    }
                    final Location location = targetBlock.getRelative(BlockFace.UP).getLocation().clone().add(0.5d, 0.0d, 0.5d);
                    for (int i = 0; i < 10; i++) {
                        player.spawnParticle(Particle.CRIT, location, 1, 0, 0, 0, 0);
                        location.add(0, 0.15, 0);
                    }

                });
            }
        }.runTaskTimer(0, 2);
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.MOONSLITE_PILLAR.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.MOONSLITE_BOMB.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.TARGET.getTalent();
    }

    @Override
    public @Nonnull String getString(Player player) {
        final int bombs = TalentHandle.MOON_SLITE_BOMB.getBombsSize(player);
        return "&e■ &l" + bombs;
    }
}
