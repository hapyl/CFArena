package me.hapyl.fight.game.heroes.archive.orc;

import com.google.common.collect.Sets;
import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.attribute.temper.TemperInstance;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.archive.orc.OrcAxe;
import me.hapyl.fight.game.talents.archive.orc.OrcGrowl;
import me.hapyl.fight.game.task.PlayerGameTask;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.spigotutils.module.math.Tick;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class Orc extends Hero implements Listener {

    private final PlayerMap<DamageData> damageMap = PlayerMap.newMap();
    private final Set<PotionEffectType> negativeEffects = Sets.newHashSet();
    private final Set<Player> awaitEffectChange = Sets.newHashSet();
    private final TemperInstance berserk =
            Temper.BERSERK_MODE.newInstance(Named.BERSERK.toString())
                    .increase(AttributeType.ATTACK, 0.5d)
                    .increase(AttributeType.SPEED, 0.05d)
                    .increase(AttributeType.CRIT_CHANCE, 0.4d)
                    .decrease(AttributeType.DEFENSE, 0.7d)
                    .message(Named.BERSERK.getCharacter() + " &aYou're berserk!");

    public Orc() {
        super("Pakarat Rakab");

        setArchetype(Archetype.DAMAGE);

        final HeroAttributes attributes = getAttributes();
        attributes.setValue(AttributeType.MAX_HEALTH, 150);
        attributes.setValue(AttributeType.DEFENSE, 0.6d);
        attributes.setValue(AttributeType.SPEED, 0.22d);
        attributes.setValue(AttributeType.CRIT_CHANCE, 0.15d);

        setWeapon(new OrcWeapon());

        setItem("a06220fdfef4d53da8bcef8cbef9a8a3add3d776de43a3781b2f58869ce3d738");

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(138, 140, 133, TrimPattern.RIB, TrimMaterial.QUARTZ);
        equipment.setLeggings(20, 19, 51);
        equipment.setBoots(Material.NETHERITE_BOOTS);

        setUltimate(
                new UltimateTalent("Berserk", 70)
                        .setDurationSec(20)
                        .setCooldown(30)
                        .setItem(Material.NETHER_WART)
                        .appendDescription(
                                """
                                        Enter %s for {duration}.
                                                                        
                                        While active, gain:
                                        • &aIncreased %s.
                                        • &aIncreased %s.
                                        • &aIncreased %s.
                                        • &c-70 %s.
                                        """,
                                Named.BERSERK,
                                AttributeType.ATTACK,
                                AttributeType.SPEED,
                                AttributeType.CRIT_CHANCE,
                                AttributeType.DEFENSE
                        )
        );

        negativeEffects.addAll(
                List.of(
                        PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING, PotionEffectType.HARM,
                        PotionEffectType.CONFUSION, PotionEffectType.BLINDNESS, PotionEffectType.HUNGER,
                        PotionEffectType.WEAKNESS, PotionEffectType.POISON, PotionEffectType.WITHER, PotionEffectType.DARKNESS
                )
        );
    }

    @EventHandler()
    public void handleNegativePotion(EntityPotionEffectEvent ev) {
        final Entity entity = ev.getEntity();
        final PotionEffect newEffect = ev.getNewEffect();
        final EntityPotionEffectEvent.Action action = ev.getAction();

        if (!(entity instanceof Player player) || action != EntityPotionEffectEvent.Action.ADDED) {
            return;
        }

        if (awaitEffectChange.contains(player)) {
            awaitEffectChange.remove(player);
            return;
        }

        if (!validatePlayer(player) || newEffect == null) {
            return;
        }

        final PotionEffectType type = newEffect.getType();
        if (isNegativeEffect(type)) {
            final PotionEffect weakerEffect = type.createEffect(
                    Math.max(newEffect.getDuration() / 2, 0),
                    Math.max(newEffect.getAmplifier() / 2, 0)
            );

            ev.setCancelled(true);

            awaitEffectChange.add(player);
            player.addPotionEffect(weakerEffect);
        }
    }

    @Nullable
    @Override
    public DamageOutput processDamageAsVictim(DamageInput input) {
        final GamePlayer player = input.getEntityAsPlayer();
        final EnumDamageCause cause = input.getDamageCauseOr(EnumDamageCause.NONE);

        if (cause != EnumDamageCause.ENTITY_ATTACK) {
            return null;
        }

        if (damageMap.computeIfAbsent(player, DamageData::new).addHitAndCheck()) {
            enterBerserk(player, Tick.fromSecond(3));
        }

        return null;
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        enterBerserk(player, getUltimateDuration());

        return UltimateCallback.OK;
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        if (!(getWeapon() instanceof OrcWeapon orcWeapon)) {
            return;
        }

        damageMap.remove(player);
        orcWeapon.remove(player);
    }

    public void enterBerserk(GamePlayer player, int duration) {
        final EntityAttributes attributes = player.getAttributes();
        berserk.temper(attributes, duration);

        // Fx
        new PlayerGameTask(player) {
            private int tick = 0;

            @Override
            public void run() {
                if (tick++ >= duration) {
                    cancel();
                    return;
                }

                // Sound FX
                if (tick % 20 == 0) {
                    player.playWorldSound(Sound.ENTITY_PIGLIN_AMBIENT, 0.75f);
                    player.playWorldSound(Sound.ENTITY_PIGLIN_ANGRY, 1.25f);
                }

                // Particle FX
                if (tick % 5 == 0) {
                    player.spawnWorldParticle(Particle.LAVA, 2, 0.1, 0.2, 0.1, 0.1f);
                }
            }

            @Override
            public void onTaskStop() {
                player.sendMessage(Named.BERSERK + " &ais over!");
            }

        }.runTaskTimer(0, 1);
    }

    @Override
    public void onStop() {
        if (getWeapon() instanceof OrcWeapon weapon) {
            weapon.removeAll();
        }
        damageMap.clear();
    }

    @Override
    public OrcGrowl getFirstTalent() {
        return (OrcGrowl) Talents.ORC_GROWN.getTalent();
    }

    @Override
    public OrcAxe getSecondTalent() {
        return (OrcAxe) Talents.ORC_AXE.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.ORC_PASSIVE.getTalent();
    }

    private boolean isNegativeEffect(PotionEffectType type) {
        return negativeEffects.contains(type);
    }
}
