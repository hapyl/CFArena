package me.hapyl.fight.game.talents.archive.alchemist;

import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.effect.GameEffectType;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.archive.alchemist.Alchemist;
import me.hapyl.fight.game.heroes.archive.alchemist.CauldronEffect;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.util.collection.RandomTable;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

import static org.bukkit.potion.PotionEffectType.*;

public class RandomPotion extends Talent {

    private final RandomTable<Effect> effects = new RandomTable<>();
    @DisplayField private final short toxinAccumulation = 12;

    public RandomPotion() {
        super(
                "Abyssal Bottle", """
                        A bottle that is capable of creating potions from the &0&lvoid &7itself.
                        Drink to gain random positive effect.
                        """
        );

        setType(Type.ENHANCE);
        setItem(Material.POTION);
        setCooldown(50);

        effects.add(new Effect("&b\uD83C\uDF0A", "Speed Boost", SPEED, 60, 1))
                .add(new Effect("☕", "Jump Boost", JUMP, 100, 1))
                .add(new Effect("&c⚔", "Strength", INCREASE_DAMAGE, 60, 3))
                .add(new Effect("&6🛡", "Resistance", DAMAGE_RESISTANCE, 80, 1))
                .add(new Effect("&9⁘⁙", "Invisibility") {
                    @Override
                    public void affect(GamePlayer player) {
                        player.addEffect(GameEffectType.INVISIBILITY, 60, true);
                    }
                })
                .add(new Effect("&c❤", "Healing") {
                    @Override
                    public void affect(GamePlayer player) {
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
                    firstEffect.getEffectChar(),
                    firstEffect.getEffectName(),
                    secondEffect.getEffectChar(),
                    secondEffect.getEffectName()
            );

            player.sendTitle(
                    "&a%s      &a%s".formatted(firstEffect.getEffectChar(), secondEffect.getEffectChar()),
                    "&6%s    &6%s".formatted(firstEffect.getEffectName(), secondEffect.getEffectName()),
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
