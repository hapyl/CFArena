package me.hapyl.fight.game.heroes.himari;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.himari.DeadEye;
import me.hapyl.fight.game.talents.himari.LuckyDay;
import me.hapyl.fight.game.talents.himari.SpikeBarrier;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Map;

public class Himari extends Hero implements Listener, PlayerDataHandler<HimariData> {

  private final PlayerDataMap<HimariData> playerData = PlayerMap.newDataMap(player -> new HimariData(player, this));

    public Himari(@Nonnull Key key) {
        super(key,"Himari");

        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE);
        profile.setGender(Gender.FEMALE);

        setDescription("""
                (Make description later)
                (remind me to pay off those xp bottles to hapyl)
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

    public SpikeBarrier getThirdTalent(){
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

        private final Map<Integer, Runnable> actions = Maps.newHashMap();

        public HimariUltimate() {
            super(Himari.this,"A message to Behold", 60);
            setDescription("""
                    Instantly charges a shot, which draws a circle in a large area.
                    At the end of the timer, a huge damage will go through the circle 3 times, before stopping.
                    Keep out. It damages you too.
                    """);

            setItem(Material.IRON_SWORD);
            setDurationSec(5);
            setCooldownSec(30);
        //    setSound(Sound.BLOCK_ANVIL_USE, 0.25f);

            actions.put(0, () -> {

            });

        }

        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player) {
            return execute(() -> {
            });
        }
    }

}
