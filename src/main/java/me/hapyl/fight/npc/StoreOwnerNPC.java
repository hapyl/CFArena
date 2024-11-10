package me.hapyl.fight.npc;

import me.hapyl.eterna.module.locaiton.Position;
import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.player.dialog.*;
import me.hapyl.eterna.module.player.quest.Quest;
import me.hapyl.eterna.module.player.quest.QuestHandler;
import me.hapyl.eterna.module.player.quest.QuestRegister;
import me.hapyl.eterna.module.player.quest.QuestStartBehaviour;
import me.hapyl.eterna.module.player.quest.objective.TalkToNpcQuestObjective;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.eterna.module.util.BukkitUtils;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.entry.Metadata;
import me.hapyl.fight.database.entry.StoreEntry;
import me.hapyl.fight.event.custom.RelicFindEvent;
import me.hapyl.fight.game.collectible.relic.Relic;
import me.hapyl.fight.quest.CFDialog;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.store.PlayerStoreOffers;
import me.hapyl.fight.store.Store;
import me.hapyl.fight.store.StoreOffer;
import me.hapyl.fight.store.StoreOfferGUI;
import me.hapyl.fight.util.StringRandom;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Random;

public class StoreOwnerNPC extends PersistentNPC implements Listener, QuestRegister {

    public static final int RELIC_ID = 107;

    private final long coinsNeededForRelic = 100_000;
    private final long refreshCost = 3;

    private final Metadata doesKnowLilasNames = new Metadata("does_know_lilas_name");

    private final DialogFirstMeeting dialogFirstMeeting = new DialogFirstMeeting();
    private final QuestFirstMeeting questFirstMeeting = new QuestFirstMeeting();

    private final Location locationInFrontOfLila = BukkitUtils.defLocation(24.5, 65.0, -3.0);

    public StoreOwnerNPC(@Nonnull Key key) {
        super(key, 22.55, 65.5, -3.0, -90f, 0, "Lila");

        setSkin(
                "ewogICJ0aW1lc3RhbXAiIDogMTY4NTcyMjUyNjY0NSwKICAicHJvZmlsZUlkIiA6ICIzM2NjZDBjYTc2MTg0ODY4YWMyNjc0YTg2YmEzMDRhNiIsCiAgInByb2ZpbGVOYW1lIiA6ICJKaWlzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2QzZTBkMjU1NDIzMTdkOWE3YmU3MGQ3ZWY0Y2I1YjJjMjU3YjUzYWVjMGZjM2RmZGUyODg1YTNhMTIxYjBhODMiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
                "i5Aaex5G/sU/NJDiFvQLQmQ2eVGr2rrlzEaK/5RlWLgVMaJyoUbyhE+MtEUHladtD3nWELz4APcKQR+XJqkQE+4h1rt1RYhgI/WJwyOf7JyFr/fanJNREGMYpX/UmrUPOUSRVg/b8jrlYX942TWR3v1HShmIXmdWO2ehc+Xq8OrLPYadIjwXoMzcjzQzw/L0d4F4o71a1IzY6tjal7jRJN0TPelX7Mi+ItDmXNPaC5iy3GIT/blSosISzWrZ78i9SRsKFSR2dlOtRMZBGsiLgBmhA/eM2EptyQUhdCxwpKSl4ePZomUXIKLkYbfavG6N8DpjTALagZuEP5RkBJ9MFGiy/YGYSIeEK6tMaG3mhavPvJU1T6EcNV0xaT8xkHDNTRDTKKc22cR4oQlUA7uPD14pdvxwQcYtEKO0zPCKfmEYLnnU2DQfxlJyj31zWVF22ZY6dCY2lv5+UmnudR8SFXG7GFyp0ZSHBwD11evoGJ1UgR44eHxdAwkGmINbmbz8dRiXjVTxUvivTlqwdtRNeu4DICC7MOsPGbJgEpO+DKjHbccMyp0uR82QRL+2+uCZ8MXHGiwI+TGSk7cou2IvPuUqLM6Azxe74L8JLJ5zcttsYUr+Lc3M4FeYo4XatoJ3Xi7A0KwIYnKP0otfkQChfBqTfyNRJkPEkvb/FAIgtYM="
        );

        sound = new PersistentNPCSound(1.5f);

        setDialog(new DialogLoop());
    }

    // Yeah, kinda weird that store handler is here, but it's fine
    @EventHandler
    public void handlePlayerInteractEvent(PlayerInteractEvent ev) {
        final Player player = ev.getPlayer();
        final Block clickedBlock = ev.getClickedBlock();

        if (clickedBlock == null || ev.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        final Store store = CF.getPlugin().getStore();
        final PlayerStoreOffers offers = store.getOffers(player);
        final Location blockLocation = clickedBlock.getLocation();

        for (StoreOffer offer : offers.getOffers()) {
            final Location location = offer.getLocation();
            final double distanceSquared = blockLocation.distanceSquared(location);

            if (distanceSquared <= 1.0d) {
                // Only show dialog if the quest wasn't completed
                if (!questFirstMeeting.hasCompleted(player)) {
                    dialogFirstMeeting.startRude(player);
                    return;
                }

                new StoreOfferGUI(player, offer);
                return;
            }
        }
    }

    @EventHandler
    public void handleRelicFindEvent(RelicFindEvent ev) {
        final Player player = ev.getPlayer();
        final Relic relic = ev.getRelic();

        if (relic.getId() != RELIC_ID) {
            return;
        }

        // Store unlocked
        if (isStoreUnlocked(player)) {
            final StoreEntry entry = CF.getDatabase(player).storeEntry;
            final long coinsSpent = entry.getCoinsSpent();

            if (coinsSpent < coinsNeededForRelic) {
                sendNpcMessage(
                        player,
                        StringRandom.of("Please stop touching my stuff...", "You haven't earned it yet~", "Pretty, isn't it?")
                );
                ev.setCancelled(true);
                return;
            }
        }

        // Only show dialog if the quest wasn't completed
        if (!questFirstMeeting.hasCompleted(player)) {
            dialogFirstMeeting.startRude(player);
        }

        ev.setCancelled(true);
    }

    public boolean isStoreUnlocked(@Nonnull Player player) {
        return questFirstMeeting.hasCompleted(player);
    }

    @Override
    public void onSpawn(@Nonnull Player player) {
        setSitting(true);
    }

    @Nonnull
    @Override
    public String getName(@Nonnull Player player) {
        return doesKnowLilasNames.has(player) ? "Lila" : "???";
    }

    @Override
    public void registerQuests(@Nonnull QuestHandler handler) {
        handler.register(questFirstMeeting);
    }

    private class QuestFirstMeeting extends Quest {

        public QuestFirstMeeting() {
            super(CF.getPlugin(), Key.ofString("store_owner_first_meeting"));

            setName("Renovation");
            setDescription("You spot a new building and decide to check it out...");

            addStartBehaviour(QuestStartBehaviour.goTo(
                    new Position(16, 62, -2, 29, 69, 5),
                    new Dialog().addEntry(DialogEntry.of(
                            "I don't recognize this place...",
                            "Is there someone sitting over there?",
                            "I should talk to them."
                    ))
            ));

            final TalkToNpcQuestObjective objective = new TalkToNpcQuestObjective(StoreOwnerNPC.this, dialogFirstMeeting);
            objective.setDescription("Talk to the stranger.");

            addObjective(objective);
        }
    }

    private class DialogFirstMeeting extends CFDialog {

        private final DialogEntry[] entryRude = DialogEntry.of(
                StoreOwnerNPC.this,
                "How rude...",
                "It's nice to ask before touching someone else's stuff, you know.",
                "Anyways... &o*clears throat*"
        );

        private final String tagIsRude = "is_rude";

        public DialogFirstMeeting() {
            addEntry(StoreOwnerNPC.this, "Welcome to my store, dear customer.");

            addEntry(new DialogOptionEntry()
                    .setOption(1, DialogOptionEntry
                            .builder()
                            .prompt("Who are you?")
                            .add(
                                    StoreOwnerNPC.this,
                                    "Oh... right, I should introduce myself...",
                                    "My name is %s! I'm renting this place as my store!".formatted(getName())
                            )
                            .add(dialog -> doesKnowLilasNames.set(dialog.getPlayer(), true))
                            .add(StoreOwnerNPC.this, "Don't mind the mess, I'm in the middle of renovating this place.")
                    )
                    .setOption(2, DialogOptionEntry
                            .builder()
                            .prompt("Do you have a sister?")
                            .add(
                                    StoreOwnerNPC.this,
                                    "What a... weird question to ask a stranger...",
                                    "But I assume it doesn't hurt to answer — yes — I do.",
                                    "It's been ages since the last time I've seen her...",
                                    "Why'd you ask? Have you seen her? She's got the same hair color as-",
                                    "Actually... never mind, let's get back to business..."
                            )
                    )
                    .setOption(3, DialogOptionEntry.goodbye(
                            "Goodbye",
                            StoreOwnerNPC.this, "See ya!"
                    ))
            );

            addEntry(StoreOwnerNPC.this, "So, dear customer, are you interested in anything?");

            addEntry(
                    new DialogOptionEntry()
                            .setOption(1, DialogOptionEntry
                                    .builder()
                                    .prompt("Yes!")
                                    .add(StoreOwnerNPC.this, "What are you interested in exactly?")
                                    .add(
                                            new DialogOptionEntry()
                                                    .setOption(1, DialogOptionEntry
                                                            .builder()
                                                            .prompt("What are you offering?")
                                                            .add(
                                                                    StoreOwnerNPC.this,
                                                                    "See those four items on the pedestals?",
                                                                    "I sell those!",
                                                                    "I change them daily, so you better buy now, or them might never return...",
                                                                    "I'm joking, of course!",
                                                                    "...or am I..."
                                                            )
                                                    )
                                                    .setOption(2, DialogOptionEntry
                                                            .builder()
                                                            .prompt("That Relic over there.")
                                                            .add(
                                                                    StoreOwnerNPC.this,
                                                                    "The Relic you say?",
                                                                    "That's not really for sale...",
                                                                    "...",
                                                                    "Okay fine, depends on how much you're offering..."
                                                            )
                                                            .add(new DialogOptionEntry()
                                                                    .setOption(1, DialogOptionEntry
                                                                            .builder()
                                                                            .prompt("1")
                                                                            .add(StoreOwnerNPC.this, "You're funny!")
                                                                    )
                                                                    .setOption(2, DialogOptionEntry
                                                                            .builder()
                                                                            .prompt("10")
                                                                            .add(
                                                                                    StoreOwnerNPC.this,
                                                                                    "Yeah, the same joke isn't funny twice."
                                                                            )
                                                                    )
                                                                    .setOption(3, DialogOptionEntry
                                                                            .builder()
                                                                            .prompt("100")
                                                                            .add(StoreOwnerNPC.this, "Or trice...")
                                                                    )
                                                            )
                                                            .add(
                                                                    StoreOwnerNPC.this,
                                                                    "You know what, how about this:",
                                                                    "You spend &nenough&f coins in my store, and I give you that Relic for free, how does that sound?"
                                                            )
                                                            .add(new DialogOptionEntry()
                                                                    .setOption(1, DialogOptionEntry
                                                                            .builder()
                                                                            .prompt("How much is enough?")
                                                                    )
                                                            )
                                                            .add(StoreOwnerNPC.this, "Enough is... enough!")
                                                    )
                                    )
                            )
                            .setOption(2, DialogOptionEntry.goodbye(
                                    "No",
                                    StoreOwnerNPC.this, "O-okay then..."
                            ))
            );

            addEntry(
                    StoreOwnerNPC.this,
                    "Well, that's settled then!",
                    "You buy my items, I give you the Relic."
            );

            addEntry(DialogEntry.of(dialog -> {
                Registries.getPointOfInterests().STORE.discover(dialog.getPlayer());
            }));
        }

        @Nonnull
        @Override
        public DialogInstance newInstance(@Nonnull Player player, @Nonnull DialogTags tags) {
            final DialogInstance instance = super.newInstance(player, tags);

            // Rude start if player has clicked at Relic or tried to open the store
            if (tags.contains(tagIsRude)) {
                final Vector vector = locationInFrontOfLila.toVector().subtract(player.getLocation().toVector()).normalize();

                vector.setY(0.35);
                vector.multiply(1.2d);

                player.setVelocity(vector);

                instance.hijackEntries(entryRude);
            }

            return instance;
        }

        public void startRude(@Nonnull Player player) {
            start(player, DialogTags.of(tagIsRude));
        }
    }

    private class DialogLoop extends CFDialog {
        public DialogLoop() {
            addEntry(StoreOwnerNPC.this, "Sup!");

            addEntry(new DialogOptionEntry()
                    .setOption(1, DialogOptionEntry
                            .builder()
                            .advanceDialog(true)
                            .prompt("Change offers")
                            .add(
                                    StoreOwnerNPC.this,
                                    "You want me to change the offers?",
                                    "Well, that'll cost ya!"
                            )
                            .add(new DialogOptionEntry()
                                    .setOption(1, DialogOptionEntry.builder()
                                            .prompt("Yes, change offers &8(-%s&8)".formatted(Currency.RUBIES.formatProduct(refreshCost)))
                                            .add(DialogEntry.of(dialog -> {
                                                final Player player = dialog.getPlayer();
                                                final PlayerDatabase database = CF.getDatabase(player);

                                                final long playerRubies = database.currencyEntry.get(Currency.RUBIES);

                                                if (playerRubies < refreshCost) {
                                                    sendNpcMessage(player, "Looks like you're too poor...");
                                                }
                                                else {
                                                    database.currencyEntry.subtract(Currency.RUBIES, refreshCost);

                                                    CF.getPlugin().getStore().refreshOrders(player);

                                                    // Fx
                                                    sendNpcMessage(player, "Close your eyes, new offers are coming~");

                                                    PlayerLib.playSound(player, Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1.5f);
                                                    PlayerLib.addEffect(player, PotionEffectType.BLINDNESS, 20, 1);
                                                }

                                                dialog.cancel();
                                            }))
                                    )
                                    .setOption(2, DialogOptionEntry.goodbye("I've changed my mind."))
                            )
                    )
                    .setOption(2, DialogOptionEntry
                            .builder()
                            .advanceDialog(true)
                            .prompt("How many coins how I spent?")
                            .add(StoreOwnerNPC.this, "Hmm...")
                            .add(StoreOwnerNPC.this, "Let me do some accounting...")
                            .add(StoreOwnerNPC.this, dialog -> {
                                final PlayerDatabase database = CF.getDatabase(dialog.getPlayer());
                                final double coinsSpent = database.storeEntry.getCoinsSpent() * new Random().nextDouble(0.87, 1.4);

                                return "You've spent around %,.0f coins.".formatted(coinsSpent);
                            })
                    )
                    .setOption(3, DialogOptionEntry.goodbye("See you later!", StoreOwnerNPC.this, "I hope so!"))
            );
        }
    }
}
