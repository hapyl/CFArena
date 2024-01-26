package me.hapyl.fight.gui;

import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.achievement.Achievements;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.heroes.CachedHeroItem;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.lobby.LobbyItems;
import me.hapyl.fight.game.setting.Settings;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledItem;
import me.hapyl.fight.gui.styled.StyledPageGUI;
import me.hapyl.fight.util.Filter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class HeroSelectGUI extends StyledPageGUI<Heroes> {

    private final Filter<Heroes, Archetype> archetypeSort;

    public HeroSelectGUI(Player player) {
        this(player, 1);
    }

    public HeroSelectGUI(Player player, int startPage) {
        super(player, "Hero Selection", Size.FOUR);

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

        // Shortcut for random hero setting (DiDen special)
        final Settings settingRandomHero = Settings.RANDOM_HERO;
        final boolean enabled = settingRandomHero.isEnabled(player);

        setItem(41, settingRandomHero.create(player).setType(enabled ? Material.LIME_DYE : Material.GRAY_DYE).asIcon(), player -> {
            settingRandomHero.setEnabled(player, !enabled);
            update();
        });
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
    public ItemStack asItem(Player player, Heroes enumHero, int index, int page) {
        final Hero hero = enumHero.getHero();

        if (enumHero.isLocked(player)) {
            return StyledItem.LOCKED_HERO.toBuilder()
                    .addLore("&7Reach level &b%s &7to unlock!", hero.getMinimumLevel())
                    .asIcon();
        }
        else {
            return hero.getCachedHeroItem().getItem(CachedHeroItem.Type.SELECT);
        }
    }

    private void updateContents() {
        setContents(archetypeSort.filter(Heroes.playableRespectLockedFavourites(player)));
    }

}
