package me.hapyl.fight.game.heroes.pytaria;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.locaiton.LocationHelper;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.Constants;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.attribute.*;
import me.hapyl.fight.game.damage.DamageCause;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Gender;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroProfile;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.pytaria.ExcellencyPassive;
import me.hapyl.fight.game.talents.pytaria.FlowerBreeze;
import me.hapyl.fight.game.talents.pytaria.FlowerEscape;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.*;
import org.bukkit.entity.Bee;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class Pytaria extends Hero {

    private final ModifierSource modifierSource = new ModifierSource(Key.ofString("excellency"), true);
    
    public Pytaria(@Nonnull Key key) {
        super(key, "Pytaria");

        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE, Archetype.MELEE, Archetype.POWERFUL_ULTIMATE, Archetype.SELF_BUFF, Archetype.SELF_SUSTAIN);
        profile.setGender(Gender.FEMALE);

        setDescription("""
                Beautiful, yet deadly opponent with addiction to flowers.
                
                She suffered all her youth, which, in the end, only made her stronger.
                """
        );
        setItem("7bb0752f9fa87a693c2d0d9f29549375feb6f76952da90d68820e7900083f801");

        final HeroAttributes attributes = getAttributes();
        attributes.setMaxHealth(120);
        attributes.setAttack(80);
        attributes.setCritChance(20);
        attributes.setCritDamage(50);
        attributes.setHeight(170);

        setWeapon(Weapon.builder(Material.ALLIUM, Key.ofString("annihilallium"))
                        .name("Annihilallium")
                        .description("A beautiful flower, nothing more.")
                        .damage(8.0)
        );

        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(222, 75, 85);
        equipment.setLeggings(54, 158, 110, TrimPattern.SILENCE, TrimMaterial.IRON);
        equipment.setBoots(179, 204, 204, TrimPattern.SILENCE, TrimMaterial.IRON);

        setUltimate(new PytariaUltimate());
    }

    @Nonnull
    @Override
    public PytariaUltimate getUltimate() {
        return (PytariaUltimate) super.getUltimate();
    }

    @Override
    public void onStart(@Nonnull GameInstance instance) {
        new GameTask() {
            @Override
            public void run() {
                getAlivePlayers().forEach(gamePlayer -> {
                    recalculateStats(gamePlayer);
                });
            }
        }.runTaskTimer(0, 5);
    }

    public void recalculateStats(@Nonnull GamePlayer player) {
        final EntityAttributes attributes = player.getAttributes();
        final ExcellencyPassive passive = getPassiveTalent();

        final double maxHealth = player.getMaxHealth();
        final double health = player.getHealth();
        final double factor = 1 - health / maxHealth;

        final double attackIncrease = factor * passive.maxAttackIncrease;
        final double critChanceIncrease = factor * passive.maxCritChanceIncrease;
        final double defenseDecrease = factor * passive.maxDefenseDecrease;
        
        player.getAttributes().addModifier(modifierSource, Constants.INFINITE_DURATION, modifier -> modifier
                .of(AttributeType.ATTACK, ModifierType.MULTIPLICATIVE, attackIncrease)
                .of(AttributeType.CRIT_CHANCE, ModifierType.MULTIPLICATIVE, critChanceIncrease)
                .of(AttributeType.DEFENSE, ModifierType.MULTIPLICATIVE, defenseDecrease)
        );
    }

    @Override
    public FlowerEscape getFirstTalent() {
        return TalentRegistry.FLOWER_ESCAPE;
    }

    @Override
    public FlowerBreeze getSecondTalent() {
        return TalentRegistry.FLOWER_BREEZE;
    }

    @Override
    public ExcellencyPassive getPassiveTalent() {
        return TalentRegistry.EXCELLENCY;
    }

    private Location getLockLocation(Bee bee, LivingGameEntity entity) {
        final Location location = bee.getLocation();
        final Location targetLocation = entity == null ? location.clone().subtract(0, 9, 0) : entity.getEyeLocation();

        final World world = bee.getWorld();
        final Vector vector = targetLocation.toVector().subtract(location.toVector()).normalize().multiply(0.5d);
        final double distance = targetLocation.distance(location);

        for (double i = 0.0D; i < distance; i += 0.5) {
            location.add(vector);

            if (location.getBlock().getType().isSolid()) {
                final Location cloned = location.add(0, 0.15, 0);
                world.spawnParticle(Particle.FLAME, cloned, 3, 0.1, 0.1, 0.1, 0.02);

                return location;
            }

            world.spawnParticle(Particle.DUST, location, 1, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 0.5f));
        }

        return location;
    }

    public class PytariaUltimate extends UltimateTalent {

        @DisplayField(percentage = true) public final double healthRegenPercent = 0.4d;
        @DisplayField public final double damage = 25;

        private PytariaUltimate() {
            super(Pytaria.this, "Feel the Breeze", 60);

            setDescription("""
                    Summon a blooming &6Bee&7 in front of &aPytaria&7.
                    
                    The Bee will lock on the closest enemy and charge for {cast}.
                    
                    Once charged, the &6Bee&7 creates an explosion at the locked location, dealing damage in small &eAoE&7.
                    
                    Also regenerates &c{healthRegenPercent} ❤&7 of &aPytaria's&7 missing health.
                    """
            );

            setTexture("d4579f1ea3864269c2148d827c0887b0c5ed43a975b102a01afb644efb85ccfd");

            setSound(Sound.ENTITY_BEE_DEATH, 0.0f);

            setCastDuration(50);
            setCooldownSec(50);
        }

        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player, boolean isFullyCharged) {
            final Location location = player.getLocation();

            location.add(location.getDirection().setY(0).multiply(5));
            location.add(0, 7, 0);

            final Bee bee = Entities.BEE.spawn(
                    location, me -> {
                        me.setSilent(true);
                        me.setAI(false);
                    }
            );

            final LivingGameEntity entity = Collect.nearestEntityPrioritizePlayers(location, 50, check -> !player.isSelfOrTeammate(check));
            final double snapShotDamage = player.getAttributes().calculate().outgoingDamage(damage, DamageCause.FEEL_THE_BREEZE);

            player.playWorldSound(location, Sound.ENTITY_BEE_LOOP_AGGRESSIVE, 1.0f);

            return builder()
                    .onCastTick(tick -> {
                        getLockLocation(bee, entity); // Keep this for effect!

                        final double y = Math.sin(Math.toRadians(tick * 5)) * 0.2d;

                        LocationHelper.offset(location, 0, y, 0, () -> bee.teleport(location));
                    })
                    .onExecute(() -> {
                        final Location lockLocation = getLockLocation(bee, entity);
                        bee.remove();

                        Collect.nearbyEntities(lockLocation, 1.0d).forEach(victim -> {
                            victim.damage(snapShotDamage, player, DamageCause.FEEL_THE_BREEZE);
                        });

                        // Heal
                        final double health = player.getHealth();
                        final double maxHealth = player.getMaxHealth();
                        final double healingAmount = (maxHealth - health) * healthRegenPercent;

                        player.heal(healingAmount);
                        player.sendMessage("&6🐝 &aHealed for &c&l%.0f&c❤&a!".formatted(healingAmount));

                        // Fx
                        PlayerLib.stopSound(Sound.ENTITY_BEE_LOOP_AGGRESSIVE);

                        player.spawnWorldParticle(location, Particle.EXPLOSION, 5, 0.2, 0.2, 0.2, 0.1f);
                        player.playWorldSound(location, Sound.ENTITY_BEE_DEATH, 1.5f);

                        player.spawnWorldParticle(lockLocation, Particle.EXPLOSION_EMITTER, 3, 0.5, 0, 0.5, 0);
                        player.playWorldSound(lockLocation, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.25f);
                    });

        }
    }
}
