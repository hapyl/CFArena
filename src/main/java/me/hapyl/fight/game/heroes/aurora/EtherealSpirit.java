package me.hapyl.fight.game.heroes.aurora;

import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.util.Removable;
import me.hapyl.eterna.module.util.Ticking;
import me.hapyl.fight.game.Named;
import me.hapyl.fight.game.entity.GamePlayer;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.talents.TalentRegistry;
import me.hapyl.fight.game.talents.aurora.EtherealArrow;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.concurrent.ConcurrentLinkedQueue;

public class EtherealSpirit implements Ticking, Removable {

    private static final ItemStack[] ARROW_COLORS = {
            createArrow(1, 206, 213),
            createArrow(12, 190, 212),
            createArrow(29, 165, 210),
            createArrow(50, 134, 207),
            createArrow(64, 113, 205),
            createArrow(79, 90, 203),
            createArrow(90, 74, 202),
            createArrow(101, 57, 201),
            createArrow(113, 40, 200),
            createArrow(126, 22, 198),
            createArrow(141, 0, 196),
    };

    private static ItemStack createArrow(int red, int green, int blue) {
        return new ItemBuilder(Material.TIPPED_ARROW).setPotionColor(Color.fromRGB(red, green, blue)).toItemStack();
    }

    private final GamePlayer aurora;
    private final LivingGameEntity entity;
    protected final ConcurrentLinkedQueue<ItemDisplay> orbiting;

    protected int duration;
    private double theta = 0.0d;

    EtherealSpirit(GamePlayer aurora, LivingGameEntity entity) {
        this.aurora = aurora;
        this.entity = entity;

        this.orbiting = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void remove() {
        orbiting.forEach(Display::remove);
        orbiting.clear();

        duration = 0;
    }

    public boolean addStack() {
        final EtherealArrow talent = TalentRegistry.ETHEREAL_ARROW;
        final int currentStacks = orbiting.size();

        if (currentStacks >= talent.maxStacks) {
            return false;
        }

        duration = talent.buffDuration;
        orbiting.add(spawnArrow(entity.getLocation()));

        // Notify entity if the first stack
        if (currentStacks == 0) {
            entity.sendMessage("%s &bAurora &8(%s&8)&b has granted you %s&b!".formatted(
                    Named.ETHEREAL_SPIRIT.getPrefixColored(),
                    aurora.getName(),
                    Named.ETHEREAL_SPIRIT
            ));
        }

        // Play sound each stack
        entity.playWorldSound(Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, 1.25f);
        entity.playWorldSound(Sound.ENTITY_ALLAY_AMBIENT_WITH_ITEM, 1.0f);

        HeroRegistry.AURORA.spawnParticles(entity.getLocation(), 10, 0.3f, 0.5f, 0.3f);

        return true;
    }

    protected void tickDown() {
        duration--;
    }

    @Override
    public void tick() {
        // Buff is over
        if (duration == 0) {
            final Display display = orbiting.poll();

            if (display != null) {
                display.remove();

                // Fx
                HeroRegistry.AURORA.spawnParticles(display.getLocation(), 3, 0.1f, 0.1f, 0.1f);
            }

            // If the orbiting is not empty, restart duration
            if (orbiting.isEmpty()) {
                return;
            }

            duration = TalentRegistry.ETHEREAL_ARROW.buffDuration;
            return;
        }

        // Update buff
        TalentRegistry.ETHEREAL_ARROW.applyBuff(entity, aurora, orbiting.size());

        // Tick orbiting
        final Location location = entity.getLocation().add(0, entity.getEyeHeight() / 2, 0);
        location.setYaw(0.0f);
        location.setPitch(0.0f);

        final double offset = Math.PI * 2 / orbiting.size();

        int index = 1;

        for (ItemDisplay display : orbiting) {
            display.setItemStack(ARROW_COLORS[(ARROW_COLORS.length - 1) - duration / ARROW_COLORS.length]);

            // Update color
            final double x = Math.sin(theta + index * offset) * 0.85d;
            final double z = Math.cos(theta + index * offset) * 0.85d;

            location.add(x, 0, z);
            display.teleport(location);
            location.subtract(x, 0, z);

            ++index;
        }

        theta += Math.PI / 16;

        tickDown();
    }

    protected static ItemDisplay spawnArrow(Location location) {
        return Entities.ITEM_DISPLAY.spawn(location, self -> {
            self.setItemStack(ARROW_COLORS[0]);
            self.setTeleportDuration(1);
            self.setBillboard(Display.Billboard.VERTICAL);

            // Transformation
            final Transformation transformation = self.getTransformation();

            final Quaternionf leftRotation = transformation.getLeftRotation();
            leftRotation.z = -0.383f;
            leftRotation.w = 0.924f;

            final Vector3f scale = transformation.getScale();
            scale.x = 0.6f;
            scale.y = 0.6f;
            scale.z = 0.6f;

            self.setTransformation(transformation);
        });
    }
}
