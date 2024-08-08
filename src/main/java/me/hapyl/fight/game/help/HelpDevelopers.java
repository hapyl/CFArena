package me.hapyl.fight.game.help;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.gui.SlotPattern;
import me.hapyl.eterna.module.inventory.gui.SmartComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class HelpDevelopers extends HelpGUI {

    private static final SlotPattern DEVELOPERS = new SlotPattern(new byte[][] { { 0, 1, 0, 1, 0, 1, 0, 1, 0 } });
    private static final SlotPattern TESTERS = new SlotPattern(new byte[][] { { 0, 0, 1, 1, 1, 1, 1, 0, 0 } });

    public HelpDevelopers(Player player) {
        super(player, "Developers");
    }

    @Nonnull
    @Override
    public Material getBorder() {
        return null;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        
        setItem(4, ItemBuilder.of(Material.BOOK, "Developers", "There we are!").asIcon());

        final SmartComponent developers = newSmartComponent();
        final SmartComponent testers = newSmartComponent();

        developers.add(ItemBuilder.playerHead(
                        "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWU1YjQ5OTRmZmMyZDMyMGQwYTQ2MGM1NzFhZTEzYjU2YzY5NmMzMTk0NjE1ZDMxYTRkN2FkMDUxMDgzOWM0NSJ9fX0=")
                .setName("&chapyl")
                .addLore("&8I little bit dumb.")
                .addLore("")
                .addLore("&fProgrammer")
                .addLore("&fHero Designer")
                .addLore("&fMap Designer")
                .addLore("&fMap Builder")
                .addLore("&fTester")
                .asIcon());


        developers.add(ItemBuilder
                .playerHead(
                        "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2ViYjEzOGQxNmM5Zjk4NGE3N2Q4NWM3OGFhMzJhYzNmNTczNjliYTMxOTcyMjY0YmNmM2Y0N2I5Y2JhNGMifX19"
                )
                .setName("&9sdimas74")
                .addLore("&8Might be drunk right now.")
                .addLore("")
                .addLore("&fHero Designer")
                .addLore("&fMap Designer")
                .addLore("&fMap Builder")
                .addLore("&fTester")
                .asIcon());

        developers.add(ItemBuilder
                .playerHead(
                        "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTk2ZTEwZDFiNDNmNzhmNWM4ZDJhYWMzMWFkNTk4NWJkY2UzYjJiOGIxZjU0Yzk0NDYxNjZkOGUzZjc4MzE2OSJ9fX0="
                )
                .setName("&6DiDenPro")
                .addLore("&8Not actually a pro.")
                .addLore()
                .addLore("&fHero Designer")
                .addLore("&fTester")
                .asIcon());

        developers.add(ItemBuilder
                .playerHead(
                        "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjFjNzY1NmEwM2FiZmQ4Y2ExYzE4NjM1MTU1ZDhkNWRmNDAyNTQ3MTIxMzY2ZDAxOGQ0YjRiMTJkOWU5NTc4YiJ9fX0="
                )
                .setName("&7Dirty&3El")
                .addLore("&8Haven't been seen in 69 years.")
                .addLore()
                .addLore("&fMap Designer")
                .addLore("&fMap Builder")
                .addLore("&fTester")
                .asIcon());

        testers.add(ItemBuilder
                .playerHead(
                        "eyJ0aW1lc3RhbXAiOjE1NTExOTk5NTY2NzEsInByb2ZpbGVJZCI6ImEwOGQ2MTI5MDRlODRkNmNhMWZlOTNhOGI4ODA2NzA5IiwicHJvZmlsZU5hbWUiOiJWaXptYXIiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzRkMzlmYzE0MzcwYmFmZWQ2MjZmZmU5YTFmMDJlZDRhN2Y5ZDE0OWY5NGYzZTEyNmJmNmEyZTA3OTc2NDQ5YzYifX19"
                )
                .setName("Vizmar")
                .addLore()
                .addLore("&fTester")
                .asIcon()
        );

        testers.add(ItemBuilder
                .playerHead(
                        "eyJ0aW1lc3RhbXAiOjE1NTExOTk5NzA3NTgsInByb2ZpbGVJZCI6IjE4NTM1YmYzODk3MTQ5OGFiYzBhN2Q3Yjc5Nzg4MTIxIiwicHJvZmlsZU5hbWUiOiJBbG1vc3RJZCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjFhMDdiYjJkNDNmNjgwN2JkZjYzZGQ2YzUxOTk3N2M2YmEyMDBlODU3ODM1ZTcwZDJmNTFlMTk2YTk4MjA0MSJ9fX0="
                )
                .setName("AlmostId")
                .addLore()
                .addLore("&fTester")
                .asIcon()
        );

        testers.add(ItemBuilder
                .playerHead(
                        "eyJ0aW1lc3RhbXAiOjE1NTEyMDAwMTYwNzUsInByb2ZpbGVJZCI6IjY3ZWY3MTVlMDE5YjQ4ZjY5NzNkMWQzOWNlOTVjZDFmIiwicHJvZmlsZU5hbWUiOiJNaXN0ZXJpb01BTiIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTU3OTEyNmYwYTNiMWY3N2MwYmVlNThlOTVkMmI0ODllZjM1YWNkNzU2YWMyNDFmZGNkNWZmNTJhZmI4MjkzNSIsIm1ldGFkYXRhIjp7Im1vZGVsIjoic2xpbSJ9fX19"
                )
                .setName("MisterioMAN")
                .addLore()
                .addLore("&fTester")
                .asIcon()
        );

        testers.add(ItemBuilder
                .playerHead(
                        "eyJ0aW1lc3RhbXAiOjE1NTEyMDAwMjM1ODEsInByb2ZpbGVJZCI6ImQyODFkYmUxY2RhMDRlOWM5ZGFhYTIwZGY0NDNiMjNkIiwicHJvZmlsZU5hbWUiOiJ0aHVuZGRhIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lMTc3YmI4ODhlYTdlOThmZDBjMmVlMWI1OGRjNDI0OGI4MzhjMzVjYTdiNjcyZDI4YjMyNGU3ZWYwOTIxMjkyIiwibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn19fX0="
                )
                .setName("thundda")
                .addLore()
                .addLore("&fTester")
                .asIcon()
        );

        testers.add(ItemBuilder
                .playerHead(
                        "ewogICJ0aW1lc3RhbXAiIDogMTYxMjQzNTY0ODEzNSwKICAicHJvZmlsZUlkIiA6ICI3ZDNjMmU1NTdkM2M0MDFmYTY0YjE2ODI5MmEzNWQyMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJhbHRlcm5hdGl2ZXVzZXIiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmFlMjk2M2NjNWJhYWI1ZWRjZjQxYTYxYWE0YWJiZjU0NWJmN2VhYTJjMTE0MDUzYmY3OWNjNGUwY2I0NjE2OCIKICAgIH0KICB9Cn0="
                )
                .setName("alternativeuser")
                .addLore()
                .addLore("&fTester")
                .asIcon()
        );

        developers.apply(this, DEVELOPERS, 1);
        testers.apply(this, TESTERS, 2);
    }

}
