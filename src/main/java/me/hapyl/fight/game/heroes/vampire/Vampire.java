package me.hapyl.fight.game.heroes.vampire;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.MapMaker;
import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.HealingOutcome;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.vampire.BatSwarm;
import me.hapyl.fight.game.talents.vampire.Bloodshift;
import me.hapyl.fight.game.talents.vampire.VampirePassive;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.EntitySpawner;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;
import java.util.Map;

public class Vampire extends Hero implements Listener, PlayerDataHandler<VampireData> {

    private final PlayerDataMap<VampireData> vampireData = PlayerMap.newDataMap(VampireData::new);

    public Vampire(@Nonnull Key key) {
        super(key, "Vorath");

        setDescription("""
                One of the royal guards at the %s&8&o, believes that with enough fire power, &oeverything&8&o is possible.
                
                Prefers NoSunBurn™ sunscreen.
                """.formatted(Affiliation.CHATEAU));

        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE, Archetype.SELF_SUSTAIN, Archetype.SELF_BUFF);
        profile.setAffiliation(Affiliation.CHATEAU);
        profile.setGender(Gender.MALE);
        profile.setRace(Race.VAMPIRE);

        setItem("8d44756e0b4ece8d746296a3d5e297e1415f4ba17647ffe228385383d161a9");

        final HeroAttributes attributes = getAttributes();
        attributes.setMaxHealth(90);

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(25, 25, 25, TrimPattern.RIB, TrimMaterial.NETHERITE);
        equipment.setLeggings(254, 253, 252, TrimPattern.SILENCE, TrimMaterial.IRON);
        equipment.setBoots(25, 25, 25, TrimPattern.SILENCE, TrimMaterial.NETHERITE);

        setWeapon(Weapon.builder(Material.GHAST_TEAR, Key.ofString("vampires_fang"))
                .name("Vampire's Fang")
                .description("""
                        A very sharp fang.
                        """)
                .damage(5.0d)
        );

        setUltimate(new VampireUltimate());
    }

    @EventHandler
    public void handleGameDamageEvent(GameDamageEvent ev) {
        final GameEntity damager = ev.getDamager();
        final double damage = ev.getDamage();

        if (!(damager instanceof GamePlayer player) || !validatePlayer(player)) {
            return;
        }

        final VampireData data = getPlayerData(player);
        final VampireState state = data.getState();
        final Bloodshift bloodshift = getFirstTalent();

        switch (state) {
            case DAMAGE -> {
                final double health = player.getHealth();
                final double healthDrain = damage * bloodshift.healthDrainPerOneDamage;

                // Drain health
                player.setHealth(Math.max(1, health - healthDrain)); // Don't go below 1

                // Multiple damage
                ev.multiplyDamage(bloodshift.calculateDamage(player));

                // Fx
            }
            case SUSTAIN -> {
                final double healthRegen = damage * bloodshift.healingFromDamage;

                // Decrease damage
                ev.multiplyDamage(bloodshift.damageReduction);

                final HealingOutcome healingOutcome = player.heal(healthRegen);

                // Fx (Only play if actually healed)
                if (healingOutcome.type() != HealingOutcome.Type.HEALED) {
                    return;
                }

                player.playWorldSound(Sound.ENTITY_WITCH_DRINK, 1.25f);
                player.playWorldSound(Sound.ENTITY_GENERIC_DRINK, 1.25f);
            }
        }
    }

    @Override
    public Bloodshift getFirstTalent() {
        return TalentRegistry.BLOODSHIFT;
    }

    @Override
    public BatSwarm getSecondTalent() {
        return TalentRegistry.BAT_SWARM;
    }

    @Override
    public VampirePassive getPassiveTalent() {
        return TalentRegistry.VANPIRE_PASSIVE;
    }

    @Nonnull
    @Override
    public PlayerDataMap<VampireData> getDataMap() {
        return vampireData;
    }

    private class VampireUltimate extends UltimateTalent {

        private final Map<AttributeType, Double> legionAttributes = MapMaker.<AttributeType, Double>ofLinkedHashMap()
                .put(AttributeType.MAX_HEALTH, 5.0d)
                .put(AttributeType.ATTACK, 10.0d)
                .put(AttributeType.CRIT_DAMAGE, 1.0d)
                .put(AttributeType.SPEED, 5.0d)
                .put(AttributeType.ATTACK_SPEED, 3.0d)
                .makeMap();

        @DisplayField private final double healing = 20.0d;

        public VampireUltimate() {
            super(Vampire.this, "Legion", 50);

            setDescription("""
                    Gather the &fspirit&7 of the warriors of %1$s&7, providing you with a temporary &abuff&7 and heals you.
                    
                    Each hero from the %1$s&7 in &a&nyour&7 &a&nteam&7 increases the following attributes for {duration}:
                    %2$s
                    """.formatted(Affiliation.CHATEAU, formatAttributeIncrease()));

            legionAttributes.forEach(((attribute, value) -> {
                addAttributeDescription(attribute.getName() + " Increase", value);
            }));

            setType(TalentType.ENHANCE);
            setItem(Material.TOTEM_OF_UNDYING);
            setSound(Sound.ENTITY_BAT_DEATH, 0.75f);

            setDurationSec(22.0f);
            setCooldownSec(25.0f);
        }

        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player) {
            return execute(() -> {
                final int legionCount = (int) player.getTeam().getPlayers().stream()
                        .filter(p -> p.getHero().getProfile().getAffiliation() == Affiliation.CHATEAU)
                        .count();

                final EntityAttributes attributes = player.getAttributes();

                legionAttributes.forEach((type, value) -> {
                    attributes.increaseTemporary(Temper.LEGION, type, type.scaleDown(value * legionCount), getDuration());
                });

                // Heal
                player.heal(healing * legionCount);

                // Fx
                player.spawnBuffDisplay("&4&l🦇 LEGION &c(%s)".formatted(legionCount), 30);
                player.sendMessage("&4&l🦇 LEGION! &cGathered spirit of %s warriors!".formatted(legionCount));

                EntitySpawner.of(Entities.BAT, self -> {
                            self.setAwake(true);
                            self.setInvulnerable(true);
                        })
                        .then(EntitySpawner.spawn(player.getLocation(), 10))
                        .then(EntitySpawner.tick(30, self -> player.spawnWorldParticle(self.getLocation().add(0, 0.2, 0), Particle.SMOKE, 1)))
                        .then(EntitySpawner.forEach(self -> {
                            final Location location = self.getLocation().add(0, 0.2, 0);

                            player.spawnWorldParticle(location, Particle.POOF, 2, 0.2f, 0.2f, 0.2f, 0.05f);
                            self.remove();
                        }))
                        .run();
            });
        }

        private String formatAttributeIncrease() {
            final StringBuilder builder = new StringBuilder();

            legionAttributes.forEach((attribute, value) -> {
                builder.append(" &8› ").append(attribute.toString()).append("\n");
            });

            return builder.toString();
        }
    }
}
