package me.hapyl.fight.game.type;

import me.hapyl.eterna.module.registry.KeyedEnum;
import me.hapyl.eterna.module.util.Enums;
import me.hapyl.fight.Message;
import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.maps.Selectable;
import me.hapyl.fight.game.type.types.Deathmatch;
import me.hapyl.fight.game.type.types.DeathmatchKills;
import me.hapyl.fight.game.type.types.FreeForAll;
import me.hapyl.fight.game.type.types.FrenzyMode;
import me.hapyl.fight.game.type.types.commission.CommissionGameType;
import me.hapyl.fight.util.handle.EnumHandleFunction;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum EnumGameType implements Selectable, KeyedEnum {

    FFA(FreeForAll::new),
    DEATH_MATCH(Deathmatch::new),
    DEATH_MATCH_KILLS(DeathmatchKills::new),
    FRENZY(FrenzyMode::new),

    COMMISSION(CommissionGameType::new) {
        @Override
        public boolean canBeSelected() {
            return false;
        }
    },

    ;

    private static List<EnumGameType> selectableGameTypes;

    private final GameType mode;

    EnumGameType(EnumHandleFunction<EnumGameType, GameType> fn) {
        this.mode = fn.apply(this);
    }

    @Nonnull
    public GameType getMode() {
        return mode;
    }

    @Override
    public boolean isSelected(@Nonnull Player player) {
        return Manager.current().currentEnumType() == this;
    }

    @Override
    public void select(@Nonnull Player player) {
        if (!canBeSelected()) {
            Message.ERROR.send(player, "This mode cannot be directly selected!");
            return;
        }

        if (isSelected(player)) {
            Message.ERROR.send(player, "This mode is already selected!");
            return;
        }

        Manager.current().setCurrentMode(this);

        Message.SUCCESS.broadcast("{%s} has selected {%s} mode!".formatted(player.getName(), getName()));
    }

    public boolean testWinCondition(@Nonnull GameInstance instance) {
        return this.mode.testWinCondition(instance);
    }

    public boolean onStop(@Nonnull GameInstance instance) {
        return this.mode.onStop(instance);
    }

    @Nonnull
    public String getName() {
        return mode.getName();
    }

    @Nonnull
    public String getDescription() {
        return mode.getDescription();
    }

    @Nonnull
    public static List<EnumGameType> getSelectableGameTypes() {
        if (selectableGameTypes == null) {
            final List<EnumGameType> types = new ArrayList<>();

            for (EnumGameType type : values()) {
                if (type.canBeSelected()) {
                    types.add(type);
                }
            }

            selectableGameTypes = Collections.unmodifiableList(types);
        }

        return selectableGameTypes;
    }
    
    @Nullable
    public static EnumGameType byName(@Nonnull String name) {
        return Enums.byName(EnumGameType.class, name);
    }
}
