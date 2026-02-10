package me.hapyl.fight.npc;

import me.hapyl.eterna.module.npc.ClickType;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.player.dialog.DialogEntry;
import me.hapyl.eterna.module.player.quest.*;
import me.hapyl.eterna.module.player.quest.objective.TalkToNpcQuestObjective;
import me.hapyl.eterna.module.reflect.Skin;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.reward.Reward;
import me.hapyl.fight.game.reward.RewardResource;
import me.hapyl.fight.quest.CFDialog;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class UndeadWatcherNPC extends PersistentNPC implements QuestRegister {
    
    private final Quest quest;
    
    public UndeadWatcherNPC(@Nonnull Key key) {
        super(
                key, 3.5, 62, 22.5, 90.0f, 0.0f, Component.text("Undead Watcher", Color.DEFAULT), Skin.of(
                        "ewogICJ0aW1lc3RhbXAiIDogMTY4MzIwNTE5NDE2MywKICAicHJvZmlsZUlkIiA6ICIzOTVkZTJlYjVjNjU0ZmRkOWQ2NDAwY2JhNmNmNjFhNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJzcGFyZXN0ZXZlIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2M4MmE5MzM1NmMyYjRhMzU4YTEzMDUwN2UzMzlkYzVlOWQ3Y2RkODQwYmY5NzdiODNhZmRiYWYxNWI3ZWI1YiIKICAgIH0KICB9Cn0=",
                        "p88TeoLDYX9rqw6pTWI+ZOrj48wPgtsl2CAx/LLB7IbIY6WqP8rZoX1lxQ+DecF8bfRUmVD4Aq3C8vTk4+bgzYZ0scBEsGqo+TvfGBpBwVMpT61o45senTplys8w85TcpXWbpGM/y+woDGQ/EO6iib3Edu2/ldxzlwjYe/k91Fed2Sgo1tffYLCk7/Z8iAwwwxstmPOJYSyLlR43fyPszvjicJMw5ASWnY75tu89kOM2unt0ZoBaH36WuKH/zDiEn8rBfwSyudXhYKwE3ai5+e5/l2OoqZ4fLfuJp5kFgr5CgaJ3QazjYnaB7xtoO6XeanRSxOK+j5Jbg614uXBzYJktpy3F+Mt7qYWz2ScJDbqD/WW82YJwQOojdTaJJJG2b+uR3U8/zBcmZ91DTK00FKRgb6YGbbHMue2U606QSIRc9o2CtsoEEvvDNALt3fUdcmL4jP17mt/815XSI8eR+sWneyFANTB+HDV1L4Rxd+sm/2c/RiiVQhFczTm0SCaxB8m/r2YpvzLJs47EymxuOB93wxy4M9+N5kI4hGi9qS3falI+jXR1LoIhpSCoVQlgoDn74tmMgKDpi1yzqlZCVGKmRbQMevMV+Qj7gY9hAfh85czfypLK6aP+rVnwqdxF4Ebqwz3UmVUZrIOIvONl+dod4v4weFhDnAArua0NJZw="
                )
        );
        
        this.quest = new UndeadQuest();
        
        getProperties().setInteractionDelay(20);
        
        sound = new PersistentNPCSound() {
            @Override
            public void play(@Nonnull Player player) {
                PlayerLib.playSound(player, Sound.ENTITY_SKELETON_AMBIENT, 1.0f);
                PlayerLib.playSound(player, Sound.ENTITY_SKELETON_HURT, 0.25f);
            }
        };
    }
    
    @Override
    public void registerQuests(@Nonnull QuestHandler handler) {
        handler.register(quest);
    }
    
    private class UndeadQuest extends Quest {
        private final Reward reward = new Reward(Key.ofString("undead_watcher_reward"), "Lost & Reclaimed")
                .withResource(RewardResource.COINS, 1_000)
                .withResource(RewardResource.RUBY, 10);
        
        public UndeadQuest() {
            super(CF.getPlugin(), Key.ofString("undead_quest"));
            
            this.addObjective(
                    new TalkToNpcQuestObjective(
                            UndeadWatcherNPC.this,
                            new CFDialog()
                                    .addEntry(DialogEntry.of(UndeadWatcherNPC.this, "..."))
                                    .addEntry(DialogEntry.of(UndeadWatcherNPC.this, "I'm the {npc_name}..."))
                                    .addEntry(DialogEntry.of(UndeadWatcherNPC.this, "I used to watch over this place..."))
                                    .addEntry(DialogEntry.of(UndeadWatcherNPC.this, "Before it was closed for good..."))
                                    .addEntry(DialogEntry.of(UndeadWatcherNPC.this, "Now I stand here, waiting patiently for my decay..."))
                                    .addEntry(DialogEntry.of(UndeadWatcherNPC.this, "Here, take those, I have no need for them anyways..."))
                    )
            );
            
            this.addStartBehaviour(QuestStartBehaviour.onJoin(false));
        }
        
        @Override
        public void onComplete(@Nonnull Player player, @Nonnull QuestData data) {
            reward.grant(player);
        }
    }
    
    @Override
    public void onClick(@Nonnull Player player, @Nonnull ClickType clickType) {
        if (quest.hasCompleted(player)) {
            sendMessage(player, Component.text("I will remain here... permanently."));
            return;
        }
        
        quest.start(player);
    }
}
