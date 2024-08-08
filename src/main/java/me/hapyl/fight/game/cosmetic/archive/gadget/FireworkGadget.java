package me.hapyl.fight.game.cosmetic.archive.gadget;

import me.hapyl.fight.Main;
import me.hapyl.fight.game.Response;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.gadget.Gadget;
import me.hapyl.fight.game.experience.Experience;
import me.hapyl.fight.game.experience.ExperienceColor;
import me.hapyl.eterna.module.util.Enums;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import javax.annotation.Nonnull;

public class FireworkGadget extends Gadget {
    public FireworkGadget() {
        super("Firework", Rarity.RARE, Material.FIREWORK_ROCKET);

        setCooldownSec(10);

        setDescription("""
                Launch a firework that explodes with the color of your level.
                """);
    }

    @Nonnull
    @Override
    public Response execute(@Nonnull Player player) {
        final World world = player.getWorld();
        final Experience experience = Main.getPlugin().getExperience();
        final ExperienceColor experienceColor = experience.getExperienceColor(experience.getLevel(player));

        world.spawn(player.getLocation(), Firework.class, self -> {
            final FireworkMeta meta = self.getFireworkMeta();
            final FireworkEffect.Builder builder = FireworkEffect.builder()
                    .with(Enums.getRandomValue(FireworkEffect.Type.class, FireworkEffect.Type.BALL))
                    .withFlicker();

            for (ChatColor color : experienceColor.getColors()) {
                if (!color.isColor()) {
                    builder.withTrail();
                    continue;
                }

                final java.awt.Color javaColor = color.asBungee().getColor();
                builder.withColor(
                        Color.fromRGB(javaColor.getRed(), javaColor.getGreen(), javaColor.getBlue())
                );
            }

            self.setMaxLife(20);
            meta.addEffect(builder.build());
            self.setFireworkMeta(meta);
        });

        return Response.OK;
    }
}
