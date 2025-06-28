package me.hapyl.fight.gui;

import me.hapyl.eterna.module.inventory.ItemBuilder;
import me.hapyl.eterna.module.inventory.gui.ActionList;
import me.hapyl.eterna.module.inventory.gui.SlotPattern;
import me.hapyl.eterna.module.inventory.gui.SmartComponent;
import me.hapyl.fight.CF;
import me.hapyl.fight.Message;
import me.hapyl.fight.database.PlayerDatabase;
import me.hapyl.fight.database.entry.Currency;
import me.hapyl.fight.database.entry.CurrencyEntry;
import me.hapyl.fight.database.entry.SkinEntry;
import me.hapyl.fight.game.color.Color;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.entity.SoundEffect;
import me.hapyl.fight.game.heroes.Hero;
import me.hapyl.fight.game.heroes.PlayerSkinPreview;
import me.hapyl.fight.game.heroes.equipment.Slot;
import me.hapyl.fight.game.skin.Skin;
import me.hapyl.fight.game.skin.Skins;
import me.hapyl.fight.game.skin.trait.SkinTrait;
import me.hapyl.fight.game.skin.trait.SkinTraitType;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class SkinGUI extends StyledGUI {
    
    private final Hero hero;
    private final int returnPage;
    private final PlayerDatabase database;
    private final SkinEntry skinEntry;
    
    public SkinGUI(Player player, Hero hero, int returnPage) {
        super(player, hero.getName() + "'s Skins", Size.FIVE);
        
        this.hero = hero;
        this.returnPage = returnPage;
        this.database = CF.getDatabase(player);
        this.skinEntry = this.database.skinEntry;
        
        openInventory();
    }
    
    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Hero Preview", player -> new HeroPreviewGUI(player, hero, returnPage));
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        
        setHeader(new ItemBuilder(Material.LEATHER_CHESTPLATE)
                          .setName("Skins")
                          .addLore()
                          .addLore("Change the appearance of this hero!")
                          .asIcon()
        );
        
        final SmartComponent component = new SmartComponent();
        
        makeItem(component, null);
        
        // Add skins
        final List<Skins> skins = Skins.byHero(hero);
        
        for (Skins enumSkin : skins) {
            makeItem(component, enumSkin);
        }
        
        component.apply(this, SlotPattern.INNER_LEFT_TO_RIGHT, 2);
    }
    
    private void makeItem(SmartComponent component, @Nullable Skins enumSkin) {
        final CurrencyEntry currencyEntry = database.currencyEntry;
        final Skins selectedSkin = skinEntry.getSelected(hero);
        
        ItemBuilder builder;
        final ActionList actionList = new ActionList();
        
        if (enumSkin == null) {
            builder = ItemBuilder.playerHeadUrl(hero.getTextureUrl());
            builder.setName("%s's Default Skin".formatted(hero.getName()));
            builder.addLore(Rarity.LEGENDARY.toString("skin"));
            builder.addLore();
            builder.addSmartLore("This is the default attire!", "&7&o");
            builder.addLore();
        }
        else {
            final Skin skin = enumSkin.getSkin();
            
            builder = new ItemBuilder(skin.getEquipment().getItem(Slot.HELMET));
            builder.setName(skin.getName());
            builder.addLore(skin.getRarity().toString("skin"));
            builder.addLore();
            builder.addTextBlockLore(skin.getDescription(), "&7&o", 35);
            builder.addLore();
            
            final Map<SkinTraitType<?>, SkinTrait> traits = skin.getTraits();
            
            if (!traits.isEmpty()) {
                builder.addLore("&6&lSpecial Effects");
                builder.addLore();
                
                int index = 0;
                
                for (Map.Entry<SkinTraitType<?>, SkinTrait> entry : traits.entrySet()) {
                    final SkinTraitType<?> type = entry.getKey();
                    final SkinTrait trait = entry.getValue();
                    
                    if (index++ != 0) {
                        builder.addLore();
                    }
                    
                    builder.addLore(" &b%s &8(%s)".formatted(trait.getName(), type.getName()));
                    builder.addSmartLore(trait.getDescription(), "&7  ");
                }
                
                builder.addLore();
            }
        }
        
        if (selectedSkin == enumSkin) {
            builder.addLore(Color.SUCCESS + "Currently selected!");
            
            actionList.setAction(
                    ClickType.LEFT, player -> {
                        Message.error(player, "Already selected!");
                        Message.sound(player, SoundEffect.FAILURE);
                        
                        update();
                    }
            );
        }
        else {
            // Can select
            if (enumSkin == null || skinEntry.isOwned(enumSkin)) {
                builder.addLore(Color.BUTTON + "Click to select!");
                
                actionList.setAction(
                        ClickType.LEFT, player -> {
                            Message.success(player, "Selected %s skin!".formatted(enumSkin == null ? "default" : enumSkin.getSkin().getName()));
                            Message.sound(player, SoundEffect.SUCCESS);
                            
                            skinEntry.setSelected(this.hero, enumSkin);
                            update();
                        }
                );
            }
            // Cannot select, display buy or just say unavailable
            else {
                final Skin skin = enumSkin.getSkin();
                final boolean purchasableWithRubies = skin.isPurchasableWithRubies();
                
                if (!purchasableWithRubies) {
                    builder.addLore(Color.ERROR + "This skin is not purchasable!");
                    
                    actionList.setAction(
                            ClickType.LEFT, player -> {
                                Message.error(player, "This skin is not purchasable!");
                                Message.sound(player, SoundEffect.FAILURE);
                            }
                    );
                }
                else {
                    final long rubyPrice = skin.getRubyPrice();
                    
                    builder.addLore("Cost:");
                    builder.addLore(" " + Currency.RUBIES.formatProduct(rubyPrice));
                    builder.addLore();
                    
                    if (!currencyEntry.has(Currency.RUBIES, rubyPrice)) {
                        builder.addLore(Color.ERROR + "Cannot afford!");
                        
                        actionList.setAction(
                                ClickType.LEFT, player -> {
                                    Message.error(player, "You cannot afford this!");
                                    Message.sound(player, SoundEffect.FAILURE);
                                    
                                    update();
                                }
                        );
                    }
                    else {
                        builder.addLore(Color.BUTTON + "Click to purchase!");
                        
                        actionList.setAction(
                                ClickType.LEFT, player -> {
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
                                        public void confirm(@Nonnull Player player) {
                                            if (validateHasCoinsOrCloseGUI(rubyPrice)) {
                                                return;
                                            }
                                            
                                            currencyEntry.subtract(Currency.RUBIES, rubyPrice);
                                            skinEntry.setOwned(enumSkin, true);
                                            
                                            Message.success(player, "Purchased %s skin!".formatted(skin.getName()));
                                            Message.sound(player, SoundEffect.SUCCESS);
                                            
                                            SkinGUI.this.update();
                                        }
                                        
                                        @Override
                                        public void cancel(@Nonnull Player player) {
                                            SkinGUI.this.update();
                                        }
                                    };
                                }
                        );
                    }
                }
            }
            
        }
        
        // Preview
        builder.addLore(Color.BUTTON_DARKER + "Right Click to preview!");
        actionList.setAction(
                ClickType.RIGHT, player -> {
                    player.closeInventory();
                    
                    new PlayerSkinPreview(player, hero, enumSkin != null ? enumSkin.getSkin() : null) {
                        @Override
                        public void onTaskStop() {
                            openInventory();
                        }
                    };
                    
                }
        );
        
        component.add(builder.asIcon(), actionList);
    }
    
    private boolean validateHasCoinsOrCloseGUI(long price) {
        if (!database.currencyEntry.has(Currency.RUBIES, price)) {
            Message.error(player, "Somehow you don't have enough rubies!");
            Message.sound(player, SoundEffect.ERROR);
            
            player.closeInventory();
            return true;
        }
        
        return false;
    }
    
}
