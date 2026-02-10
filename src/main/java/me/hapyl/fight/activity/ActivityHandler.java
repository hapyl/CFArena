package me.hapyl.fight.activity;

import com.google.common.collect.Sets;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class ActivityHandler {
    
    private final static Set<ActivityInstance> instances;
    
    static {
        instances = Sets.newHashSet();
    }
    
    private ActivityHandler() {
    }
    
    public static <T extends Activity> void startActivity(@Nonnull Supplier<T> supplier) {
        final T activity = supplier.get();
        final List<Player> players = activity.players();
        
        // Stop activities for all players; if they have the same activity, the iterator will remove the first instance
        players.forEach(ActivityHandler::stopCurrentActivity);
        
        // Create an instance
        instances.add(new ActivityInstance(players, activity));
    }
    
    @Nullable
    public static Activity getActivity(@Nonnull Player player) {
        return getActivity(player, Activity.class);
    }
    
    @Nullable
    public static <T extends Activity> T getActivity(@Nonnull Player player, @Nonnull Class<T> clazz) {
        return instances.stream()
                        .filter(instance -> instance.hasPlayer(player) && clazz.isInstance(instance.activity()))
                        .map(instance -> clazz.cast(instance.activity()))
                        .findFirst()
                        .orElse(null);
    }
    
    public static boolean hasActivity(@Nonnull Player player) {
        return hasActivity(player, Activity.class);
    }
    
    public static boolean hasActivity(@Nonnull Player player, @Nonnull Class<? super Activity> clazz) {
        return instances.stream().anyMatch(instance -> clazz.isInstance(instance) && instance.hasPlayer(player));
    }
    
    public static void stopCurrentActivity(@Nonnull Player player) {
        stopInstance0(instance -> instance.hasPlayer(player), ActivityInstance::cancel);
    }
    
    public static void handleKick(Player player) {
        stopInstance0(instance -> instance.hasPlayer(player), instance -> instance.cancelKick(player));
    }
    
    public static void stopActivity(@Nonnull Activity activity) {
        stopInstance0(instance -> instance.activity() == activity, ActivityInstance::cancel);
    }
    
    private static void stopInstance0(Predicate<ActivityInstance> what, Consumer<ActivityInstance> how) {
        final Iterator<ActivityInstance> iterator = instances.iterator();
        
        while (iterator.hasNext()) {
            final ActivityInstance instance = iterator.next();
            
            if (what.test(instance)) {
                how.accept(instance);
                iterator.remove();
                return;
            }
        }
    }
}
