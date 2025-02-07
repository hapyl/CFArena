package me.hapyl.fight.quest;

import me.hapyl.eterna.module.player.dialog.Dialog;
import me.hapyl.eterna.module.player.dialog.DialogEntry;
import me.hapyl.fight.game.setting.EnumSetting;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class CFDialog extends Dialog {

    @Override
    public int getEntryDelay(@Nonnull DialogEntry entry, @Nonnull Player player) {
        int delay = super.getEntryDelay(entry, player);

        if (EnumSetting.ACCELERATE_DIALOG.isEnabled(player)) {
            delay /= 2;
        }

        return Math.max(1, delay);
    }
}
