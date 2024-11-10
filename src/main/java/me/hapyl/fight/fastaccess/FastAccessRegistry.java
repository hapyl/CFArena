package me.hapyl.fight.fastaccess;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.registry.SimpleRegistry;
import me.hapyl.eterna.module.util.Compute;
import me.hapyl.fight.CF;
import me.hapyl.fight.game.Manager;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.Cosmetic;
import me.hapyl.fight.game.cosmetic.CosmeticRegistry;
import me.hapyl.fight.game.cosmetic.Type;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.HeroRegistry;
import me.hapyl.fight.game.maps.EnumLevel;
import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.game.setting.EnumSetting;
import me.hapyl.fight.game.team.Entry;
import me.hapyl.fight.game.team.GameTeam;
import me.hapyl.fight.game.type.EnumGameType;
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class FastAccessRegistry extends SimpleRegistry<FastAccess> {

    private final Map<Category, List<FastAccess>> byCategory;

    public FastAccessRegistry() {
        byCategory = Maps.newHashMap();

        // Hero select
        for (Hero hero : HeroRegistry.playable()) {
            register(new FastAccess("select_hero_" + hero.getKeyAsString(), Category.SELECT_HERO) {
                @Override
                public void onClick(@Nonnull Player player) {
                    Manager.current().setSelectedHero(player, hero);
                }

                @Override
                public boolean shouldDisplayTo(@Nonnull Player player) {
                    return hero.isValidHero() && !hero.isLocked(player);
                }

                @Nonnull
                @Override
                public ItemBuilder create(@Nonnull Player player) {
                    final PlayerProfile profile = CF.getProfile(player);
                    final Hero currentHero = profile.getHero();

                    return new ItemBuilder(hero.getItem(player))
                            .setName("Select " + hero.getName())
                            .addLore()
                            .addSmartLore("Changes the current hero of yours.", "&8&o")
                            .addLore()
                            .addLore("Change the hero to: " + Color.GOLD + hero.getNameSmallCaps())
                            .addLore("Current hero: " + Color.GOLD + currentHero.getNameSmallCaps());
                }
            });
        }

        // Map select
        for (EnumLevel enumMap : EnumLevel.getPlayableMaps()) {
            register(new FastAccess("select_map_" + enumMap.getKeyAsString(), Category.SELECT_MAP) {
                @Override
                public void onClick(@Nonnull Player player) {
                    enumMap.select(player);
                }

                @Nonnull
                @Override
                public ItemBuilder create(@Nonnull Player player) {
                    return new ItemBuilder(enumMap.getLevel().getMaterial())
                            .setName("Select " + enumMap.getName())
                            .addLore()
                            .addSmartLore("Changes the current map.", "&8&o")
                            .addLore()
                            .addLore("Change map to: " + Color.GOLD + enumMap.getName())
                            .addLore("Current map: " + Color.GOLD + Manager.current().getCurrentMap().getName());
                }
            });
        }

        // Mode select
        for (EnumGameType enumMode : EnumGameType.values()) {
            register(new FastAccess("select_mode_" + enumMode.getKeyAsString(), Category.SELECT_MODE) {
                @Override
                public void onClick(@Nonnull Player player) {
                    enumMode.select(player);
                }

                @Nonnull
                @Override
                public ItemBuilder create(@Nonnull Player player) {
                    return new ItemBuilder(enumMode.getMode().getMaterial())
                            .setName("Select " + enumMode.getName())
                            .addLore()
                            .addSmartLore("Changes the current mode.", "&8&o")
                            .addLore()
                            .addLore("Change mode to: " + Color.GOLD + enumMode.getName())
                            .addLore("Current mode: " + Color.GOLD + Manager.current().getCurrentMode().getName());
                }
            });
        }

        // Team select
        for (GameTeam enumTeam : GameTeam.values()) {
            register(new FastAccess("join_team_" + enumTeam.getKeyAsString(), Category.JOIN_TEAM) {
                @Override
                public void onClick(@Nonnull Player player) {
                    enumTeam.addEntry(Entry.of(player));
                }

                @Nonnull
                @Override
                public ItemBuilder create(@Nonnull Player player) {
                    final GameTeam playerTeam = GameTeam.getEntryTeam(Entry.of(player));

                    return new ItemBuilder(enumTeam.getMaterial())
                            .setName("Join " + enumTeam.getName() + " Team")
                            .addLore()
                            .addSmartLore("Changes the current team of yours.", "&8&o")
                            .addLore()
                            .addLore("Change team to: " + enumTeam.getNameSmallCapsColorized())
                            .addLore("Your current team: " + (playerTeam != null ? playerTeam.getNameSmallCapsColorized() : "None"));
                }
            });
        }

        // Toggle Setting
        for (EnumSetting enumSetting : EnumSetting.values()) {
            register(new FastAccess("toggle_setting_" + enumSetting.getKeyAsString(), Category.TOGGLE_SETTING) {
                @Override
                public void onClick(@Nonnull Player player) {
                    enumSetting.setEnabled(player, !enumSetting.isEnabled(player));
                }

                @Nonnull
                @Override
                public ItemBuilder create(@Nonnull Player player) {
                    final boolean enabled = enumSetting.isEnabled(player);

                    return new ItemBuilder(enumSetting.getMaterial())
                            .setName("Toggle " + enumSetting.getName())
                            .addLore()
                            .addSmartLore("Toggles the setting value.", "&8&o")
                            .addLore()
                            .addLore("Setting: " + Color.GOLD + enumSetting.getName())
                            .addSmartLore(enumSetting.getDescription(), " &7&o")
                            .addLore()
                            .addLore((enabled ? "&aCurrently Enabled!" : "&cCurrently Disabled!"));
                }
            });
        }

        // Select Gadget
        final CosmeticRegistry registry = Registries.getCosmetics();

        for (Cosmetic cosmetic : registry.byType(Type.GADGET)) {
            register(new FastAccess("select_gadget_" + cosmetic.getKeyAsString(), Category.SELECT_GADGET) {
                @Override
                public void onClick(@Nonnull Player player) {
                    if (!cosmetic.isUnlocked(player)) {
                        return;
                    }

                    cosmetic.select(player);
                }

                @Override
                public boolean shouldDisplayTo(@Nonnull Player player) {
                    return cosmetic.isUnlocked(player);
                }

                @Nonnull
                @Override
                public ItemBuilder create(@Nonnull Player player) {
                    final Cosmetic selectedGadget = CF.getDatabase(player).cosmeticEntry.getSelected(Type.GADGET);

                    return new ItemBuilder(cosmetic.getIcon())
                            .setName("Select " + cosmetic.getName() + " Gadget")
                            .addLore()
                            .addSmartLore("Select a gadget.", "&8&o")
                            .addLore()
                            .addLore("Gadget to select: " + Color.GOLD + cosmetic.getName())
                            .addLore("Selected gadget: " +
                                    (selectedGadget != null ? Color.GOLD + selectedGadget.getName() : "&8None!"));
                }
            });
        }
    }

    @Override
    public FastAccess register(@Nonnull FastAccess fastAccess) {
        byCategory.compute(fastAccess.getCategory(), Compute.listAdd(fastAccess));
        return super.register(fastAccess);
    }

    @Override
    public boolean unregister(@Nonnull FastAccess fastAccess) {
        byCategory.compute(fastAccess.getCategory(), Compute.listRemove(fastAccess));
        return super.unregister(fastAccess);
    }

    @Nonnull
    public List<FastAccess> values(Player player) {
        final List<FastAccess> list = values();
        list.removeIf(filter -> !filter.shouldDisplayTo(player));

        return list;
    }

    @Nonnull
    public List<FastAccess> getByCategory(@Nonnull Category category) {
        return CFUtils.copyList(byCategory.get(category));
    }
}
