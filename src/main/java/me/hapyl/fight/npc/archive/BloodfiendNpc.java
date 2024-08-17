package me.hapyl.fight.npc.archive;

import me.hapyl.eterna.module.player.PlayerSkin;
import me.hapyl.fight.dialog.Dialog;
import me.hapyl.fight.dialog.DialogEntry;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.npc.PersistentNPC;
import me.hapyl.fight.util.StringRandom;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class BloodfiendNpc extends PersistentNPC {

    private final Dialog firstDialog = new Dialog("vampire_talk_sunscreen")
            .addEntries(DialogEntry.npc(
                    this,
                    "...",
                    "Uhh, hello?",
                    "Can't you see you blocking the shadow?",
                    "I forgot to bring my sunscreen, so I have to hide here for a while.",
                    "I've already sent a bat courier to the &cChâteau&f to bring my sunscreen.",
                    "...",
                    "You're still blocking the shadow though, move."
            ));

    public BloodfiendNpc() {
        super(1.5, 58, 42.5, 135.0f, 0.0f, "Bloodfiend");

        final PlayerSkin skin = HeroRegistry.BLOODFIEND.getSkin();

        if (skin != null) {
            setSkin(skin.getTexture(), skin.getSignature());
        }

        setInteractionDelay(20);
    }

    @Override
    public void onClick(@Nonnull Player player) {
        if (!firstDialog.hasTalked(player)) {
            firstDialog.start(player);
            return;
        }

        // Talk, waiting for blood
        sendMessage(player, StringRandom.of("Move.", "Back off.", "..."));
    }

    @Override
    public void onSpawn(Player player) {
        setSitting(true);
    }
}
