package me.hapyl.fight.npc;

import me.hapyl.eterna.module.npc.ClickType;
import me.hapyl.eterna.module.npc.NpcProperties;
import me.hapyl.eterna.module.player.dialog.Dialog;
import me.hapyl.eterna.module.player.dialog.DialogEntry;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.quest.CFDialog;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Objects;

public class BloodfiendNPC extends PersistentNPC {
    
    private final Dialog dialog = new CFDialog().addEntry(DialogEntry.of(
            this,
            "...",
            "Uhh, hello?",
            "Can't you see you blocking the shadow?",
            "I forgot to bring my sunscreen, so I have to hide here for a while.",
            "I've already sent a bat courier to the &cChâteau&f to bring my sunscreen.",
            "...",
            "You're still blocking the shadow though, move."
    ));
    
    public BloodfiendNPC(@Nonnull Key key) {
        super(key, 1.5, 58, 42.5, 135.0f, 0.0f, Component.text("Bloodfiend"), Objects.requireNonNull(HeroRegistry.BLOODFIEND.getSkin()));
        
        final NpcProperties properties = getProperties();
        properties.setInteractionDelay(20);
    }
    
    @Override
    public void onClick(@Nonnull Player player, @Nonnull ClickType clickType) {
        this.dialog.start(player);
    }
    
    @Override
    public void onSpawn(@Nonnull Player player) {
        setSitting(true);
    }
}
