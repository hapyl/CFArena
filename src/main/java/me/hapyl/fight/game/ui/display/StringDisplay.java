package me.hapyl.fight.game.ui.display;

import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.entity.Entities;
import me.hapyl.eterna.module.math.Numbers;
import me.hapyl.fight.CF;
import me.hapyl.fight.event.DamageInstance;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.game.entity.LivingGameEntity;
import me.hapyl.fight.game.task.GameTask;
import me.hapyl.fight.game.task.ShutdownAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * Base class to display text elements in the world.
 * <p>
 * This implementation uses the new TextDisplay entity.
 */
public class StringDisplay {
    
    protected final int stay;
    @Nullable protected DisplayAnimation animation;
    @Nonnull protected Transformation initTransformation = transformationScale(1.0f);
    @Nonnull protected String string;
    protected float viewRange;
    
    public StringDisplay(@Nonnull String string, final int stay) {
        this.string = string;
        this.stay = stay;
        this.viewRange = 16;
    }
    
    public StringDisplay(final int stay) {
        this("", stay);
    }
    
    @Nullable
    public DisplayAnimation getAnimation() {
        return animation;
    }
    
    public void setAnimation(@Nullable DisplayAnimation animation) {
        this.animation = animation;
    }
    
    public void setViewRange(float viewRange) {
        this.viewRange = viewRange;
    }
    
    /**
     * Called once at {@link #display(Location)}
     *
     * @param display - Text display.
     */
    public void onStart(@Nonnull TextDisplay display) {
        display.setInterpolationDuration(stay);
    }
    
    /**
     * Called once before removing the display.
     *
     * @param display - Text display.
     */
    public void onEnd(@Nonnull TextDisplay display) {
    }
    
    /**
     * Gets the opaque value for this tick.
     *
     * @param tick - Current tick, 0-max;
     * @return the value to set.
     */
    public short opaque(int tick) {
        return (short) (tick * 5);
    }
    
    public void onPrepare(@Nonnull TextDisplay display) {
    }
    
    /**
     * Starts display if {@link #string} is not empty nor blank.
     *
     * @param location - Location to spawn at.
     *                 Location will be tempered with {@link #getLocation(Location)}
     *                 before spawning text display. Override {@link #getLocation(Location)}
     *                 to change the behavior.
     */
    public void display(@Nonnull Location location) {
        if (string.isEmpty() || string.isBlank()) {
            return;
        }
        
        final TextDisplay text = Entities.TEXT_DISPLAY.spawn(
                getLocation(location), self -> {
                    self.setBillboard(Display.Billboard.CENTER);
                    self.setSeeThrough(true);
                    self.setTeleportDuration(1);
                    self.setInterpolationDuration(0);
                    self.setTransformation(initTransformation);
                    self.setTextOpacity((byte) -1);
                    self.setText(Chat.color(string));
                    self.setViewRange(viewRange);
                    
                    onPrepare(self);
                }
        );
        
        onStart(text);
        
        new GameTask() {
            private int tick = 0;
            
            @Override
            public void run() {
                // The text will become almost opaque at the duration end
                final short opaque = (short) (-200 * (double) tick / stay);
                
                text.setTextOpacity(Numbers.clampByte((byte) opaque));
                
                if (tick++ >= stay || (animation != null && animation.animate(text, tick, stay))) {
                    onEnd();
                }
            }
            
            private void onEnd() {
                StringDisplay.this.onEnd(text);
                
                cancel();
                text.remove();
            }
        }.runTaskTimer(0, 1).setShutdownAction(ShutdownAction.IGNORE);
    }
    
    @Nonnull
    public Location getLocation(Location location) {
        return location.clone().add(randomDouble(), new Random().nextDouble() * 0.25d, randomDouble());
    }
    
    private double randomDouble() {
        final double random = new Random().nextDouble();
        return new Random().nextBoolean() ? random : -random;
    }
    
    public static void buff(@Nonnull Location location, @Nonnull AttributeType type) {
        ascend(location, "%s &a&lBUFF!".formatted(type), 30);
    }
    
    public static void debuff(@Nonnull Location location, @Nonnull AttributeType type) {
        descend(location, "%s &c&lDE-BUFF!".formatted(type), 30);
    }
    
    public static void ascend(@Nonnull Location location, @Nonnull String string, int stay) {
        final StringDisplay display = new StringDisplay(string, stay) {
            @Override
            public short opaque(int tick) {
                return (short) (tick * 20);
            }
        };
        
        display.animation = DisplayAnimation.sinAscend();
        display.display(location);
    }
    
    public static void descend(@Nonnull Location location, String string, int stay) {
        final StringDisplay display = new StringDisplay(string, stay) {
            @Override
            public short opaque(int tick) {
                return (short) (tick * 20);
            }
        };
        
        display.animation = DisplayAnimation.sinDescend();
        display.display(location);
    }
    
    public static void damage(@Nonnull Location location, @Nonnull DamageInstance instance) {
        final StringDisplay display = new StringDisplay(instance.getDamageFormatted(), instance.isCrit() ? 40 : 20) {
            private final LivingGameEntity damager = instance.getDamager();
            
            @Override
            public void onPrepare(@Nonnull TextDisplay display) {
                if (damager != null) {
                    display.setVisibleByDefault(false);
                }
            }
            
            @Override
            public void onStart(@Nonnull TextDisplay display) {
                // Show only for the players who can see the damager
                if (damager != null) {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if (player.equals(damager.getEntity()) || damager.isVisibleTo(player)) {
                            player.showEntity(CF.getPlugin(), display);
                        }
                    });
                }
                
                display.setDefaultBackground(false);
            }
        };
        
        display.initTransformation = transformationScale(instance.isCrit() ? 1.25f : 1.0f);
        display.animation = DisplayAnimation.sinAscend();
        
        display.display(location);
    }
    
    private static Transformation transformationScale(float xyz) {
        return transformationScale(xyz, xyz, xyz);
    }
    
    private static Transformation transformationScale(float x, float y, float z) {
        return new Transformation(
                new Vector3f(0.0f, 0.0f, 0.0f),
                new AxisAngle4f(0.0f, 0.0f, 0.0f, 0.0f),
                new Vector3f(x, y, z),
                new AxisAngle4f(0.0f, 0.0f, 0.0f, 0.0f)
        );
    }
    
}
