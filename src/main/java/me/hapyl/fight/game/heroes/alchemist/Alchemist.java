package me.hapyl.fight.game.heroes.alchemist;

import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.EffectFlag;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.UltimateResponse;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.talents.techie.Talent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.RandomTable;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.Numbers;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Sound.ENTITY_WITCH_AMBIENT;
import static org.bukkit.Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR;

public class Alchemist extends Hero implements UIComponent, PlayerElement {

    private final RandomTable<MadnessEffect> positiveEffects = new RandomTable<>();
    private final RandomTable<MadnessEffect> negativeEffects = new RandomTable<>();
    private final Map<GamePlayer, Integer> toxinLevel = new HashMap<>();
    private final Map<UUID, CauldronEffect> cauldronEffectMap = new HashMap<>();

    public Alchemist(@Nonnull Heroes handle) {
        super(handle, "Alchemist");

        setArchetype(Archetype.STRATEGY);
        setGender(Gender.FEMALE);

        setDescription(
                "An alchemist who was deceived by the creation of the abyss."
        );
        setItem("661691fb01825b9d9ec1b8f04199443146aa7d5627aa745962c0704b6a236027");

        setWeapon(new Weapon(Material.STICK)
                .addEnchant(Enchantment.KNOCKBACK, 1)
                .setName("Stick")
                .setDamage(8.0d)
                .setDescription("Turns out that a stick used in brewing can also be used in battle."));

        final HeroAttributes attributes = getAttributes();
        attributes.set(AttributeType.MAX_HEALTH, 125);
        attributes.set(AttributeType.DEFENSE, 0.5d);
        attributes.set(AttributeType.SPEED, 0.22d);

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(31, 5, 3, TrimPattern.SHAPER, TrimMaterial.COPPER);

        positiveEffects.add(new MadnessEffect("made you &lFASTER", PotionEffectType.SPEED, 30, 2))
                .add(new MadnessEffect("gave you &lJUMP BOOST", PotionEffectType.JUMP, 30, 1))
                .add(new MadnessEffect("made you &lSTRONGER", 30) {
                    @Override
                    public void affect(@Nonnull GamePlayer player, @Nonnull GamePlayer victim) {
                        final EntityAttributes playerAttributes = player.getAttributes();

                        playerAttributes.increaseTemporary(Temper.ALCHEMIST, AttributeType.ATTACK, 3.125, duration);
                    }
                })
                .add(new MadnessEffect("&lPROTECTED&a you", 30) {
                    @Override
                    public void affect(@Nonnull GamePlayer player, @Nonnull GamePlayer victim) {
                        final EntityAttributes playerAttributes = player.getAttributes();

                        playerAttributes.increaseTemporary(Temper.ALCHEMIST, AttributeType.DEFENSE, 0.25d, duration);
                    }
                })
                .add(new MadnessEffect("healed half of your missing health", 30) {
                    @Override
                    public void affect(@Nonnull GamePlayer player, @Nonnull GamePlayer victim) {
                        final double missingHealth = player.getMaxHealth() - player.getHealth();

                        player.heal(missingHealth / 2d);
                    }
                });

        negativeEffects.add(new MadnessEffect("&lpoisoned you", PotionEffectType.POISON, 15, 0) {
                    @Override
                    public void affect(@Nonnull GamePlayer player, @Nonnull GamePlayer victim) {
                        victim.getEntityData().setLastDamager(player);
                    }
                })
                .add(new MadnessEffect("&lblinded you", PotionEffectType.BLINDNESS, 15, 0))
                .add(new MadnessEffect("&lis withering your blood", PotionEffectType.WITHER, 7, 0) {
                    @Override
                    public void affect(@Nonnull GamePlayer player, @Nonnull GamePlayer victim) {
                        victim.getEntityData().setLastDamager(player);
                    }
                })
                .add(new MadnessEffect("&lslowed you", PotionEffectType.SLOW, 15, 2))
                .add(new MadnessEffect("&lmade you weaker", null, 15, 0) {
                    @Override
                    public void affect(@Nonnull GamePlayer player, @Nonnull GamePlayer victim) {
                        final EntityAttributes entityAttributes = victim.getAttributes();

                        entityAttributes.decreaseTemporary(Temper.ALCHEMIST, AttributeType.ATTACK, 0.5d, duration);
                    }
                })
                .add(new MadnessEffect("&lis... confusing?", PotionEffectType.CONFUSION, 15, 0));

        setUltimate(new AlchemistUltimate());
    }

    @Override
    public void processDamageAsDamager(@Nonnull DamageInstance instance) {
        final LivingGameEntity victim = instance.getEntity();
        final LivingGameEntity player = instance.getDamagerAsPlayer();

        if (player == null) {
            return;
        }

        final CauldronEffect effect = cauldronEffectMap.get(player.getUUID());

        if (!instance.isEntityAttack() || effect == null || effect.getEffectHits() <= 0) {
            return;
        }

        final Effects randomEffect = getRandomEffect();

        victim.addEffect(randomEffect, 3, 20);
        effect.decrementEffectPotions();

        player.sendMessage(
                "&c¤ &eVenom Touch applied &l%s &eto %s. &l%s &echarges left.",
                Chat.capitalize(randomEffect.getName()),
                victim.getName(),
                effect.getEffectHits()
        );

        player.playWorldSound(player.getLocation(), ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 2.0f);
    }

    public CauldronEffect getEffect(GamePlayer player) {
        return this.cauldronEffectMap.get(player.getUUID());
    }

    public void startCauldronBoost(GamePlayer player) {
        this.cauldronEffectMap.put(player.getUUID(), new CauldronEffect());
    }

    @Override
    public void onStart(@Nonnull GamePlayer player) {
        toxinLevel.put(player, 0);
    }

    @Override
    public void onStop() {
        toxinLevel.clear();
        cauldronEffectMap.clear();
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        cauldronEffectMap.remove(player.getUUID());
        toxinLevel.remove(player);
    }

    @Override
    public void onStart() {
        new GameTask() {
            @Override
            public void run() {
                CF.getAlivePlayers(Heroes.ALCHEMIST).forEach(gamePlayer -> {
                    if (isToxinLevelBetween(gamePlayer, 50, 75)) {
                        gamePlayer.addEffect(Effects.POISON, 2, 20);
                    }
                    else if (isToxinLevelBetween(gamePlayer, 75, 90)) {
                        gamePlayer.addEffect(Effects.WITHER, 1, 20);
                    }
                    else if (getToxinLevel(gamePlayer) >= 100) {
                        gamePlayer.dieBy(EnumDamageCause.TOXIN);
                    }

                    setToxinLevel(gamePlayer, getToxinLevel(gamePlayer) - 1);
                });
            }
        }.runTaskTimer(0, 10);
    }

    @Override
    public Talent getFirstTalent() {
        return Talents.POTION.getTalent();
    }

    @Override
    public Talent getSecondTalent() {
        return Talents.CAULDRON.getTalent();
    }

    @Override
    public Talent getPassiveTalent() {
        return Talents.INTOXICATION.getTalent();
    }

    public void addToxin(GamePlayer player, int value) {
        setToxinLevel(player, getToxinLevel(player) + value);
    }

    @Override
    @Nonnull
    public String getString(@Nonnull GamePlayer player) {
        final int toxinLevel = getToxinLevel(player);
        return getToxinColor(player) + "☠ &l" + toxinLevel + "%";
    }

    // some effects aren't really allowed so
    // replaced with EffectType because bukkit effects aren't enums
    private Effects getRandomEffect() {
        return Effects.getRandomEffect(EffectFlag.VANILLA);
    }

    private int getToxinLevel(GamePlayer player) {
        return toxinLevel.getOrDefault(player, 0);
    }

    private void setToxinLevel(GamePlayer player, int i) {
        toxinLevel.put(player, Numbers.clamp(i, 0, 100));
    }

    private boolean isToxinLevelBetween(GamePlayer player, int a, int b) {
        final int toxinLevel = getToxinLevel(player);
        return toxinLevel >= a && toxinLevel < b;
    }

    private String getToxinColor(GamePlayer player) {
        if (isToxinLevelBetween(player, 30, 50)) {
            return "&e";
        }
        else if (isToxinLevelBetween(player, 50, 75)) {
            return "&6";
        }
        else if (isToxinLevelBetween(player, 75, 90)) {
            return "&c";
        }
        else if (isToxinLevelBetween(player, 90, 100)) {
            return "&4";
        }

        return "&a";
    }

    private class AlchemistUltimate extends UltimateTalent {
        public AlchemistUltimate() {
            super("Alchemical Madness", 50);

            setDescription("""
                    Call upon the darkest spells to cast random &c&lNegative &7effect on your foes for &b15s &7and random &a&lPositive &7effect on yourself for &b30s&7.
                    """);

            setType(TalentType.ENHANCE);
            setItem(Material.FERMENTED_SPIDER_EYE);
            setSound(ENTITY_WITCH_AMBIENT, 0.5f);
            setCooldownSec(30);
        }

        @Nonnull
        @Override
        public UltimateResponse useUltimate(@Nonnull GamePlayer player) {
            final MadnessEffect positiveEffect = positiveEffects.getRandomElement();
            final MadnessEffect negativeEffect = negativeEffects.getRandomElement();

            positiveEffect.applyEffects(player, player);
            Collect.enemyPlayers(player).forEach(alivePlayer -> negativeEffect.applyEffects(player, alivePlayer));

            return UltimateResponse.OK;
        }
    }
}
