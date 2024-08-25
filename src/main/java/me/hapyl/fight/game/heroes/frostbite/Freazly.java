package me.hapyl.fight.game.heroes.frostbite;


import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Gender;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.UltimateResponse;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.frostbite.IcyShardsPassive;
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Color;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class Freazly extends Hero {

    public Freazly(@Nonnull Key key) {
        super(key, "Frostbite");

        setArchetypes(Archetype.HEXBANE, Archetype.RANGE, Archetype.POWERFUL_ULTIMATE);
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
        return TalentRegistry.ICICLES;
    }

    @Override
    public Talent getSecondTalent() {
        return TalentRegistry.ICE_CAGE;
    }

    @Override
    public IcyShardsPassive getPassiveTalent() {
        return TalentRegistry.ICY_SHARDS;
    }

    public class FrostbiteUltimate extends UltimateTalent {

        @DisplayField public final int blockCount = 24;
        @DisplayField public final double distance = 10.0d;

        @DisplayField public final double critChanceReduction = 1;
        @DisplayField public final double critDamageReduction = 1;
        @DisplayField public final double cooldownIncrease = 0.5d;

        @DisplayField public final int debuffDuration = 20;

        public FrostbiteUltimate(int pointCost) {
            super(Freazly.this, "Eternal Freeze", pointCost);

            setDescription("""
                    Unleash the {name} upon your enemies, creating a massive &f&lsnow field&7 that &4debuffs&7 enemies.
                    
                    The field orbits around for {duration}:
                    └ &bDecreasing&7 enemies %s and %s.
                    └ &bSlowing&7 and &bimpairing&7 vision.
                    └ Increasing cooldowns.
                    """.formatted(AttributeType.CRIT_CHANCE, AttributeType.CRIT_DAMAGE)
            );

            setType(TalentType.IMPAIR);
            setItem(Material.HEART_OF_THE_SEA);
            setDurationSec(10);
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            new EternalFreeze(player, this);

            return UltimateResponse.OK;
        }
    }
}
