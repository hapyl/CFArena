package me.hapyl.fight.npc.archive;

import me.hapyl.fight.Main;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.CollectibleEntry;
import me.hapyl.fight.dialog.Dialog;
import me.hapyl.fight.dialog.DialogEntry;
import me.hapyl.fight.game.collectible.relic.RelicHunt;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.task.ShutdownAction;
import me.hapyl.fight.gui.styled.eye.EyeGUI;
import me.hapyl.fight.npc.PersistentNPC;
import me.hapyl.fight.util.TickingScheduler;
import me.hapyl.eterna.module.hologram.PlayerHologram;
import me.hapyl.eterna.module.hologram.StringArray;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class TheEyeNPC extends PersistentNPC implements TickingScheduler {

    private final PlayerHologram holograms;
    private final RelicHunt relicHunt;
    private final Dialog dialog = new Dialog("eye_first_talk")
            .addEntries(DialogEntry.npc(
                    this,
                    "Hello and welcome, {player}.",
                    "I'm the &d&l{npc_name}&r, overseer of this place.",
                    "I see everything, everywhere, all at once...",
                    "Anyway... I can &osee&r that you haven't a clue about &dRelics&r.",
                    "&dRelics&r are hidden gems, scattered all around the world.",
                    "I used to be able to see them, all of them...",
                    "But now, I can barely feel their presence...",
                    "Help me by reminding of their location, so I can see them again, would you?",
                    "I will reward you plenty!",
                    "There is one &drelic&r right next to me, I can see this one clearly.",
                    "Talk to me again to &osee&r your &dRelic Hunt&r progress."
            ));
    private int tick;

    public TheEyeNPC() {
        super(-5.5d, 62.0d, 6.5d, -135f, 0, "The Eye");

        setSkin(
                "ewogICJ0aW1lc3RhbXAiIDogMTYxMzMwMjM5NTg0NSwKICAicHJvZmlsZUlkIiA6ICI4NDMwMDNlM2JlNTY0M2Q5OTQxMTBkMzJhMzU2MTk2MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJHYWJvTWNHYW1lciIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hODhiMWNkOTU3NDY3MmU4ZTMyNjJmMjEwYzBkZGRiYzA4MmVhNzU2OWU4ZTcwZjBjMDdiNGJlZTc1ZTMyZjYyIgogICAgfQogIH0KfQ==",
                "IfwCAwsmxYOgutDZaDi6mjsp5Hzl0oV6zXyYWl6iNDFyEXZisKbYIDHbw/Vua4T8FD2gwWHtM4nTduvRk2DRcmLubkpUmmJ+t/9/6oEa9N5VfG6veAA436fSnfhEl+F/MR2gTQFz7nJb/S0E/WmZGcMr7deGL61tdsFVVKwJOHboM/fokGWpfhMG7LRY5uI9S4CIC0f0sKiFRtDC0fXGNKpkumMYn8t6oCjpQnvESYRVV8AD6Ap8s2ajTRYM/OhFJEulTIXP8N70bD8qClpEpbL4RuC0fuEEuoGSIWvQ4PwFc5uSnT1WnVPMreSD8P5XEaxiNRlqHReTQ6Bz9XuYc7uMfYn+DneWZSBf4Y3SmfnSNtax7W+e1CXxMCDMQHFtAxoaNmmQ6lWKSEFSiGEAPILdM7Nq0HaM6ITgUW2TlSt+k82hh4jwp6C+G2y5+h5S55eLzQ5ERbjvvDZuHRLr0izlz/kszVdtcAGKxTGrHbWs+pYboTWrLEXiTJLYS5dUar3/TeCf6uh2WyeOUqpgbCwrYLKzbxXxYNKTGcbv7Sva72YodKH7uvl6uCp17/n0+PGV2ymzxzVb9w/oMlrUzz3QdfA9capV7jIDSrItnu0+AFL7O80LDTGTKUdFiIQDuaOKFh11DW8bGzJtRPPeD4RDvE3kiy4cvgZCh/0xQUg="
        );

        holograms = new PlayerHologram(getLocation().add(0, 2.25, 0));
        relicHunt = Main.getPlugin().getRelicHunt();

        schedule(0, 20).setShutdownAction(ShutdownAction.IGNORE);
    }

    @Override
    public void onClick(@Nonnull Player player) {
        if (!dialog.hasTalked(player)) {
            dialog.start(player);
            return;
        }

        new EyeGUI(player);
    }

    @Override
    public void onCreate(@Nonnull Player player) {
        holograms.create(player);
    }

    @Override
    public void tick() {
        holograms.setLines(player -> {
            final PlayerProfile profile = PlayerProfile.getProfile(player);

            if (profile == null) {
                return StringArray.empty();
            }

            final PlayerDatabase database = profile.getDatabase();

            if (!dialog.hasTalked(player)) {
                return blink("ᴏ̨ᴜᴇsᴛ ᴀᴠᴀɪʟᴀʙʟᴇ");
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

        tick++;
    }

    private StringArray blink(@Nonnull String message) {
        return StringArray.of(
                (tick % 2 == 0 ? "&6&l" : "&e&l") + message
        );
    }

}
