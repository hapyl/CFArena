package me.hapyl.fight.gui;

import me.hapyl.fight.database.entry.RandomHeroEntry;
import me.hapyl.fight.game.heroes.Archetype;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.gui.styled.StyledItem;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.Action;
import me.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;

import javax.annotation.Nullable;
import java.util.Set;

public class HeroPreferencesGUI extends StyledGUI {

    private final int[] slots = { 19, 20, 21, 23, 24, 25 };
    private final RandomHeroEntry entry;
    private final Set<Archetype> include;

    public HeroPreferencesGUI(PlayerProfile profile) {
        super(profile.getPlayer(), "Random Hero Preferences", Size.FIVE);

        this.entry = profile.getDatabase().randomHeroEntry;
        this.include = entry.getInclude();

        openInventory();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Hero Selection", HeroSelectGUI::new);
    }

    @Override
    public void onUpdate() {
        setHeader(StyledItem.RANDOM_HERO_PREFERENCES.asIcon());

        final Archetype[] archetypes = Archetype.values();
        final boolean isEmpty = include.isEmpty();

        for (int i = 0; i < slots.length; i++) {
            final int slot = slots[i];
            final Archetype archetype = archetypes[i];
            final boolean isEnabled = include.contains(archetype);

            final ItemBuilder builder = new ItemBuilder(archetype.getMaterial())
                    .setName(archetype.toString())
                    .addLore("&8Archetype")
                    .addLore()
                    .addSmartLore(archetype.getDescription(), "&7&o")
                    .predicate(isEnabled, ItemBuilder::glow)
                    .addLore();

            final String archetypeName = archetype.getName();

            if (isEmpty) {
                builder.addTextBlockLore("""
                        &f&lNo filter set!
                        &7Any hero can be randomly selected!
                                                
                        &eClick to enable!
                        """);
            }
            else {
                if (isEnabled) {
                    builder.addTextBlockLore("""
                            &a&lEnabled!
                            &7%s heroes can be randomly selected!
                                                        
                            &eClick to disable!
                            """.formatted(archetypeName));
                }
                else {
                    builder.addTextBlockLore("""
                            &c&lDisabled!
                            &7%s heroes &ncannot&7 be randomly selected!
                                                        
                            &eClick to enable!
                            """.formatted(archetypeName));
                }
            }

            setItem(slot, builder.toItemStack());
            setItem(
                    slot + 9,
                    new ItemBuilder(isEnabled ? Material.LIME_DYE : Material.GRAY_DYE).setName(archetype.toString())
                            .addLore(isEnabled ? "&8Enabled" : "&8Disabled")
                            .addLore()
                            .addLore("&eClick to " + (isEnabled ? "disable" : "enable") + "!")
                            .toItemStack()
            );

            final Action action = player -> {
                if (isEnabled) {
                    include.remove(archetype);
                }
                else {
                    include.add(archetype);
                }

                PlayerLib.plingNote(player, 2.0f);
                update();
            };

            setClick(slot, action);
            setClick(slot + 9, action);
        }

        // Confirm
        setItem(51,
                new ItemBuilder(Material.EMERALD_BLOCK)
                        .setName("&aSave!")
                        .addLore("&8Everything looks fine!")
                        .addLore()
                        .addSmartLore("This will automatically enable random hero selection if it isn't already!", "&7&o")
                        .addLore()
                        .addLore("&eClick to save!")
                        .toItemStack(), player -> {
                    entry.setEnabled(true);
                    entry.setInclude(include);

                    new HeroSelectGUI(player);
                }
        );
    }

}
