package me.hapyl.fight.game.talents.himari;

import me.hapyl.fight.game.Debug;
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
import me.hapyl.fight.registry.Key;
import me.hapyl.fight.util.displayfield.DisplayField;
import org.bukkit.Material;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class LuckyDay extends Talent {



    private final char[] chars = {'ᛚ', 'ᚢ', 'ᚲ', 'ᚲ'};

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

        new TickingGameTask(){
            private final int maxRollTime = getDuration();
            private int currentIndex = 0;

            @Override
            public void run(int tick) {
                if(tick >= maxRollTime){
                    resultRemembering(player);
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


    public int talentChoice(GamePlayer player) {
        // This thing will be activated  upon generating a number and giving it out.
        // it's main purpose is to choice which talent or effect will the player receive.

        int randomNumber = player.random.nextInt(6);
        return randomNumber;
    }



    @DisplayField
    private final double damageBuff = 7.0d;

    public void effectPicked(int talentChoiceResult, GamePlayer player) {
        final EntityAttributes attributes = player.getAttributes();
        final HimariData data = getHero().getPlayerData(player);

        switch (talentChoiceResult) {
            case 0:
                //Punishment for gambling too much
                player.addEffect(Effects.WITHER, 4, 115);
                player.addEffect(Effects.BLINDNESS,2,100);
                player.sendSubtitle("&lFeel the cost.", 2, 110, 6);
                Debug.info("0");
                break;

            case 1:
                //Increase ult Energy to it's maximum
                player.addEnergy(60);
                Debug.info("1");
                break;

             case 2:
                 //Healing / Increasing Max HP
                player.sendSubtitle("&lYou feel easier on your soul.",2,90,6);

                if (player.getHealth() != player.getMaxHealth()){
                    player.heal(80);
                } else {
                    player.getAttributes().increaseTemporary(Temper.LUCKINESS, AttributeType.MAX_HEALTH, 180, 130);
                    player.heal(80);
                }
                Debug.info("2");
                break;

            case 3:
                //damage buff
                attributes.increaseTemporary(Temper.LUCKINESS, AttributeType.ATTACK, damageBuff, 120);
                player.sendSubtitle("&lYou feel stronger right away.",2,100,6);
                Debug.info("3");
                break;

            case 4:
                //unlock talent 2
                data.setTalent(TalentRegistry.DEAD_EYE);
                player.sendSubtitle("&lYou got a new Talent to use!",2,70,6);
                Debug.info("4");
                break;

            case 5:
                //unlock talent 3
                data.setTalent(TalentRegistry.SPIKE_BARRIER);
                player.sendSubtitle("&lYou got a new Talent to use!",2,70,6);
                Debug.info("5");
                break;

            default: //if default you're a dumbass
                player.sendSubtitle("No effect...",2,7,1);
                break;
        }
    }

    public void resultRemembering(GamePlayer player) {
        int talentChoiceResult = talentChoice(player);
        effectPicked(talentChoiceResult, player);
    }
}
