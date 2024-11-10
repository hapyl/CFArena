package me.hapyl.fight.game.heroes.alchemist;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.Notifier;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.event.custom.GameDamageEvent;
import me.hapyl.fight.game.Disabled;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.HeroAttributes;
import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.EffectFlag;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.heroes.ultimate.UltimateInstance;
import me.hapyl.fight.game.heroes.ultimate.UltimateTalent;
import me.hapyl.fight.game.loadout.HotBarSlot;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.alchemist.*;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.ui.UIComponent;
import me.hapyl.fight.game.weapons.Weapon;
import me.hapyl.fight.util.collection.RandomTable;
import me.hapyl.fight.util.collection.player.PlayerDataMap;
import me.hapyl.fight.util.collection.player.PlayerMap;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Sound.*;

public class Alchemist extends Hero implements UIComponent, PlayerDataHandler<AlchemistData>, Listener, Disabled {

    private final PlayerDataMap<AlchemistData> playerData = PlayerMap.newDataMap(AlchemistData::new);

    private final RandomTable<MadnessEffect> positiveEffects = new RandomTable<>();
    private final RandomTable<MadnessEffect> negativeEffects = new RandomTable<>();
    private final Map<GamePlayer, Integer> toxinLevel = new HashMap<>();
    private final Map<UUID, CauldronEffect> cauldronEffectMap = new HashMap<>();

    public Alchemist(@Nonnull Key key) {
        super(key, "Alchemist");

        final HeroProfile profile = getProfile();
        profile.setArchetypes(Archetype.DAMAGE, Archetype.STRATEGY, Archetype.SELF_SUSTAIN);
        profile.setGender(Gender.FEMALE);

        setDescription(
                "An alchemist who was deceived by the abyss."
        );
        setItem("661691fb01825b9d9ec1b8f04199443146aa7d5627aa745962c0704b6a236027");

        setWeapon(Weapon.builder(Material.STICK, Key.ofString("alchemist_stick"))
                .name("Stick")
                .description("Turns out that a stick used in brewing can also be used in battle.")
                .enchant(Enchantment.KNOCKBACK, 1)
                .damage(8.0d)
        );

        final HeroAttributes attributes = getAttributes();
        attributes.set(AttributeType.MAX_HEALTH, 125);
        attributes.set(AttributeType.DEFENSE, 0.5d);
        attributes.set(AttributeType.SPEED, 0.22d);

        final Equipment equipment = getEquipment();
        equipment.setChestPlate(31, 5, 3, TrimPattern.SHAPER, TrimMaterial.COPPER);

        setEventHandler(new AlchemistEventHandler());

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

        player.sendMessage("&c¤ &eVenom Touch applied &l%s &eto %s. &l%s &echarges left.".formatted(
                Chat.capitalize(randomEffect.getName()),
                victim.getName(),
                effect.getEffectHits()
        ));

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
    public void onStop(@Nonnull GameInstance instance) {
        toxinLevel.clear();
        cauldronEffectMap.clear();
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        cauldronEffectMap.remove(player.getUUID());
        toxinLevel.remove(player);
    }

    @Override
    public void onStart(@Nonnull GameInstance instance) {
        new GameTask() {
            @Override
            public void run() {
                getAlivePlayers().forEach(gamePlayer -> {
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

    public void addToxin(GamePlayer player, int value) {
        setToxinLevel(player, getToxinLevel(player) + value);
    }

    @Override
    public boolean processInvisibilityDamage(@Nonnull GamePlayer player, @Nonnull LivingGameEntity entity, double damage) {
        player.removeEffect(Effects.INVISIBILITY);
        player.sendSubtitle("&cYou dealt damage and lost your invisibility!", 5, 10, 5);

        return false;
    }

    @EventHandler
    public void handleGameDamageEvent(GameDamageEvent ev) {
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

    @Override
    @Nonnull
    public String getString(@Nonnull GamePlayer player) {
        final int toxinLevel = getToxinLevel(player);
        return getToxinColor(player) + "☠ &l" + toxinLevel + "%";
    }

    @Nonnull
    @Override
    public PlayerDataMap<AlchemistData> getDataMap() {
        return playerData;
    }

    public void setState(@Nonnull GamePlayer player, @Nonnull AlchemistState state) {
        final AlchemistData data = getPlayerData(player);

        if (data.state == state) {
            player.sendMessage(Notifier.ERROR, "Already in this state!");
            return;
        }

        data.state = state;
        data.state.apply(player);
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
        toxinLevel.put(player, Math.clamp(i, 0, 100));
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

                player.schedule(() -> {
                    data.setActivePotion(player, potion);

                    // Fx
                    player.playWorldSound(ENTITY_WITCH_DRINK, 1.0f);
                }, talent.castDuration);
                return true;
            }

            return false;
        }
    }

    private class AlchemistUltimate extends UltimateTalent {
        public AlchemistUltimate() {
            super(Alchemist.this, "Alchemical Madness", 50);

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
        public UltimateInstance newInstance(@Nonnull GamePlayer player) {
            return null;
        }

    }
}
