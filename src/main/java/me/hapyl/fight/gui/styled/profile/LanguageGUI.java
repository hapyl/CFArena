package me.hapyl.fight.gui.styled.profile;

import me.hapyl.fight.game.profile.PlayerProfile;
import me.hapyl.fight.gui.styled.ReturnData;
import me.hapyl.fight.gui.styled.Size;
import me.hapyl.fight.gui.styled.StyledGUI;
import me.hapyl.fight.translate.Language;
import me.hapyl.fight.ux.Message;
import me.hapyl.spigotutils.module.inventory.ItemBuilder;
import me.hapyl.spigotutils.module.inventory.gui.SlotPattern;
import me.hapyl.spigotutils.module.inventory.gui.SmartComponent;

import javax.annotation.Nullable;

public class LanguageGUI extends StyledGUI {

    private final PlayerProfile profile;

    public LanguageGUI(PlayerProfile profile) {
        super(profile.getPlayer(), "Select Language", Size.FOUR);

        this.profile = profile;

        openInventory();
    }

    @Nullable
    @Override
    public ReturnData getReturnData() {
        return ReturnData.of("Profile", PlayerProfileGUI::new);
    }

    @Override
    public void onUpdate() {
        final Language playerLanguage = profile.getDatabase().getLanguage();
        final SmartComponent component = newSmartComponent();

        setHeader(ItemBuilder.playerHeadUrl(playerLanguage.getHeadTexture())
                .setName(playerLanguage.getTranslated("language.language"))
                .asIcon());

        for (Language language : Language.values()) {
            final ItemBuilder builder = ItemBuilder.playerHeadUrl(language.getHeadTexture())
                    .setName(language.getTranslated("language.name"))
                    .addLore();

            if (language == playerLanguage) {
                component.add(builder.addLore(language.getTranslated("gui.button.already_selected")).asIcon(), player -> {
                    Message.error(player, "<gui.button.already_selected>");
                });
            }
            else {
                component.add(builder.addLore(language.getTranslated("gui.button.select")).asIcon(), player -> {
                    profile.getDatabase().setLanguage(language);
                    player.closeInventory();

                    Message.success(player, "<language.selected>");
                });
            }
        }

        component.apply(this, SlotPattern.DEFAULT, 2);
    }
}
