package me.hapyl.fight.game.heroes.archive.nightmare;

import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.techie.Talent;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import me.hapyl.fight.util.displayfield.DisplayFieldProvider;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.Set;

public class Nightmare extends Hero implements DisplayFieldProvider {

    @DisplayField
    private final double omenDamageMultiplier = 1.5d;

    private final PlayerMap<OmenDebuff> omenDebuffMap = PlayerMap.newConcurrentMap();
    private final TemperInstance temperInstance = Temper.NIGHTMARE_BUFF
            .newInstance("In the Shadows")
            .increase(AttributeType.ATTACK, 0.5d)
            .increase(AttributeType.SPEED, 0.05d);

    public Nightmare(@Nonnull Heroes handle) {
        super(handle, "Nightmare");

        setArchetype(Archetype.DAMAGE);

        setDescription("A spirit from the worst nightmares, blinds enemies and strikes from behind!");
        setItem("79c55e0e4af71824e8da68cde87de717b214f92e9949c4b16da22b357f97b1fc");

        setWeapon(new Weapon(Material.NETHERITE_SWORD)
                .setName("Oathbreaker")
                .setDescription("A sword that is capable of splitting dreams in half.")
                .setDamage(7.0d));

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(50, 0, 153);
        equipment.setLeggings(40, 0, 153);
        equipment.setBoots(30, 0, 153);

        setUltimate(new UltimateTalent(
                this, "Your Worst Nightmare",
                "Applies the &4👻 &c&lOmen&7 to all living opponents for {duration}.",
                55
        ).setDuration(240)
                .setType(Talent.Type.IMPAIR)
                .setItem(Material.BLACK_DYE)
                .setCooldownSec(30)
                .setSound(Sound.ENTITY_WITCH_CELEBRATE, 0.0f));

        copyDisplayFieldsToUltimate();
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        getDebuff(player).clear();
    }

    @Override
    public void onStop() {
        omenDebuffMap.clear();
    }

    @Nonnull
    public OmenDebuff getDebuff(@Nonnull GamePlayer player) {
        return omenDebuffMap.computeIfAbsent(player, OmenDebuff::new);
    }

    // Moved light level test in runnable
    @Override
    public void onStart() {
        new TickingGameTask() {
            @Override
            public void run(int tick) {
                // Tick debuff
                omenDebuffMap.values().forEach(OmenDebuff::tick);

                // Tick buff
                if (tick % 20 == 0) {
                    for (GamePlayer player : getAlivePlayers()) {
                        if (player.getBlockLight() > 7) {
                            continue;
                        }

                        temperInstance.temper(player, 30);

                        player.spawnWorldParticle(Particle.LAVA, 5, 0.15d, 0.15d, 0.15d, 0.01f);
                        player.spawnWorldParticle(Particle.SMOKE_LARGE, 5, 0.15d, 0.15d, 0.15d, 0.01f);
                    }
                }
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        final OmenDebuff debuff = getDebuff(player);
        final Set<GamePlayer> enemies = Collect.enemyPlayers(player);
        final int enemiesSize = enemies.size();

        if (enemiesSize == 0) {
            player.sendMessage("&4👻 &cOmen didn't affect anything!");
        }
        else {
            player.sendMessage("&4👻 &aOmen affected %s enemies!", enemiesSize);
        }

        enemies.forEach(enemy -> debuff.setOmen(enemy, getUltimateDuration()));

        return UltimateCallback.OK;
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final GamePlayer damager = instance.getDamagerAsPlayer();
        final LivingGameEntity entity = instance.getEntity();

        if (damager == null || !instance.isEntityAttack()) {
            return;
        }

        final OmenDebuff debuff = getDebuff(damager);

        if (!debuff.isAffected(entity)) {
            return;
        }

        instance.multiplyDamage(omenDamageMultiplier);
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.PARANOIA.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.SHADOW_SHIFT.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.IN_THE_SHADOWS.getTalent();
    }
}
