package me.hapyl.fight.npc;

import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.parkour.ParkourCourse;
import me.hapyl.fight.game.parkour.storage.SlimeParkour;
import me.hapyl.fight.game.playerskin.PlayerSkin;
import me.hapyl.fight.npc.archive.TheEyeNPC;
import me.hapyl.fight.util.StringRandom;
import me.hapyl.spigotutils.module.player.PlayerLib;
import me.hapyl.spigotutils.module.reflect.npc.ClickType;
import me.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import me.hapyl.spigotutils.module.reflect.npc.NPCPose;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public enum PersistentNPCs {

    GUIDE(
            new PersistentNPC(3.5d, 64.0d, 3.5d, 135.0f, 0.0f, "Guide") {
                @Override
                public void onPrepare() {
                    setSkin(
                            "ewogICJ0aW1lc3RhbXAiIDogMTY0Nzc5Mzg1MTk1OCwKICAicHJvZmlsZUlkIiA6ICIxNzU1N2FjNTEzMWE0YTUzODAwODg3Y2E4ZTQ4YWQyNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJQZW50YXRpbCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kYjc5Yzc3MTc5MmU4NTE1NjcwYTQ2YjlmNzliZmIzYWVmMDE5MWNmMDBkN2ZkNDM1ZjEzMmJlNTIxYjFiNjZjIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
                            "gO9BuD+ogui4QAi+31a7QyXHaztdowhPacXuhC1OwDdUkdr79yTyVg5Evqml8jYd3+iix6PW+FbER56jlH3x9GIVKLusVHqJo5P1HkjcA0AbTXcgY5Vhb9WIEvwzj1CAm/3N7gqhJPnRKC4gNiw+lHpfoA86RSTODrsO2Zvdx+r6OGIbPexSBJ1ahtwdxj3YHDDhYaqKp54xt7L9/nc7qCoCH3jiL6iBZwDnF+i3BUunWgfya/05O3LVdRY3k1uwaXw2tC0k7puiQDUIwZ1NsBdcSb3RXW4RsbP+bDGNf+7B6aPtFQol1BUUz6VuSrvcQUYenWvnborJkUhgc/ublcMPoSbFVHmvy6PnKtjujjZbeUUjxcT6In9S/77k2MAR3qiAVBUH9H7gX2x5CVEqNEelB/O4AoTj87Lj0A5tGlJGATS0mZRZ/F5aqnUvqsLnFhMVxrEAF4ej991X5ZVZ459cE2nXOQmlcskchZTHsu4fj0OF1nfpeYWYCuiGfG7kitPXmH30Vdz1IuaPiFECgY0+s5PYZ9m09abELPhJ/8VPyUTlhhnUJp9xnKuc875YuXi02neI1lkhy6bgY3nHxixc1Smy1/ixuwPRnA9SsJAq5wjWMWHRmXxdag0HVMrchhlD51iuPoa+Uq/zA1GJlshhoCSegbaT6QTi+HTTbnQ="
                    );

                    addDialogLine("Hello there {player}!");
                    addDialogLine("I see you are new to the server.");
                    addDialogLine("...As it happens, I am too!");
                    addDialogLine("Thus, I cannot help you for now.");
                    addDialogLine("But I will be able to help you in the future.");
                }
            }
    ),

    THE_EYE(new TheEyeNPC()),

    HYPIXEL(
            new PersistentNPC(10, 65, 24, 102.5f, 0.0f, null) {
                @Override
                public void onPrepare() {
                    setSkin(
                            "eyJ0aW1lc3RhbXAiOjE1ODY5NTMzOTMyMjMsInByb2ZpbGVJZCI6ImRlNTcxYTEwMmNiODQ4ODA4ZmU3YzlmNDQ5NmVjZGFkIiwicHJvZmlsZU5hbWUiOiJNSEZfTWluZXNraW4iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzMwMGI1M2Y2Y2RmZmZhNDg1MTM3YjdmMzRmN2ZkMjQyNjUwNWJiZjQ1NGU2MWNiZGY2ZmFjMGRkODRhNDc2ZTUifX19",
                            "RJBBaFik7HpM7SMIpjbB6BP2LOSc/iBTKRutAEYYV8GtbVQzUoKw1mnLQAi2GKilzP65i/qBKUBpgZM805uMXT6SFVuvnLlO54e6T6Hs71hYR366Fd03NCohD7W+ssMqdoMRyM/e/90h7Mt5mCBzvVnhtxeAZBxkF/7+Y8jCwIY8ozBX696k6gxCpRTiAnh21NCs6ZH+AEcAqLd0CcSGVzUCeXC6x5PERUYKh2uR/fr1ouvh9F7jAkTiTEwtYties/Rzl8EgQxstRDaiJVCBhb4adOKwG14tQhLPPLJD0HvXOqEsblWR8dRzFK6ZzkgsXEgJ66rGhgkKACLmVycleLMtashKMFlXwYAsV/6uq2xdyHiRIya+p9F8pSHqTuLQzuUGg4tT9Z4eDtNszsa+sm4II6T5q/vdm+Qd+zvhKtlXJ9hXcY+8pDqjoEaGbuhEMCrQvTl64ACEApu8Mj7NDldINuBkHKz1YFIlBpNkpDtV6M5Qz48AriYZ0eWNhxa6Gl6una7iSBR8GfTv7D9OrKvHlb2H/O8QA9HNEyfJ3a05eOGX4jUqfEmasoeYCrbu6lNnCKTeIPyuLfogWlhrkWuLmRjz92o4ygWoNksHrXUmDn/zYaufKqxrLBgDweRkJDs/5XeGR6ouTbdPR+owM3gnPBV3JiB6CqDt1QHUxxc="
                    );

                    setLookAtCloseDist(0);
                    setPose(NPCPose.CROUCHING);
                }
            }
    ),

    SLIME_PARKOUR_ENJOYER(
            new PersistentNPC(3.5, 62, 22.5, 90.0f, 0.0f, "Undead Watcher") {
                @Override
                public void onPrepare() {
                    setSkin(
                            "ewogICJ0aW1lc3RhbXAiIDogMTY4MzIwNTE5NDE2MywKICAicHJvZmlsZUlkIiA6ICIzOTVkZTJlYjVjNjU0ZmRkOWQ2NDAwY2JhNmNmNjFhNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJzcGFyZXN0ZXZlIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2M4MmE5MzM1NmMyYjRhMzU4YTEzMDUwN2UzMzlkYzVlOWQ3Y2RkODQwYmY5NzdiODNhZmRiYWYxNWI3ZWI1YiIKICAgIH0KICB9Cn0=",
                            "p88TeoLDYX9rqw6pTWI+ZOrj48wPgtsl2CAx/LLB7IbIY6WqP8rZoX1lxQ+DecF8bfRUmVD4Aq3C8vTk4+bgzYZ0scBEsGqo+TvfGBpBwVMpT61o45senTplys8w85TcpXWbpGM/y+woDGQ/EO6iib3Edu2/ldxzlwjYe/k91Fed2Sgo1tffYLCk7/Z8iAwwwxstmPOJYSyLlR43fyPszvjicJMw5ASWnY75tu89kOM2unt0ZoBaH36WuKH/zDiEn8rBfwSyudXhYKwE3ai5+e5/l2OoqZ4fLfuJp5kFgr5CgaJ3QazjYnaB7xtoO6XeanRSxOK+j5Jbg614uXBzYJktpy3F+Mt7qYWz2ScJDbqD/WW82YJwQOojdTaJJJG2b+uR3U8/zBcmZ91DTK00FKRgb6YGbbHMue2U606QSIRc9o2CtsoEEvvDNALt3fUdcmL4jP17mt/815XSI8eR+sWneyFANTB+HDV1L4Rxd+sm/2c/RiiVQhFczTm0SCaxB8m/r2YpvzLJs47EymxuOB93wxy4M9+N5kI4hGi9qS3falI+jXR1LoIhpSCoVQlgoDn74tmMgKDpi1yzqlZCVGKmRbQMevMV+Qj7gY9hAfh85czfypLK6aP+rVnwqdxF4Ebqwz3UmVUZrIOIvONl+dod4v4weFhDnAArua0NJZw="
                    );
                }

                @Override
                public void onClick(Player player, HumanNPC npc, ClickType clickType) {
                    final SlimeParkour parkour = (SlimeParkour) ParkourCourse.SLIME_PARKOUR.getParkour();
                    final Location location = npc.getLocation();
                    final int fails = parkour.getFails();

                    npc.sendNpcMessage(player, StringRandom.of(
                            "I've seen players die %s times here...",
                            "There have been at least %s recorded deaths...",
                            "There goes another one, %s...",
                            "Why do they keep dying? It's been %s times."
                    ).formatted(fails));

                    PlayerLib.playSound(player, Sound.ENTITY_SKELETON_AMBIENT, 1.0f);
                    PlayerLib.playSound(player, Sound.ENTITY_SKELETON_HURT, 0.25f);
                }
            }
    ),

    BLOODFIEND(
            new PersistentNPC(1.5, 57.5, 42.5, 135.0f, 0.0f, "Bloodfiend") {
                @Override
                public void onPrepare() {
                    final PlayerSkin skin = Heroes.BLOODFIEND.getHero().getSkin();
                    if (skin != null) {
                        setSkin(skin.getTexture(), skin.getSignature());
                    }

                    addDialogLine("...");
                    addDialogLine("Uhh, hello?");
                    addDialogLine("Can't you see you blocking the shadow?");
                    addDialogLine("I forgot to bring my sunscreen, so I have to hide here for a while.");
                    addDialogLine("I've already sent a bat courier to the &cChâteau&f to bring my sunscreen.");
                    addDialogLine("...");
                    addDialogLine("You're still blocking the shadow though, move.");
                }

                @Override
                public void onSpawn(Player player) {
                    setSitting(true);
                }
            }
    ),

    ;

    private final PersistentNPC npc;

    PersistentNPCs(PersistentNPC npc) {
        this.npc = npc;
    }

    @Nonnull
    public PersistentNPC getNpc() {
        return npc;
    }
}
