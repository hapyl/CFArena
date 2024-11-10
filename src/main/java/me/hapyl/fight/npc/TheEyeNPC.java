package me.hapyl.fight.npc;

import me.hapyl.eterna.module.hologram.StringArray;
import me.hapyl.eterna.module.player.dialog.Dialog;
import me.hapyl.eterna.module.player.dialog.DialogOptionEntry;
import me.hapyl.eterna.module.player.quest.*;
import me.hapyl.eterna.module.player.quest.objective.TalkToNpcQuestObjective;
import me.hapyl.eterna.module.reflect.npc.ClickType;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CollectibleEntry;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.gui.styled.eye.EyeGUI;
import me.hapyl.fight.quest.FindRelicQuestObjective;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class TheEyeNPC extends PersistentNPC implements QuestRegister {

    public static final int RELIC_ID = 106;

    private final QuestChain questChain = new QuestChain(Key.ofString("the_eye"));

    public TheEyeNPC(@Nonnull Key key) {
        super(key, -5.5d, 62.0d, 6.5d, -135f, 0, "The Eye");

        setSkin(
                "ewogICJ0aW1lc3RhbXAiIDogMTYxMzMwMjM5NTg0NSwKICAicHJvZmlsZUlkIiA6ICI4NDMwMDNlM2JlNTY0M2Q5OTQxMTBkMzJhMzU2MTk2MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJHYWJvTWNHYW1lciIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hODhiMWNkOTU3NDY3MmU4ZTMyNjJmMjEwYzBkZGRiYzA4MmVhNzU2OWU4ZTcwZjBjMDdiNGJlZTc1ZTMyZjYyIgogICAgfQogIH0KfQ==",
                "IfwCAwsmxYOgutDZaDi6mjsp5Hzl0oV6zXyYWl6iNDFyEXZisKbYIDHbw/Vua4T8FD2gwWHtM4nTduvRk2DRcmLubkpUmmJ+t/9/6oEa9N5VfG6veAA436fSnfhEl+F/MR2gTQFz7nJb/S0E/WmZGcMr7deGL61tdsFVVKwJOHboM/fokGWpfhMG7LRY5uI9S4CIC0f0sKiFRtDC0fXGNKpkumMYn8t6oCjpQnvESYRVV8AD6Ap8s2ajTRYM/OhFJEulTIXP8N70bD8qClpEpbL4RuC0fuEEuoGSIWvQ4PwFc5uSnT1WnVPMreSD8P5XEaxiNRlqHReTQ6Bz9XuYc7uMfYn+DneWZSBf4Y3SmfnSNtax7W+e1CXxMCDMQHFtAxoaNmmQ6lWKSEFSiGEAPILdM7Nq0HaM6ITgUW2TlSt+k82hh4jwp6C+G2y5+h5S55eLzQ5ERbjvvDZuHRLr0izlz/kszVdtcAGKxTGrHbWs+pYboTWrLEXiTJLYS5dUar3/TeCf6uh2WyeOUqpgbCwrYLKzbxXxYNKTGcbv7Sva72YodKH7uvl6uCp17/n0+PGV2ymzxzVb9w/oMlrUzz3QdfA9capV7jIDSrItnu0+AFL7O80LDTGTKUdFiIQDuaOKFh11DW8bGzJtRPPeD4RDvE3kiy4cvgZCh/0xQUg="
        );
    }

    @Override
    public void onClick(@Nonnull Player player, @Nonnull ClickType clickType) {
        new EyeGUI(player);
    }

    @Override
    public void tick() {
        super.tick();

        setAboveHead(player -> {
            final PlayerProfile profile = CF.getProfile(player);
            final PlayerDatabase database = CF.getDatabase(player);

            if (!questChain.hasCompletedAllQuests(player)) {
                return blink("ᴏ̨ᴜᴇsᴛ ᴀᴠᴀɪʟᴀʙʟᴇ"); // Yeah the Q looks goofy ignore that 🤣
            }

            // Check for daily reward
            if (database.dailyRewardEntry.canClaimAny()) {
                return blink("ᴅᴀɪʟʏ ʀᴇᴡᴀʀᴅ ᴀᴠᴀɪʟᴀʙʟᴇ");
            }

            // Check for bonds
            if (profile.getChallengeList().hasCompleteAndNonClaimed()) {
                return blink("ᴅᴀɪʟʏ ʙᴏɴᴅ ᴄᴏᴍᴘʟᴇᴛᴇ");
            }

            // Check for relic rewards
            final CollectibleEntry collectibleEntry = database.collectibleEntry;

            if (collectibleEntry.canClaimAnyTier()) {
                return blink("ᴄᴀɴ ᴄʟᴀɪᴍ ʀᴇʟɪᴄ ʀᴇᴡᴀʀᴅs");
            }

            if (collectibleEntry.canLevelUpStabilizer()) {
                return blink("ᴄᴀɴ ʟᴇᴠᴇʟ ᴜᴘ ʀᴇʟɪᴄ sᴛᴀʙɪʟɪᴢᴇʀ");
            }

            return StringArray.empty();
        });
    }

    @Override
    public void registerQuests(@Nonnull QuestHandler handler) {
        questChain.addQuest(new QuestFirstMeeting());

        handler.register(questChain);
    }

    private class QuestFirstMeeting extends Quest {

        public QuestFirstMeeting() {
            super(CF.getPlugin(), Key.ofString("the_eye_first_meeting"));

            setName("A Stranger");
            setDescription("""
                    Someone wants to talk to you.
                    """);

            addStartBehaviour(QuestStartBehaviour.onJoin());

            addObjective(new TalkToNpcQuestObjective(TheEyeNPC.this, new Dialog()
                    .addEntry(
                            TheEyeNPC.this,
                            "Hello and welcome, {player}!",
                            "I'm the &d&l{npc_name}&r, the overseer of this place.",
                            "I see everything, everywhere, all at once...",
                            "Tell me, outlander, is there anything you wish to know?"
                    )
                    .addEntry(new DialogOptionEntry()
                            .setOption(1, DialogOptionEntry
                                    .builder()
                                    .prompt("Tell me about Relics")
                                    .add(
                                            TheEyeNPC.this,
                                            "Relics...",
                                            "Relics are hidden gems, scattered all around the world.",
                                            "I used to be able to see them, all of them...",
                                            "But now, I can barely feel their presence...",
                                            "Help me by reminding of their location, so I can see them again, would you?",
                                            "I will reward you plenty!"
                                    )
                            )
                            .setOption(2, DialogOptionEntry
                                    .builder()
                                    .prompt("Tell me about Bonds")
                                    .add(
                                            TheEyeNPC.this,
                                            "Bonds...",
                                            "I know it's the first time we've met, but I can sense...",
                                            "That you and I have bonds.",
                                            "You must experience them to gain wisdom!"
                                    )
                            )
                    )
                    .addEntry(
                            TheEyeNPC.this,
                            "One last thing.",
                            "I can sense that there is a relic next to me, but I can't quite see it.",
                            "Please help me see it..."
                    )
            ));

            addObjective(new FindRelicQuestObjective(RELIC_ID) {
                @Nonnull
                @Override
                public String getDescription() {
                    return "Find a Relic next to %s.".formatted(TheEyeNPC.this.getName());
                }

                @Override
                public void onStart(@Nonnull Player player) {
                    // Forcefully remove the relic to avoid soft-lock
                    CF.getDatabase(player).collectibleEntry.removeFound(RELIC_ID);
                }
            });

            addObjective(new TalkToNpcQuestObjective(TheEyeNPC.this, new Dialog()
                    .addEntry(
                            TheEyeNPC.this,
                            "Oh, yes!",
                            "I can see it clearly now...",
                            "Thanks for your help, outlander.",
                            "Talk to me anytime if you need any help."
                    )
            ));
        }
    }

}
