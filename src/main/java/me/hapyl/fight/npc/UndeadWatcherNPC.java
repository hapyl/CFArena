package me.hapyl.fight.npc;

import me.hapyl.eterna.module.player.PlayerLib;
import me.hapyl.eterna.module.reflect.npc.ClickType;
import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.parkour.ParkourCourse;
import me.hapyl.fight.game.parkour.storage.SlimeParkour;
import me.hapyl.fight.util.StringRandom;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class UndeadWatcherNPC extends PersistentNPC {
    public UndeadWatcherNPC(@Nonnull Key key) {
        super(key, 3.5, 62, 22.5, 90.0f, 0.0f, "Undead Watcher");

        setSkin(
                "ewogICJ0aW1lc3RhbXAiIDogMTY4MzIwNTE5NDE2MywKICAicHJvZmlsZUlkIiA6ICIzOTVkZTJlYjVjNjU0ZmRkOWQ2NDAwY2JhNmNmNjFhNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJzcGFyZXN0ZXZlIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2M4MmE5MzM1NmMyYjRhMzU4YTEzMDUwN2UzMzlkYzVlOWQ3Y2RkODQwYmY5NzdiODNhZmRiYWYxNWI3ZWI1YiIKICAgIH0KICB9Cn0=",
                "p88TeoLDYX9rqw6pTWI+ZOrj48wPgtsl2CAx/LLB7IbIY6WqP8rZoX1lxQ+DecF8bfRUmVD4Aq3C8vTk4+bgzYZ0scBEsGqo+TvfGBpBwVMpT61o45senTplys8w85TcpXWbpGM/y+woDGQ/EO6iib3Edu2/ldxzlwjYe/k91Fed2Sgo1tffYLCk7/Z8iAwwwxstmPOJYSyLlR43fyPszvjicJMw5ASWnY75tu89kOM2unt0ZoBaH36WuKH/zDiEn8rBfwSyudXhYKwE3ai5+e5/l2OoqZ4fLfuJp5kFgr5CgaJ3QazjYnaB7xtoO6XeanRSxOK+j5Jbg614uXBzYJktpy3F+Mt7qYWz2ScJDbqD/WW82YJwQOojdTaJJJG2b+uR3U8/zBcmZ91DTK00FKRgb6YGbbHMue2U606QSIRc9o2CtsoEEvvDNALt3fUdcmL4jP17mt/815XSI8eR+sWneyFANTB+HDV1L4Rxd+sm/2c/RiiVQhFczTm0SCaxB8m/r2YpvzLJs47EymxuOB93wxy4M9+N5kI4hGi9qS3falI+jXR1LoIhpSCoVQlgoDn74tmMgKDpi1yzqlZCVGKmRbQMevMV+Qj7gY9hAfh85czfypLK6aP+rVnwqdxF4Ebqwz3UmVUZrIOIvONl+dod4v4weFhDnAArua0NJZw="
        );

        setInteractionDelay(20);

        sound = new PersistentNPCSound() {
            @Override
            public void play(@Nonnull Player player) {
                PlayerLib.playSound(player, Sound.ENTITY_SKELETON_AMBIENT, 1.0f);
                PlayerLib.playSound(player, Sound.ENTITY_SKELETON_HURT, 0.25f);
            }
        };
    }

    @Override
    public void onClick(@Nonnull Player player, @Nonnull ClickType clickType) {
        final SlimeParkour parkour = (SlimeParkour) ParkourCourse.SLIME_PARKOUR.getParkour();
        final int fails = parkour.getFails();

        if (fails > 0) {
            sendNpcMessage(player, StringRandom.of(
                    "I've seen players die %s times here...",
                    "There have been at least %s recorded deaths...",
                    "There goes another one, %s...",
                    "Why do they keep dying? It's been %s times."
            ).formatted(fails));
        }
        else {
            sendNpcMessage(player, "No one has died yet here, could you be the first one?");
        }
    }
}
