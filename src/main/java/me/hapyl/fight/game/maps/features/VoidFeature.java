package me.hapyl.fight.game.maps.features;

import me.hapyl.fight.game.damage.EnumDamageCause;
import me.hapyl.fight.game.effect.Effects;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.maps.MapFeature;
import me.hapyl.fight.util.collection.player.PlayerMap;
import me.hapyl.spigotutils.module.math.Numbers;
import org.bukkit.Sound;

import javax.annotation.Nonnull;

public class VoidFeature extends MapFeature {

    private final PlayerMap<Integer> voidMap = PlayerMap.newMap();
    private final char[] chars = { 'ᛈ', 'ᚢ', 'ᛋ', 'ᛏ', 'ᛟ', 'ᛏ', 'ᚨ' };

    public VoidFeature(String name, String description) {
        super(name, description);
    }

    @Override
    public void onDeath(@Nonnull GamePlayer player) {
        voidMap.remove(player);
    }

    @Override
    public void onStop() {
        voidMap.clear();
    }

    public void removeVoidValue(@Nonnull GamePlayer player) {
        voidMap.computeIfPresent(player, (pl, a) -> Numbers.clamp(a - 1, 0, chars.length));

        displayVoidValues(player);

        // Remove after displaying the value to not spam the zero thingy
        voidMap.removeIf(player, v -> v <= 0);
    }

    public void addVoidValue(@Nonnull GamePlayer player, int i) {
        voidMap.compute(player, (p, v) -> Numbers.clamp(v != null ? v + i : i, 0, chars.length));

        displayVoidValues(player);
    }

    protected void displayVoidValues(@Nonnull GamePlayer player) {
        final Integer current = voidMap.get(player);

        if (current == null) {
            return;
        }

        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < chars.length; i++) {
            builder.append(i < current ? "&d&n" : "&8").append(chars[i]);
        }

        String subtitle = "";
        switch (current) {
            case 5 -> subtitle = "Void is Watching...";
            case 6 -> subtitle = "Vulnerable to Void";
            case 7 -> {
                subtitle = "Void Consuming You";
                player.damage(30, EnumDamageCause.LIBRARY_VOID);
                player.addEffect(Effects.WITHER, 0, 20);
            }
        }

        player.sendTitle(builder.toString(), "&6" + subtitle, 0, 20, 5);
        player.playSound(Sound.AMBIENT_SOUL_SAND_VALLEY_MOOD, 2.0f);
    }
}
