package me.hapyl.fight.game.heroes.archive.moonwalker;

import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.archive.moonwalker.MoonPillarTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Moonwalker extends Hero implements PlayerElement, UIComponent {

    public Moonwalker() {
        super("Moonwalker");

        setMinimumLevel(3);
        setArchetype(Archetype.RANGE);

        setDescription("A traveller from another planet... or, should I say moon? Brings his skills and... planets... with himself!");
        setItem("1cf8fbd76586920c5273519927862fdc111705a1851d4d1aac450bcfd2b3a");

        final HeroAttributes attributes = getAttributes();

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(255, 255, 255);
        equipment.setLeggings(186, 186, 186);
        equipment.setBoots(105, 105, 105);

        setWeapon(new MoonwalkerWeapon());
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
    public String predicateMessage(Player player) {
        return "Not a valid block!";
    }

    @Override
    public void useUltimate(Player player) {
        getUltimate().useUltimate(player);
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
    public MoonPillarTalent getFirstTalent() {
        return (MoonPillarTalent) Talents.MOONSLITE_PILLAR.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.MOON_GRAVITY.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.TARGET.getTalent();
    }

    @Override
    public @Nonnull String getString(Player player) {
        return "";
    }
}
