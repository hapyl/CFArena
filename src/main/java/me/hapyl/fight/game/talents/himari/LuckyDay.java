package me.hapyl.fight.game.talents.himari;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.CollectionUtils;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.attribute.EntityAttributes;
import me.hapyl.fight.game.attribute.temper.Temper;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.heroes.himari.Himari;
import me.hapyl.fight.game.heroes.himari.HimariData;
import me.hapyl.fight.game.talents.Talent;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.task.TickingGameTask;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.List;

public class LuckyDay extends Talent {

    @DisplayField
    private final double damageBuff = 7.0d;

    private final List<HimariAction> actions = Lists.newArrayList();
    private final char[] chars = { 'ᛚ', 'ᚢ', 'ᚲ', 'ᚲ' };

    public LuckyDay(@Nonnull Key key) {
        super(key, "Lucky Day");

        setDescription("""
                &8;;This Talent is default. It doesn't need luck, but it takes time to reload.
                
                Instantly starts a "Roll the Dice" effect.
                As soon as it finishes, you get a random result.
                It could be %s boost, additional %s (Could also heal!), %s boost, or even charge your %s.
                All comes with a cost, however. You may get corrupted by the &0&lVoid.&7
                If you're lucky enough, you will unlock one of your talents.
                1 Talent or effect at once!
                """.formatted(AttributeType.DEFENSE, AttributeType.MAX_HEALTH, AttributeType.ATTACK, AttributeType.ENERGY_RECHARGE));
        //shortly: you do gambling, you win or die

        setItem(Material.BOOK);
        setDuration(4);
        setCooldownSec(18);

        actions.add(player -> {
            player.addEffect(Effects.WITHER, 4, 115);
            player.addEffect(Effects.BLINDNESS, 2, 100);
            player.sendSubtitle("&lFeel the cost.", 2, 110, 6);
        });

        actions.add(GamePlayer::chargeUltimate);
        actions.add(player -> {
            //Healing / Increasing Max HP
            player.sendSubtitle("&lYou feel easier on your soul.", 2, 90, 6);

            if (player.getHealth() != player.getMaxHealth()) {
                player.heal(80);
            }
            else {
                player.getAttributes().increaseTemporary(Temper.LUCKINESS, AttributeType.MAX_HEALTH, 180, 130);
                player.heal(80);
            }
        });

        actions.add(player -> {
            final EntityAttributes attributes = player.getAttributes();

            //damage buff
            attributes.increaseTemporary(Temper.LUCKINESS, AttributeType.ATTACK, damageBuff, 120);
            player.sendSubtitle("&lYou feel stronger right away.", 2, 100, 6);
        });

        actions.add(player -> {
            final HimariData data = getHero().getPlayerData(player);

            //unlock talent 2
            data.setTalent(TalentRegistry.DEAD_EYE);
            player.sendSubtitle("&lYou got a new Talent to use!", 2, 70, 6);
        });
        actions.add(player -> {
            final HimariData data = getHero().getPlayerData(player);

            //unlock talent 3
            data.setTalent(TalentRegistry.SPIKE_BARRIER);
            player.sendSubtitle("&lYou got a new Talent to use!", 2, 70, 6);
        });
    }

    @Override
    public Response execute(@Nonnull GamePlayer player) {
        // Preconditions
        HimariData data = player.getPlayerData(HeroRegistry.HIMARI);

        // 1 > Check if Himari talent is set
        HimariTalent currentTalent = data.getTalent();
        if (currentTalent != null) {
            return Response.error("Can't use that again until the new talent is used..");
        }

        // The dice-rolling and effect application logic will run in the task

        new TickingGameTask() {
            private final int maxRollTime = getDuration();
            private int currentIndex = 0;

            @Override
            public void run(int tick) {
                if (tick >= maxRollTime) {
                    talentChoice(player);
                    cancel();
                    return;  // Exit the method after canceling the task.
                }

                if (currentIndex < chars.length) {
                    player.sendSubtitle(Character.toString(chars[currentIndex]), 1, 5, 2);
                    player.playSound(Sound.BLOCK_LEVER_CLICK, 2);
                    currentIndex++;
                }
            }
        }.runTaskTimer(0, 9);
        return Response.ok();
    }

    public Himari getHero() {
        return HeroRegistry.HIMARI;
    }

    public void talentChoice(GamePlayer player) {
        // This thing will be activated  upon generating a number and giving it out.
        // it's main purpose is to choice which talent or effect will the player receive.

        final HimariAction action = CollectionUtils.randomElement(actions);

        if (action != null) {
            action.execute(player);
        }
    }
}
