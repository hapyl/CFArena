package me.hapyl.fight.npc;

import me.hapyl.eterna.module.player.PlayerSkin;
import me.hapyl.eterna.module.player.dialog.DialogEntry;
import me.hapyl.eterna.module.reflect.npc.ClickType;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.quest.CFDialog;
import me.hapyl.fight.util.StringRandom;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class BloodfiendNPC extends PersistentNPC {

    public BloodfiendNPC(@Nonnull Key key) {
        super(key, 1.5, 58, 42.5, 135.0f, 0.0f, "Bloodfiend");

        final PlayerSkin skin = HeroRegistry.BLOODFIEND.getSkin();

        if (skin != null) {
            setSkin(skin.getTexture(), skin.getSignature());
        }

        setInteractionDelay(20);

        setDialog(new CFDialog().addEntry(DialogEntry.of(
                this,
                "...",
                "Uhh, hello?",
                "Can't you see you blocking the shadow?",
                "I forgot to bring my sunscreen, so I have to hide here for a while.",
                "I've already sent a bat courier to the &cChâteau&f to bring my sunscreen.",
                "...",
                "You're still blocking the shadow though, move."
        )));
    }

    @Override
    public void onClick(@Nonnull Player player, @Nonnull ClickType clickType) {
        sendNpcMessage(player, StringRandom.of("Move.", "Back off.", "..."));
    }

    @Override
    public void onSpawn(@Nonnull Player player) {
        setSitting(true);
    }
}
