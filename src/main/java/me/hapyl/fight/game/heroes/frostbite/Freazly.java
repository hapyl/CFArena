package me.hapyl.fight.game.heroes.frostbite;

import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.UltimateResponse;
import me.hapyl.fight.game.talents.frostbite.IcyShardsPassive;
import me.hapyl.fight.game.talents.techie.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.util.Collect;
import org.bukkit.Color;

import javax.annotation.Nonnull;

public class Freazly extends Hero {

    public Freazly(@Nonnull Heroes handle) {
        super(handle, "Frostbite");

        setArchetype(Archetype.HEXBANE);
        setGender(Gender.UNKNOWN);
        setDescription("A very cold entity to the touch.");
        setItem("cad7486b5d20823d5c24cba1850a600a7744209899828b19ccf93f69f2187058");

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(Color.fromRGB(139, 169, 214));
        equipment.setLeggings(Color.fromRGB(116, 141, 179));
        equipment.setBoots(Color.fromRGB(45, 54, 69));

        setWeapon(new FrostbiteWeapon());
        setUltimate(new FrostbiteUltimate(60));
    }

    @Override
    public void processDamageAsVictim(@Nonnull DamageInstance instance) {
        final IcyShardsPassive talent = getPassiveTalent();
        final GamePlayer player = instance.getEntityAsPlayer();

        if (player.hasCooldown(talent.getMaterial())) {
            return;
        }

        if (player.random.nextFloat() > talent.chance) {
            return;
        }

        // Launch icicles
        talent.launchIcicles(player);
        talent.startCd(player);
    }

    @Nonnull
    @Override
    public FrostbiteUltimate getUltimate() {
        return (FrostbiteUltimate) super.getUltimate();
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.ICICLES.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.ICE_CAGE.getTalent();
    }

    @Override
    public IcyShardsPassive getPassiveTalent() {
        return (IcyShardsPassive) Talents.ICY_SHARDS.getTalent();
    }

}
