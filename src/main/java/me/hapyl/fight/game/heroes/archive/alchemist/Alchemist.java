package me.hapyl.fight.game.heroes.archive.alchemist;

import io.netty.util.internal.ThreadLocalRandom;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.io.DamageInput;
import me.hapyl.fight.event.io.DamageOutput;
import me.hapyl.fight.game.EnumDamageCause;
import me.hapyl.fight.game.PlayerElement;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.UltimateCallback;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.Talents;
import me.hapyl.fight.game.talents.UltimateTalent;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.Collect;
import me.hapyl.fight.util.collection.RandomTable;
import me.hapyl.spigotutils.module.chat.Chat;
import me.hapyl.spigotutils.module.math.Numbers;
import me.hapyl.spigotutils.module.player.PlayerLib;
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
import static org.bukkit.potion.PotionEffectType.*;

public class Alchemist extends Hero implements UIComponent, PlayerElement {

    private final RandomTable<Effect> positiveEffects = new RandomTable<>();
    private final RandomTable<Effect> negativeEffects = new RandomTable<>();
    private final Map<GamePlayer, Integer> toxinLevel = new HashMap<>();
    private final Map<UUID, CauldronEffect> cauldronEffectMap = new HashMap<>();

    public Alchemist() {
        super("Alchemist");

        setArchetype(Archetype.STRATEGY);

        setDescription(
                "An alchemist who was deceived by the creation of the abyss. In return of help received an Abyssal Bottle that creates potions from the &0&lvoid &8itself."
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

        positiveEffects.add(new Effect("made you &lFASTER", PotionEffectType.SPEED, 30, 2))
                .add(new Effect("gave you &lJUMP BOOST", PotionEffectType.JUMP, 30, 1))
                .add(new Effect("made you &lSTRONGER", 30) {
                    @Override
                    public void affect(@Nonnull GamePlayer player, @Nonnull GamePlayer victim) {
                        final EntityAttributes playerAttributes = player.getAttributes();

                        playerAttributes.increaseTemporary(Temper.ALCHEMIST, AttributeType.ATTACK, 0.5d, duration);
                    }
                })
                .add(new Effect("gave you &lRESISTANCE", 30) {
                    @Override
                    public void affect(@Nonnull GamePlayer player, @Nonnull GamePlayer victim) {
                        final EntityAttributes playerAttributes = player.getAttributes();

                        playerAttributes.increaseTemporary(Temper.ALCHEMIST, AttributeType.DEFENSE, 0.25d, duration);
                    }
                })
                .add(new Effect("healed half of your missing health", 30) {
                    @Override
                    public void affect(@Nonnull GamePlayer player, @Nonnull GamePlayer victim) {
                        final double missingHealth = player.getMaxHealth() - player.getHealth();

                        player.heal(missingHealth / 2d);
                    }
                });

        negativeEffects.add(new Effect("&lpoisoned you", PotionEffectType.POISON, 15, 0) {
                    @Override
                    public void affect(@Nonnull GamePlayer player, @Nonnull GamePlayer victim) {
                        victim.getData().setLastDamager(player);
                    }
                })
                .add(new Effect("&lblinded you", PotionEffectType.BLINDNESS, 15, 0))
                .add(new Effect("&lis withering your blood", PotionEffectType.WITHER, 7, 0) {
                    @Override
                    public void affect(@Nonnull GamePlayer player, @Nonnull GamePlayer victim) {
                        victim.getData().setLastDamager(player);
                    }
                })
                .add(new Effect("&lslowed you", PotionEffectType.SLOW, 15, 2))
                .add(new Effect("&lmade you weaker", null, 15, 0) {
                    @Override
                    public void affect(@Nonnull GamePlayer player, @Nonnull GamePlayer victim) {
                        final EntityAttributes entityAttributes = victim.getAttributes();

                        entityAttributes.decreaseTemporary(Temper.ALCHEMIST, AttributeType.ATTACK, 0.5d, duration);
                    }
                })
                .add(new Effect("&lis... confusing?", PotionEffectType.CONFUSION, 15, 0));

        setUltimate(new UltimateTalent(
                "Alchemical Madness",
                "Call upon the darkest spells to cast random &c&lNegative &7effect on your foes for &b15s &7and random &a&lPositive &7effect on yourself for &b30s&7.",
                50
        ).setCooldownSec(30)
                .setType(Talent.Type.ENHANCE)
                .setItem(Material.FERMENTED_SPIDER_EYE)
                .setSound(ENTITY_WITCH_AMBIENT, 0.5f));
    }

    @Override
    public UltimateCallback useUltimate(@Nonnull GamePlayer player) {
        final Effect positiveEffect = positiveEffects.getRandomElement();
        final Effect negativeEffect = negativeEffects.getRandomElement();

        positiveEffect.applyEffects(player, player);
        Collect.enemyPlayers(player).forEach(alivePlayer -> negativeEffect.applyEffects(player, alivePlayer));

        return UltimateCallback.OK;
    }

    @Override
    public DamageOutput processDamageAsDamager(DamageInput input) {
        final LivingGameEntity victim = input.getEntity();
        final LivingGameEntity player = input.getDamagerAsPlayer();

        if (player == null) {
            return null;
        }

        final CauldronEffect effect = cauldronEffectMap.get(player.getUUID());

        if (!input.isEntityAttack() || effect == null || effect.getEffectHits() <= 0) {
            return null;
        }

        final PotionEffectType randomEffect = getRandomEffect();
        victim.addPotionEffect(randomEffect, 20, 3);
        effect.decrementEffectPotions();

        player.sendMessage(
                "&c¤ &eVenom Touch applied &l%s &eto %s. &l%s &echarges left.",
                Chat.capitalize(randomEffect.getName()),
                victim.getName(),
                effect.getEffectHits()
        );
        PlayerLib.playSound(player.getLocation(), ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 2.0f);
        return null;
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
                        gamePlayer.addPotionEffect(POISON, 20, 2);
                    }
                    else if (isToxinLevelBetween(gamePlayer, 75, 90)) {
                        gamePlayer.addPotionEffect(WITHER, 20, 1);
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
        return getToxinColor(player) + "☠ &l" + toxinLevel + "%%";
    }

    // some effects aren't really allowed so
    private PotionEffectType getRandomEffect() {
        final PotionEffectType value = PotionEffectType.values()[ThreadLocalRandom.current().nextInt(PotionEffectType.values().length)];
        return (value == BAD_OMEN || value == HEAL || value == HEALTH_BOOST || value == REGENERATION || value == ABSORPTION ||
                value == SATURATION || value == LUCK || value == UNLUCK || value == HERO_OF_THE_VILLAGE) ? getRandomEffect() : value;
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


}
