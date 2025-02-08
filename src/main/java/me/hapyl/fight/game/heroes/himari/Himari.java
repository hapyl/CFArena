package me.hapyl.fight.game.heroes.himari;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.himari.DeadEye;
import me.hapyl.fight.game.talents.himari.HimariActionList;
import me.hapyl.fight.game.talents.himari.LuckyDay;
import me.hapyl.fight.game.talents.himari.SpikeBarrier;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class Himari extends Hero implements Listener, PlayerDataHandler<HimariData> {
    @DisplayField private final double speedAmplify = 0.4d;

    private final PlayerDataMap<HimariData> playerData = PlayerMap.newDataMap(player -> new HimariData(player, this));
    private final TemperInstance temperInstance = Temper.LUCKINESS.newInstance()
            .increase(AttributeType.SPEED, speedAmplify);

    public Himari(@Nonnull Key key) {
        super(key, "Himari");

        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE);
        profile.setGender(Gender.FEMALE);

        setDescription("""
                A girl who LOVES to gamble. No matter if it's for money, results, dares, or even her life!
                She's pretty cute if you know her well.
                However, Doctor Ed doesn't like her that much...
                """);

        setItem("23172927c6518ee184a1466d5f1ea81b989ced61a5d5159e3643bb9caf9c189f");

        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(128, 128, 128, TrimPattern.FLOW, TrimMaterial.NETHERITE);
        equipment.setLeggings(59, 59, 57, TrimPattern.FLOW, TrimMaterial.NETHERITE);
        equipment.setBoots(51, 49, 49, TrimPattern.FLOW, TrimMaterial.NETHERITE);

        setWeapon(Weapon.builder(Material.ENCHANTED_BOOK, Key.ofString("teachings_of_freedom"))
                        .name("Teachings of Freedom")
                        .description("""
                                        &8;;Default Weapon. No luck needed.
                                        A book that contains a lot of teachings and theory.
                                        There are many pages, some of them &f&lglow&7 as you observe more.
                                        """
                                //  (she skipped a lot of lessons btw, fuck dr.ed)
                        ).damage(5.0d));


        setUltimate(new HimariUltimate());
    }


    @Override
    public LuckyDay getFirstTalent() {
        return TalentRegistry.LUCKY_DAY;
    }

    @Override
    public DeadEye getSecondTalent() {
        return TalentRegistry.DEAD_EYE;
    }

    public SpikeBarrier getThirdTalent() {
        return TalentRegistry.SPIKE_BARRIER;
    }

    @Override
    public Talent getPassiveTalent() {
        return null;
    }

    @Override
    public @NotNull PlayerDataMap<HimariData> getDataMap() {
        return playerData;
    }

    private class HimariUltimate extends UltimateTalent {

        private final HimariActionList actionList = new HimariActionList();

        @DisplayField private final short witherAmplifier = 4;
        @DisplayField private final int witherDuration = 155;

        public HimariUltimate() {
            super(Himari.this, "All in", 60);
            setDescription("""
                    Throw a cube that gives out a random number from 1 to 4, which will result in the effect.
                    
                    You can get increased %s, poison your last offender, heal yourself or get punished for your impudence.
                    """.formatted(AttributeType.SPEED));

            setItem(Material.IRON_SWORD);
            setDurationSec(3);
            setCooldownSec(30);

            actionList.append(player -> {
                //move speed
                temperInstance.temper(player, 100);
                player.sendSubtitle("&o&lYou hear a whisper in your head: &0&o&lMove..", 2, 100, 6);
                return true;
            });

            actionList.append(player -> {
                final GameEntity lastAttacker = player.getLastDamager();
                //poison last entity who hurt Himari
                if (lastAttacker instanceof LivingGameEntity livingAttacker) {
                    livingAttacker.addEffect(Effects.WITHER, witherAmplifier, witherDuration);
                    player.sendSubtitle("&o&lYou hear a whisper in your head: &0&o&lThey will regret...", 2, 60, 6);
                    return true;
                }
                return false;
            });

            actionList.append(player -> {
                //Self-damage (haram!!)
                player.damage(20, player, EnumDamageCause.GAMBLE);
                player.sendSubtitle("&o&lYou hear a whisper in your head: &0&o&lJudgement.",2, 80, 6);
                return true;
            });

            actionList.append(player -> {
                //heal player if low on health.
                if (player.getHealth() < player.getMaxHealth() * 0.3) {
                    player.heal(40);
                    player.sendSubtitle("&o&lYou hear a whisper in your head: &0&o&lBack in action..",2, 60, 6);
                    return true;
                }
                return false;
            });
        }

        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player) {
            return execute(() -> {
                actionList.randomActionAndExecute(player);

                // Fx

                // ^ Fx is not needed here, since CF already uses dragon roar
                // or what fucking ever that sound is
                // anyway, it fits good enough
            });
        }
    }

}
