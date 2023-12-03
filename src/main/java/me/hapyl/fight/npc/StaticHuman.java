package me.hapyl.fight.npc;

import me.hapyl.fight.gui.EyeGUI;
import me.hapyl.spigotutils.module.reflect.npc.ClickType;
import me.hapyl.spigotutils.module.reflect.npc.Human;
import me.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import me.hapyl.spigotutils.module.reflect.npc.NPCPose;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public enum StaticHuman {

    GUIDE(
            "&aGuide", getLocation(3.5d, 64.0d, 3.5d, 135.0f, 0.0f), new String[]
            {
                    "ewogICJ0aW1lc3RhbXAiIDogMTY0Nzc5Mzg1MTk1OCwKICAicHJvZmlsZUlkIiA6ICIxNzU1N2FjNTEzMWE0YTUzODAwODg3Y2E4ZTQ4YWQyNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJQZW50YXRpbCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kYjc5Yzc3MTc5MmU4NTE1NjcwYTQ2YjlmNzliZmIzYWVmMDE5MWNmMDBkN2ZkNDM1ZjEzMmJlNTIxYjFiNjZjIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
                    "gO9BuD+ogui4QAi+31a7QyXHaztdowhPacXuhC1OwDdUkdr79yTyVg5Evqml8jYd3+iix6PW+FbER56jlH3x9GIVKLusVHqJo5P1HkjcA0AbTXcgY5Vhb9WIEvwzj1CAm/3N7gqhJPnRKC4gNiw+lHpfoA86RSTODrsO2Zvdx+r6OGIbPexSBJ1ahtwdxj3YHDDhYaqKp54xt7L9/nc7qCoCH3jiL6iBZwDnF+i3BUunWgfya/05O3LVdRY3k1uwaXw2tC0k7puiQDUIwZ1NsBdcSb3RXW4RsbP+bDGNf+7B6aPtFQol1BUUz6VuSrvcQUYenWvnborJkUhgc/ublcMPoSbFVHmvy6PnKtjujjZbeUUjxcT6In9S/77k2MAR3qiAVBUH9H7gX2x5CVEqNEelB/O4AoTj87Lj0A5tGlJGATS0mZRZ/F5aqnUvqsLnFhMVxrEAF4ej991X5ZVZ459cE2nXOQmlcskchZTHsu4fj0OF1nfpeYWYCuiGfG7kitPXmH30Vdz1IuaPiFECgY0+s5PYZ9m09abELPhJ/8VPyUTlhhnUJp9xnKuc875YuXi02neI1lkhy6bgY3nHxixc1Smy1/ixuwPRnA9SsJAq5wjWMWHRmXxdag0HVMrchhlD51iuPoa+Uq/zA1GJlshhoCSegbaT6QTi+HTTbnQ="
            },
            self -> {
                self.addTextAboveHead("&e&lCLICK");

                self.addDialogLine("Hello there {player}!");
                self.addDialogLine("I see you are new to the server.");
                self.addDialogLine("...As it happens, I am too!");
                self.addDialogLine("Thus, I cannot help you for now.");
                self.addDialogLine("But, I will be able to help you in the future.");
            }
    ),

    THE_EYE(
            "&aThe Eye", getLocation(28.5, 65, 0.5, 90, 0), new String[]
            {
                    "ewogICJ0aW1lc3RhbXAiIDogMTYxMzMwMjM5NTg0NSwKICAicHJvZmlsZUlkIiA6ICI4NDMwMDNlM2JlNTY0M2Q5OTQxMTBkMzJhMzU2MTk2MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJHYWJvTWNHYW1lciIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hODhiMWNkOTU3NDY3MmU4ZTMyNjJmMjEwYzBkZGRiYzA4MmVhNzU2OWU4ZTcwZjBjMDdiNGJlZTc1ZTMyZjYyIgogICAgfQogIH0KfQ==",
                    "IfwCAwsmxYOgutDZaDi6mjsp5Hzl0oV6zXyYWl6iNDFyEXZisKbYIDHbw/Vua4T8FD2gwWHtM4nTduvRk2DRcmLubkpUmmJ+t/9/6oEa9N5VfG6veAA436fSnfhEl+F/MR2gTQFz7nJb/S0E/WmZGcMr7deGL61tdsFVVKwJOHboM/fokGWpfhMG7LRY5uI9S4CIC0f0sKiFRtDC0fXGNKpkumMYn8t6oCjpQnvESYRVV8AD6Ap8s2ajTRYM/OhFJEulTIXP8N70bD8qClpEpbL4RuC0fuEEuoGSIWvQ4PwFc5uSnT1WnVPMreSD8P5XEaxiNRlqHReTQ6Bz9XuYc7uMfYn+DneWZSBf4Y3SmfnSNtax7W+e1CXxMCDMQHFtAxoaNmmQ6lWKSEFSiGEAPILdM7Nq0HaM6ITgUW2TlSt+k82hh4jwp6C+G2y5+h5S55eLzQ5ERbjvvDZuHRLr0izlz/kszVdtcAGKxTGrHbWs+pYboTWrLEXiTJLYS5dUar3/TeCf6uh2WyeOUqpgbCwrYLKzbxXxYNKTGcbv7Sva72YodKH7uvl6uCp17/n0+PGV2ymzxzVb9w/oMlrUzz3QdfA9capV7jIDSrItnu0+AFL7O80LDTGTKUdFiIQDuaOKFh11DW8bGzJtRPPeD4RDvE3kiy4cvgZCh/0xQUg="
            },
            self -> {
                self.addTextAboveHead("&e&lCLICK");
            }, EyeGUI::new
    ),

    HYPIXEL(null, getLocation(10, 65, 24, 102.5f, 0.0f), new String[]
            {
                    "eyJ0aW1lc3RhbXAiOjE1ODY5NTMzOTMyMjMsInByb2ZpbGVJZCI6ImRlNTcxYTEwMmNiODQ4ODA4ZmU3YzlmNDQ5NmVjZGFkIiwicHJvZmlsZU5hbWUiOiJNSEZfTWluZXNraW4iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzMwMGI1M2Y2Y2RmZmZhNDg1MTM3YjdmMzRmN2ZkMjQyNjUwNWJiZjQ1NGU2MWNiZGY2ZmFjMGRkODRhNDc2ZTUifX19",
                    "RJBBaFik7HpM7SMIpjbB6BP2LOSc/iBTKRutAEYYV8GtbVQzUoKw1mnLQAi2GKilzP65i/qBKUBpgZM805uMXT6SFVuvnLlO54e6T6Hs71hYR366Fd03NCohD7W+ssMqdoMRyM/e/90h7Mt5mCBzvVnhtxeAZBxkF/7+Y8jCwIY8ozBX696k6gxCpRTiAnh21NCs6ZH+AEcAqLd0CcSGVzUCeXC6x5PERUYKh2uR/fr1ouvh9F7jAkTiTEwtYties/Rzl8EgQxstRDaiJVCBhb4adOKwG14tQhLPPLJD0HvXOqEsblWR8dRzFK6ZzkgsXEgJ66rGhgkKACLmVycleLMtashKMFlXwYAsV/6uq2xdyHiRIya+p9F8pSHqTuLQzuUGg4tT9Z4eDtNszsa+sm4II6T5q/vdm+Qd+zvhKtlXJ9hXcY+8pDqjoEaGbuhEMCrQvTl64ACEApu8Mj7NDldINuBkHKz1YFIlBpNkpDtV6M5Qz48AriYZ0eWNhxa6Gl6una7iSBR8GfTv7D9OrKvHlb2H/O8QA9HNEyfJ3a05eOGX4jUqfEmasoeYCrbu6lNnCKTeIPyuLfogWlhrkWuLmRjz92o4ygWoNksHrXUmDn/zYaufKqxrLBgDweRkJDs/5XeGR6ouTbdPR+owM3gnPBV3JiB6CqDt1QHUxxc="
            },
            self -> {
                self.setPose(NPCPose.CROUCHING);
            }
    ),
    ;

    private final Human human;

    StaticHuman(String name, Location location, String[] textures, @Nullable Consumer<Human> callback) {
        this(name, location, textures, callback, null);
    }

    StaticHuman(String name, Location location, String[] textures) {
        this(name, location, textures, null, null);
    }

    StaticHuman(String name, Location location, String[] textures, @Nullable Consumer<Human> callback, @Nullable Consumer<Player> onClick) {
        this.human = new HumanNPC(location, name, "") {
            @Override
            public void onClick(Player player, HumanNPC npc, ClickType clickType) {
                if (onClick != null) {
                    onClick.accept(player);
                }
            }
        };

        this.human.setSkin(textures[0], textures[1]);
        this.human.setLookAtCloseDist(10);

        if (callback != null) {
            callback.accept(this.human);
        }
    }

    public static Location getLocation(double x, double y, double z) {
        return getLocation(x, y, z, 0.0f, 0.0f);
    }

    public static Location getLocation(double x, double y, double z, float yaw, float pitch) {
        return new Location(Bukkit.getWorlds().get(0), x, y, z, yaw, pitch);
    }

    public Human getHuman() {
        return human;
    }
}
