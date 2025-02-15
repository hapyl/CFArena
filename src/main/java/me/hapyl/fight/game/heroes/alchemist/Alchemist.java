package me.hapyl.fight.game.heroes.alchemist;

import com.google.common.collect.Sets;
import me.hapyl.eterna.module.math.Tick;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.Message;
import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GameEntity;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.HeroEquipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.alchemist.*;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

import static org.bukkit.Sound.*;

public class Alchemist extends Hero implements UIComponent, PlayerDataHandler<AlchemistData>, Listener {

    public final Weapon stickMissing = Weapon.builder(Material.CLAY_BALL, Key.ofString("alchemist_stick_missing"))
                                             .name("&4Stick is Missing!")
                                             .description("""
                                                     Your stick is currently brewing a potion!
                                                     &8&o;;Click the cauldron to pause the brewing process and get your stick back.
                                                     """)
                                             .damage(1)
                                             .build();

    private final PlayerDataMap<AlchemistData> playerData = PlayerMap.newDataMap(AlchemistData::new);
    private final Set<AbyssalCurse> curseSet = Sets.newHashSet();

    private final int alchemicalMadnessPositiveDuration = Tick.fromSecond(20);
    private final int alchemicalMadnessNegativeDuration = alchemicalMadnessPositiveDuration / 2;

    private final AlchemistEffect[][] alchemicalMadnessEffects;

    public Alchemist(@Nonnull Key key) {
        super(key, "Alchemist");

        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE, Archetype.STRATEGY, Archetype.SELF_SUSTAIN);
        profile.setGender(Gender.FEMALE);

        setDescription(
                "An alchemist who was deceived by the abyss."
        );
        setItem("661691fb01825b9d9ec1b8f04199443146aa7d5627aa745962c0704b6a236027");

        setWeapon(new AlchemistWeapon());

        final HeroAttributes attributes = getAttributes();
        attributes.set(AttributeType.MAX_HEALTH, 125);
        attributes.set(AttributeType.DEFENSE, 0.5d);
        attributes.set(AttributeType.SPEED, 0.22d);

        final HeroEquipment equipment = getEquipment();
        equipment.setChestPlate(31, 5, 3, TrimPattern.SHAPER, TrimMaterial.COPPER);
        equipment.setBoots(102, 55, 38, TrimPattern.SILENCE, TrimMaterial.COPPER);

        setEventHandler(new AlchemistEventHandler());
        setUltimate(new AlchemistUltimate());

        // Init alchemical madness effects
        alchemicalMadnessEffects = new AlchemistEffect[][] {
                // Positive
                new AlchemistEffect[] {
                        AlchemistEffect.ofStatTemper("made you &4&lStronger", Color.fromRGB(99, 5, 14), AttributeType.ATTACK, 0.5d, alchemicalMadnessPositiveDuration),
                        AlchemistEffect.ofStatTemper("made you &b&lFaster", Color.fromRGB(99, 191, 247), AttributeType.SPEED, 0.05d, alchemicalMadnessPositiveDuration),
                        AlchemistEffect.ofStatTemper("made you &3&lJump Higher", Color.fromRGB(47, 139, 196), AttributeType.JUMP_STRENGTH, 0.2d, alchemicalMadnessPositiveDuration),
                        AlchemistEffect.ofStatTemper("&2&lProtected&e you", Color.fromRGB(22, 150, 8), AttributeType.DEFENSE, 0.5d, alchemicalMadnessPositiveDuration),
                        AlchemistEffect.of(
                                "&a&lHealed&e you!", Color.fromRGB(93, 245, 76), (entity, player) -> {
                                    final double missingHealth = player.getMaxHealth() - player.getHealth();

                                    player.heal(missingHealth * 0.5d);
                                }
                        )
                },

                // Negative
                new AlchemistEffect[] {
                        AlchemistEffect.ofStatTemper("made you &4&lWeaker", Color.fromRGB(99, 5, 14), AttributeType.ATTACK, -0.3d, alchemicalMadnessNegativeDuration),
                        AlchemistEffect.ofStatTemper("made you &b&lSlower", Color.fromRGB(99, 191, 247), AttributeType.SPEED, -0.05d, alchemicalMadnessNegativeDuration),
                        AlchemistEffect.ofStatTemper("made you &3&lJump Lower", Color.fromRGB(47, 139, 196), AttributeType.JUMP_STRENGTH, -0.1d, alchemicalMadnessNegativeDuration),
                        AlchemistEffect.ofStatTemper("took your &2&lProtection&e away", Color.fromRGB(22, 150, 8), AttributeType.DEFENSE, -0.5d, alchemicalMadnessNegativeDuration),
                        AlchemistEffect.of(
                                "&c&lHurt&e you", Color.fromRGB(196, 39, 63), (entity, player) -> {
                                    entity.damage(entity.getMaxHealth() * 0.3d, player, EnumDamageCause.MAGIC);
                                }
                        ),
                        AlchemistEffect.of(
                                "&8&lblinded&e you", Color.fromRGB(46, 40, 41), (entity, player) -> {
                                    entity.addEffect(Effects.BLINDNESS, alchemicalMadnessNegativeDuration, true);
                                }
                        )
                }
        };
    }

    public AlchemistEffect randomAlchemicalEffect(boolean positive) {
        return CollectionUtils.randomElementOrFirst(alchemicalMadnessEffects[positive ? 0 : 1]);
    }

    public void removeCurse(@Nonnull AbyssalCurse curse) {
        curseSet.remove(curse);
    }

    @Override
    public void onStart(@Nonnull GameInstance instance) {
        final IntoxicationPassive passiveTalent = getPassiveTalent();

        new GameTask() {
            @Override
            public void run() {
                getAlivePlayers().forEach(player -> {
                    if (isToxinLevelBetween(player, 60, 80)) {
                        player.addEffect(Effects.POISON, 20, true);
                    }
                    else if (isToxinLevelBetween(player, 80, 99)) {
                        player.damage(1, EnumDamageCause.TOXIN);
                        player.addEffect(Effects.DARKNESS, 20, true);
                        player.addEffect(Effects.NAUSEA, 20, true);

                        player.removeEnergy(1, player);
                    }
                    else if (getToxinLevel(player) >= 100) {
                        player.dieBy(EnumDamageCause.TOXIN);
                    }

                    // Decrement toxin
                    setToxinLevel(player, getToxinLevel(player) - passiveTalent.corrosionDecrement);
                });
            }
        }.runTaskTimer(0, passiveTalent.corrosionDecrementPeriod);
    }


    @Override
    public RandomPotion getFirstTalent() {
        return TalentRegistry.POTION;
    }

    @Override
    public CauldronAbility getSecondTalent() {
        return TalentRegistry.CAULDRON;
    }

    @Override
    public IntoxicationPassive getPassiveTalent() {
        return TalentRegistry.INTOXICATION;
    }

    @Override
    public boolean processInvisibilityDamage(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, double damage) {
        player.removeEffect(Effects.INVISIBILITY);
        player.sendSubtitle("&cYou dealt damage and lost your invisibility!", 5, 10, 5);

        return false;
    }

    @EventHandler
    public void handleExtraHealing(GameDamageEvent ev) {
        final LivingGameEntity entity = ev.getEntity();

        if (!(entity instanceof GamePlayer player)) {
            return;
        }

        if (!validatePlayer(player)) {
            return;
        }

        if (getPlayerData(player).activePotion instanceof AlchemistPotionHealing.ExtraHealing extraHealing) {
            extraHealing.cancelExtraHealing();
        }
    }

    @EventHandler
    public void handleAbyssCurse(GameDamageEvent ev) {
        final LivingGameEntity entity = ev.getEntity();
        final GameEntity damager = ev.getDamager();

        if (!(entity instanceof GamePlayer playerEntity) || !(damager instanceof GamePlayer playerDamager)) {
            return;
        }

        final EnumDamageCause cause = ev.getCause();

        if (cause == null || !cause.isMelee()) {
            return;
        }

        final AbyssalCurse curse = getCurse(playerDamager);

        if (curse == null) {
            return;
        }

        if (isCursed(playerEntity)) {
            playerDamager.sendMessage(Message.ERROR, "This player is already cursed!");
            return;
        }

        curse.transfer(playerEntity);
    }

    public boolean isCursed(@Nonnull GamePlayer player) {
        for (AbyssalCurse curse : curseSet) {
            if (curse.player().equals(player)) {
                return true;
            }
        }

        return false;
    }

    @Override
    @Nonnull
    public String getString(@Nonnull GamePlayer player) {
        final double toxinLevel = getToxinLevel(player);
        final String toxinColor = getToxinColor(player);

        return "%1$s&l%2$s %1$s%3$.0f%%".formatted(toxinColor, Named.ABYSS_CORROSION.getCharacter(), toxinLevel);
    }

    @Nonnull
    @Override
    public PlayerDataMap<AlchemistData> getDataMap() {
        return playerData;
    }

    public void setState(@Nonnull GamePlayer player, @Nonnull AlchemistState state) {
        final AlchemistData data = getPlayerData(player);

        if (data.state == state) {
            player.sendMessage(Message.ERROR, "Already in this state!");
            return;
        }

        data.state = state;
        data.state.apply(player);
    }

    @Override
    public void onStop(@Nonnull GameInstance instance) {
        curseSet.clear();
    }

    @Nullable
    private AbyssalCurse getCurse(@Nonnull GamePlayer bearer) {
        for (AbyssalCurse curse : curseSet) {
            if (curse.player().equals(bearer)) {
                return curse;
            }
        }

        return null;
    }

    private void setToxinLevel(@Nonnull GamePlayer player, double newValue) {
        getPlayerData(player).toxin = Math.max(0, newValue);
    }

    private double getToxinLevel(@Nonnull GamePlayer player) {
        return getPlayerData(player).toxin;
    }

    private boolean isToxinLevelBetween(GamePlayer player, int a, int b) {
        final double toxinLevel = getToxinLevel(player);
        return toxinLevel >= a && toxinLevel < b;
    }

    private String getToxinColor(GamePlayer player) {
        if (isToxinLevelBetween(player, 30, 50)) {
            return "&e";
        }
        else if (isToxinLevelBetween(player, 50, 75)) {
            return "&d";
        }
        else if (isToxinLevelBetween(player, 75, 90)) {
            return "&5";
        }
        else if (getToxinLevel(player) >= 90) {
            return "&5&l";
        }

        return "&a";
    }

    private final class AlchemistEventHandler extends HeroEventHandler {

        public AlchemistEventHandler() {
            super(Alchemist.this);
        }

        @Override
        public boolean handlePlayerClick(@Nonnull GamePlayer player, @Nonnull HotBarSlot slot) {
            final AlchemistData data = getPlayerData(player);

            // If clicked at first slot & choosing potion
            if (data.state == AlchemistState.CHOOSING_POTION && slot == HotBarSlot.TALENT_1) {
                setState(player, AlchemistState.NORMAL);
                TalentRegistry.POTION.startCd(player, 5);

                // Fx
                player.playWorldSound(ITEM_ARMOR_EQUIP_ELYTRA, 0.5f);
                return true;
            }

            if (data.state == AlchemistState.NORMAL) {
                return super.handlePlayerClick(player, slot);
            }

            final RandomPotion talent = getFirstTalent();
            final AlchemistPotion potion = talent.potionMap.get(slot);

            if (potion != null) {
                setState(player, AlchemistState.NORMAL);
                talent.startCd(player, talent.getCooldown() + talent.castDuration);

                player.schedule(
                        () -> {
                            data.setActivePotion(player, potion);

                            // Fx
                            player.playWorldSound(ENTITY_WITCH_DRINK, 1.0f);
                        }, talent.castDuration
                );
                return true;
            }

            return false;
        }
    }

    private int getCurseDuration(@Nonnull GamePlayer player, int baseDuration) {
        final double toxinLevel = getToxinLevel(player);
        final IntoxicationPassive passiveTalent = getPassiveTalent();

        return (int) (baseDuration * (1 - toxinLevel * passiveTalent.curseDecrementPerOneCorrosion));
    }

    private class AlchemistUltimate extends UltimateTalent {
        public AlchemistUltimate() {
            super(Alchemist.this, "Abyssal Curse", 60);

            setDescription("""
                    Apply &5Abyssal Curse&7 to yourself that gradually becomes &4u&kn&4s&4ta&kbl&4e&7.
                    
                    Hit a &nplayer&7 to transfer the curse to that player.
                    &8&o;;Other players can also transfer the curse.
                    
                    After the curse becomes &4unstable&7, it explodes, instantly &ckilling&7 the bearer.
                    """);

            setType(TalentType.DAMAGE);
            setItem(Material.FERMENTED_SPIDER_EYE);

            setCastDurationSec(1.2f);
            setDurationSec(15);
            setCooldownSec(30);
        }

        @Nonnull
        @Override
        public UltimateInstance newInstance(@Nonnull GamePlayer player) {
            return builder()
                    .onCastStart(() -> {
                        // Fx
                        player.spawnWorldParticle(player.getMidpointLocation(), Particle.ENCHANT, 100, 0, 0, 0, 1.0f);
                        player.playWorldSound(ENTITY_EVOKER_PREPARE_ATTACK, 2.0f);
                    })
                    .onExecute(() -> {
                        final int duration = getCurseDuration(player, getDuration());
                        curseSet.add(new AbyssalCurse(player, duration));

                        // Fx
                        player.playWorldSound(ENTITY_ELDER_GUARDIAN_AMBIENT, 2.0f);
                    });
        }

    }

}
