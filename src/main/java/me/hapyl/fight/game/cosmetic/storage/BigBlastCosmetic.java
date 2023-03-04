package me.hapyl.fight.game.cosmetic.storage;

import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.Display;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.shop.Rarity;
import me.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.meta.FireworkMeta;

public class BigBlastCosmetic extends Cosmetic implements Listener {

    private final Color[] VALID_COLORS = new Color[] {
            Color.RED, Color.ORANGE, Color.YELLOW, Color.LIME, Color.AQUA, Color.BLUE, Color.PURPLE, Color.FUCHSIA, Color.WHITE
    };

    public BigBlastCosmetic() {
        super("Big Blast", "A big explosion with a random color.", 500, Type.DEATH, Rarity.RARE, Material.FIREWORK_ROCKET);
    }

    @Override
    public void onDisplay(Display display) {
        final Location location = display.getLocation();
        final World world = display.getWorld();

        final Firework firework = world.spawn(location.add(0.0d, 1.0d, 0.0d), Firework.class);
        final Color randomColor = CollectionUtils.randomElement(VALID_COLORS, VALID_COLORS[0]);

        final FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(
                FireworkEffect.builder()
                        .flicker(false)
                        .trail(false)
                        .withColor(randomColor)
                        .with(FireworkEffect.Type.BALL_LARGE)
                        .build()
        );

        fireworkMeta.setPower(0);

        firework.setFireworkMeta(fireworkMeta);
        firework.detonate();
    }

    @EventHandler
    public void onFireworkDamage(EntityDamageByEntityEvent ev) {
        if (ev.getDamager() instanceof Firework) {
            ev.setDamage(0.0d);
            ev.setCancelled(true);
        }
    }

}
