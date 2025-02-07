package me.hapyl.fight.game.help;

import com.google.common.collect.Lists;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.gui.SlotPattern;
import me.hapyl.eterna.module.inventory.gui.SmartComponent;
import me.hapyl.fight.game.NonNullItemCreator;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.util.BrowserLink;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class HelpDevelopers extends HelpGUI {

    private static final List<Developer> DEVELOPERS;

    static {
        DEVELOPERS = Lists.newArrayList();

        addDeveloper(Developer.of(
                "da8adca36d7756cca2975d1a1f6b5ab56cda82d88f9de0d3de595332c8035cb0",
                "&4hapyl",
                "Did Mojang break something again?",
                DeveloperRole.PROGRAMMER,
                DeveloperRole.DESIGN_HERO,
                DeveloperRole.DESIGN_MAP,
                DeveloperRole.BUILDER
        ).linkTo("GitHub", "https://github.com/hapyl"));

        addDeveloper(Developer.of(
                "cebb138d16c9f984a77d85c78aa32ac3f57369ba31972264bcf3f47b9cba4c",
                "&9sdimas74",
                "Probably drunk right now.",
                DeveloperRole.DESIGN_HERO,
                DeveloperRole.DESIGN_MAP,
                DeveloperRole.BUILDER,
                DeveloperRole.LORE_WRITER
        ));

        addDeveloper(Developer.of(
                "e96e10d1b43f78f5c8d2aac31ad5985bdce3b2b8b1f54c9446166d8e3f783169",
                "&6DiDenPro",
                "Not actually a pro.",
                DeveloperRole.DESIGN_MAP,
                DeveloperRole.DESIGN_HERO
        ));

        addDeveloper(Developer.of(
                "f1c7656a03abfd8ca1c18635155d8d5df402547121366d018d4b4b12d9e9578b",
                "&7Dirty&3El",
                "Haven't been seen in 69 years.",
                DeveloperRole.DESIGN_MAP,
                DeveloperRole.BUILDER
        ));

        addDeveloper(Developer.of(
                "67576a010e53097b618121c07024799369f688c7e3476dfbea6ea250b2ddf221",
                "RobCos_",
                "idk add description yourself", // fixme: @RobCos_
                DeveloperRole.PROGRAMMER,
                DeveloperRole.DESIGN_HERO
        ));

        // Testers
        addDeveloper(Developer.ofTester("4d39fc14370bafed626ffe9a1f02ed4a7f9d149f94f3e126bf6a2e07976449c6", "Vizmar"));
        addDeveloper(Developer.ofTester("f1a07bb2d43f6807bdf63dd6c519977c6ba200e857835e70d2f51e196a982041", "AlmostId"));
        addDeveloper(Developer.ofTester("a579126f0a3b1f77c0bee58e95d2b489ef35acd756ac241fdcd5ff52afb82935", "MisterioMAN"));
        addDeveloper(Developer.ofTester("e177bb888ea7e98fd0c2ee1b58dc4248b838c35ca7b672d28b324e7ef0921292", "thundda"));
        addDeveloper(Developer.ofTester("6ae2963cc5baab5edcf41a61aa4abbf545bf7eaa2c114053bf79cc4e0cb46168", "alternativeuser"));
    }

    public HelpDevelopers(Player player) {
        super(player, "Developers");
    }

    @Nonnull
    @Override
    public Material getBorder() {
        return Material.GREEN_STAINED_GLASS_PANE;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        setItem(4, ItemBuilder.of(Material.BOOK, "Developers", "There we are!").asIcon());

        final SmartComponent developers = newSmartComponent();
        final SmartComponent testers = newSmartComponent();

        for (Developer developer : DEVELOPERS) {
            final ItemStack item = developer.createItem();
            final SmartComponent component = !developer.roles.contains(DeveloperRole.TESTER) ? developers : testers;

            component.add(item, player -> {
                final BrowserLink link = developer.optionalLink;

                if (link != null) {
                    link.openUrl(player);
                }
            });
        }

        developers.apply(this, SlotPattern.FANCY, 1);
        testers.apply(this, SlotPattern.FANCY, 2);

        // You
        setItem(31, new ItemBuilder(Material.PLAYER_HEAD)
                .setSkullOwner(player.getName())
                .setName("&aYou!")
                .addLore()
                .addLore("&f• &6A Very Special Person")
                .asIcon()
        );
    }

    private static void addDeveloper(Developer developer) {
        DEVELOPERS.add(developer);
    }

    private enum DeveloperRole {
        PROGRAMMER("Programmer"),
        DESIGN_HERO("Hero Designer"),
        DESIGN_MAP("Map Designer"),
        BUILDER("Builder"),
        LORE_WRITER("Lore Writer"),
        TESTER("Tester");

        private final String name;

        DeveloperRole(String name) {
            this.name = name;
        }

        @Nonnull
        public String getName() {
            return name;
        }
    }

    private static class Developer implements NonNullItemCreator {
        private final String headTexture;
        private final String name;
        private final String description;
        private final List<DeveloperRole> roles;

        private BrowserLink optionalLink;
        private ItemStack itemStack;

        private Developer(String headTexture, String name, String description) {
            this.headTexture = headTexture;
            this.name = name;
            this.description = description;
            this.roles = Lists.newArrayList();
        }

        public Developer linkTo(@Nonnull String name, @Nonnull String link) {
            this.optionalLink = new BrowserLink(name, link);
            return this;
        }

        @Nonnull
        @Override
        public ItemStack getItem() {
            if (itemStack == null) {
                return itemStack = createItem();
            }

            return itemStack;
        }

        @Override
        @Nonnull
        public ItemStack createItem() {
            final ItemBuilder builder = ItemBuilder.playerHeadUrl(headTexture);
            builder.setName(name);

            if (description != null) {
                builder.addLore("&8&o" + description);
            }

            builder.addLore();

            for (DeveloperRole role : DeveloperRole.values()) {
                if (roles.contains(role)) {
                    builder.addLore("&f• &6" + role.getName());
                }
            }

            if (optionalLink != null) {
                builder.addLore();
                builder.addLore(Color.BUTTON + "Click to open %s's %s!".formatted(name, Color.BUTTON + optionalLink.name()));
            }

            return builder.asIcon();
        }

        public static Developer of(@Nonnull String headTexture, @Nonnull String name, @Nonnull String description, @Nonnull DeveloperRole... roles) {
            final Developer developer = new Developer(headTexture, name, description);
            developer.roles.addAll(Arrays.asList(roles));

            return developer;
        }

        public static Developer ofTester(@Nonnull String headTexture, @Nonnull String name) {
            final Developer developer = new Developer(headTexture, name, null);
            developer.roles.add(DeveloperRole.TESTER);

            return developer;
        }
    }

}
