package me.hapyl.fight.fastaccess;

import com.google.common.collect.Maps;
import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.registry.SimpleRegistry;
import me.hapyl.eterna.module.util.Compute;
import me.hapyl.fight.CF;
import me.hapyl.fight.database.rank.PlayerRank;
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
import me.hapyl.fight.registry.Registries;
import me.hapyl.fight.util.CFUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
                public ItemStack getMaterial(@Nonnull Player player) {
                    return hero.getItem(player);
                }

                @Nonnull
                @Override
                public String getName() {
                    return "Select %s".formatted(hero.getName());
                }

                @Override
                public void appendBuilder(@Nonnull Player player, @Nonnull ItemBuilder builder) {
                    final PlayerProfile profile = CF.getProfile(player);
                    final Hero currentHero = profile.getHero();

                    builder.addLore("Change the hero to: %s".formatted(Color.GOLD + hero.getNameSmallCaps()))
                           .addLore("Current hero: %s".formatted(Color.GOLD + currentHero.getNameSmallCaps()));
                }

            });
        }

        // Map select
        for (EnumLevel enumMap : EnumLevel.getPlayableMaps()) {
            if (!enumMap.isPlayable()) {
                continue;
            }

            register(new FastAccess("select_map_" + enumMap.getKeyAsString(), Category.SELECT_MAP) {
                @Override
                public void onClick(@Nonnull Player player) {
                    enumMap.select(player);
                }

                @Nonnull
                @Override
                public ItemStack getMaterial(@Nonnull Player player) {
                    return ItemStack.of(enumMap.getLevel().getMaterial());
                }

                @Nonnull
                @Override
                public String getName() {
                    return "Select %s".formatted(enumMap.getName());
                }

                @Override
                public boolean shouldDisplayTo(@Nonnull Player player) {
                    return CF.getProfile(player).getRank().isOrHigher(PlayerRank.GAME_MANAGER);
                }

                @Override
                public void appendBuilder(@Nonnull Player player, @Nonnull ItemBuilder builder) {
                    builder.addLore("Change map to: %s".formatted(Color.GOLD + enumMap.getName()))
                           .addLore("Current map: %s".formatted(Color.GOLD + Manager.current().currentEnumLevel().getName()));
                }
            });
        }

        // Team select
        for (GameTeam enumTeam : GameTeam.values()) {
            if (!enumTeam.isAllowJoin()) {
                continue;
            }

            register(new FastAccess("join_team_" + enumTeam.getKeyAsString(), Category.JOIN_TEAM) {
                @Override
                public void onClick(@Nonnull Player player) {
                    enumTeam.addEntry(Entry.of(player));
                }

                @Nonnull
                @Override
                public ItemStack getMaterial(@Nonnull Player player) {
                    return ItemStack.of(enumTeam.getMaterial());
                }

                @Nonnull
                @Override
                public String getName() {
                    return "Join %s Team".formatted(enumTeam.getName());
                }

                @Override
                public void appendBuilder(@Nonnull Player player, @Nonnull ItemBuilder builder) {
                    final GameTeam playerTeam = GameTeam.getEntryTeam(Entry.of(player));

                    builder.addLore("Change team to: " + enumTeam.getNameSmallCapsColorized())
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
                public ItemStack getMaterial(@Nonnull Player player) {
                    return ItemStack.of(enumSetting.getMaterial());
                }

                @Nonnull
                @Override
                public String getName() {
                    return "Toggle %s Setting".formatted(enumSetting.getName());
                }

                @Override
                public void appendBuilder(@Nonnull Player player, @Nonnull ItemBuilder builder) {
                    final boolean isEnabled = enumSetting.isEnabled(player);

                    builder.addLore("Setting: " + Color.GOLD + enumSetting.getName())
                           .addLore()
                           .addTextBlockLore(enumSetting.getDescription(), "&7&o ")
                           .addLore()
                           .addLore((isEnabled ? Color.SUCCESS.bold() + "Currently Enabled!" : Color.ERROR.bold() + "Currently Disabled!"));
                }

            });
        }

        // Select Gadget
        final CosmeticRegistry registry = Registries.cosmetics();

        for (Cosmetic cosmetic : registry.byType(Type.GADGET)) {
            register(new FastAccess("select_gadget_" + cosmetic.getKeyAsString(), Category.SELECT_GADGET) {
                @Override
                public void onClick(@Nonnull Player player) {
                    if (!cosmetic.isUnlocked(player)) {
                        return;
                    }

                    cosmetic.select(player);
                }

                @Nonnull
                @Override
                public ItemStack getMaterial(@Nonnull Player player) {
                    return ItemStack.of(cosmetic.getIcon());
                }

                @Nonnull
                @Override
                public String getName() {
                    return "Select %s Gadget".formatted(cosmetic.getName());
                }

                @Override
                public void appendBuilder(@Nonnull Player player, @Nonnull ItemBuilder builder) {
                    final Cosmetic selectedGadget = CF.getDatabase(player).cosmeticEntry.getSelected(Type.GADGET);

                    builder.addLore("Gadget to select: " + Color.GOLD + cosmetic.getName())
                           .addLore("Selected gadget: " + (selectedGadget != null ? Color.GOLD + selectedGadget.getName() : "&8None!"));
                }

                @Override
                public boolean shouldDisplayTo(@Nonnull Player player) {
                    return cosmetic.isUnlocked(player);
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
