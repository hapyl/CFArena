package me.hapyl.fight.game.entity;

import me.hapyl.fight.game.entity.custom.*;
import me.hapyl.fight.game.entity.custom.genie.Genie;
import me.hapyl.fight.game.entity.custom.voids.VoidAbomination;
import org.bukkit.Location;
import org.bukkit.entity.*;

import javax.annotation.Nonnull;

// Stores both custom entity types
public enum GameEntities {

    // Test entities
    PIGGY(GameEntityType.of("Piggy", Pig.class)),
    ZOMBOO(GameEntityType.of("Zomboo", Zombie.class).setType(EntityType.HOSTILE)),
    PIGMEME(GameEntityType.of("Pigmeme", PigZombie.class).setType(EntityType.PASSIVE)),
    ABOBO(new Abobo()),

    WARDEN_DEFENDER(new WardenDefender()),
    VOIDGLOOM(new Voidgloom()),
    ANGRY_PIGLIN(new AngryPiglin()),
    BLADESOUL(new Bladesoul()),
    GENIE(new Genie()),

    // Void
    VOID_ABOMINATION(new VoidAbomination()),

    ;

    public final GameEntityType<?> type;

    GameEntities(GameEntityType<?> clazz) {
        this.type = clazz;
    }

    @Nonnull
    public final LivingGameEntity spawn(@Nonnull Location location) {
        return type.spawn(location);
    }

}
