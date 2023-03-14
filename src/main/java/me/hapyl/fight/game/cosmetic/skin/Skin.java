package me.hapyl.fight.game.cosmetic.skin;

import me.hapyl.fight.game.heroes.ClassEquipment;
import me.hapyl.fight.game.heroes.Heroes;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class Skin {

    static {
        new Skin(null, null).setEffectHandler(new EffectHandler() {

            @Override
            public void onTick(Player player, int tick) {

            }

            @Override
            public void onKill(Player player, LivingEntity victim) {

            }

            @Override
            public void onDeath(Player player, LivingEntity killer) {

            }

            @Override
            public void onMove(Player player, Location to) {

            }

            @Override
            public void onStandingStill(Player player) {

            }
        });
    }

    private final Heroes hero;
    private final String name;
    private final ClassEquipment equipment;

    private String description;
    private EffectHandler effectHandler;

    public Skin(Heroes hero, String name) {
        this.hero = hero;
        this.name = name;
        this.equipment = new ClassEquipment();
        this.effectHandler = EffectHandler.NONE;
    }

    public void setEffectHandler(@Nonnull EffectHandler effectHandler) {
        Validate.notNull(effectHandler, "EffectHandler cannot be null, use EffectHandler.NONE instead!");
        this.effectHandler = effectHandler;
    }

    @Nonnull
    public EffectHandler getEffectHandler() {
        return effectHandler;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public ClassEquipment getEquipment() {
        return equipment;
    }

    public Heroes getHero() {
        return hero;
    }

    public void equip(Player player) {
        getEquipment().equip(player);
    }

}
