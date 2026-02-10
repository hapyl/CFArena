package me.hapyl.fight.game.heroes.warden;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.Debug;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.weapons.Weapon;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class Warden extends Hero implements Disabled {
    
    public Warden(@NotNull Key key){
        super(key, "Void Warden");
        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE, Archetype.MELEE);
        profile.setGender(Gender.MALE);
        profile.setRace(Race.VOID_CREATURE);

        setDescription("""
                A creature that was born between the abyss and reality. It lives to stop the war between those two.
                Everything has it's dark side: Void Warden creates his clone, The Self, to help him.
                """);

        setItem("65560c36a6df0453609126ee4d34fb977fde7278228722f14d117ffd236ed31e");


        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(90, 90, 90);
        equipment.setLeggings(90, 90, 90);
        equipment.setBoots(90, 90, 90);

        setWeapon(Weapon.builder(Material.NETHER_STAR, Key.ofString("disorder"))
                .name("Disorder")
                .description("""
                         Void Warden's personal defense weapon, it's hard to comprehend what it resembles.
                         It slayed everything it met.
                         """
                ).damage(5.5d));

        setUltimate(new Warden.WardenUltimate());
    }

    public void paradoxLogic(@Nonnull DamageInstance instance) {
        final GamePlayer player = instance.getDamagerAsPlayer();
        final LivingGameEntity entity = instance.getEntity();

        if (player == null){
            return;
        }

        boolean isAbyss = true; // always start with Abyss
        if (!isAbyss) {
            player.heal(10);
            Debug.info("&4&lREALITY: &7You healed for 10 HP.");
        } else {
            // entity.getAttributes().temper(Temper.PARADOX, 5).relative(AttributeType.ATTACK,-0.15);
            Debug.info("&0&lABYSS: &7You reduced the enemy's attack.");
        }

    }

    @Override
    public Talent getFirstTalent() {
        return TalentRegistry.FRACTURE;
    }

    @Override
    public Talent getSecondTalent() {
        return TalentRegistry.DISUNION;
    }

    @Override
    public Talent getPassiveTalent() {
        return TalentRegistry.PARADOX;
    }

    public class WardenUltimate extends UltimateTalent {

            public WardenUltimate() {
                super(Warden.this, "Double Vision", 70);
                setDescription("""
                    clone
                    """);

                setMaterial(Material.ENDER_EYE);
                setDurationSec(50);
                setCooldownSec(60);
                //9d7116e9186be505a8512b67ffbb83b29b041b6e12464ee1490bb146ffbf38e3 -> The Self head (use for clone)

            }

            @Nonnull
            @Override
            public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
                return execute(() -> {

                });
            }
        }

    }
