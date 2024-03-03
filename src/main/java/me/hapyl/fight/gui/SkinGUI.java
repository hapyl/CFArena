package me.hapyl.fight.gui;

import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.entry.CurrencyEntry;
import me.hapyl.fight.database.entry.SkinEntry;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.cosmetic.skin.Skin;
import me.hapyl.fight.game.cosmetic.skin.Skins;
import me.hapyl.fight.game.entity.SoundEffect;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.Heroes;
import me.hapyl.fight.game.heroes.PlayerSkinPreview;
import me.hapyl.fight.game.heroes.equipment.Slot;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.ux.Notifier;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class SkinGUI extends StyledGUI {

    private final Heroes enumHero;
    private final int returnPage;
    private final PlayerDatabase database;
    private final SkinEntry skinEntry;

    public SkinGUI(Player player, Heroes enumHero, int returnPage) {
        super(player, enumHero.getName() + "'s Skins", Size.FIVE);

        this.enumHero = enumHero;
        this.returnPage = returnPage;
        this.database = PlayerDatabase.getDatabase(player);
        this.skinEntry = this.database.skinEntry;

        openInventory();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Hero Preview", player -> new HeroPreviewGUI(player, enumHero, returnPage));
    }

    @Override
    public void onUpdate() {
        setHeader(new ItemBuilder(Material.LEATHER_CHESTPLATE)
                .setName("Skins")
                .addLore()
                .addLore("Change the appearance of this hero!")
                .asIcon()
        );

        final SmartComponent component = new SmartComponent();

        makeItem(component, null);

        // Add skins
        final List<Skins> skins = Skins.byHero(enumHero);

        for (Skins enumSkin : skins) {
            makeItem(component, enumSkin);
        }

        component.apply(this, SlotPattern.INNER_LEFT_TO_RIGHT, 2);
    }

    private void makeItem(SmartComponent component, @Nullable Skins enumSkin) {
        final CurrencyEntry currencyEntry = database.currencyEntry;
        final Skins selectedSkin = skinEntry.getSelected(enumHero);
        final Hero hero = enumHero.getHero();

        ItemBuilder builder;
        final GUIClick click = new GUIClick();

        if (enumSkin == null) {
            builder = ItemBuilder.playerHeadUrl(hero.getTextureUrl());
            builder.setName("%s's Default Skin".formatted(hero.getName()));
            builder.addLore(Rarity.LEGENDARY.toString("sᴋɪɴ"));
            builder.addLore();
            builder.addSmartLore("This is the default attire!", "&7&o");
            builder.addLore();
        }
        else {
            final Skin skin = enumSkin.getSkin();

            builder = new ItemBuilder(skin.getEquipment().getItem(Slot.HELMET));
            builder.setName(skin.getName());
            builder.addLore(skin.getRarity().toString("sᴋɪɴ"));
            builder.addLore();
            builder.addSmartLore(skin.getDescription(), "&7&o");
            builder.addLore();
        }

        if (selectedSkin == enumSkin) {
            builder.addLore(Color.SUCCESS + "Currently selected!");

            click.setAction(ClickType.LEFT, player -> {
                Notifier.error(player, "Already selected!");
                Notifier.sound(player, SoundEffect.FAILURE);

                update();
            });
        }
        else {
            // Can select
            if (enumSkin == null || skinEntry.isOwned(enumSkin)) {
                builder.addLore(Color.BUTTON + "Click to select!");

                click.setAction(ClickType.LEFT, player -> {
                    Notifier.success(player, "Selected %s skin!".formatted(enumSkin == null ? "default" : enumSkin.getSkin().getName()));
                    Notifier.sound(player, SoundEffect.SUCCESS);

                    skinEntry.setSelected(enumHero, enumSkin);
                    update();
                });
            }
            // Cannot select, display buy or just say unavailable
            else {
                final Skin skin = enumSkin.getSkin();
                final boolean purchasableWithRubies = skin.isPurchasableWithRubies();

                if (!purchasableWithRubies) {
                    builder.addLore(Color.ERROR + "This skin is not purchasable!");

                    click.setAction(ClickType.LEFT, player -> {
                        Notifier.error(player, "This skin is not purchasable!");
                        Notifier.sound(player, SoundEffect.FAILURE);
                    });
                }
                else {
                    final long rubyPrice = skin.getRubyPrice();

                    builder.addLore("Cost:");
                    builder.addLore(" " + Currency.RUBIES.formatProduct(rubyPrice));
                    builder.addLore();

                    if (!currencyEntry.has(Currency.RUBIES, rubyPrice)) {
                        builder.addLore(Color.ERROR + "Cannot afford!");

                        click.setAction(ClickType.LEFT, player -> {
                            Notifier.error(player, "You cannot afford this!");
                            Notifier.sound(player, SoundEffect.FAILURE);

                            update();
                        });
                    }
                    else {
                        builder.addLore(Color.BUTTON + "Click to purchase!");

                        click.setAction(ClickType.LEFT, player -> {
                            if (validateHasCoinsOrCloseGUI(rubyPrice)) {
                                return;
                            }

                            new ConfirmGUI(player, "Confirm Purchase") {
                                @Nonnull
                                @Override
                                public ItemStack quoteItem() {
                                    return new ItemBuilder(skin.getEquipment().getItem(Slot.HELMET))
                                            .setName("Confirm Purchase")
                                            .addLore("&8" + skin.getName() + " Skin")
                                            .addLore()
                                            .addLore("Cost")
                                            .addLore(" " + Currency.RUBIES.formatProduct(rubyPrice))
                                            .asIcon();
                                }

                                @Override
                                public void onConfirm(@Nonnull Player player) {
                                    if (validateHasCoinsOrCloseGUI(rubyPrice)) {
                                        return;
                                    }

                                    currencyEntry.subtract(Currency.RUBIES, rubyPrice);
                                    skinEntry.setOwned(enumSkin, true);

                                    Notifier.success(player, "Purchased %s skin!".formatted(skin.getName()));
                                    Notifier.sound(player, SoundEffect.SUCCESS);

                                    SkinGUI.this.update();
                                }

                                @Override
                                public void onCancel(@Nonnull Player player) {
                                    SkinGUI.this.update();
                                }
                            }.openInventory();
                        });
                    }
                }
            }
        }

        click.setAction(ClickType.RIGHT, player -> {
            closeInventory();

            new PlayerSkinPreview(player, hero, enumSkin != null ? enumSkin.getSkin() : null) {
                @Override
                public void onTaskStop() {
                    openInventory();
                }
            };

        });

        component.add(builder.asIcon(), click);
    }

    private boolean validateHasCoinsOrCloseGUI(long price) {
        if (!database.currencyEntry.has(Currency.RUBIES, price)) {
            Notifier.error(player, "Somehow you don't have enough rubies!");
            Notifier.sound(player, SoundEffect.ERROR);

            closeInventory();
            return true;
        }

        return false;
    }

}
