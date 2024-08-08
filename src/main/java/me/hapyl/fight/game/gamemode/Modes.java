package me.hapyl.fight.game.gamemode;

import me.hapyl.fight.game.GameInstance;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.gamemode.modes.*;
import me.hapyl.fight.game.maps.Selectable;
import me.hapyl.eterna.module.chat.Chat;
import me.hapyl.eterna.module.util.Validate;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum Modes implements Selectable {

    FFA(new FreeForAll()),
    DEATH_MATCH(new Deathmatch()),
    DEATH_MATCH_KILLS(new DeathmatchKills()),
    //RUSH(new Rush()),
    FRENZY(new FrenzyMode()),
    //TTT(new AmongUs()), todo
    ;

    private final CFGameMode mode;

    Modes(CFGameMode mode) {
        this.mode = mode;
    }

    public CFGameMode getMode() {
        return mode;
    }

    @Override
    public boolean isSelected(@Nonnull Player player) {
        return Manager.current().getCurrentMode() == this;
    }

    @Override
    public void select(@Nonnull Player player) {
        if (isSelected(player)) {
            Chat.sendMessage(player, "&6&lMODE! &cAlready selected!");
            return;
        }

        Manager.current().setCurrentMode(this);

        Chat.broadcast("&6&lMODE! &e%s selected &l%s&e!".formatted(player.getName(), getName()));
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

    @Nullable
    public static Modes byName(String name, @Nullable Modes def) {
        final Modes value = Validate.getEnumValue(Modes.class, name == null ? FFA.name() : name);
        return value == null ? def : value;
    }

}
