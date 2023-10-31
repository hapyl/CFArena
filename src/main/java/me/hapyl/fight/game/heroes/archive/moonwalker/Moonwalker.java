package me.hapyl.fight.game.heroes.archive.moonwalker;

import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.archive.moonwalker.MoonPillarTalent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Moonwalker extends Hero implements PlayerElement {

    public Moonwalker() {
        super("Moonwalker");

        setMinimumLevel(3);
        setArchetype(Archetype.RANGE);

        setDescription("A traveler from another planet... or, should I say moon? Brings his skills and... planets... with himself!");
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
    public Block getTargetBlock(GamePlayer player) {
        return player.getTargetBlockExact(25);
    }

    @Override
    public boolean predicateUltimate(@Nonnull GamePlayer player) {
        return getTargetBlock(player) != null;
    }

    @Override
    public String predicateMessage(@Nonnull GamePlayer player) {
        return "Not a valid block!";
    }

    @Override
    public void useUltimate(@Nonnull GamePlayer player) {
        getUltimate().execute(player);
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.getInventory().setItem(9, new ItemStack(Material.ARROW));
        player.addPotionEffect(PotionEffectType.SLOW_FALLING.createEffect(999999, 2));
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
}
