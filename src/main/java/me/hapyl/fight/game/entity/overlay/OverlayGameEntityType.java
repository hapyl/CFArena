package me.hapyl.fight.game.entity.overlay;

import me.hapyl.fight.game.entity.GameEntityType;
import me.hapyl.fight.game.entity.NamedGameEntity;
import me.hapyl.fight.game.heroes.equipment.Equipment;
import me.hapyl.fight.game.playerskin.PlayerSkin;
import org.bukkit.entity.Zombie;

import javax.annotation.Nonnull;

public class OverlayGameEntityType extends GameEntityType<Zombie> {

    private final PlayerSkin skin;
    private final Equipment equipment;

    public OverlayGameEntityType(@Nonnull String name, @Nonnull PlayerSkin skin) {
        super(name, Zombie.class);

        this.skin = skin;
        this.equipment = new Equipment();
    }

    @Nonnull
    public Equipment getEquipment() {
        return equipment;
    }

    @Nonnull
    public PlayerSkin getSkin() {
        return skin;
    }

    @Override
    public final void onSpawn(@Nonnull Zombie entity) {
        entity.setAdult();
        entity.setVisibleByDefault(false);
        entity.setSilent(true);

        equipment.equip(entity);
    }

    @Nonnull
    @Override
    public NamedGameEntity<Zombie> create(@Nonnull Zombie bukkitEntity) {
        return new OverlayNamedGameEntity(OverlayGameEntityType.this, bukkitEntity);
    }

}
