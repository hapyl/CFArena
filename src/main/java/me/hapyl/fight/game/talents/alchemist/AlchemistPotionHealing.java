package me.hapyl.fight.game.talents.alchemist;

import me.hapyl.eterna.module.math.Tick;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.heroes.alchemist.ActivePotion;
import me.hapyl.fight.game.heroes.alchemist.AlchemistData;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.Color;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

import static org.bukkit.Sound.BLOCK_BREWING_STAND_BREW;

public class AlchemistPotionHealing extends AlchemistPotion {

    private final double healing = 30;
    private final double extraHealing = 10;

    private final int extraHealingThreshold = Tick.fromSecond(3);

    public AlchemistPotionHealing() {
        super("Potion of Healing", 20, Color.fromRGB(209, 13, 19));

        setDescription("""
                &aHeals&7 you for &a%.0f &c❤&7.
                
                If you &ndon't &ntake&7 &cdamage&7 in &b%s&7 after using this potion, &aheal&7 for additional &a%.0f&7 &c❤&7.
                """.formatted(healing, CFUtils.formatTick(extraHealingThreshold), extraHealing)
        );
    }

    @Nonnull
    @Override
    public ActivePotion use(@Nonnull AlchemistData data, @Nonnull GamePlayer player) {
        player.heal(healing);

        return new ExtraHealing(data, player, this);
    }

    public class ExtraHealing extends ActivePotion {

        public ExtraHealing(AlchemistData data, GamePlayer player, AlchemistPotion potion) {
            super(data, player, potion);
        }

        @Override
        public void run(int tick) {
            super.run(tick);

            // Extra healing
            if (tick == extraHealingThreshold) {
                data.cancelActivePotion();
                player.heal(extraHealing);

                // Fx
                player.sendTitle("&2\uD83D\uDC9E", "&aʜᴇᴀʟᴇᴅ!", 2, 8, 2);
                player.playWorldSound(BLOCK_BREWING_STAND_BREW, 1.25f);
            }

            // Fx
        }

        public void cancelExtraHealing() {
            data.cancelActivePotion();

            player.sendTitle("&4\uD83D\uDC9E", "&cʜᴇᴀʟɪɴɢ ᴄᴀɴᴄᴇʟʟᴇᴅ, ʏᴏᴜ ᴛᴏᴏᴋ ᴅᴀᴍᴀɢᴇ!", 2, 8, 2);
            player.playWorldSound(Sound.ENTITY_WITCH_HURT, 1.25f);
        }
    }
}
