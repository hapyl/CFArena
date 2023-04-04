package me.hapyl.fight.npc;

import me.hapyl.spigotutils.module.reflect.npc.Human;
import org.bukkit.Bukkit;
import org.bukkit.Location;

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
    ;

    private final Human human;

    StaticHuman(String name, Location location, String[] textures, Consumer<Human> callback) {
        this.human = Human.create(location, name);
        this.human.setSkin(textures[0], textures[1]);
        this.human.setLookAtCloseDist(10);

        callback.accept(this.human);
    }

    public Human getHuman() {
        return human;
    }

    public static Location getLocation(double x, double y, double z) {
        return getLocation(x, y, z, 0.0f, 0.0f);
    }

    public static Location getLocation(double x, double y, double z, float yaw, float pitch) {
        return new Location(Bukkit.getWorlds().get(0), x, y, z, yaw, pitch);
    }
}
