package me.hapyl.fight.gui;

import me.hapyl.fight.database.entry.RandomHeroEntry;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.heroes.*;
import me.hapyl.fight.game.lobby.LobbyItems;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledItem;
import me.hapyl.fight.gui.styled.StyledPageGUI;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.util.Filter;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.player.PlayerLib;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Set;

public class HeroSelectGUI extends StyledPageGUI<Hero> {

    private final Filter<Hero, Archetype> archetypeSort;
    private final PlayerProfile profile;

    public HeroSelectGUI(Player player) {
        this(player, 1);
    }

    public HeroSelectGUI(Player player, int startPage) {
        super(player, "Hero Selection", Size.FOUR);

        this.profile = Manager.current().getOrCreateProfile(player);

        this.archetypeSort = new Filter<>(Archetype.class, Archetype.NOT_SET) {
            @Override
            public boolean isKeep(@Nonnull Hero heroes, @Nonnull Archetype archetype) {
                return heroes.getArchetypes().contains(archetype);
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
                final Hero lastSelectedHero = entry.getLastSelectedHero();
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
    public void onClick(@Nonnull Player player, @Nonnull Hero enumHero, int index, int page, @Nonnull ClickType clickType) {
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
            Registries.getAchievements().RULES_ARE_NOT_FOR_ME.complete(player);
        }
    }

    @Nonnull
    @Override
    public ItemStack asItem(@Nonnull Player player, Hero hero, int index, int page) {
        if (hero.isLocked(player)) {
            return StyledItem.LOCKED_HERO.toBuilder()
                    .addLore("&7Reach level &b%s &7to unlock!".formatted(hero.getMinimumLevel()))
                    .asIcon();
        }
        else {
            return hero.getItemMaker().makeItem(HeroPlayerItemMaker.Type.SELECT, player);
        }
    }

    private void updateContents() {
        setContents(archetypeSort.filter(HeroRegistry.playableRespectLockedFavourites(player)));
    }

}
