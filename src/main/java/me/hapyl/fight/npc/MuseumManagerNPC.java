package me.hapyl.fight.npc;

import me.hapyl.eterna.module.player.dialog.Dialog;
import me.hapyl.eterna.module.player.dialog.DialogEntry;
import me.hapyl.eterna.module.player.dialog.DialogTags;
import me.hapyl.eterna.module.reflect.npc.ClickType;
import me.hapyl.eterna.module.registry.Key;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class MuseumManagerNPC extends PersistentNPC {

    private final Dialog renovatingDialog = new Dialog()
            .addEntry(DialogEntry.of(
                    this,
                    "Hello, Stranger!",
                    "I'm... It doesn't matter who am I right now, nor do I have to tell you.",
                    "I'm currently renovating this place, so scram."
            ));

    public MuseumManagerNPC(@Nonnull Key key) {
        super(key, -34, 63, 1, "???");

        setSkin(
                "eyJ0aW1lc3RhbXAiOjE1ODYwNzg0MDcyODksInByb2ZpbGVJZCI6IjU2Njc1YjIyMzJmMDRlZTA4OTE3OWU5YzkyMDZjZmU4IiwicHJvZmlsZU5hbWUiOiJUaGVJbmRyYSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGMxODg4ZjI4NDI1OTVlNmM0ZWVkN2IyMmJlMDA0Zjg1YjJkMGUwZWE3ZmUxZGVkN2MzYTQ0MTZlYjBkMTdmNyJ9fX0=",
                "oeEMwjprVdtvsGQYDT1HcD23HRO61tqLk9qIRWtuzNwx8OgChkL79Zdn22FoQxyiLwzJvsLXNyJFLh4j6CN6EfidwrbYjwMqFJSae5xFxNYB1z8tJqpIMpuVcSWRdEB8uk06U/hZiXbYeG6M7H3UEs01GOjRycrULLwzHrBKEzogTvOx1QbTUuE6xrVU0Mdnq6JUFyxNLnoAyKChliSyIbFuRLTylJJiisWJHSynfiAm8seumk53owg7cmha9SdZYaKvgKy7uYZT3xuqpAa9BcE3Wcag/j8taSIghGXn5rI+mXiA9sPgDccqdles9Hqw7DbDIQ0rZ1pZAKMh4qs7y3bWH/6Yu9WIyvlf20GLkF7Z+uwp5Hgm42a8KdpMGiOckKMtBmclIbgtOw8qdzR0vPZhvehXHpmomMJrZRDEOFME3ANoAQgU2pryGMSYiQH0tFPjjW9CGTDBXABdC+NwvJp5kGoTfMox+xkG4GZwI/swnzMl2R1NQkeX+IwD494knDLi6OXJpGUEnexiA/grMO1ONEy/ia1gcccQbZ2hUzNyZN4kBr1z51gyiIw2Fk0SO3v+IzFajAqPOFdaDzfHMhLBKi31poHQeLCrTLhoEG+hsU8w6tFldGUZvX3EPx5Rko4tZtxkhYtahKGlgThcQ79Htar6c4HhFw/l2r8MoGM="
        );

        setInteractionDelay(20);
    }

    @Override
    public void onClick(@Nonnull Player player, @Nonnull ClickType clickType) {
        if (renovatingDialog.start(player, DialogTags.empty())) {
            return;
        }

        sendNpcMessage(player, "Scram");
    }
}
