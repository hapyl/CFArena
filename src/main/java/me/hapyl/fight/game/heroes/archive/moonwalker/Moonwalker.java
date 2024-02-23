package me.hapyl.fight.game.heroes.archive.moonwalker;

import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.EquipmentSlot;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.archive.moonwalker.MoonPillarTalent;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.ability.held.HeldData;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Moonwalker extends Hero implements PlayerElement, DisabledHero, UIComponent {

    public Moonwalker(@Nonnull Heroes handle) {
        super(handle, "Moonwalker");

        setMinimumLevel(3);
        setArchetype(Archetype.RANGE);
        setAffiliation(Affiliation.SPACE);
        setGender(Gender.MALE);
        setRace(Race.ALIEN);

        setDescription("A traveler from another planet... or, should I say moon? Brings his skills and... planets... with himself!");
        setItem("1cf8fbd76586920c5273519927862fdc111705a1851d4d1aac450bcfd2b3a");

        final HeroAttributes attributes = getAttributes();

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(255, 255, 255);
        equipment.setLeggings(186, 186, 186);
        equipment.setBoots(45, 28, 77);

        setWeapon(new MoonwalkerWeapon());
        setUltimate(new MoonwalkerUltimate(this));
    }

    @Override
    public MoonwalkerWeapon getWeapon() {
        return (MoonwalkerWeapon) super.getWeapon();
    }

    @Nonnull
    @Override
    public MoonwalkerUltimate getUltimate() {
        return (MoonwalkerUltimate) super.getUltimate();
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
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        getUltimate().execute(player);

        return UltimateCallback.OK;
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        player.setItem(EquipmentSlot.ARROW, new ItemStack(Material.ARROW));
        player.addEffect(Effects.SLOW_FALLING, 2, -1);
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

    @Nonnull
    @Override
    public String getString(@Nonnull GamePlayer player) {
        final MoonwalkerWeapon weapon = getWeapon();
        final HeldData data = weapon.ability.getHeldData(player);

        return "- " + data.getUnit();
    }
}
