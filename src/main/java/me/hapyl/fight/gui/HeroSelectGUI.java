package me.hapyl.fight.gui;

import me.hapyl.fight.database.entry.RandomHeroEntry;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.HeroPlayerItemMaker;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.lobby.LobbyItems;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledItem;
import me.hapyl.fight.gui.styled.StyledPageGUI;
import me.hapyl.fight.util.Filter;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Set;

public class HeroSelectGUI extends StyledPageGUI<Heroes> {

    private final Filter<Heroes, Archetype> archetypeSort;
    private final PlayerProfile profile;

    public HeroSelectGUI(Player player) {
        this(player, 1);
    }

    public HeroSelectGUI(Player player, int startPage) {
        super(player, "Hero Selection", Size.FOUR);

        this.profile = Manager.current().getOrCreateProfile(player);

        this.archetypeSort = new Filter<>(Archetype.class, Archetype.NOT_SET) {
            @Override
            public boolean isKeep(@Nonnull Heroes heroes, @Nonnull Archetype archetype) {
                return heroes.getHero().getArchetype() == archetype;
            }
        };

        setFit(Fit.SLIM);

        updateContents();
        openInventory(startPage);
    }

    @Override
    public void onUpdate() {
        setHeader(LobbyItems.HERO_SELECT.getItem().getItemStack());

        // Add sort button
        archetypeSort.setFilterItem(this, 39, (onClick, sort) -> {
            updateContents();
            update();
        });

        // Random hero preferences

        final RandomHeroEntry entry = profile.getDatabase().randomHeroEntry;
        final boolean randomHeroEnabled = entry.isEnabled();

        final ItemBuilder builder = StyledItem.RANDOM_HERO_PREFERENCES.toBuilder();
        final Set<Archetype> include = entry.getInclude();

        builder.addLore();

        if (randomHeroEnabled) {
            builder.addLore("&a&lEnabled!");
            builder.addSmartLore("A random hero will be selected when the game starts!", "&7&o");

            builder.addLore();

            // No filter, any hero can be selected
            if (include.isEmpty()) {
                builder.addLore("&f&lNo filter!");
                builder.addSmartLore("Any hero you own can be randomly selected!", "&7&o");
            }
            else {
                builder.addLore("&f&lFiltered!");
                builder.addSmartLore("Only heroes with this archetype(s) can be randomly selected!", "&7&o");
                builder.addLore();

                for (Archetype archetype : Archetype.values()) { // preserve order
                    if (!include.contains(archetype)) {
                        continue;
                    }

                    builder.addLore("• " + archetype.toString());
                }
            }
        }
        else {
            builder.addLore("&c&lDisabled!");
            builder.addSmartLore("You will be playing as %s when the game starts!".formatted(profile.getHero().getName()), "&7&o");
        }

        builder.addLore();

        setItem(
                41,
                builder.addTextBlockLore("""
                        &eClick to open preferences
                        &6Right Click to toggle
                        """).toItemStack()
        );

        setClick(41, click -> {
            new HeroPreferencesGUI(profile);
        }, ClickType.LEFT, ClickType.SHIFT_LEFT);

        setClick(41, click -> {
            entry.setEnabled(!randomHeroEnabled);

            // Restore hero
            if (randomHeroEnabled) {
                final Heroes lastSelectedHero = entry.getLastSelectedHero();
                entry.setLastSelectedHero(null);

                if (lastSelectedHero != null) {
                    profile.setSelectedHero(lastSelectedHero);
                }
            }

            PlayerLib.plingNote(player, 2.0f);
            update();
        }, ClickType.RIGHT, ClickType.SHIFT_RIGHT);
    }

    @Override
    public void onClick(@Nonnull Player player, @Nonnull Heroes enumHero, int index, int page, @Nonnull ClickType clickType) {
        if (enumHero.isLocked(player)) {
            return;
        }

        if (clickType == ClickType.LEFT || clickType == ClickType.SHIFT_LEFT) {
            Manager.current().setSelectedHero(player, enumHero);
        }
        else if (clickType == ClickType.RIGHT || clickType == ClickType.SHIFT_RIGHT) {
            new HeroPreviewGUI(player, enumHero, page);
        }
        else {
            Achievements.RULES_ARE_NOT_FOR_ME.complete(player);
        }
    }

    @Nonnull
    @Override
    public ItemStack asItem(@Nonnull Player player, Heroes enumHero, int index, int page) {
        final Hero hero = enumHero.getHero();

        if (enumHero.isLocked(player)) {
            return StyledItem.LOCKED_HERO.toBuilder()
                    .addLore("&7Reach level &b%s &7to unlock!", hero.getMinimumLevel())
                    .asIcon();
        }
        else {
            return hero.getItemMaker().makeItem(HeroPlayerItemMaker.Type.SELECT, player);
        }
    }

    private void updateContents() {
        setContents(archetypeSort.filter(Heroes.playableRespectLockedFavourites(player)));
    }

}
