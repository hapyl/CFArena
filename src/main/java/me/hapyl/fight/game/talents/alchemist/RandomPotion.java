package me.hapyl.fight.game.talents.alchemist;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.alchemist.Alchemist;
import me.hapyl.fight.game.heroes.alchemist.CauldronEffect;
import me.hapyl.fight.game.talents.TalentType;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.collection.RandomTable;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class RandomPotion extends Talent {

    private final RandomTable<Effect> effects = new RandomTable<>();

    @DisplayField private final short toxinAccumulation = 12;
    @DisplayField private final int effectDuration = 60;

    public RandomPotion() {
        super(
                "Abyssal Bottle", """
                        A bottle that is capable of creating potions from the &0&lvoid &7itself.
                        Drink to gain random positive effect.
                        """
        );

        setType(TalentType.ENHANCE);
        setItem(Material.POTION);
        setCooldown(50);

        effects.add(new Effect("&b\uD83C\uDF0A", "Speed Boost", Effects.SPEED, 1, effectDuration))
                .add(new Effect("☕", "Jump Boost", Effects.JUMP_BOOST, 1, effectDuration))
                .add(new Effect("&c⚔", "Attack") {
                    @Override
                    public void affect(@Nonnull GamePlayer player) {
                        final EntityAttributes attributes = player.getAttributes();

                        attributes.increaseTemporary(Temper.ALCHEMIST, AttributeType.ATTACK, 2.5, effectDuration);
                    }
                })
                .add(new Effect("&6🛡", "Defense") {
                    @Override
                    public void affect(@Nonnull GamePlayer player) {
                        final EntityAttributes attributes = player.getAttributes();

                        attributes.increaseTemporary(Temper.ALCHEMIST, AttributeType.DEFENSE, 0.5d, effectDuration);
                    }
                })
                .add(new Effect("&9⁘⁙", "Invisibility") {
                    @Override
                    public void affect(@Nonnull GamePlayer player) {
                        player.addEffect(Effects.INVISIBILITY, effectDuration, true);
                    }
                })
                .add(new Effect("&c❤", "Healing") {
                    @Override
                    public void affect(@Nonnull GamePlayer player) {
                        player.heal(10);
                    }
                });

    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        final Alchemist hero = (Alchemist) Heroes.ALCHEMIST.getHero();
        final CauldronEffect effect = hero.getEffect(player);

        hero.addToxin(player, toxinAccumulation);

        if (effect != null && effect.getDoublePotion() > 0) {
            effect.decrementDoublePotions();
            final Effect firstEffect = effects.getRandomElement();
            final Effect secondEffect = effects.getRandomElementNot(firstEffect);

            firstEffect.applyEffectsIgnoreFx(player);
            secondEffect.applyEffectsIgnoreFx(player);

            // Display Improved
            player.sendMessage("&e☕ &a&lDouble Potion has %s changes left", effect.getDoublePotion());
            player.sendMessage(
                    " &aGained %s &a%s &aand %s &a%s",
                    firstEffect.getChar(),
                    firstEffect.getName(),
                    secondEffect.getChar(),
                    secondEffect.getName()
            );

            player.sendTitle(
                    "&a%s      &a%s".formatted(firstEffect.getChar(), secondEffect.getChar()),
                    "&6%s    &6%s".formatted(firstEffect.getName(), secondEffect.getName()),
                    5,
                    10,
                    5
            );

            player.playSound(Sound.ITEM_BOTTLE_FILL, 1.25f);
            return Response.OK;
        }

        effects.getRandomElement().applyEffects(player);
        return Response.OK;
    }
}
