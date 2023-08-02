package me.hapyl.fight.game.entity;

import me.hapyl.fight.game.entity.custom.Abobo;
import me.hapyl.fight.game.entity.custom.AngryPiglin;
import me.hapyl.fight.game.entity.custom.Voidgloom;
import me.hapyl.fight.game.entity.custom.WardenDefender;
import org.bukkit.Location;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Zombie;

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

    ;

    public final GameEntityType<?> type;

    GameEntities(GameEntityType<?> clazz) {
        this.type = clazz;
    }

    @Nonnull
    public final LivingGameEntity spawn(@Nonnull Location location) {
        return type.create0(location);
    }

}
